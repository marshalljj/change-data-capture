package com.majian.changedatacapture.core.task;

import com.majian.changedatacapture.core.Row;
import com.majian.changedatacapture.core.stream.Refresher;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskProcessor {

    private Refresher refresher;
    private String name;

    public TaskProcessor(Refresher refresher, String name) {
        this.refresher = refresher;
        this.name = name;
    }

    public String getName() {
        return name;
    }


    private void processForEach(Row row) {
        String table = refresher.tableName();
        Object id = row.getValue(refresher.primaryKey());
        try {
            refresher.refresh(row);
            log.info("binlog compensation completed: table={}, id={}", table, id);
        } catch (Exception e) {
            log.error("binlog compensation failed: table={}, id={}", table, id, e);
        }
    }

    public void scan(List<? extends Row> rows) {
        rows.parallelStream().forEach(this::processForEach);
    }


}
