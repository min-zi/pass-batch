package com.ming.pass.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "com.ming.pass.repository")
@EntityScan(basePackages = "com.ming.pass.repository")
@Configuration
public class JpaConfig {
}
