package kr.GenAi.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.GenAi.web.Entity.Community;

public interface CommunityRepository extends JpaRepository<Community, Integer> {

    /* 최신순 전체 조회 */
    List<Community> findAllByOrderByCreatedAtDesc();

    /* 제목 검색 + 최신순 */
    List<Community> findByTitleContainingOrderByCreatedAtDesc(String keyword);
}