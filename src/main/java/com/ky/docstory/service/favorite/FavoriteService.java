package com.ky.docstory.service.favorite;

import com.ky.docstory.dto.repository.MyRepositoryResponse;
import com.ky.docstory.entity.User;
import java.util.List;
import java.util.UUID;

public interface FavoriteService {
    List<MyRepositoryResponse> getMyFavoriteRepositories(User currentUser);
    MyRepositoryResponse addFavorite(UUID repositoryId, User currentUser);
    void removeFavorite(UUID repositoryId, User currentUser);
}