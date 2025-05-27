package com.ky.docstory.swagger;

import com.ky.docstory.dto.proposal.ProposalResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Proposal 상태 변경 응답 Wrapper")
public class ProposalStatusUpdateResponseWrapper {

    @Schema(description = "응답 코드", example = "100")
    public int code;

    @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
    public String message;

    @Schema(description = "상태가 변경된 Proposal 정보")
    public ProposalResponse data;
}
