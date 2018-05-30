package com.majian.changedatacapture.core.binlog;

public interface Header {

    EventType getEventType();

    CharSequence getTableName();
}
