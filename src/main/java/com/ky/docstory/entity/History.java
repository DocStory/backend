package com.ky.docstory.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@Table(name = "history")
public class History extends BaseEntity {

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "file_id", nullable = false, unique = true)
    private File file;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HistoryStatus historyStatus;

    public enum HistoryStatus {
        NORMAL, MAIN, ABANDONED
    }

    public void changeStatus(HistoryStatus newStatus) {
        this.historyStatus = newStatus;
    }

}
