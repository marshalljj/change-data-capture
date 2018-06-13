package com.majian.changedatacapture.autoconfig;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sun.tools.java.ClassType;

@Configuration
@ConditionalOnMissingBean(name = "binlogDataSource")
public class BinlogDataSourceAutoConfiguration {

    @Value("${binlog.datasource.url}")
    public String url;
    @Value("${binlog.datasource.username}")
    public String userName;
    @Value("${binlog.datasource.password}")
    public String password;
    @Value("${binlog.datasource.driver}")
    public String driver;


    @Bean
    public DataSource binlogDataSource() throws ClassNotFoundException {
        Class type = Class.forName("com.zaxxer.hikari.HikariDataSource");
        return DataSourceBuilder.create()
            .type(type)
            .url(url)
            .driverClassName(driver)
            .username(userName).password(password)
            .build();
    }

}
