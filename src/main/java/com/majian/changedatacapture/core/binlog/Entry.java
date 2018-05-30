package com.majian.changedatacapture.core.binlog;

public interface Entry {

    EntryType getEntryType();

    Header getHeader();

    byte[] getStoreValue();
}
