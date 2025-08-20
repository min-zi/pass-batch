package com.ming.pass.repository.booking;

import com.ming.pass.repository.BaseEntity;
import com.ming.pass.repository.pass.PassEntity;
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
    @JoinColumn(name = "passSeq", insertable = false, updatable = false)
    private PassEntity passEntity;

    //endedAt 기준, yyyy-MM-HH 00:00:00
    public LocalDateTime getStatisticsAt() {
        return this.endedAt.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }
}
