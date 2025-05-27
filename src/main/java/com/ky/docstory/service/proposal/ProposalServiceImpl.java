package com.ky.docstory.service.proposal;

import com.ky.docstory.common.code.DocStoryResponseCode;
import com.ky.docstory.common.exception.BusinessException;
import com.ky.docstory.common.validator.ProposalMergeValidator;
import com.ky.docstory.dto.proposal.*;
import com.ky.docstory.entity.*;
import com.ky.docstory.repository.HistoryRepository;
import com.ky.docstory.repository.ProposalRepository;
import com.ky.docstory.repository.RepositoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProposalServiceImpl implements ProposalService {

    private final ProposalRepository proposalRepository;
    private final HistoryRepository historyRepository;
    private final RepositoryRepository repositoryRepository;
    private final ProposalMergeValidator validator;


    @Override
    @Transactional
    public ProposalResponse createProposal(ProposalCreateRequest request, User currentUser) {
        History history = historyRepository.findById(request.historyId())
                .orElseThrow(() -> new BusinessException(DocStoryResponseCode.NOT_FOUND));

        Proposal proposal = Proposal.builder()
                .history(history)
                .title(request.title())
                .description(request.description())
                .status(Proposal.Status.OPEN)
                .createdBy(currentUser)
                .build();

        proposalRepository.save(proposal);
        return ProposalResponse.from(proposal);
    }

    @Override
    @Transactional(readOnly = true)
    public ProposalResponse getProposalById(UUID proposalId, User currentUser) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new BusinessException(DocStoryResponseCode.NOT_FOUND));

        return ProposalResponse.from(proposal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProposalResponse> getProposalsByRepository(UUID repositoryId, ProposalFilterType filterType, User currentUser) {
        if (!repositoryRepository.existsById(repositoryId)) {
            throw new BusinessException(DocStoryResponseCode.NOT_FOUND);
        }

        List<Proposal.Status> statuses = filterType.toStatuses();
        List<Proposal> proposals = proposalRepository.findAllByRepositoryIdAndStatuses(repositoryId, statuses.isEmpty() ? null : statuses);

        return proposals.stream()
                .map(ProposalResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public ProposalResponse updateProposal(UUID proposalId, ProposalUpdateRequest request, User currentUser) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new BusinessException(DocStoryResponseCode.NOT_FOUND));

        if (!proposal.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new BusinessException(DocStoryResponseCode.FORBIDDEN);
        }

        if (proposal.getStatus() == Proposal.Status.MERGED) {
            throw new BusinessException(DocStoryResponseCode.ALREADY_MERGED_PROPOSAL);
        }

        proposal.updateContent(request.title(), request.description());

        return ProposalResponse.from(proposal);
    }

    @Override
    @Transactional
    public ProposalResponse updateProposalStatus(UUID proposalId, ProposalStatusUpdateRequest request, User currentUser) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new BusinessException(DocStoryResponseCode.NOT_FOUND));

        if (!proposal.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new BusinessException(DocStoryResponseCode.FORBIDDEN);
        }

        proposal.changeStatus(request.status());

        return ProposalResponse.from(proposal);
    }

    @Override
    @Transactional
    public ProposalResponse mergeProposal(UUID proposalId, User currentUser) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new BusinessException(DocStoryResponseCode.NOT_FOUND));

        validator.validateMergePermission(proposal, currentUser);
        validator.validateMergeable(proposal);

        Set<UUID> mainHistoryIds = collectMainChainHistories(proposal.getHistory());

        updateHistoriesStatus(proposal.getHistory().getRepository(), proposal.getHistory().getFile().getRootFile(), mainHistoryIds);

        proposal.merge();

        return ProposalResponse.from(proposal);
    }

    private Set<UUID> collectMainChainHistories(History targetHistory) {
        Set<UUID> mainHistoryIds = new HashSet<>();
        File currentFile = targetHistory.getFile();

        while (currentFile != null) {
            Optional<History> connectedHistory = historyRepository.findByFile(currentFile);
            connectedHistory.ifPresent(history -> mainHistoryIds.add(history.getId()));
            currentFile = currentFile.getParentFile();
        }
        return mainHistoryIds;
    }

    private void updateHistoriesStatus(Repository repository, File rootFile, Set<UUID> mainHistoryIds) {
        List<History> histories = historyRepository.findAllByRepositoryId(repository.getId());

        for (History history : histories) {
            File file = history.getFile();
            if (file == null || file.getRootFile() == null) continue;
            if (!file.getRootFile().getId().equals(rootFile.getId())) continue;

            if (mainHistoryIds.contains(history.getId())) {
                history.changeStatus(History.HistoryStatus.MAIN);
            } else if (history.getHistoryStatus() == null) {
                history.changeStatus(History.HistoryStatus.NORMAL);
            } else {
                history.changeStatus(History.HistoryStatus.ABANDONED);
            }
        }
    }

}
