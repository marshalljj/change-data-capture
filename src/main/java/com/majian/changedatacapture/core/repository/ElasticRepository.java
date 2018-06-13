package com.majian.changedatacapture.core.repository;

import com.majian.changedatacapture.core.GsonUtils;
import java.util.Map;
import org.elasticsearch.client.Client;

public class ElasticRepository {

    private Client client;
    private String index;

    public ElasticRepository(Client client, String index) {
        this.client = client;
        this.index = index;
    }

    public void put(String type, Object key, Map<String, ?> data) {

        client.prepareIndex(index, type, String.valueOf(key))
            .setSource(GsonUtils.toJson(data)).execute();
    }

    public void put(String type, Object object) {
        client.prepareIndex(index, type)
            .setSource(GsonUtils.toJson(object)).execute();
    }
}
