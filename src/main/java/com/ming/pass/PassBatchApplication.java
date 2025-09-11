package com.ming.pass;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootApplication
public class PassBatchApplication {

    @Bean
    public Step passStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("passStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    System.out.println("Execute PassStep");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Job passJob(JobRepository jobRepository, Step passStep) {
        return new JobBuilder("passJob", jobRepository)
                .incrementer(new org.springframework.batch.core.launch.support.RunIdIncrementer())
                .start(passStep)
                .build();
    }

    // 직접 JobLauncher를 실행하는 ApplicationRunner 추가
    @Bean
    public ApplicationRunner runJob(Job passJob, JobLauncher jobLauncher) {
        return args -> {
            jobLauncher.run(passJob, new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis()) // 매번 유니크한 파라미터
                    .toJobParameters());
        };
    }

    public static void main(String[] args) {SpringApplication.run(PassBatchApplication.class, args);
	}

}
