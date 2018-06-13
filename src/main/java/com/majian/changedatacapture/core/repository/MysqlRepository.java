package com.majian.changedatacapture.core.repository;

import java.util.List;
import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.CollectionUtils;

public class MysqlRepository {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public MysqlRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Map<String, Object> queryForMap(String sql, Map<String, ?> paramMap) {
        List<Map<String, Object>> maps = queryForList(sql, paramMap);
        if (CollectionUtils.isEmpty(maps)) {
            return null;
        }
        return maps.get(0);
    }

    public List<Map<String, Object>> queryForList(String sql, Map<String, ?> paramMap) throws DataAccessException {
        return namedParameterJdbcTemplate.queryForList(sql, paramMap);
    }
}
