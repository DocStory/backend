package com.ky.docstory.controller.repository;

import com.ky.docstory.auth.CurrentUser;
import com.ky.docstory.common.dto.DocStoryResponseBody;
import com.ky.docstory.dto.repository.MyRepositoryResponse;
import com.ky.docstory.dto.repository.RepositoryCreateRequest;
import com.ky.docstory.dto.repository.RepositoryResponse;
import com.ky.docstory.entity.User;
import com.ky.docstory.swagger.AlreadyHandledErrorResponseWrapper;
import com.ky.docstory.swagger.BadRequestErrorResponseWrapper;
import com.ky.docstory.swagger.FavoriteRepositoryResponseWrapper;
import com.ky.docstory.swagger.InternalServerErrorResponseWrapper;
import com.ky.docstory.swagger.MyRepositoryListResponseWrapper;
import com.ky.docstory.swagger.NotFoundErrorResponseWrapper;
import com.ky.docstory.swagger.RepositoryCreateResponseWrapper;
import com.ky.docstory.swagger.SuccessEmptyResponseWrapper;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Repository", description = "레포지토리 관련 API")
@RequestMapping("/api/repositories")
public interface RepositoryApi {

    @PostMapping
    @Operation(summary = "레포지토리 생성", description = "새로운 레포지토리를 생성하고, 생성자는 팀장(ADMIN)으로 등록됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "레포지토리가 성공적으로 생성되었습니다.",
                    content = @Content(schema = @Schema(implementation = RepositoryCreateResponseWrapper.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = BadRequestErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = UnauthorizedErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorResponseWrapper.class))),

    })
    ResponseEntity<DocStoryResponseBody<RepositoryResponse>> createRepository(
            @Parameter(hidden = true)
            @CurrentUser User currentUser,
            @RequestBody @Valid RepositoryCreateRequest request);

    @Operation(summary = "내가 속한 레포지토리 목록 조회", description = "현재 로그인한 사용자가 속한 모든 레포지토리 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "레포지토리 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = MyRepositoryListResponseWrapper.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = UnauthorizedErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorResponseWrapper.class)))
    })
    @GetMapping("/my")
    ResponseEntity<DocStoryResponseBody<List<MyRepositoryResponse>>> getMyRepositories(
            @Parameter(hidden = true) @CurrentUser User currentUser);

    @GetMapping("/favorites")
    @Operation(summary = "내 즐겨찾기 레포지토리 목록 조회", description = "현재 로그인한 사용자가 즐겨찾기한 레포지토리 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "즐겨찾기 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = MyRepositoryListResponseWrapper.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = UnauthorizedErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorResponseWrapper.class)))
    })
    ResponseEntity<DocStoryResponseBody<List<MyRepositoryResponse>>> getMyFavoriteRepositories(
            @Parameter(hidden = true)
            @CurrentUser User currentUser
    );

    @PostMapping("/{repositoryId}/favorite")
    @Operation(summary = "레포지토리 즐겨찾기 추가", description = "레포지토리를 즐겨찾기합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "즐겨찾기 추가 성공",
                    content = @Content(schema = @Schema(implementation = FavoriteRepositoryResponseWrapper.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = BadRequestErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = UnauthorizedErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "레포지토리를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = NotFoundErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "409", description = "이미 즐겨찾기에 추가되어있습니다.",
                    content = @Content(schema = @Schema(implementation = AlreadyHandledErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorResponseWrapper.class)))
    })
    ResponseEntity<DocStoryResponseBody<MyRepositoryResponse>> addFavorite(
            @Parameter(hidden = true)
            @CurrentUser User currentUser,
            @PathVariable("repositoryId") UUID repositoryId
    );

    @DeleteMapping("/{repositoryId}/favorite")
    @Operation(summary = "레포지토리 즐겨찾기 제거", description = "레포지토리를 즐겨찾기에서 제거합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "즐겨찾기 제거 성공",
                    content = @Content(schema = @Schema(implementation = SuccessEmptyResponseWrapper.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
                    content = @Content(schema = @Schema(implementation = BadRequestErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자입니다.",
                    content = @Content(schema = @Schema(implementation = UnauthorizedErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "즐겨찾기 또는 레포지토리를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = NotFoundErrorResponseWrapper.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorResponseWrapper.class)))
    })
    ResponseEntity<DocStoryResponseBody<Void>> removeFavorite(
            @Parameter(hidden = true)
            @CurrentUser User currentUser,
            @PathVariable("repositoryId") UUID repositoryId
    );

}
