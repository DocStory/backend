package com.ky.docstory.dto.history;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "히스토리 수정 요청 DTO")
public record HistoryUpdateRequest(

        @NotBlank(message = "히스토리 제목은 필수입니다.")
        @Schema(description = "히스토리 제목", example = "히스토리 수정 합니다.", required = true)
        String title,

        @Size(max = 2000, message = "설명은 2000자 이하여야 합니다.")
        @Schema(description = "히스토리 설명", example = "히스토리 수정 내용입니다.", required = false)
        String content
) {}
