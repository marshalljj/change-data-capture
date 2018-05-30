package com.majian.changedatacapture.core.binlog;

public interface Message {

    Entry getEntry();

    static Message parseFrom(byte[] message) {
        return null;
    }
}
