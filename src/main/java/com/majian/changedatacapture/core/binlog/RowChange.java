package com.majian.changedatacapture.core.binlog;

import java.nio.ByteBuffer;
import java.util.List;

public interface RowChange {

    List<RowData> getRowDatasList();

    static RowChange parseFrom(byte[] value) {
        return null;
    }

    static RowChange parseFrom(ByteBuffer value) {
        return null;
    }
}
