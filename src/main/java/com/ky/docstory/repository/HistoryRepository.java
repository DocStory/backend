package com.ky.docstory.repository;

import com.ky.docstory.entity.File;
import com.ky.docstory.entity.History;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, UUID> {
    List<History> findByFileIn(List<File> files);
    List<History> findAllByRepositoryId(UUID repositoryId);
    Optional<History> findByFile(File file);

}
