package com.majian.changedatacapture.core.stream;

import com.majian.changedatacapture.core.Row;
import com.majian.changedatacapture.core.binlog.Column;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class ColumnRow implements Row {

    private List<Column> columns;

    public ColumnRow(List<Column> columns) {
        this.columns = columns;
    }

    @Override
    public Object getValue(String columnName) {
        Column column = getColumn(columnName);
        if (column == null) {
            return null;
        }
        return ColumnParser.getTypedValue(column);
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        columns.forEach(column -> {
            map.put(column.getName(), column.getValue());
        });
        return map;
    }


    private Column getColumn(String name) {
        for (Column column : columns) {
            if (StringUtils.equalsIgnoreCase(column.getName(), name)) {
                return column;
            }
        }
        return null;
    }
}
