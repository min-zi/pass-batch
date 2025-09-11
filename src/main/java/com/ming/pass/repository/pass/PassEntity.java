package com.ming.pass.repository.pass;

import com.ming.pass.repository.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "pass")
public class PassEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int passSeq;
    private int packageSeq;
    private String userId;

    @Enumerated(EnumType.STRING)
    private PassStatus status; //만료 되었는지 아닌지
    private Integer remainingCount; //패키지 테이블의 count 값에서 하나씩 차감

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime expiredAt;
}