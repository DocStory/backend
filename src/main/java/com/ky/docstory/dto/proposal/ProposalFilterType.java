package com.ky.docstory.dto.proposal;

import com.ky.docstory.entity.Proposal;
import java.util.List;

public enum ProposalFilterType {
    ALL,
    OPEN,
    CLOSED;

    public List<Proposal.Status> toStatuses() {
        return switch (this) {
            case ALL -> List.of();
            case OPEN -> List.of(Proposal.Status.OPEN);
            case CLOSED -> List.of(Proposal.Status.CLOSED, Proposal.Status.MERGED);
        };
    }
}
