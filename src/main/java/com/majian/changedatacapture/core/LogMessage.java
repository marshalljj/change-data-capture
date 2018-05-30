package com.majian.changedatacapture.core;

import java.util.Date;
import lombok.Value;

@Value
public class LogMessage {

    private String table;
    private Object id;
    private Date changeTime;
    private Date processTime;
    private long elapsedMillis;

    public LogMessage(String table, Object id, Date changeTime) {
        this.table = table;
        this.id = id;
        this.changeTime = changeTime;
        this.processTime = new Date();
        this.elapsedMillis = processTime.getTime() - changeTime.getTime();
    }

    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }
}
