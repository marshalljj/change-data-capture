package com.majian.changedatacapture.autoconfig;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean(name = "binlogDataSource")
public class BinlogDataSourceAutoConfiguration {

    @Value("${binlog.datasource.key}")
    public String configKey;

    @Value("${binlog.datasource.tag}")
    public String configTag;


    @Bean
    public DataSource binlogDataSource() {
        throw new RuntimeException("此处还为实现");
    }

}
