package com.majian.changedatacapture.autoconfig;

import com.majian.changedatacapture.configuration.ProcessorFactory;
import com.majian.changedatacapture.configuration.ProcessorFactoryBuilder;
import com.majian.changedatacapture.core.converters.JsonToMapConverter;
import com.majian.changedatacapture.core.converters.JsonToStringArrayConverter;
import com.majian.changedatacapture.core.stream.BinlogProcessor;
import com.majian.changedatacapture.core.task.TaskProcessor;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    ElasticClientConfiguration.class,
    BinlogKafkaConfiguration.class,
    BinlogDataSourceAutoConfiguration.class})
public class BinlogAutoConfiguration {


    @Value("${binlog.elasticsearch.index}")
    private String index;
    @Value("${binlog.factory.config-file}")
    private String configFile;
    @Value("${binlog.stream.record.es-type:stream_flow}")
    private String recordType;
    @Value("${binlog.stream.record.enabled:true}")
    private boolean recordEnabled;

    @Resource(name = "binlogElasticClient")
    private Client client;
    @Resource(name = "binlogDataSource")
    private DataSource dataSource;

    @Bean
    public ProcessorFactory processorFactory() {
        return new ProcessorFactoryBuilder()
            .withConfigLocation(configFile)
            .withElasticsearch(client, index)
            .withDataSource(dataSource)
            .withPerformanceRecorder(recordEnabled, recordType)
            .registerConverters(new JsonToStringArrayConverter(), new JsonToMapConverter())
            .build();
    }

    /**
     * 增量部分
     */
    @Bean
    BinlogProcessor binlogProcessor(ProcessorFactory processorFactory) {
        return processorFactory.getBinlogProcessor();
    }

    /**
     * 定时任务补偿
     */
    @Bean
    List<TaskProcessor> taskProcessors(ProcessorFactory processorFactory) {
        return processorFactory.getTaskProcessors();
    }

    @Bean
    public BinlogListener binlogListener(BinlogProcessor binlogProcessor) {
        return new BinlogListener(binlogProcessor);
    }
}
