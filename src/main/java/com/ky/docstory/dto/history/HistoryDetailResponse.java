package com.ky.docstory.dto.history;

import com.ky.docstory.dto.file.FileResponse;
import com.ky.docstory.dto.user.UserResponse;
import com.ky.docstory.entity.History;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "히스토리 상세 정보 응답 DTO")
public record HistoryDetailResponse (

        @Schema(description = "히스토리 ID", example = "3c1a0ab4-0b90-4db4-b8e0-1ef2b6db1944")
        UUID Id,

        @Schema(description = "히스토리 제목", example = "오타 수정")
        String title,

        @Schema(description = "히스토리 메세지", example = "2번째 문장 오타 수정")
        String content,

        @Schema(description = "변경 파일 목록 - main 노드부터 순서대로")
        List<FileResponse> files,

        @Schema(description = "히스토리 생성자 정보")
        UserResponse createdBy,

        @Schema(description = "히스토리 생성 일시", example = "2025-04-02T12:00:00Z")
        LocalDateTime createAt
) {
        public static HistoryDetailResponse from(History history, List<FileResponse> fileResponses,
                                                 UserResponse userResponse) {
                return new HistoryDetailResponse(
                        history.getId(),
                        history.getTitle(),
                        history.getContent(),
                        fileResponses,
                        userResponse,
                        history.getCreatedAt()
                );
        }
}
