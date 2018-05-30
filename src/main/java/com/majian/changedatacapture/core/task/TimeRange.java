package com.majian.changedatacapture.core.task;

import java.sql.Timestamp;

public class TimeRange {

    //9999-12-31 23:59:59.999
    private static final long MAX = 253402271999999L;

    private Timestamp begin = new Timestamp(0L);
    private Timestamp end = new Timestamp(MAX);

    public TimeRange() {
    }

    public TimeRange withBegin(Timestamp begin) {
        this.begin = begin;
        return this;
    }

    public TimeRange withEnd(Timestamp end) {
        this.end = end;
        return this;
    }

    public Timestamp getBegin() {
        return begin;
    }

    public Timestamp getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return String.format("[%s,%s]", begin, end);
    }
}
