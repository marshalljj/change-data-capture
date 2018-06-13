package com.majian.changedatacapture.core.task;

import com.majian.changedatacapture.configuration.ChangeSource;
import com.majian.changedatacapture.core.Refresher;
import com.majian.changedatacapture.core.Row;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskProcessor {

    private Refresher refresher;
    private String name;
    private ChangeSource changeSource;

    public TaskProcessor(Refresher refresher, String name,
        ChangeSource changeSource) {
        this.refresher = refresher;
        this.name = name;
        this.changeSource = changeSource;
    }

    public String getName() {
        return name;
    }


    private void processForEach(Row row) {
        String table = changeSource.getTable();
        Object id = row.getValue(changeSource.getPrimaryKey());
        try {
            Date updateTime = (Date) row.getValue(changeSource.getUpdateTimeField());
            refresher.refresh(row, updateTime);
            log.info("binlog compensation completed: table={}, id={}", table, id);
        } catch (Exception e) {
            log.error("binlog compensation failed: table={}, id={}", table, id, e);
        }
    }

    public void scan(List<? extends Row> rows) {
        rows.parallelStream().forEach(this::processForEach);
    }


}
