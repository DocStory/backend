package com.ky.docstory.common.validator;

import com.ky.docstory.common.code.DocStoryResponseCode;
import com.ky.docstory.common.exception.BusinessException;
import com.ky.docstory.entity.Proposal;
import com.ky.docstory.entity.Team;
import com.ky.docstory.entity.User;
import com.ky.docstory.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProposalMergeValidator {

    private final TeamRepository teamRepository;

    public void validateMergePermission(Proposal proposal, User currentUser) {
        boolean isCreator = proposal.getCreatedBy().getId().equals(currentUser.getId());

        Team team = teamRepository.findByRepositoryAndUser(proposal.getHistory().getRepository(), currentUser)
                .orElseThrow(() -> new BusinessException(DocStoryResponseCode.FORBIDDEN));

        boolean hasMergeAuthority = (team.getRole() == Team.Role.ADMIN || team.getRole() == Team.Role.REVIEWER);

        if (!(isCreator || hasMergeAuthority)) {
            throw new BusinessException(DocStoryResponseCode.FORBIDDEN);
        }
    }

    public void validateMergeable(Proposal proposal) {
        if (proposal.getStatus() == Proposal.Status.MERGED) {
            throw new BusinessException(DocStoryResponseCode.ALREADY_MERGED_PROPOSAL);
        }
    }
}
