package com.majian.changedatacapture.core.converters;

import com.majian.changedatacapture.core.Converter;
import com.majian.changedatacapture.core.GsonUtils;
import java.util.Map;

public class JsonToMapConverter implements Converter {

    @Override
    public String getName() {
        return "json2map";
    }

    @Override
    public Map convert(Object source) {
        return GsonUtils.fromJson((String) source, Map.class);
    }
}
