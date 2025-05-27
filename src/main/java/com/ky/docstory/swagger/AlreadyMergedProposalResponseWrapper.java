package com.ky.docstory.swagger;

import com.ky.docstory.dto.proposal.ProposalResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "MERGED 상태 Proposal 변경 시 응답")
public class AlreadyMergedProposalResponseWrapper {

    @Schema(description = "에러 코드", example = "2000")
    public int code;

    @Schema(description = "에러 메시지", example = "이미 병합된 Proposal은 수정할 수 없습니다.")
    public String message;

    @Schema(description = "데이터 없음", nullable = true)
    public ProposalResponse data;
}
