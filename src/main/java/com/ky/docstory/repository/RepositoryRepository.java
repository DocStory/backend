package com.ky.docstory.repository;

import com.ky.docstory.entity.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepositoryRepository extends JpaRepository<Repository, UUID> {
}
