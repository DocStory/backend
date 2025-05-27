package com.ky.docstory.controller.repository;

import com.ky.docstory.auth.CurrentUser;
import com.ky.docstory.common.dto.DocStoryResponseBody;
import com.ky.docstory.dto.repository.MyRepositoryResponse;
import com.ky.docstory.dto.repository.RepositoryCreateRequest;
import com.ky.docstory.dto.repository.RepositoryResponse;
import com.ky.docstory.entity.User;
import com.ky.docstory.service.favorite.FavoriteService;
import com.ky.docstory.service.repository.RepositoryService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RepositoryController implements RepositoryApi {

    private final RepositoryService repositoryService;
    private final FavoriteService favoriteService;

    @Override
    public ResponseEntity<DocStoryResponseBody<RepositoryResponse>> createRepository(
            @CurrentUser User currentUser,
            @RequestBody @Valid RepositoryCreateRequest request) {
        RepositoryResponse response = repositoryService.createRepository(request, currentUser);
        return ResponseEntity.ok(DocStoryResponseBody.success(response));
    }

    @Override
    public ResponseEntity<DocStoryResponseBody<List<MyRepositoryResponse>>> getMyRepositories(User currentUser) {
        List<MyRepositoryResponse> response = repositoryService.getMyRepositories(currentUser);
        return ResponseEntity.ok(DocStoryResponseBody.success(response));
    }

    @Override
    public ResponseEntity<DocStoryResponseBody<List<MyRepositoryResponse>>> getMyFavoriteRepositories(User currentUser) {
        List<MyRepositoryResponse> myRepositoryResponses = favoriteService.getMyFavoriteRepositories(currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(DocStoryResponseBody.success(myRepositoryResponses));

    }

    @Override
    public ResponseEntity<DocStoryResponseBody<MyRepositoryResponse>> addFavorite(User currentUser, UUID repositoryId) {
        MyRepositoryResponse myRepositoryResponse = favoriteService.addFavorite(repositoryId, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(DocStoryResponseBody.success(myRepositoryResponse));
    }

    @Override
    public ResponseEntity<DocStoryResponseBody<Void>> removeFavorite(User currentUser, UUID repositoryId) {
        favoriteService.removeFavorite(repositoryId, currentUser);
        return ResponseEntity.status(HttpStatus.OK).body(DocStoryResponseBody.success(null));
    }

}
