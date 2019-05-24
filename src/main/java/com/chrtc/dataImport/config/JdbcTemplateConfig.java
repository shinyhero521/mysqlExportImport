package com.chrtc.dataImport.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class JdbcTemplateConfig {

   /* @Bean(name = "jdbcTemplateOne")
    JdbcTemplate jdbcTemplateOne(@Qualifier("DataSourceOne") DataSource DataSourceOne) {
        return new JdbcTemplate(DataSourceOne);
    }

    @Bean(name = "jdbcTemplateTwo")
    JdbcTemplate jdbcTemplateTwo(@Qualifier("DataSourceTwo") DataSource DataSourceTwo) {
        return new JdbcTemplate(DataSourceTwo);
    }*/

}