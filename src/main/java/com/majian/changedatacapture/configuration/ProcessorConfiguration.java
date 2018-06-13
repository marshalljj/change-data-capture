package com.majian.changedatacapture.configuration;

import lombok.Data;
import org.springframework.util.Assert;

@Data
public class ProcessorConfiguration {
    private String name;
    private ElasticConfiguration elasticConfiguration;
    private ChangeSource changeSource;
    private String mapper;
    private String rootKey;
    private Node node;

    public ProcessorConfiguration() {
    }

    public String getElasticSearchType() {
        return elasticConfiguration.getType();
    }

    public void checkValid() {
        Assert.notNull(node, "node cant be null");
        Assert.notNull(rootKey, "root-key cant be null");
        Assert.notNull(changeSource, "change-source cant be null");
        Assert.notNull(elasticConfiguration, "es cant be null");
        Assert.notNull(name, "processor's name attribute can't be null");
    }
}
