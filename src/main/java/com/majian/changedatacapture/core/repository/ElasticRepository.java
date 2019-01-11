package com.majian.changedatacapture.core.repository;

import com.majian.changedatacapture.core.GsonUtils;
import java.util.Map;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;

public class ElasticRepository {

    private Client client;
    private String index;
    private String type;

    public ElasticRepository(Client client, String index, String type) {
        this.client = client;
        this.index = index;
        this.type = type;
    }

    public void put(Object key, Map<String, ?> data) {

        client.prepareIndex(index, type, String.valueOf(key))
            .setSource(GsonUtils.toJson(data)).execute();
    }

    public void delete(Object rootKeyValue) {
        DeleteRequest request = Requests.deleteRequest(index).id(String.valueOf(rootKeyValue));
        client.delete(request);
    }
}
