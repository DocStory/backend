package com.ky.docstory.controller.history;

import com.ky.docstory.common.dto.DocStoryResponseBody;
import com.ky.docstory.dto.history.HistoryCreateRequest;
import com.ky.docstory.dto.history.HistoryDetailResponse;
import com.ky.docstory.dto.history.HistoryFileResponse;
import com.ky.docstory.dto.history.HistoryListResponse;
import com.ky.docstory.dto.history.HistoryResponse;
import com.ky.docstory.dto.history.HistoryUpdateRequest;
import com.ky.docstory.entity.User;
import com.ky.docstory.service.history.HistoryService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class HistoryController implements HistoryApi{

    private final HistoryService historyService;

    @Override
    public ResponseEntity<DocStoryResponseBody<HistoryResponse>> createHistory(
            User currentUser,
            UUID repositoryId,
            HistoryCreateRequest historyCreateRequest,
            MultipartFile file) {
        HistoryResponse historyResponse = historyService.createHistory(repositoryId, historyCreateRequest, file, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(DocStoryResponseBody.success(historyResponse));
    }

    @Override
    public ResponseEntity<DocStoryResponseBody<List<HistoryFileResponse>>> getHistoryRootFiles(
            User currentUser,
            UUID repositoryId) {
        List<HistoryFileResponse> historyFileResponseList = historyService.getHistoryRootFiles(repositoryId, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(DocStoryResponseBody.success(historyFileResponseList));
    }

    @Override
    public ResponseEntity<DocStoryResponseBody<List<List<HistoryListResponse>>>> getFilteredHistories(
            User currentUser,
            UUID repositoryId,
            List<UUID> rootIds,
            String status) {
        List<List<HistoryListResponse>> historyListResponses = historyService.getFilteredHistories(repositoryId, rootIds, status, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(DocStoryResponseBody.success(historyListResponses));
    }

    @Override
    public ResponseEntity<DocStoryResponseBody<HistoryDetailResponse>> getHistoryDetail(
            User currentUser,
            UUID repositoryId,
            UUID historyId) {
        HistoryDetailResponse historyDetailResponse = historyService.getHistoryDetail(repositoryId, historyId, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(DocStoryResponseBody.success(historyDetailResponse));
    }

    @Override
    public ResponseEntity<DocStoryResponseBody<HistoryResponse>> updateHistory(
            User currentUser,
            UUID repositoryId,
            UUID historyId,
            HistoryUpdateRequest historyUpdateRequest) {
        HistoryResponse historyResponse = historyService.updateHistory(repositoryId, historyId, historyUpdateRequest, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(DocStoryResponseBody.success(historyResponse));
    }
}
