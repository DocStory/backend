package com.ky.docstory.service.favorite;

import com.ky.docstory.common.code.DocStoryResponseCode;
import com.ky.docstory.common.exception.BusinessException;
import com.ky.docstory.common.validator.RepositoryValidator;
import com.ky.docstory.dto.repository.MyRepositoryResponse;
import com.ky.docstory.entity.Favorite;
import com.ky.docstory.entity.Repository;
import com.ky.docstory.entity.Team;
import com.ky.docstory.entity.User;
import com.ky.docstory.repository.FavoriteRepository;
import com.ky.docstory.repository.TeamRepository;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final RepositoryValidator repositoryValidator;
    private final TeamRepository teamRepository;

    @Override
    @Transactional(readOnly = true)
    public List<MyRepositoryResponse> getMyFavoriteRepositories(User currentUser) {
        List<Favorite> favorites = favoriteRepository.findAllByUser(currentUser);

        List<UUID> repositoryIds = favorites.stream()
                .map(fav -> fav.getRepository().getId())
                .toList();

        List<Team> teams = teamRepository.findAllByRepositoryIdsAndUserId(repositoryIds, currentUser.getId());

        Map<UUID, Team> teamMap = teams.stream()
                .collect(Collectors.toMap(
                        team -> team.getRepository().getId(),
                        team -> team
                ));

        return favorites.stream()
                .map(fav -> {
                    Repository repo = fav.getRepository();
                    Team team = teamMap.get(repo.getId());
                    if (team == null) {
                        throw new BusinessException(DocStoryResponseCode.FORBIDDEN);
                    }
                    return MyRepositoryResponse.from(team, true);
                })
                .toList();
    }

    @Override
    @Transactional
    public MyRepositoryResponse addFavorite(UUID repositoryId, User currentUser) {
        Repository repository = repositoryValidator.validateRepositoryAccess(repositoryId, currentUser.getId());

        boolean exists = favoriteRepository.existsByUserAndRepository(currentUser, repository);
        if (exists) {
            throw new BusinessException(DocStoryResponseCode.ALREADY_HANDLED);
        }

        Favorite favorite = Favorite.builder()
                .user(currentUser)
                .repository(repository)
                .build();

        favoriteRepository.save(favorite);

        Team team = teamRepository.findByRepositoryAndUser(repository, currentUser)
                .orElseThrow(() -> new BusinessException(DocStoryResponseCode.FORBIDDEN));

        return MyRepositoryResponse.from(team, true);
    }

    @Override
    @Transactional
    public void removeFavorite(UUID repositoryId, User currentUser) {
        Repository repository = repositoryValidator.validateRepositoryAccess(repositoryId, currentUser.getId());

        Favorite favorite = favoriteRepository.findByUserAndRepository(currentUser, repository)
                .orElseThrow(() -> new BusinessException(DocStoryResponseCode.NOT_FOUND));

        favoriteRepository.delete(favorite);
    }

}
