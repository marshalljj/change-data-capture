package com.majian.changedatacapture.configuration;

import lombok.Data;

@Data
public class ProcessorConfiguration {
    private String name;
    private ElasticConfiguration elasticConfiguration;
    private ChangeSource changeSource;
    private Node node;

    public ProcessorConfiguration() {
    }

}
