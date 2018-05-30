package com.majian.changedatacapture.autoconfig;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@Configuration
@EnableKafka
public class BinlogKafkaConfiguration {

    @Value("${binlog.kafka.bootstrap-servers}")
    private String bootStrapServers;
    @Value("${binlog.kafka.group-id}")
    private String groupId;

    @Bean("binlogContainerFactory")
    ConcurrentKafkaListenerContainerFactory<Integer, String> binlogContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(binlogConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<Integer, String> binlogConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        DefaultKafkaConsumerFactory defaultKafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(props);
        defaultKafkaConsumerFactory.setKeyDeserializer(new StringDeserializer());
        defaultKafkaConsumerFactory.setValueDeserializer(new ByteArrayDeserializer());
        return defaultKafkaConsumerFactory;
    }


}
