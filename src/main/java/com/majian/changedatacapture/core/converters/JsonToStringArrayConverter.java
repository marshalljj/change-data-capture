package com.majian.changedatacapture.core.converters;

import com.majian.changedatacapture.core.Converter;
import com.majian.changedatacapture.core.GsonUtils;
import com.google.gson.reflect.TypeToken;
import java.util.List;

public class JsonToStringArrayConverter implements Converter {

    @Override
    public String getName() {
        return "json2stringArray";
    }

    @Override
    public List<String> convert(Object source) {
        return GsonUtils.fromJson((String) source, new TypeToken<List<String>>() {
        });
    }
}
