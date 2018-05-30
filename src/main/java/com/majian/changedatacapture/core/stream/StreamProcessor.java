package com.majian.changedatacapture.core.stream;

import com.majian.changedatacapture.configuration.ChangeSource;
import com.majian.changedatacapture.core.LogMessage;
import com.majian.changedatacapture.core.Refresher;
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

    public StreamProcessor(Refresher refresher,
        ChangeSource changeSource, String name) {
        this.refresher = refresher;
        this.changeSource = changeSource;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private Object getValue(List<Column> row, String columnName) {
        Column column = getColumn(row, columnName);
        if (column == null) {
            return null;
        }
        return ColumnParser.getTypedValue(column);
    }

    private Column getColumn(List<Column> row, String name) {
        for (Column column : row) {
            if (StringUtils.equalsIgnoreCase(column.getName(), name)) {
                return column;
            }
        }
        return null;
    }

    private void refresh(List<Column> row) {
        Object key = getValue(row, changeSource.getRootKey());
        Date updateTime = (Date) getValue(row, changeSource.getUpdateTimeField());
        if (key != null) {
            refresher.refresh(key, updateTime);
            log.info("binlog stream process completed: {}", new LogMessage(changeSource.getTable(), key, updateTime));
        }
    }

    public String tableName() {
        return changeSource.getTable();
    }

    public void processEachChange(RowChange rowChange) {
        for (RowData rowData : rowChange.getRowDatasList()) {
            List<Column> afterColumnsList = rowData.getAfterColumnsList();
            refresh(afterColumnsList);
        }
    }
}
