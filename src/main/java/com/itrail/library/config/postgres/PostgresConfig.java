package com.itrail.library.config.postgres;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.itrail.library.repository") 
public class PostgresConfig {
    
}
