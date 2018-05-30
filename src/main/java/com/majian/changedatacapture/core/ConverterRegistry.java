package com.majian.changedatacapture.core;

import java.util.HashMap;
import java.util.Map;

public class ConverterRegistry {

    private static final ConverterRegistry INSTANCE = new ConverterRegistry();

    private final Map<String, Converter> CONVERTER_MAPPING = new HashMap<>();

    public void register(Converter converter) {
        if (converter != null) {
            CONVERTER_MAPPING.put(converter.getName(), converter);
        }
    }

    public Converter getConverter(String name) {
        return CONVERTER_MAPPING.getOrDefault(name, null);
    }

    public static ConverterRegistry getInstance() {
        return INSTANCE;
    }

}
