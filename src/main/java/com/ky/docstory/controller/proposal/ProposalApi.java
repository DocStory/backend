package com.ky.docstory.controller.proposal;

import com.ky.docstory.auth.CurrentUser;
import com.ky.docstory.common.dto.DocStoryResponseBody;
import com.ky.docstory.dto.proposal.*;
import com.ky.docstory.entity.User;
import com.ky.docstory.swagger.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Proposal", description = "Proposal 관련 API")
@RequestMapping("/api/proposals")
public interface ProposalApi {

    @PostMapping
    @Operation(summary = "Proposal 생성", description = "문서 히스토리에 대해 제안을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "제안이 성공적으로 생성되었습니다.",
                    content = @Content(schema = @Schema(implementation = ProposalCreateResponseWrapper.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = BadRequestErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = UnauthorizedErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorResponseWrapper.class)))
    })
    ResponseEntity<DocStoryResponseBody<ProposalResponse>> createProposal(
            @Parameter(hidden = true) @CurrentUser User currentUser,
            @RequestBody @Valid ProposalCreateRequest request);

    @GetMapping("/{proposalId}")
    @Operation(summary = "Proposal 단건 조회", description = "Proposal ID로 단일 제안을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ProposalCreateResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "해당 Proposal이 존재하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = NotFoundErrorResponseWrapper.class)))
    })
    ResponseEntity<DocStoryResponseBody<ProposalResponse>> getProposalById(
            @Parameter(description = "Proposal ID", required = true) @PathVariable UUID proposalId,
            @Parameter(hidden = true) @CurrentUser User currentUser);

    @GetMapping("/repository/{repositoryId}")
    @Operation(summary = "Proposal 목록 조회", description = "레포지토리 내 Proposal을 필터 조건에 따라 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = ProposalListResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "레포지토리를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = NotFoundErrorResponseWrapper.class)))
    })
    ResponseEntity<DocStoryResponseBody<List<ProposalResponse>>> getProposalsByRepository(
            @Parameter(description = "레포지토리 ID", required = true) @PathVariable UUID repositoryId,
            @Parameter(description = "필터 (ALL, OPEN, CLOSED)", required = true) @RequestParam ProposalFilterType filter,
            @Parameter(hidden = true) @CurrentUser User currentUser);


    @PutMapping("/{proposalId}")
    @Operation(summary = "Proposal 수정", description = "작성자가 제안을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = ProposalUpdateResponseWrapper.class))),
            @ApiResponse(responseCode = "403", description = "작성자만 수정할 수 있습니다.",
                    content = @Content(schema = @Schema(implementation = ForbiddenErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "409", description = "MERGED 상태의 제안은 수정할 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = AlreadyHandledErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "해당 제안을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = NotFoundErrorResponseWrapper.class)))
    })
    ResponseEntity<DocStoryResponseBody<ProposalResponse>> updateProposal(
            @Parameter(description = "수정할 Proposal ID", required = true) @PathVariable UUID proposalId,
            @RequestBody @Valid ProposalUpdateRequest request,
            @Parameter(hidden = true) @CurrentUser User currentUser);

    @PatchMapping("/{proposalId}/status")
    @Operation(summary = "Proposal 상태 변경", description = "작성자가 제안의 상태를 변경합니다. MERGED 상태는 되돌릴 수 없습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상태 변경 성공",
                    content = @Content(schema = @Schema(implementation = ProposalStatusUpdateResponseWrapper.class))),
            @ApiResponse(responseCode = "403", description = "작성자만 상태를 변경할 수 있습니다.",
                    content = @Content(schema = @Schema(implementation = ForbiddenErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "409", description = "MERGED 제안은 상태 변경할 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = AlreadyMergedProposalResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "해당 Proposal을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = NotFoundErrorResponseWrapper.class)))
    })
    ResponseEntity<DocStoryResponseBody<ProposalResponse>> updateProposalStatus(
            @Parameter(description = "상태를 변경할 Proposal ID", required = true) @PathVariable UUID proposalId,
            @RequestBody @Valid ProposalStatusUpdateRequest request,
            @Parameter(hidden = true) @CurrentUser User currentUser);

    @PatchMapping("/{proposalId}/merge")
    @Operation(summary = "Proposal 병합", description = "Proposal을 병합하고 연결된 파일 트리 기준으로 History 상태를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "병합 성공",
                    content = @Content(schema = @Schema(implementation = ProposalMergeResponseWrapper.class))),
            @ApiResponse(responseCode = "403", description = "병합 권한이 없습니다.",
                    content = @Content(schema = @Schema(implementation = ForbiddenErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "409", description = "이미 병합된 Proposal입니다.",
                    content = @Content(schema = @Schema(implementation = AlreadyMergedProposalResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "해당 Proposal을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = NotFoundErrorResponseWrapper.class)))
    })
    ResponseEntity<DocStoryResponseBody<ProposalResponse>> mergeProposal(
            @Parameter(description = "병합할 Proposal ID", required = true) @PathVariable UUID proposalId,
            @Parameter(hidden = true) @CurrentUser User currentUser);

}
