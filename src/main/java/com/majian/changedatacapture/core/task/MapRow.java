package com.majian.changedatacapture.core.task;

import com.majian.changedatacapture.core.Row;
import java.util.Collections;
import java.util.Map;

public class MapRow implements Row {

    private Map<String, Object> columns;

    public MapRow(Map<String, Object> columns) {
        this.columns = columns;
    }

    @Override
    public Object getValue(String columnName) {
        return columns.get(columnName);
    }

    @Override
    public Map<String, Object> toMap() {
        return Collections.unmodifiableMap(columns);
    }
}
