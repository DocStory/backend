package com.ky.docstory.service.file;

import com.ky.docstory.common.code.DocStoryResponseCode;
import com.ky.docstory.common.exception.BusinessException;
import com.ky.docstory.dto.file.FileDownloadResponse;
import com.ky.docstory.dto.file.FileInfo;
import com.ky.docstory.dto.file.FileUploadResponse;
import com.ky.docstory.entity.File;
import com.ky.docstory.entity.Repository;
import com.ky.docstory.entity.User;
import com.ky.docstory.repository.FileRepository;
import com.ky.docstory.repository.RepositoryRepository;
import io.awspring.cloud.s3.S3Template;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService{

    private final S3Template s3Template;
    private final FileRepository fileRepository;
    private final RepositoryRepository repositoryRepository;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Override
    @Transactional
    public FileUploadResponse uploadFile(MultipartFile file, UUID repositoryId, UUID parentFileId, User currentUser) {
        Repository repository = repositoryRepository.findById(repositoryId)
                .orElseThrow(() -> new BusinessException(DocStoryResponseCode.NOT_FOUND));

        File parentFile = null;
        if (parentFileId != null) {
            parentFile = fileRepository.findById(parentFileId)
                    .orElseThrow(() -> new BusinessException(DocStoryResponseCode.NOT_FOUND));
        }

        FileInfo fileInfo = createFileInfo(file);

        // S3에 파일 원본 저장
        try (InputStream inputStream = file.getInputStream()) {
            s3Template.upload(bucketName, fileInfo.filePath(), inputStream);
        } catch (IOException e) {
            throw new BusinessException(DocStoryResponseCode.FILE_UPLOAD_FAILED);
        }

        // DB에 파일 메타 정보 저장
        File savedfile = File.builder()
                .repository(repository)
                .parentFile(parentFile)
                .originFilename(fileInfo.originalFilename())
                .saveFilename(fileInfo.saveFilename())
                .filepath(fileInfo.filePath())
                .fileType(fileInfo.fileType())
                .build();

        fileRepository.save(savedfile);

        return FileUploadResponse.from(savedfile);
    }

    @Override
    public FileDownloadResponse downloadFile(UUID fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(DocStoryResponseCode.NOT_FOUND));

        try {
            byte[] fileData = getFileFromS3(file.getFilepath());
            return new FileDownloadResponse(file.getOriginFilename(), file.getFileType(), fileData);
        } catch (IOException e) {
            throw new BusinessException(DocStoryResponseCode.FILE_DOWNLOAD_FAILED);
        }
    }

    private FileInfo createFileInfo(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BusinessException(DocStoryResponseCode.PARAMETER_ERROR);
        }

        int idx = originalFilename.lastIndexOf(".");
        String extension = originalFilename.substring(idx + 1).toUpperCase();

        File.FileType fileType;
        try {
            fileType = File.FileType.valueOf(extension);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(DocStoryResponseCode.PARAMETER_ERROR);
        }

        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yy-MM-dd"));
        String saveFilename = UUID.randomUUID().toString();
        String filePath = String.format("files/%s/%s.%s", dateDir, saveFilename, fileType);

        return new FileInfo(originalFilename, saveFilename, fileType, filePath);
    }

    private byte[] getFileFromS3(String filePath) throws IOException {
        Resource resource = s3Template.download(bucketName, filePath);

        try (InputStream inputStream = resource.getInputStream()) {
            return inputStream.readAllBytes();
        }
    }

}
