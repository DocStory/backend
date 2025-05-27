package com.ky.docstory.service.file;

import com.ky.docstory.common.code.DocStoryResponseCode;
import com.ky.docstory.common.exception.BusinessException;
import com.ky.docstory.dto.file.FileCategory;
import com.ky.docstory.dto.file.FileDownloadResponse;
import com.ky.docstory.dto.file.FileInfo;
import com.ky.docstory.dto.file.FileUploadResponse;
import com.ky.docstory.entity.File;
import com.ky.docstory.entity.Repository;
import com.ky.docstory.entity.User;
import com.ky.docstory.repository.FileRepository;
import com.ky.docstory.repository.RepositoryRepository;
import com.ky.docstory.repository.UserRepository;
import io.awspring.cloud.s3.S3Template;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService{

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    private final S3Template s3Template;
    private final FileRepository fileRepository;
    private final RepositoryRepository repositoryRepository;
    private final UserRepository userRepository;
    private final Tika tika = new Tika();

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    @Override
    @Transactional
    public FileUploadResponse uploadFile(MultipartFile file, UUID repositoryId, UUID parentFileId, User currentUser) {
        validateFile(file, FileCategory.DOCUMENT);

        Repository repository = repositoryRepository.findById(repositoryId)
                .orElseThrow(() -> new BusinessException(DocStoryResponseCode.NOT_FOUND));

        File parentFile = null;
        File rootFile = null;

        if (parentFileId != null) {
            parentFile = fileRepository.findById(parentFileId)
                    .orElseThrow(() -> new BusinessException(DocStoryResponseCode.NOT_FOUND));
            rootFile = parentFile.getRootFile() != null ? parentFile.getRootFile() : parentFile;
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
                .rootFile(rootFile)
                .originFilename(fileInfo.originalFilename())
                .saveFilename(fileInfo.saveFilename())
                .filepath(fileInfo.filePath())
                .fileType(fileInfo.fileType())
                .build();

        savedfile.calculateLevel();

        fileRepository.save(savedfile);

        return FileUploadResponse.from(savedfile);
    }

    @Override
    public FileDownloadResponse downloadFile(UUID fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(DocStoryResponseCode.NOT_FOUND));

        try {
            byte[] fileData = getFileFromS3(file.getFilepath());
            return new FileDownloadResponse(file.getOriginFilename(), fileData);
        } catch (IOException e) {
            throw new BusinessException(DocStoryResponseCode.FILE_DOWNLOAD_FAILED);
        }
    }

    @Override
    @Transactional
    public User uploadProfileImage(String imageUrl, String providerId, String nickname) {
        String fileType = "jpeg";

        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yy-MM-dd"));
        String saveFilename = UUID.randomUUID() + "." + fileType;
        String filePath = String.format("profileImage/%s/%s", dateDir, saveFilename);

        // S3에 파일 원본 저장
        try (InputStream inputStream = new URL(imageUrl).openStream()) {
            s3Template.upload(bucketName, filePath, inputStream);
        } catch (IOException e) {
            throw new BusinessException(DocStoryResponseCode.FILE_UPLOAD_FAILED);
        }

        User newUser = User.builder()
                .providerId(providerId)
                .nickname(nickname)
                .profilePath(filePath)
                .profileFileName(saveFilename)
                .build();

        return userRepository.save(newUser);
    }

    private FileInfo createFileInfo(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        int idx = originalFilename.lastIndexOf(".");
        String extension = originalFilename.substring(idx + 1).toUpperCase();

        File.FileType fileType;
        fileType = File.FileType.valueOf(extension);

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

    private void validateFile(MultipartFile file, FileCategory category) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(DocStoryResponseCode.INVALID_FILE);
        }

        String originalFilename = file.getOriginalFilename();
        if (!originalFilename.contains(".")) {
            throw new BusinessException(DocStoryResponseCode.INVALID_FILE);
        }

        if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
            throw new BusinessException(DocStoryResponseCode.INVALID_FILE);
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toUpperCase();

        Map<String, String> allowedTypes = category.getAllowedTypes();

        if (!allowedTypes.containsKey(extension)) {
            throw new BusinessException(DocStoryResponseCode.INVALID_FILE);
        }

        String tikaMime;
        try {
            tikaMime = tika.detect(file.getInputStream());
        } catch (IOException e) {
            throw new BusinessException(DocStoryResponseCode.INVALID_FILE);
        }

        String expectedMime = allowedTypes.get(extension);

        boolean isValidMime = tikaMime.equals(expectedMime)
                || (extension.equals("DOCX") && tikaMime.equals("application/x-tika-ooxml"))
                || ((extension.equals("HWP") || extension.equals("HWPX")) && tikaMime.equals("application/zip"));

        if (!isValidMime) {
            throw new BusinessException(DocStoryResponseCode.INVALID_FILE);
        }
    }
}
