package com.ky.docstory.repository;

import com.ky.docstory.entity.Repository;
import com.ky.docstory.entity.Favorite;
import com.ky.docstory.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {
    Optional<Favorite> findByUserAndRepository(User user, Repository repository);

    boolean existsByUserAndRepository(User user, Repository repository);

    @Query("SELECT f FROM Favorite f JOIN FETCH f.repository r WHERE f.user = :user")
    List<Favorite> findAllByUser(@Param("user") User user);
}
