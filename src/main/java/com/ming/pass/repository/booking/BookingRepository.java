package com.ming.pass.repository.booking;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<BookingEntity, Integer> {
    @Transactional // DB 의 상태를 변경하는 작업에 대한 트랜잭션을 관리하는 데 사용
    @Modifying // 데이터 변경이 일어나는 쿼리(INSERT, DELETE, UPDATE)를 실행할 때 사용
    @Query(value = "UPDATE BookingEntity b" +
            "          SET b.usedPass = :usedPass" +
            "              b.modifiedAt = CURRENT_TIMESTAMP" +
            "        WHERE b.passSeq = :passSeq")
    int updateUsedPass(Integer passSeq, boolean usedPass);
}
