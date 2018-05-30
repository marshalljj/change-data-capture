package com.majian.changedatacapture.autoconfig;

import com.google.common.base.Splitter;
import com.google.common.base.Splitter.MapSplitter;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.stream.Collectors;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.shield.ShieldPlugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BinlogElasticProperties.class)
public class ElasticClientConfiguration {

    @Autowired
    private BinlogElasticProperties properties;

    @Bean
    public Client client() {
        Settings settings = Settings.builder()
            .put("cluster.name", properties.getClusterName())
            .put(properties.getProperties())
            .build();

        return TransportClient.builder()
            .addPlugin(ShieldPlugin.class)
            .settings(settings)
            .build()
            .addTransportAddresses(getAddresses(properties.getClusterNodes()));

    }

    private InetSocketTransportAddress[] getAddresses(String clusterNodes) {
        MapSplitter mapSplitter = Splitter.on(",").withKeyValueSeparator(":");
        Map<String, String> nodesMap = mapSplitter.split(clusterNodes);
        return nodesMap.entrySet().stream()
            .map(entry -> new InetSocketTransportAddress(
                new InetSocketAddress(entry.getKey(), Integer.parseInt(entry.getValue()))))
            .collect(Collectors.toList()).toArray(new InetSocketTransportAddress[]{});
    }

}
