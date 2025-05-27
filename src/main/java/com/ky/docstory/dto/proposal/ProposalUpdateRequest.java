package com.ky.docstory.dto.proposal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Proposal 수정 요청 DTO")
public record ProposalUpdateRequest(

        @NotBlank(message = "제안 제목은 필수입니다.")
        @Size(max = 255, message = "제안 제목은 255자 이하여야 합니다.")
        @Schema(description = "제안 제목", example = "내용 수정 제안", required = true)
        String title,

        @Schema(description = "제안 설명", example = "여기 내용을 이렇게 고치면 어떨까요?")
        @Size(max = 2000, message = "설명은 2000자 이하여야 합니다.")
        String description
) {}
