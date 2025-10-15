package com.ming.pass.repository.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import com.vladmihalcea.hibernate.type.json.JsonType;

import java.util.Map;

@Getter
@Setter
@ToString
@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    private String userId;

    private String userName;
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    private String phone;

    @Type(JsonType.class) // DB 에 json 형태로 저장된 컬럼 ↔ 자바 객체(Map/List)로 자동 매핑
    @Column(columnDefinition = "json") // Hibernate 가 테이블을 생성할 때 해당 컬럼을 JSON 타입으로 생성
    private Map<String, Object> meta;

    public String getUuid() {
        String uuid = null;
        if (meta.containsKey("uuid")) {  // meta Map 안에 "uuid" 라는 key 가 있는지 확인
            uuid = String.valueOf(meta.get("uuid"));
        }
        return uuid;
    }
}
