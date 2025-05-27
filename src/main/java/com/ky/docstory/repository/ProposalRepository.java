package com.ky.docstory.repository;

import com.ky.docstory.entity.Proposal;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProposalRepository extends JpaRepository<Proposal, UUID> {

    @Query("""
        SELECT p FROM Proposal p
        WHERE p.history.repository.id = :repositoryId
        AND (:statuses IS NULL OR p.status IN :statuses)
        """)
    List<Proposal> findAllByRepositoryIdAndStatuses(@Param("repositoryId") UUID repositoryId,
                                                    @Param("statuses") List<Proposal.Status> statuses);
}
