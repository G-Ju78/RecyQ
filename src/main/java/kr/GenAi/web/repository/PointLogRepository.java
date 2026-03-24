package kr.GenAi.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.GenAi.web.Entity.PointLog;

public interface PointLogRepository extends JpaRepository<PointLog, Integer> {

}
