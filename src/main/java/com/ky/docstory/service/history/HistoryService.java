package com.ky.docstory.service.history;

import com.ky.docstory.dto.history.HistoryCreateRequest;
import com.ky.docstory.dto.history.HistoryDetailResponse;
import com.ky.docstory.dto.history.HistoryFileResponse;
import com.ky.docstory.dto.history.HistoryListResponse;
import com.ky.docstory.dto.history.HistoryResponse;
import com.ky.docstory.dto.history.HistoryUpdateRequest;
import com.ky.docstory.entity.User;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface HistoryService {
    HistoryResponse createHistory(UUID repositoryId, HistoryCreateRequest historyCreateRequest, MultipartFile file, User currentUser);
    List<HistoryFileResponse> getHistoryRootFiles(UUID repositoryId, User currentUser);
    List<List<HistoryListResponse>> getFilteredHistories(UUID repositoryId, List<UUID> rootIds, String status, User currentUser);
    HistoryDetailResponse getHistoryDetail(UUID repositoryId, UUID historyId, User currentUser);
    HistoryResponse updateHistory(UUID repositoryId, UUID historyId, HistoryUpdateRequest historyUpdateRequest, User currentUser);
}
