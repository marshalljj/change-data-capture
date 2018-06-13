package com.majian.changedatacapture.core.binlog;

public interface Column {

    String getValue();

    int getSqlType();

    String getName();
}
