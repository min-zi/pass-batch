package com.ming.pass.job.notification;

import com.ming.pass.adapter.message.KakaoTalkMessageAdapter;
import com.ming.pass.repository.notification.NotificationEntity;
import com.ming.pass.repository.notification.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class SendNotificationItemWriter implements ItemWriter<NotificationEntity> {
    private final NotificationRepository notificationRepository;
    private final KakaoTalkMessageAdapter kakaoTalkMessageAdapter;

    public SendNotificationItemWriter(NotificationRepository notificationRepository, KakaoTalkMessageAdapter kakaoTalkMessageAdapter) {
        this.notificationRepository = notificationRepository;
        this.kakaoTalkMessageAdapter = kakaoTalkMessageAdapter;
    }

    @Override
    public void write(Chunk<? extends NotificationEntity> notificationEntities) throws Exception {
        int count = 0; // 성공적으로 알람을 보낸 개수를 카운트

        for (NotificationEntity notificationEntity: notificationEntities) {
            boolean successful = kakaoTalkMessageAdapter.sendKakaoTalkMessage(notificationEntity.getUuid(), notificationEntity.getText());

            if (successful) {
                notificationEntity.setSent(true); // 전송 후 sent = true 로 업데이트
                notificationEntity.setSentAt(LocalDateTime.now());
                notificationRepository.save(notificationEntity);
                count ++;
            }

            // Chunk 가 종료 되면 transactionManager.commit() 이 이루어지고 DB 에 반영, 중간 실패 시 rollback 가능
        }
        log.info("SendNotificationItemWriter - write: 수업 전 알람 {}/{}건 전송 성공", count, notificationEntities.size());
    }

}
