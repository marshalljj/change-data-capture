package com.majian.changedatacapture.core.task;

import java.util.Date;
import lombok.Value;

@Value
public class Pair{
    private String table;
    private Object rootKey;
    private Object id;
    private Date updateTime;
}
