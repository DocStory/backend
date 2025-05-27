package com.ky.docstory.service.history;

import com.ky.docstory.common.code.DocStoryResponseCode;
import com.ky.docstory.common.exception.BusinessException;
import com.ky.docstory.dto.file.FileResponse;
import com.ky.docstory.dto.file.FileUploadResponse;
import com.ky.docstory.dto.history.HistoryCreateRequest;
import com.ky.docstory.dto.history.HistoryDetailResponse;
import com.ky.docstory.dto.history.HistoryFileResponse;
import com.ky.docstory.dto.history.HistoryFilter;
import com.ky.docstory.dto.history.HistoryListResponse;
import com.ky.docstory.dto.history.HistoryResponse;
import com.ky.docstory.dto.history.HistoryUpdateRequest;
import com.ky.docstory.dto.user.UserResponse;
import com.ky.docstory.entity.File;
import com.ky.docstory.entity.History;
import com.ky.docstory.entity.User;
import com.ky.docstory.repository.FileRepository;
import com.ky.docstory.repository.HistoryRepository;
import com.ky.docstory.repository.TeamRepository;
import com.ky.docstory.service.file.FileService;
import com.ky.docstory.service.user.UserService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final UserService userService;
    private final FileService fileService;
    private final TeamRepository teamRepository;
    private final FileRepository fileRepository;
    private final HistoryRepository historyRepository;

    @Override
    @Transactional
    public HistoryResponse createHistory(UUID repositoryId, HistoryCreateRequest historyCreateRequest,
                                         MultipartFile file, User currentUser) {

        validateRepositoryAccess(repositoryId, currentUser.getId());

        FileUploadResponse fileUploadResponse = fileService.uploadFile(file, repositoryId, historyCreateRequest.parentFileId(), currentUser);

        File savedFile = getFileByIdOrThrow(fileUploadResponse.id());

        History history = History.builder()
                .title(historyCreateRequest.title())
                .content(historyCreateRequest.content())
                .createdBy(currentUser)
                .repository(savedFile.getRepository())
                .file(savedFile)
                .historyStatus(History.HistoryStatus.NORMAL)
                .build();

        History savedHistory = historyRepository.save(history);

        return HistoryResponse.from(savedHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoryFileResponse> getHistoryRootFiles(UUID repositoryId, User currentUser) {

        validateRepositoryAccess(repositoryId, currentUser.getId());

        List<File> files = fileRepository.findByRepositoryIdAndParentFileIsNull(repositoryId);

        return files.stream()
                .map(HistoryFileResponse::from)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<List<HistoryListResponse>> getFilteredHistories(UUID repositoryId, List<UUID> rootIds, String status,
                                                                User currentUser) {

        validateRepositoryAccess(repositoryId, currentUser.getId());

        if (rootIds == null || rootIds.isEmpty()) {
            List<File> rootFiles = fileRepository.findByRepositoryIdAndParentFileIsNull(repositoryId);
            rootIds = rootFiles.stream()
                    .map(File::getId)
                    .toList();
        }

        List<List<HistoryListResponse>> responseList = new ArrayList<>();

        for (UUID rootId : rootIds) {
            File rootFile = getFileByIdOrThrow(rootId);
            List<File> fileTree = getFileTreeFromRoot(rootFile);
            List<History> histories = historyRepository.findByFileIn(fileTree);

            List<HistoryListResponse> filtered = histories.stream()
                    .filter(h -> HistoryFilter.matches(h.getHistoryStatus(), status))
                    .map(HistoryListResponse::from)
                    .toList();

            if (!filtered.isEmpty()) {
                responseList.add(filtered);
            }
        }

        return responseList;
    }

    @Override
    @Transactional(readOnly = true)
    public HistoryDetailResponse getHistoryDetail(UUID repositoryId, UUID historyId, User currentUser) {

        validateRepositoryAccess(repositoryId, currentUser.getId());

        History savedHistory = getHistoryByIdOrThrow(historyId);

        List<FileResponse> fileResponses = getFileResponseTreeFromChild(savedHistory.getFile());

        UserResponse userResponse = userService.getUserInfo(currentUser);

        return HistoryDetailResponse.from(savedHistory, fileResponses, userResponse);
    }

    @Override
    @Transactional
    public HistoryResponse updateHistory(UUID repositoryId, UUID historyId, HistoryUpdateRequest historyUpdateRequest,
                                         User currentUser) {

        validateRepositoryAccess(repositoryId, currentUser.getId());

        History prevHistory = getHistoryByIdOrThrow(historyId);

        if (!currentUser.getId().equals(prevHistory.getCreatedBy().getId())) {
            throw new BusinessException(DocStoryResponseCode.FORBIDDEN);
        }

        History history = History.builder()
                .id(historyId)
                .title(historyUpdateRequest.title())
                .content(historyUpdateRequest.content())
                .createdBy(currentUser)
                .repository(prevHistory.getRepository())
                .file(prevHistory.getFile())
                .historyStatus(History.HistoryStatus.NORMAL)
                .build();

        History savedHistory = historyRepository.save(history);

        return HistoryResponse.from(savedHistory);
    }

    private List<File> getFileTreeFromRoot(File root) {
        List<File> files = new ArrayList<>();
        files.add(root);

        List<File> children = fileRepository.findByParentFile(root);
        for (File child : children) {
            files.addAll(getFileTreeFromRoot(child));
        }

        return files;
    }

    private List<FileResponse> getFileResponseTreeFromChild(File file) {
        List<FileResponse> fileResponses = new ArrayList<>();

        while (file != null) {
            fileResponses.add(FileResponse.from(file));
            file = file.getParentFile();
        }

        Collections.reverse(fileResponses);

        return fileResponses;
    }

    private void validateRepositoryAccess(UUID repositoryId, UUID userId) {
        teamRepository.findByRepositoryIdAndUserId(repositoryId, userId)
                .orElseThrow(() -> new BusinessException(DocStoryResponseCode.FORBIDDEN));
    }

    private History getHistoryByIdOrThrow(UUID historyId) {
        return historyRepository.findById(historyId)
                .orElseThrow(() -> new BusinessException(DocStoryResponseCode.NOT_FOUND));
    }

    private File getFileByIdOrThrow(UUID fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(DocStoryResponseCode.NOT_FOUND));
    }

}
