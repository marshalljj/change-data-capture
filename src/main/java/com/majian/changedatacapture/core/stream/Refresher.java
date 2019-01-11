package com.majian.changedatacapture.core.stream;

import com.majian.changedatacapture.configuration.ChangeSource;
import com.majian.changedatacapture.configuration.Field;
import com.majian.changedatacapture.core.Converter;
import com.majian.changedatacapture.core.ConverterRegistry;
import com.majian.changedatacapture.core.Row;
import com.majian.changedatacapture.core.binlog.Column;
import com.majian.changedatacapture.core.binlog.EventType;
import com.majian.changedatacapture.core.binlog.RowChange;
import com.majian.changedatacapture.core.binlog.RowData;
import com.majian.changedatacapture.core.repository.ElasticRepository;
import com.majian.changedatacapture.core.repository.MysqlRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


@Slf4j
public class Refresher {

    private String name;
    private MysqlRepository mysqlRepository;
    private ElasticRepository elasticRepository;
    private ChangeSource changeSource;
    private String sqlTemplate;
    private String docId;
    private List<Field> fields;


    public String getName() {
        return name;
    }


    public String tableName() {
        return changeSource.getTable();
    }

    public String primaryKey() {
        return changeSource.getPrimaryKey();
    }


    public void refresh(Row row) {
        Object rootKeyValue = row.getValue(docId);

        Map<String, Object> data = transform(row);
        if (data == null) {
            elasticRepository.delete(rootKeyValue);
        }

        Map<String, Object> newData = processField(data);
        elasticRepository.put(rootKeyValue, newData);
    }

    private Map<String, Object> transform(Row row) {
        List<Map<String, Object>> rows = mysqlRepository.queryForList(sqlTemplate, row.toMap());
        return rows.isEmpty()? null:rows.get(0);
    }

    /**
     * 处理 指定字段内容
     */
    private Map<String, Object> processField(Map<String, Object> source) {
        Map<String, Object> result = new HashMap<>(source.size());
        for (Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Converter converter = getConverter(key);
            if (converter != null) {
                value = converter.convert(value);
            }
            result.put(key, value);
        }
        return result;
    }

    private Converter getConverter(String key) {
        for (Field field : fields) {
            if (StringUtils.equals(key, field.getName())) {
                return ConverterRegistry.getInstance().getConverter(field.getConverter());
            }
        }
        return null;
    }

}
