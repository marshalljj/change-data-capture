package com.majian.changedatacapture.core.stream;

import com.majian.changedatacapture.core.binlog.Column;
import java.sql.JDBCType;
import java.sql.Timestamp;

public class ColumnParser {

    public static Object getTypedValue(Column column) {
        String strValue = column.getValue();
        JDBCType jdbcType = JDBCType.valueOf(column.getSqlType());
        return convertToJavaObject(strValue, jdbcType);

    }

    private static Object convertToJavaObject(String value, JDBCType jdbcType) {
        switch (jdbcType) {
            case CHAR:
            case VARCHAR:
            case LONGVARCHAR:
                return value;
            case TINYINT:
                return Byte.valueOf(value);
            case SMALLINT:
                return Short.valueOf(value);
            case INTEGER:
                return Integer.valueOf(value);
            case BIGINT:
                return Long.valueOf(value);
            case TIMESTAMP:
                return Timestamp.valueOf(value);
            default:
                return value;
        }
    }
}
