package kr.GenAi.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import kr.GenAi.web.Entity.PointLog;
import java.util.List;

public interface PointLogRepository extends JpaRepository<PointLog, Integer> {

    // 1. 오늘 적립한 포인트 합계 (Native Query)
    @Query(value = "SELECT COALESCE(SUM(p.rec_point), 0) FROM tb_point_log p " +
                   "JOIN tb_user u ON p.user_code = u.user_code " +
                   "WHERE u.id = :userId AND DATE(p.created_at) = CURDATE() AND p.rec_point > 0", nativeQuery = true)
    int sumTodayPoint(@Param("userId") String userId);

    // 2. 이번 달 적립한 포인트 합계 (Native Query)
    @Query(value = "SELECT COALESCE(SUM(p.rec_point), 0) FROM tb_point_log p " +
                   "JOIN tb_user u ON p.user_code = u.user_code " +
                   "WHERE u.id = :userId AND MONTH(p.created_at) = MONTH(CURDATE()) " +
                   "AND YEAR(p.created_at) = YEAR(CURDATE()) AND p.rec_point > 0", nativeQuery = true)
    int sumMonthPoint(@Param("userId") String userId);

    // 3. 최근 내역 최신순 5개 (JPA Method)
    List<PointLog> findTop5ByUser_IdOrderByCreatedAtDesc(String userId);

    // 4. 전체 내역 최신순 (JPA Method)
    List<PointLog> findAllByUser_IdOrderByCreatedAtDesc(String userId);

    // 5. 오늘 퀴즈 참여 여부 확인 (log_type 컬럼 사용)
    @Query(value = "SELECT COUNT(*) FROM tb_point_log p " +
                   "JOIN tb_user u ON p.user_code = u.user_code " +
                   "WHERE u.id = :userId AND DATE(p.created_at) = CURDATE() AND p.log_type = 'QUIZ'", nativeQuery = true)
    int countTodayQuiz(@Param("userId") String userId);
}