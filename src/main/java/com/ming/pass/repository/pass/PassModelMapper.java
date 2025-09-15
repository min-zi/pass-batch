package com.ming.pass.repository.pass;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE) // ReportingPolicy.IGNORE: 일치하지 않는 필드는 무시
public interface PassModelMapper {
    PassModelMapper INSTANCE = Mappers.getMapper(PassModelMapper.class);

    // 필드명이 같지 않거나 custom 하게 매핑해주기 위해서는 @Mapping 을 추가해주면 됨
    @Mapping(target = "status", qualifiedByName = "status")
    @Mapping(target = "remainingCount", source = "bulkPassEntity.count")
    PassEntity toPassEntity(BulkPassEntity bulkPassEntity, String userId); // bulkPassEntity 의 같은 이름 필드(startedAt, endedAt 등)를 자동 매핑

    // 어떤 BulkPassStatus 값이 들어와도 그냥 무조건 PassStatus.READY 반환하도록 고정
    @Named("status")
    default PassStatus status(BulkPassStatus status) {
        return PassStatus.READY;
    }
}
