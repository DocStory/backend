package com.ky.docstory.repository;

import com.ky.docstory.entity.Repository;
import com.ky.docstory.entity.Team;
import com.ky.docstory.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {
    boolean existsByRepositoryAndUser(Repository repository, User user);

    @Query("SELECT t FROM Team t JOIN FETCH t.user WHERE t.repository = :repository")
    List<Team> findAllByRepositoryWithUser(@Param("repository") Repository repository);

    List<Team> findAllByRepository(Repository repository);

    Optional<Team> findByRepositoryAndUser(Repository repository, User user);

    Optional<Team> findByRepositoryAndUserId(Repository repository, UUID userId);

    @Query("SELECT t FROM Team t JOIN FETCH t.repository r JOIN FETCH r.owner WHERE t.user = :user")
    List<Team> findAllByUserWithRepository(@Param("user") User user);

    Optional<Team> findByRepositoryIdAndUserId(UUID repositoryId, UUID userId);

    @Query("SELECT t FROM Team t WHERE t.repository.id IN :repositoryIds AND t.user.id = :userId")
    List<Team> findAllByRepositoryIdsAndUserId(@Param("repositoryIds") List<UUID> repositoryIds, @Param("userId") UUID userId);

}
