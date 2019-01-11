package com.majian.changedatacapture.configuration;

import java.util.List;
import lombok.Data;

@Data
public class ProcessorConfiguration {
    private String name;
    private ElasticProperties elasticProperties;
    private KafkaProperties kafkaProperties;
    private MysqlProperties mysqlProperties;
    private String docId;
    private String sqlTemplate;
    private List<Field> fields;

    public ProcessorConfiguration() {
    }

}
