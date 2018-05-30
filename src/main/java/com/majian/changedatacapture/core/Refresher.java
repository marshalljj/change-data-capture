package com.majian.changedatacapture.core;

import com.majian.changedatacapture.configuration.ElasticConfiguration;
import com.majian.changedatacapture.configuration.Field;
import com.majian.changedatacapture.configuration.Node;
import com.majian.changedatacapture.core.repository.ElasticRepository;
import com.majian.changedatacapture.core.repository.MysqlRepository;
import com.google.common.collect.Lists;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

@Slf4j
public class Refresher {

    private MysqlRepository mysqlRepository;
    private ElasticRepository elasticRepository;
    private Node tree;
    private ElasticConfiguration elasticConfiguration;

    public Refresher(MysqlRepository mysqlRepository,
        ElasticRepository elasticRepository, Node tree,
        ElasticConfiguration elasticConfiguration) {
        this.mysqlRepository = mysqlRepository;
        this.elasticRepository = elasticRepository;
        this.tree = tree;
        this.elasticConfiguration = elasticConfiguration;
    }

    public void refresh(Object key, Date date) {
        Optional<Map<String, Object>> optional = load(key);
        optional.ifPresent(data -> {
            data.put(ElasticConfiguration.ES_UPDATE_TIME_FIELD, date);
            elasticRepository.put(elasticConfiguration.getType(),key, data);
        });
    }

    private Optional<Map<String, Object>> load(Object id) {
        String sqlTemplate = tree.getSql();
        Map<String, Object> root = mysqlRepository.queryForMap(SqlParser.parseSql(sqlTemplate, id));
        if (root == null) {
            return Optional.empty();
        }
        root = processField(root, tree.getFields());
        List<Node> children = tree.getChildren();
        for (Node childNode : children) {
            if (!childNode.isMulti()) {
                Map child = mysqlRepository.queryForMap(SqlParser.parseSql(childNode.getSql(), id));
                if (child != null) {
                    child = processField(child, childNode.getFields());
                }
                root.put(childNode.getName(), child);
            } else {
                List<Map<String, Object>> child = mysqlRepository.queryForList(SqlParser.parseSql(childNode.getSql(), id));
                if (child != null) {
                    child = batchProcessField(child, childNode.getFields());
                }
                root.put(childNode.getName(), child);
            }
        }
        return Optional.of(root);
    }


    private List<Map<String, Object>> batchProcessField(List<Map<String, Object>> sources, List<Field> fields) {
        List<Map<String, Object>> targets = Lists.newArrayList();
        for (Map<String, Object> source : sources) {
            targets.add(processField(source, fields));
        }
        return targets;
    }

    /**
     * 处理 指定字段内容
     */
    private Map processField(Map<String, Object> source, List<Field> fields) {
        if (CollectionUtils.isEmpty(fields)) {
            return source;
        }
        Map<String, String> field2Converter = fields.stream()
            .collect(Collectors.toMap(Field::getName, Field::getConverter));
        Map<String, Object> result = new HashMap<>(source.size());
        for (Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String converterName = field2Converter.get(key);
            Converter converter = ConverterRegistry.getInstance().getConverter(converterName);
            if (converter != null) {
                value = converter.convert(value);
            }
            result.put(key, value);
        }
        return result;
    }

}
