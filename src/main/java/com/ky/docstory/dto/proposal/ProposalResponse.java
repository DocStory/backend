package com.ky.docstory.dto.proposal;

import com.ky.docstory.entity.Proposal;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Proposal 응답 DTO")
public record ProposalResponse(

        @Schema(description = "Proposal ID", example = "d947891e-3c5e-4ae0-9d71-eee6f3f2c3a1")
        UUID id,

        @Schema(description = "제목", example = "문서 수정 제안")
        String title,

        @Schema(description = "설명", example = "문서 내용에 오타가 있어 수정 제안합니다.")
        String description,

        @Schema(description = "상태", example = "OPEN")
        String status
) {
    public static ProposalResponse from(Proposal proposal) {
        return new ProposalResponse(
                proposal.getId(),
                proposal.getTitle(),
                proposal.getDescription(),
                proposal.getStatus().name()
        );
    }
}
