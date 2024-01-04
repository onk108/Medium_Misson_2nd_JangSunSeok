package com.ll.medium.domain.post.post.repository;

import com.ll.medium.domain.post.post.entity.payPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface payPostRepository extends JpaRepository<payPost, Long> {
    List<payPost> findTop30ByIsPublishedOrderByIdDesc(boolean isPublished);

    Page<payPost> findByTitleContainingIgnoreCaseOrBodyContainingIgnoreCase(String kw, String kw_, Pageable pageable);
}
