package com.majian.changedatacapture.core;

import com.majian.changedatacapture.core.repository.MysqlRepository;
import com.majian.changedatacapture.core.task.MapRow;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SqlRowMapper implements RowMapper{

    private String sqlTemplate;
    private MysqlRepository mysqlRepository;

    public SqlRowMapper(String sqlTemplate,
        MysqlRepository mysqlRepository) {
        this.sqlTemplate = sqlTemplate;
        this.mysqlRepository = mysqlRepository;
    }

    @Override
    public List<Row> map(Row row) {
        List<Map<String, Object>> maps = mysqlRepository.queryForList(sqlTemplate, row.toMap());
        return maps.stream().map(MapRow::new).collect(Collectors.toList());
    }
}
