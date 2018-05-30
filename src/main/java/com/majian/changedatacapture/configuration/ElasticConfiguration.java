package com.majian.changedatacapture.configuration;

import lombok.Data;

@Data
public class ElasticConfiguration {
    public static final String ES_UPDATE_TIME_FIELD="es_update_time";
    private String type;
}
