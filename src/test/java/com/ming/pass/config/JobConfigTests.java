package com.ming.pass.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class JobConfigTests {
    @Bean
    public Job expiredPassesJob(JobRepository jobRepository, Step expirePassesStep) {
        return new JobBuilder("expiredPassesJob", jobRepository)
                .start(expirePassesStep)
                .build();
    }
}
