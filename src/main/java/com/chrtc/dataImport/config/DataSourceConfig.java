package com.chrtc.dataImport.config;


import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
  /*  @Bean(name = "DataSourceOne")
    @ConfigurationProperties(prefix = "spring.datasource.one")
    DataSource DataSourceOne() {
        return DruidDataSourceBuilder.create().build();
    }
    @Bean(name = "DataSourceTwo")
    @ConfigurationProperties(prefix = "spring.datasource.two")
    DataSource DataSourceTwo() {
        return DruidDataSourceBuilder.create().build();
    }*/
}