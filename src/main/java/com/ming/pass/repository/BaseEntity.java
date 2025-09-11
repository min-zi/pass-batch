package com.ming.pass.repository;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass //엔티티가 이 baseEntity 를 상속하면 createdAt, modifiedAt 을 칼럼으로 인식하게 됨
@EntityListeners(AuditingEntityListener.class)
//JPA 엔티티의 이벤트가 발생할 때 콜백 처리를 하고 코드 실행 (인자로는 커스텀 콜백을 요청할 클래스 지정)
//Auditing 사용 할 땐 JPA에서 제공하는 AuditingEntityListener 를 인자로 넘기면 됨
//touchcreate, touchUpdate 메소드가 엔티티의 생성, 수정이 일어나면 콜백이 실행돼 시간을 만들어 주게 됨
public abstract class BaseEntity {
    @CreatedDate //생성된 엔티티 자동 저장
    @Column(updatable = false, nullable = false) //업데이트하지 않도록
    private LocalDateTime createdAt;
    @LastModifiedDate //변경할 때 업데이트
    private LocalDateTime modifiedAt;
}
