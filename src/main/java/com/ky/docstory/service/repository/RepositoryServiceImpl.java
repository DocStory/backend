package com.ky.docstory.service.repository;

import com.ky.docstory.dto.repository.MyRepositoryResponse;
import com.ky.docstory.dto.repository.RepositoryCreateRequest;
import com.ky.docstory.dto.repository.RepositoryResponse;
import com.ky.docstory.entity.Repository;
import com.ky.docstory.entity.Team;
import com.ky.docstory.entity.User;
import com.ky.docstory.repository.FavoriteRepository;
import com.ky.docstory.repository.RepositoryRepository;
import com.ky.docstory.repository.TeamRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RepositoryServiceImpl implements RepositoryService {

    private final RepositoryRepository repositoryRepository;
    private final TeamRepository teamRepository;
    private final FavoriteRepository favoriteRepository;

    @Override
    @Transactional
    public RepositoryResponse createRepository(RepositoryCreateRequest request, User owner) {
        Repository repository = Repository.builder()
                .name(request.name())
                .description(request.description())
                .owner(owner)
                .build();

        repositoryRepository.save(repository);

        Team team = Team.builder()
                .repository(repository)
                .user(owner)
                .role(Team.Role.ADMIN)
                .build();

        teamRepository.save(team);

        return RepositoryResponse.from(repository);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MyRepositoryResponse> getMyRepositories(User currentUser) {
        List<Team> myTeams = teamRepository.findAllByUserWithRepository(currentUser);

        Set<UUID> favoriteIds = favoriteRepository.findAllByUser(currentUser).stream()
                .map(fav -> fav.getRepository().getId())
                .collect(Collectors.toSet());

        return myTeams.stream()
                .map(team -> {
                    boolean isFavorite = favoriteIds.contains(team.getRepository().getId());
                    return MyRepositoryResponse.from(team, isFavorite);
                })
                .toList();
    }
}