package com.ky.docstory.controller.history;

import com.ky.docstory.auth.CurrentUser;
import com.ky.docstory.common.dto.DocStoryResponseBody;
import com.ky.docstory.dto.history.HistoryCreateRequest;
import com.ky.docstory.dto.history.HistoryDetailResponse;
import com.ky.docstory.dto.history.HistoryFileResponse;
import com.ky.docstory.dto.history.HistoryListResponse;
import com.ky.docstory.dto.history.HistoryResponse;
import com.ky.docstory.dto.history.HistoryUpdateRequest;
import com.ky.docstory.entity.User;
import com.ky.docstory.swagger.BadRequestErrorResponseWrapper;
import com.ky.docstory.swagger.ForbiddenErrorResponseWrapper;
import com.ky.docstory.swagger.HistoryDetailResponseWrapper;
import com.ky.docstory.swagger.HistoryFileResponseWrapper;
import com.ky.docstory.swagger.HistoryListResponseWrapper;
import com.ky.docstory.swagger.HistoryResponseWrapper;
import com.ky.docstory.swagger.InternalServerErrorResponseWrapper;
import com.ky.docstory.swagger.NotFoundErrorResponseWrapper;
import com.ky.docstory.swagger.UnauthorizedErrorResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "History", description = "History 관련 API")
@RequestMapping("/api/repositories/{repositoryId}")
public interface HistoryApi {

    @PostMapping(
            value = "/history",
            consumes = {
                    MediaType.MULTIPART_FORM_DATA_VALUE,
            }
    )
    @Operation(summary = "히스토리 생성", description = "파일 버전 관리를 위한 기록을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "히스토리를 성공적으로 생성했습니다.",
                    content = @Content(schema = @Schema(implementation = HistoryResponseWrapper.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = BadRequestErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = UnauthorizedErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "403", description = "레포지토리 권한이 없습니다.",
                    content = @Content(schema = @Schema(implementation = ForbiddenErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "레포지토리를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = NotFoundErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorResponseWrapper.class))),
    })
    ResponseEntity<DocStoryResponseBody<HistoryResponse>> createHistory(
            @Parameter(hidden = true)
            @CurrentUser User currentUser,
            @PathVariable UUID repositoryId,
            @RequestPart @Valid HistoryCreateRequest historyCreateRequest,
            @Parameter(
                    description = "히스토리 등록 파일",
                    content = @Content(mediaType = "multipart/form-data")
            )
            @RequestPart MultipartFile file
    );

    @GetMapping(
            value = "/history/files"
    )
    @Operation(summary = "히스토리 검색 루트 파일 목록 조회", description = "히스토리 검색을 위해 필터로 사용할 현재 저장소의 모든 루트 파일 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "히스토리 검색을 위한 메인 파일 목록을 성공적으로 받아왔습니다.",
                    content = @Content(schema = @Schema(implementation = HistoryFileResponseWrapper.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = BadRequestErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = UnauthorizedErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "403", description = "레포지토리 권한이 없습니다.",
                    content = @Content(schema = @Schema(implementation = ForbiddenErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "레포지토리를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = NotFoundErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorResponseWrapper.class))),
    })
    ResponseEntity<DocStoryResponseBody<List<HistoryFileResponse>>> getHistoryRootFiles(
            @Parameter(hidden = true)
            @CurrentUser User currentUser,
            @PathVariable UUID repositoryId
    );

    @GetMapping(
            value = "/history"
    )
    @Operation(summary = "히스토리 목록 조회", description = "선택된 루트 파일들과 히스토리 상태 필터 조건에 따라 히스토리 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "히스토리 목록을 성공적으로 조회했습니다.",
                    content = @Content(schema = @Schema(implementation = HistoryListResponseWrapper.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = BadRequestErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = UnauthorizedErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "403", description = "레포지토리 권한이 없습니다.",
                    content = @Content(schema = @Schema(implementation = ForbiddenErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "레포지토리를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = NotFoundErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorResponseWrapper.class))),
    })
    ResponseEntity<DocStoryResponseBody<List<List<HistoryListResponse>>>> getFilteredHistories(
            @Parameter(hidden = true)
            @CurrentUser User currentUser,
            @PathVariable UUID repositoryId,
            @Parameter(description = "히스토리 필터 기준이 되는 루트 파일 ID 목록")
            @RequestParam(required = false) List<UUID> rootIds,
            @Parameter(description = "히스토리 상태 필터 (all | main | sub | aband)", example = "all")
            @RequestParam(defaultValue = "all") String status
    );

    @GetMapping(
            value = "/history/{historyId}"
    )
    @Operation(summary = "히스토리 상세 조회", description = "히스토리의 상세 정보를 조회 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "히스토리 검색을 위한 파일을 성공적으로 받아왔습니다.",
                    content = @Content(schema = @Schema(implementation = HistoryDetailResponseWrapper.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = BadRequestErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = UnauthorizedErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "403", description = "레포지토리 혹은 히스토리 권한이 없습니다.",
                    content = @Content(schema = @Schema(implementation = ForbiddenErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "레포지토리 혹은 히스토리를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = NotFoundErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorResponseWrapper.class))),
    })
    ResponseEntity<DocStoryResponseBody<HistoryDetailResponse>> getHistoryDetail(
            @Parameter(hidden = true)
            @CurrentUser User currentUser,
            @PathVariable UUID repositoryId,
            @PathVariable UUID historyId
    );

    @PutMapping(
            value = "/history/{historyId}"
    )
    @Operation(summary = "히스토리 수정", description = "파일 버전 관리를 위한 기록을 수정합니다.(제목과 내용만 수정 가능)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "히스토리를 성공적으로 수정했습니다.",
                    content = @Content(schema = @Schema(implementation = HistoryResponseWrapper.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = BadRequestErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = UnauthorizedErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "403", description = "레포지토리 혹은 히스토리 권한이 없습니다.",
                    content = @Content(schema = @Schema(implementation = ForbiddenErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "레포지토리 혹은 히스토리를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = NotFoundErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorResponseWrapper.class))),
    })
    ResponseEntity<DocStoryResponseBody<HistoryResponse>> updateHistory(
            @Parameter(hidden = true)
            @CurrentUser User currentUser,
            @PathVariable UUID repositoryId,
            @PathVariable UUID historyId,
            @RequestBody @Valid HistoryUpdateRequest historyUpdateRequest
    );

}
