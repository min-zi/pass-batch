package com.ming.pass.job.pass;

import com.ming.pass.config.JobConfigTests;
import com.ming.pass.repository.pass.PassEntity;
import com.ming.pass.repository.pass.PassRepository;
import com.ming.pass.repository.pass.PassStatus;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBatchTest // 테스트 전용 컨텍스트에 JobLauncherTestUtils 빈이 자동 등록
@SpringBootTest
@Import(JobConfigTests.class) // 테스트 전용 Job을 사용
@ActiveProfiles("test")
public class ExpirePassesJobConfigTest {
    // end-to-end 테스트 사용
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @BeforeEach
    // launchJob() 호출 전에 JobLauncherTestUtils 에 expiredPassesJob 빈을 setJob() 으로 연결
    void setUp(@Qualifier("expiredPassesJob") Job expiredPassesJob) {
        jobLauncherTestUtils.setJob(expiredPassesJob);
    }

    @Autowired
    private PassRepository passRepository;

    @Test
    public void expirePassesStep() throws Exception {
        //given
        addPassEntities(10);

        //when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        JobInstance jobInstance = jobExecution.getJobInstance();

        //then
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
        assertEquals("expiredPassesJob", jobInstance.getJobName());

    }

    // pass 에 랜덤 값을 넣어주는 함수
    // progressed 로 들어간 pass 들을 expired 시키는 게 목표
    private void addPassEntities(int size) {
        final LocalDateTime now = LocalDateTime.now();
        final Random random = new Random();

        List<PassEntity> passEntities = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            PassEntity passEntity = new PassEntity();
            passEntity.setPackageSeq(1);
            passEntity.setUserId("A" + 1000000 + i);
            passEntity.setStatus(PassStatus.PROGRESSED);
            passEntity.setRemainingCount(random.nextInt(11));
            passEntity.setStartedAt(now.minusDays(60));
            passEntity.setEndedAt(now.minusDays(1));
            passEntities.add(passEntity);
        }
        passRepository.saveAll(passEntities);

    }
}
