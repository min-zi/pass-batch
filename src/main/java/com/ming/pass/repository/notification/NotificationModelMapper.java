package com.ming.pass.repository.notification;

import com.ming.pass.repository.booking.BookingEntity;
import com.ming.pass.util.LocalDateTimeUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

// ReportingPolicy.IGNORE: 매핑되지 않은 필드는 무시
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationModelMapper {
    NotificationModelMapper INSTANCE = Mappers.getMapper(NotificationModelMapper.class);

    // 필드명이 같지 않거나 custom 하게 매핑해주기 위해서는 @Mapping 을 추가해주면 됨
    @Mapping(target = "uuid", source = "bookingEntity.userEntity.uuid")
    @Mapping(target = "text", source = "bookingEntity.startedAt", qualifiedByName = "text")
    NotificationEntity toNotificationEntity(BookingEntity bookingEntity, NotificationEvent event);

    // 알람 보낼 메세지 작성 추가
    @Named("text")
    default String text(LocalDateTime startedAt) {
        return String.format("안녕하세요, %s 수업 시작합니다. 수업 전 출석 체크 부탁드립니다 😊", LocalDateTimeUtils.format(startedAt));
    }
}
