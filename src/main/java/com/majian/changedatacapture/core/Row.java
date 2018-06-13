package com.majian.changedatacapture.core;

import java.util.Map;

public interface Row {

    /**
     * 根据列名获取对应列的值
     * @param columnName
     * @return
     */
    Object getValue(String columnName);

    Map<String,Object> toMap();

}
