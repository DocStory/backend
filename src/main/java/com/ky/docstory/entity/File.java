package com.ky.docstory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@Table(name = "file")
public class File extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repository_id", nullable = false)
    private Repository repository;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_file_id")
    private File parentFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_file_id")
    private File rootFile;

    @Column(nullable = false)
    private String originFilename;

    @Column(nullable = false)
    private String saveFilename;

    @Column(nullable = false, unique = true)
    private String filepath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileType fileType;

    @Column(nullable = false)
    private int level;

    public enum FileType {
        HWP, DOCX, PDF, HWPX
    }

    public void calculateLevel() {
        int depth = 0;
        File parent = this.parentFile;
        while (parent != null) {
            depth++;
            parent = parent.getParentFile();
        }
        this.level = depth;
    }
}
