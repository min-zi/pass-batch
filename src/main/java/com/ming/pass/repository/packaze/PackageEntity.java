package com.ming.pass.repository.packaze;

import com.ming.pass.repository.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "package")
public class PackageEntity extends BaseEntity {
    @Id //PK 정의
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer packageSeq;

    private String packageName;
    private Integer count;
    private Integer period;
}
