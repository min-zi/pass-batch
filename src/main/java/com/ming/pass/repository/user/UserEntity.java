package com.ming.pass.repository.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    @Column(columnDefinition = "json") //DB 컬럼 타입을 json으로 정의
    @JdbcTypeCode(SqlTypes.JSON) //DB에 json 형태로 저장된 컬럼 ↔ 자바 객체(Map/List)로 자동 매핑
    private Map<String, Object> meta;

    public String getUuid() {
        String uuid = null;
        if (meta.containsKey("uuid")) {  //meta Map 안에 "uuid"라는 key가 있는지 확인
            uuid = String.valueOf(meta.get("uuid"));
        }
        return uuid;
    }
}
