package com.home.step;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

@Configuration
public class ReaderConfig {
    @Bean
    public String sql(){
        return "select stepName from batch_step_execution";
    }

    @Bean
    public RowMapper mapper(){
        return (resultSet, i) -> resultSet.getString("step_name");
    }
}
