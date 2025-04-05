package com.ky.docstory.dto.history;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(description = "히스토리 생성 요청 DTO")
public record HistoryCreateRequest (

        @NotBlank(message = "히스토리 제목은 필수입니다.")
        @Schema(description = "히스토리 제목", example = "히스토리 생성합니다.", required = true)
        String title,

        @Size(max = 2000, message = "설명은 2000자 이하여야 합니다.")
        @Schema(description = "히스토리 설명", example = "히스토리 생성 내용입니다.", required = false)
        String content,

        @Schema(description = "히스토리에 등록할 파일의 베이스가 되는 파일 ID", example = "3c1a0ab4-0b90-4db4-b8e0-1ef2b6db1944", required = false)
        UUID parentFileId
) {}
