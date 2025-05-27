package com.ky.docstory.entity;

import com.ky.docstory.common.code.DocStoryResponseCode;
import com.ky.docstory.common.exception.BusinessException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@Table(name = "proposal")
public class Proposal extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "history_id", nullable = false)
    private History history;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    public enum Status {
        OPEN,
        MERGED,
        CLOSED
    }

    public void updateContent(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void changeStatus(Status newStatus) {
        if (this.status == Status.MERGED) {
            throw new BusinessException(DocStoryResponseCode.ALREADY_MERGED_PROPOSAL);
        }
        if (this.status == Status.CLOSED && newStatus != Status.OPEN) {
            throw new BusinessException(DocStoryResponseCode.RESOURCE_CONFLICT);
        }
        if (this.status == Status.OPEN &&
                !(newStatus == Status.CLOSED || newStatus == Status.MERGED)) {
            throw new BusinessException(DocStoryResponseCode.RESOURCE_CONFLICT);
        }
        this.status = newStatus;
    }

    public void merge() {
        if (this.status == Status.MERGED) {
            throw new BusinessException(DocStoryResponseCode.ALREADY_MERGED_PROPOSAL);
        }
        this.status = Status.MERGED;
    }

}
