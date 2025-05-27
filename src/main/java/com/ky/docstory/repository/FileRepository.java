package com.ky.docstory.repository;

import com.ky.docstory.entity.File;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, UUID> {
    List<File> findByRepositoryIdAndParentFileIsNull(UUID repositoryId);

    List<File> findByParentFile(File parentFile);
}
