package com.ky.docstory.repository;

import com.ky.docstory.entity.Proposal;
import com.ky.docstory.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByProposal(Proposal proposal);
} 