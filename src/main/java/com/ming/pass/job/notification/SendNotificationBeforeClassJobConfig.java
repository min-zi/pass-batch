package com.ming.pass.job.notification;

import com.ming.pass.repository.booking.BookingEntity;
import com.ming.pass.repository.notification.NotificationEntity;
import com.ming.pass.repository.notification.NotificationEvent;
import com.ming.pass.repository.notification.NotificationModelMapper;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;

import java.util.Map;

@Configuration
public class SendNotificationBeforeClassJobConfig {
    private final int CHUNK_SIZE = 10;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final SendNotificationItemWriter sendNotificationItemWriter;

    public SendNotificationBeforeClassJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager, EntityManagerFactory entityManagerFactory, SendNotificationItemWriter sendNotificationItemWriter) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.entityManagerFactory = entityManagerFactory; // JPA EntityManager 를 만드는 팩토리, DB 접근에 사용
        this.sendNotificationItemWriter = sendNotificationItemWriter;
    }

    @Bean
    public Job sendNotificationBeforeClassJob() {
        return new JobBuilder("sendNotificationBeforeClassJob", jobRepository)
                .start(addNotificationStep())
                .next(sendNotificationStep())
                .build();
    }

    @Bean
    public Step addNotificationStep() {
        return new StepBuilder("addNotificationStep", jobRepository)
                .<BookingEntity, NotificationEntity>chunk(CHUNK_SIZE, transactionManager) // chunk 기반 Step 선언
                .reader(addNotificationItemReader())
                .processor(addNotificationItemProcessor())
                .writer(addNotificationItemWrite())
                .build();

    }

    /*
     * JpaPagingItemReader: JPA에서 사용하는 페이징 기법
     * 조회한 데이터에 대한 업데이트는 안할거라 cursor 를 사용할 필요가 없기 때문에 이번은 paging 기법을 사용
     * 쿼리 당 pageSize 만큼 가져오며 다른 PagingItemReader 와 마찬가지로 Thread-safe 함
     * */

    // DB에서 Booking(예약)을 꺼내오는 부분
    @Bean
    public JpaPagingItemReader<BookingEntity> addNotificationItemReader() {
        // 상태(status)가 준비중이며 시작일시(startedAt)가 10분 후 시작하는 예약이 알람 대상이 됨
        return new JpaPagingItemReaderBuilder<BookingEntity>()
                .name("addNotificationItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(CHUNK_SIZE)
                .queryString("select b from BookingEntity b join fetch b.userEntity where b.status = :status and b.startedAt <= :startedAt order by b.bookingSeq")
                .build();
    }

    // Booking → Notification 변환하고 알람 내용/타입을 만드는 로직
    @Bean
    public ItemProcessor<BookingEntity, NotificationEntity> addNotificationItemProcessor() {
        return bookingEntity -> NotificationModelMapper.INSTANCE.toNotificationEntity(bookingEntity, NotificationEvent.BEFORE_CLASS);
    }

    // NotificationEntity 를 DB 에 저장
    @Bean
    public JpaItemWriter<NotificationEntity> addNotificationItemWrite() {
        return new JpaItemWriterBuilder<NotificationEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    /* 여기까지 첫 번째 스텝 끝 */

    // DB 에서 아직 보내지 않은 예약 알람을 읽어와 멀티스레드로 알람을 보내는 Step
    @Bean
    public Step sendNotificationStep() {
        return new StepBuilder("sendNotificationStep", jobRepository)
                .<NotificationEntity, NotificationEntity>chunk(CHUNK_SIZE, transactionManager)
                .reader(sendNotificationItemReader())
                .writer(sendNotificationItemWriter)
                .taskExecutor(new SimpleAsyncTaskExecutor()) // 여러 스레드를 만들어 Step 을 병렬로 실행 가능
                .build();
    }

    @Bean
    public SynchronizedItemStreamReader<NotificationEntity> sendNotificationItemReader() {
        // Reader 는 안전하게 직렬로 읽어오고, Writer 는 멀티스레드로 돌려 병렬로 속도가 빨라짐
        // 이벤트(event)가 수업 전이며 발송 여부(sent)가 미발송인 알람이 조회 대상이 됨
        JpaCursorItemReader<NotificationEntity> itemReader = new JpaCursorItemReaderBuilder<NotificationEntity>()
                .name("sendNotificationItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select n from NotificationEntity n where n.event = :event and n.sent = :sent")
                .parameterValues(Map.of("event", NotificationEvent.BEFORE_CLASS, "sent", false))
                .build();

        // Reader가 읽어온 Chunk 단위의 NotificationEntity 리스트를 차례로 Writer 에 전달하기 위한 래퍼
        SynchronizedItemStreamReader<NotificationEntity> syncReader = new SynchronizedItemStreamReader<>();
        syncReader.setDelegate(itemReader);

        return syncReader;
    }

}