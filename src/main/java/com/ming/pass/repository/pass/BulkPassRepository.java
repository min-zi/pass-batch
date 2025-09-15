package com.ming.pass.repository.pass;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BulkPassRepository extends JpaRepository<BulkPassEntity, Integer> {
    // status 값이 파라미터로 들어간 :status 값과 같고 startedAt 이 파라미터로 들어간 :startedAt 보다 크면 대상이 됨
    // WHERE status = :status AND startedAt > :startedAt
    List<BulkPassEntity> findByStatusAndStartedAtGreaterThan(BulkPassStatus status, LocalDateTime startedAt);
}
