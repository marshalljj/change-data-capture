package com.majian.changedatacapture.core.stream;

import com.majian.changedatacapture.configuration.ChangeSource;
import com.majian.changedatacapture.core.LogMessage;
import com.majian.changedatacapture.core.Refresher;
import com.majian.changedatacapture.core.Row;
import com.majian.changedatacapture.core.binlog.Column;
import com.majian.changedatacapture.core.binlog.RowChange;
import com.majian.changedatacapture.core.binlog.RowData;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


@Slf4j
public class StreamProcessor {

    private Refresher refresher;
    private ChangeSource changeSource;
    private String name;
    private PerformanceRecorder performanceRecorder;

    public StreamProcessor(Refresher refresher,
        ChangeSource changeSource, String name,
        PerformanceRecorder performanceRecorder) {
        this.refresher = refresher;
        this.changeSource = changeSource;
        this.name = name;
        this.performanceRecorder = performanceRecorder;
    }

    public String getName() {
        return name;
    }



    private void refresh(Row row) {
        Object key = row.getValue(changeSource.getPrimaryKey());
        Date updateTime = (Date) row.getValue(changeSource.getUpdateTimeField());
        refresher.refresh(row, updateTime);
        performanceRecorder.persist(new LogMessage(changeSource.getTable(), key, updateTime));

    }

    public String tableName() {
        return changeSource.getTable();
    }

    public void processEachChange(RowChange rowChange) {
        for (RowData rowData : rowChange.getRowDatasList()) {
            List<Column> afterColumnsList = rowData.getAfterColumnsList();
            refresh(new ColumnRow(afterColumnsList));
        }
    }
}
