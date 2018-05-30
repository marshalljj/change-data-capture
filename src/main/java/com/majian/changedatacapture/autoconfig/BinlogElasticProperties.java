package com.majian.changedatacapture.autoconfig;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(
    prefix = "binlog.elasticsearch"
)
public class BinlogElasticProperties {
    private String clusterName = "elasticsearch";
    private String clusterNodes;
    private Map<String, String> properties = new HashMap();

    public BinlogElasticProperties() {
    }

    public String getClusterName() {
        return this.clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getClusterNodes() {
        return this.clusterNodes;
    }

    public void setClusterNodes(String clusterNodes) {
        this.clusterNodes = clusterNodes;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
