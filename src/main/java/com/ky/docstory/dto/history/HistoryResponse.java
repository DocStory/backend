package com.ky.docstory.dto.history;

import com.ky.docstory.entity.File;
import com.ky.docstory.entity.History;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Optional;
import java.util.UUID;

@Schema(description = "히스토리 응답 DTO")
public record HistoryResponse (

        @Schema(description = "히스토리 ID", example = "3c1a0ab4-0b90-4db4-b8e0-1ef2b6db1944")
        UUID id,

        @Schema(description = "히스토리 제목", example = "피드백 반영 파일")
        String title,

        @Schema(description = "히스토리 설명", example = "피드백 반영한 파일을 등록합니다!")
        String content,

        @Schema(description = "히스토리에 등록한 파일명", example = "docStory 수정본.pdf")
        String fileName,

        @Schema(description = "히스토리에 등록할 파일의 베이스가 되는 파일명", example = "docStory.pdf")
        String parentFileName

) {
        public static HistoryResponse from(History history) {
                return new HistoryResponse(
                        history.getId(),
                        history.getTitle(),
                        history.getContent(),
                        history.getFile().getOriginFilename(),
                        Optional.ofNullable(history.getFile().getParentFile())
                                .map(File::getOriginFilename)
                                .orElse(null)
                );
        }
}
