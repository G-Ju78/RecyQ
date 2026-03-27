package kr.GenAi.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import kr.GenAi.web.Entity.PointLog;
import java.util.List;

public interface PointLogRepository extends JpaRepository<PointLog, Integer> {

    // 🌟 1. 오늘 적립한 포인트 합계 (tb_point_log 로 완벽 수정)
    @Query(value = "SELECT COALESCE(SUM(p.rec_point), 0) FROM tb_point_log p JOIN tb_user u ON p.user_code = u.user_code WHERE u.id = :userId AND DATE(p.created_at) = CURDATE() AND p.rec_point > 0", nativeQuery = true)
    int sumTodayPoint(@Param("userId") String userId);

    // 🌟 2. 이번달 적립한 포인트 합계 (tb_point_log 로 완벽 수정)
    @Query(value = "SELECT COALESCE(SUM(p.rec_point), 0) FROM tb_point_log p JOIN tb_user u ON p.user_code = u.user_code WHERE u.id = :userId AND MONTH(p.created_at) = MONTH(CURDATE()) AND YEAR(p.created_at) = YEAR(CURDATE()) AND p.rec_point > 0", nativeQuery = true)
    int sumMonthPoint(@Param("userId") String userId);

    // 3. 최근 내역 최신순으로 5개 가져오기 (마이페이지용 - JPA가 알아서 테이블명 찾아감)
    List<PointLog> findTop5ByUser_IdOrderByCreatedAtDesc(String userId);

    // 4. 전체 내역 최신순으로 모두 가져오기 (포인트리스트용 - JPA가 알아서 테이블명 찾아감)
    List<PointLog> findAllByUser_IdOrderByCreatedAtDesc(String userId);
}