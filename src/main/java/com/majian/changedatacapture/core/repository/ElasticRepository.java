package com.majian.changedatacapture.core.repository;

import com.majian.changedatacapture.configuration.ElasticConfiguration;
import com.majian.changedatacapture.core.GsonUtils;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.get.GetField;

public class ElasticRepository {

    private Client client;
    private String index;

    public ElasticRepository(Client client, String index) {
        this.client = client;
        this.index = index;
    }

    public void put(String type, Object key, Map<String, ?> data) {

        client.prepareIndex(index, type, String.valueOf(key))
            .setSource(GsonUtils.toJson(data)).get();
    }

    public Optional<Date> loadUpdateTimeById(String type, String id) {
        GetResponse getResponse = client
            .prepareGet(index, type, id)
            .setFields(ElasticConfiguration.ES_UPDATE_TIME_FIELD)
            .get();
        if (!getResponse.isExists()) {
            return Optional.empty();
        }

        GetField field = getResponse.getField(ElasticConfiguration.ES_UPDATE_TIME_FIELD);
        return Optional.ofNullable(field).map(value -> new Date((Long) value.getValue()));
    }
}
