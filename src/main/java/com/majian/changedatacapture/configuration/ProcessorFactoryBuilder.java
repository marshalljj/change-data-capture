package com.majian.changedatacapture.configuration;

import com.majian.changedatacapture.core.Converter;
import com.majian.changedatacapture.core.ConverterRegistry;
import com.majian.changedatacapture.core.DefaultRowMapper;
import com.majian.changedatacapture.core.Refresher;
import com.majian.changedatacapture.core.RowMapper;
import com.majian.changedatacapture.core.SqlRowMapper;
import com.majian.changedatacapture.core.repository.ElasticRepository;
import com.majian.changedatacapture.core.repository.MysqlRepository;
import com.majian.changedatacapture.core.stream.PerformanceRecorder;
import com.majian.changedatacapture.core.stream.StreamProcessor;
import com.majian.changedatacapture.core.task.TaskProcessor;
import com.thoughtworks.xstream.XStream;
import java.io.InputStream;
import javax.sql.DataSource;
import org.elasticsearch.client.Client;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class ProcessorFactoryBuilder {

    private ProcessorFactoryConfiguration factoryConfiguration;
    private ElasticRepository elasticRepository;
    private MysqlRepository mysqlRepository;
    private PerformanceRecorder performanceRecorder;

    public ProcessorFactoryBuilder withConfigLocation(String location) {
        XStream xStream = new XStream();
        xStream.alias("processors", ProcessorFactoryConfiguration.class);
        xStream.addImplicitCollection(ProcessorFactoryConfiguration.class, "processors");
        xStream.alias("processor", ProcessorConfiguration.class);
        xStream.alias("es", ElasticConfiguration.class);
        xStream.alias("field", Field.class);
        xStream.alias("node", Node.class);
        xStream.aliasField("es", ProcessorConfiguration.class, "elasticConfiguration");
        xStream.useAttributeFor(ProcessorConfiguration.class, "name");
        xStream.addImplicitCollection(Node.class, "children");
        xStream.useAttributeFor(Field.class, "name");
        xStream.useAttributeFor(Field.class, "converter");
        xStream.aliasAttribute(ProcessorConfiguration.class, "changeSource", "change-source");
        xStream.aliasField("update-time-field", ChangeSource.class, "updateTimeField");
        xStream.aliasField("primary-key", ChangeSource.class, "primaryKey");
        xStream.aliasField("mapper", ProcessorConfiguration.class, "mapper");
        xStream.aliasField("root-key", ProcessorConfiguration.class, "rootKey");
        InputStream resourceAsStream = ProcessorFactoryBuilder.class.getClassLoader().getResourceAsStream(location);
        factoryConfiguration = (ProcessorFactoryConfiguration) xStream.fromXML(resourceAsStream);
        factoryConfiguration.checkValid();
        return this;
    }

    public ProcessorFactoryBuilder withElasticsearch(Client client, String index) {
        this.elasticRepository = new ElasticRepository(client, index);
        return this;
    }

    public ProcessorFactoryBuilder withDataSource(DataSource dataSource) {
        mysqlRepository = new MysqlRepository(new NamedParameterJdbcTemplate(dataSource));
        return this;
    }

    public ProcessorFactoryBuilder withPerformanceRecorder(boolean recordEnabled, String recordType) {
        this.performanceRecorder = new PerformanceRecorder(elasticRepository, recordEnabled, recordType);
        return this;
    }

    public ProcessorFactoryBuilder registerConverters(Converter... converters) {
        for (Converter converter : converters) {
            ConverterRegistry.getInstance().register(converter);
        }
        return this;
    }

    public ProcessorFactory build() {
        ProcessorFactory processorFactory = new ProcessorFactory();
        for (ProcessorConfiguration processorConfiguration : factoryConfiguration.getProcessors()) {
            ChangeSource changeSource = processorConfiguration.getChangeSource();
            String processorName = processorConfiguration.getName();
            RowMapper rowMapper = getRowMapper(processorConfiguration.getMapper());
            Refresher refresher = new Refresher(mysqlRepository, elasticRepository, processorConfiguration, rowMapper);
            StreamProcessor streamProcessor = new StreamProcessor(refresher, changeSource, processorName,
                performanceRecorder);

            TaskProcessor taskProcessor = new TaskProcessor(refresher, processorName, changeSource);

            processorFactory.addStreamProcessor(streamProcessor);
            processorFactory.addTaskProcessor(taskProcessor);

        }
        return processorFactory;
    }

    private RowMapper getRowMapper(String mapper) {
        if (mapper == null) {
            return new DefaultRowMapper();
        } else {
            return new SqlRowMapper(mapper, mysqlRepository);
        }

    }

}
