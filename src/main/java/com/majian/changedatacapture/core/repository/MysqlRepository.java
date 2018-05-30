package com.majian.changedatacapture.core.repository;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;

public class MysqlRepository {

    private JdbcTemplate jdbcTemplate;

    public MysqlRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map queryForMap(String sql) {
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        if (CollectionUtils.isEmpty(maps)) {
            return null;
        } else {
            return maps.get(0);
        }
    }

    public List<Map<String, Object>> queryForList(String sql) {
        return jdbcTemplate.queryForList(sql);
    }

}
