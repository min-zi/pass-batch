package com.ming.pass.job.pass;

import com.ming.pass.repository.pass.PassEntity;
import com.ming.pass.repository.pass.PassStatus;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.Map;

// 진행 중이지만 이미 종료된 이용권들을 찾아서 만료 상태로 업데이트하는 배치 잡 클래스
@Configuration
@RequiredArgsConstructor
public class ExpirePassesJobConfig {
    private final int CHUNK_SIZE = 5;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job expirePassesJob() {
        return new JobBuilder("expirePassesJob", jobRepository)
                .start(expirePassesStep())
                .build();
    }

    @Bean
    public Step expirePassesStep() {
        return new StepBuilder("expirePassesStep", jobRepository)
                .<PassEntity, PassEntity>chunk(CHUNK_SIZE, transactionManager)

                // Chunk-Oriented Processing 패턴
                .reader(expirePassesItemReader())
                .processor(expirePassesItemProcessor())
                .writer(expirePassesItemWriter())
                .build();
    }

    // cursor item reader 를 사용: status 값이 progressed 인 값만 읽어와서 expired 변경 할 때 누락 없이 데이터 변경에 무관한 무결성 조회가 가능
    @Bean
    @StepScope
    public JpaCursorItemReader<PassEntity> expirePassesItemReader() {
        return new JpaCursorItemReaderBuilder<PassEntity>()
                .name("expirePassesItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT p FROM PassEntity p WHERE p.status = :status AND p.endedAt <= :endedAt")
                .parameterValues(Map.of(
                        "status", PassStatus.PROGRESSED,
                        "endedAt", LocalDateTime.now()
                ))
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<PassEntity, PassEntity> expirePassesItemProcessor() {
        return passEntity -> {
            passEntity.setStatus(PassStatus.EXPIRED);
            passEntity.setExpiredAt(LocalDateTime.now());
            return passEntity;
        };
    }

    @Bean
    public JpaItemWriter<PassEntity> expirePassesItemWriter() {
        return new JpaItemWriterBuilder<PassEntity>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

}


