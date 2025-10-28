package com.ming.pass.repository.statistics;

import com.ming.pass.repository.booking.BookingEntity;
import com.ming.pass.repository.booking.BookingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "statistics")
public class StatisticsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer statisticsSeq;
    private LocalDateTime statisticsAt; // 일 단위

    private int allCount;
    private int attendedCount;
    private int cancelledCount;

    public static StatisticsEntity create(final BookingEntity bookingEntity) {
        StatisticsEntity statisticsEntity = new StatisticsEntity();
        statisticsEntity.setStatisticsAt(bookingEntity.getStartedAt()); // 그날 시작된 수업 기준으로 통계를 만들기 위해 startedAt 으로 설정
        statisticsEntity.setAllCount(1); // 지금 들어온 예약이 첫 번째니까 1로 시작

        // 예약이 출석이면 attended Count 1로 초기화
        if (bookingEntity.isAttended()) {
            statisticsEntity.setAttendedCount(1);
        }

        // 취소했으면 cancelled Count 를 1로 초기화
        if (BookingStatus.CANCELLED.equals(bookingEntity.getStatus())) {
            statisticsEntity.setCancelledCount(1);
        }
        return statisticsEntity;

    }

    public void add(final BookingEntity bookingEntity) {
        this.allCount++;

        if (bookingEntity.isAttended()) {
            this.attendedCount++;
        }

        if (BookingStatus.CANCELLED.equals(bookingEntity.getStatus())) {
            this.cancelledCount++;
        }
    }

}
