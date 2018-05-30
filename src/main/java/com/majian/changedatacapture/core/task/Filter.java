package com.majian.changedatacapture.core.task;

import com.majian.changedatacapture.configuration.ChangeSource;
import com.majian.changedatacapture.core.repository.MysqlRepository;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Filter {

    private MysqlRepository mysqlRepository;
    private ChangeSource changeSource;

    public Filter(MysqlRepository mysqlRepository,
        ChangeSource changeSource) {
        this.mysqlRepository = mysqlRepository;
        this.changeSource = changeSource;
    }

    public List<Pair> filterIdByUpdateTimeRange(TimeRange timeRange) {
        Timestamp begin = timeRange.getBegin();
        Timestamp end = timeRange.getEnd();
        String primaryKey = changeSource.getPrimaryKey();
        String updateTimeField = changeSource.getUpdateTimeField();
        String table = changeSource.getTable();
        String rootKeyName = changeSource.getRootKey();

        String sql = String
            .format("select %s, %s, %s from %s where %s BETWEEN '%s' and '%s'", primaryKey, updateTimeField, rootKeyName, table,
                updateTimeField, begin, end);
        List<Map<String, Object>> rows = mysqlRepository.queryForList(sql);

        return rows.stream()
            .map(entry -> {
                Object key = entry.get(primaryKey);
                Object rootKey = entry.get(rootKeyName);
                Date updateTime = (Date) entry.get(updateTimeField);
                return new Pair(table, rootKey,key, updateTime);
            })
            .collect(Collectors.toList());
    }

}
