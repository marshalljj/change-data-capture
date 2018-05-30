package com.majian.changedatacapture.configuration;

import com.majian.changedatacapture.core.Converter;
import com.majian.changedatacapture.core.ConverterRegistry;
import com.majian.changedatacapture.core.Refresher;
import com.majian.changedatacapture.core.repository.ElasticRepository;
import com.majian.changedatacapture.core.repository.MysqlRepository;
import com.majian.changedatacapture.core.stream.StreamProcessor;
import com.majian.changedatacapture.core.task.ExpiredJudge;
import com.majian.changedatacapture.core.task.Filter;
import com.majian.changedatacapture.core.task.TaskProcessor;
import com.thoughtworks.xstream.XStream;
import java.io.InputStream;
import javax.sql.DataSource;
import org.elasticsearch.client.Client;
import org.springframework.jdbc.core.JdbcTemplate;

public class ProcessorFactoryBuilder {

    private ProcessorFactoryConfiguration factoryConfiguration;
    private ElasticRepository elasticRepository;
    private MysqlRepository mysqlRepository;

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
        xStream.aliasField("root-key", ChangeSource.class, "rootKey");
        xStream.aliasField("primary-key", ChangeSource.class, "primaryKey");

        InputStream resourceAsStream = ProcessorFactoryBuilder.class.getClassLoader().getResourceAsStream(location);
        factoryConfiguration = (ProcessorFactoryConfiguration) xStream.fromXML(resourceAsStream);
        return this;
    }

    public ProcessorFactoryBuilder withElasticsearch(Client client, String index) {
        this.elasticRepository = new ElasticRepository(client, index);
        return this;
    }

    public ProcessorFactoryBuilder withDataSource(DataSource dataSource) {
        mysqlRepository = new MysqlRepository(new JdbcTemplate(dataSource));
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
            ElasticConfiguration elasticConfiguration = processorConfiguration.getElasticConfiguration();
            ChangeSource changeSource = processorConfiguration.getChangeSource();
            Node root = processorConfiguration.getNode();
            String processorName = processorConfiguration.getName();

            Refresher refresher = new Refresher(mysqlRepository, elasticRepository, root, elasticConfiguration);
            StreamProcessor streamProcessor = new StreamProcessor(refresher, changeSource, processorName);

            ExpiredJudge expiredJudge = new ExpiredJudge(mysqlRepository, elasticRepository,changeSource, elasticConfiguration);
            Filter filter = new Filter(mysqlRepository, changeSource);
            TaskProcessor taskProcessor = new TaskProcessor(expiredJudge, refresher, filter, processorName);

            processorFactory.addStreamProcessor(streamProcessor);
            processorFactory.addTaskProcessor(taskProcessor);

        }
        return processorFactory;
    }

}
