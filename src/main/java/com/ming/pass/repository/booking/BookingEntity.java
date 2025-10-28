package com.ming.pass.repository.booking;

import com.ming.pass.repository.BaseEntity;
import com.ming.pass.repository.pass.PassEntity;
import com.ming.pass.repository.user.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "booking")
public class BookingEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 기본 키 생성을 DB에 위임 (AUTO_INCREMENT)
    private Integer bookingSeq;
    private Integer passSeq;
    private String userId;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    private boolean usedPass;
    private boolean attended;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime cancelledAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UserId", insertable = false, updatable = false)
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "passSeq", insertable = false, updatable = false)
    private PassEntity passEntity;

    // 날짜별로 예약이 몇 건 있었는지 통계를 원함, 같은 날짜의 예약들 시간이 다 제 각각이면 Map 에서 다른 key 로 인식함
    // endedAt 기준, 통계를 날짜 단위로 묶기위해 시/분/초를 00:00:00 으로 초기화함
    public LocalDateTime getStatisticsAt() {
        return this.endedAt.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }

}
