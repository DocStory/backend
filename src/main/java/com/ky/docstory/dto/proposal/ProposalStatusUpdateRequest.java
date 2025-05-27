package com.ky.docstory.dto.proposal;

import com.ky.docstory.entity.Proposal;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Proposal 상태 변경 요청 DTO")
public record ProposalStatusUpdateRequest(

        @NotNull(message = "변경할 상태는 필수입니다.")
        @Schema(description = "변경할 상태 (OPEN, CLOSED, MERGED)", example = "CLOSED", required = true)
        Proposal.Status status
) {}
