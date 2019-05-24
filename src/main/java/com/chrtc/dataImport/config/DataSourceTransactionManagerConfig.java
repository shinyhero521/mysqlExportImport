package com.chrtc.dataImport.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DataSourceTransactionManagerConfig {

    /**
     * 装配事务管理器
     * @return
     */
  /*  @Bean(name = "transactionOne")
    public DataSourceTransactionManager transactionOne(@Qualifier("DataSourceOne") DataSource DataSourceOne) {
        return new DataSourceTransactionManager(DataSourceOne);
    }*/
    /**
     * 装配事务管理器
     * @return
     */
   /* @Bean(name = "transactionTwo")
    public DataSourceTransactionManager transactionTwo(@Qualifier("DataSourceTwo") DataSource DataSourceTwo) {
        return new DataSourceTransactionManager(DataSourceTwo);
    }*/

}
