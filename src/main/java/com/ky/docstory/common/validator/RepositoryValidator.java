package com.ky.docstory.common.validator;

import com.ky.docstory.common.code.DocStoryResponseCode;
import com.ky.docstory.common.exception.BusinessException;
import com.ky.docstory.entity.Repository;
import com.ky.docstory.repository.RepositoryRepository;
import com.ky.docstory.repository.TeamRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RepositoryValidator {

    private final RepositoryRepository repositoryRepository;
    private final TeamRepository teamRepository;

    @Transactional(readOnly = true)
    public Repository validateRepositoryAccess(UUID repositoryId, UUID userId) {
        Repository repository = repositoryRepository.findById(repositoryId)
                .orElseThrow(() -> new BusinessException(DocStoryResponseCode.NOT_FOUND));

        teamRepository.findByRepositoryAndUserId(repository, userId)
                .orElseThrow(() -> new BusinessException(DocStoryResponseCode.FORBIDDEN));

        return repository;
    }
}
