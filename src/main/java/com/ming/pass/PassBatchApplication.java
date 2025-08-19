package com.ming.pass;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.PlatformTransactionManager;

import java.awt.desktop.SystemEventListener;

@SpringBootApplication
public class PassBatchApplication {

    @Bean
    public Step passStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("passStep", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("Execute PassStep");
                        return RepeatStatus.FINISHED;
                    }
                }, transactionManager)
                .build();
    }

    @Bean
    public Job passJob(JobRepository jobRepository, Step passStep) {
        return new JobBuilder("passJob", jobRepository)
                .start(passStep)
                .build();
    }

	public static void main(String[] args) {
		SpringApplication.run(PassBatchApplication.class, args);
	}

}
