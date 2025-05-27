package com.ky.docstory.dto.proposal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Proposal 생성 요청 DTO")
public record ProposalCreateRequest(

        @Schema(description = "히스토리 ID", required = true, example = "8e7b7c0a-0d52-4c5f-8fa4-d1f7c69d02ae")
        UUID historyId,

        @NotBlank(message = "제목은 필수입니다.")
        @Size(max = 255, message = "제목은 255자 이하여야 합니다.")
        @Schema(description = "제목", example = "문서 수정 제안")
        String title,

        @Size(max = 2000, message = "설명은 2000자 이하여야 합니다.")
        @Schema(description = "설명", example = "문서 내용에 오타가 있어 수정 제안합니다.")
        String description
) {}
