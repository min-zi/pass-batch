package com.ming.pass.repository.statistics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsRepository extends JpaRepository<StatisticsEntity, Integer> {
    // 실행 순서는 GROUP BY → SUM() 합계 계산 → SELECT new 생성자 호출
    // 2. 그 날의 합계를 계산
    // 3. DB 조회 결과를 AggregatedStatistics 클래스의 생성자를 이용해서 직접 자바 객체로 매핑
    @Query(value = "SELECT new com.ming.pass.repository.statistics.AggregatedStatistics(s.statisticsAt, SUM(s.allCount), SUM(s.attendedCount), SUM(s.cancelledCount))" +
            "           FROM StatisticsEntity s " +
            "           WHERE s.statisticsAt BETWEEN :from AND :to " +
            "           GROUP BY s.statisticsAt" ) // 1. 통계 날짜 기준으로 하루 단위로 데이터를 묶어서
    List<AggregatedStatistics> findByStatisticsAtBetweenAndGroupBy(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);
}
