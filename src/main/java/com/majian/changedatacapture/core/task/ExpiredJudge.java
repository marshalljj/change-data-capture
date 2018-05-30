package com.majian.changedatacapture.core.task;

import static com.majian.changedatacapture.core.SqlParser.parseSql;

import com.majian.changedatacapture.configuration.ChangeSource;
import com.majian.changedatacapture.configuration.ElasticConfiguration;
import com.majian.changedatacapture.core.repository.ElasticRepository;
import com.majian.changedatacapture.core.repository.MysqlRepository;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

public class ExpiredJudge {

    private MysqlRepository mysqlRepository;
    private ElasticRepository elasticRepository;
    private ChangeSource changeSource;
    private ElasticConfiguration elasticConfiguration;

    public ExpiredJudge(MysqlRepository mysqlRepository,
        ElasticRepository elasticRepository, ChangeSource changeSource,
        ElasticConfiguration elasticConfiguration) {
        this.mysqlRepository = mysqlRepository;
        this.elasticRepository = elasticRepository;
        this.changeSource = changeSource;
        this.elasticConfiguration = elasticConfiguration;
    }

    public boolean isExpired(Pair pair) {
        Object id = pair.getId();
        Object rootKey = pair.getRootKey();
        Optional<Date> elasticOptional = elasticRepository.loadUpdateTimeById(elasticConfiguration.getType(), String.valueOf(rootKey));

        return elasticOptional.map(esModify -> {
            Optional<Date> sqlOptional = this.loadUpdateTimeByKey(id);
            Date sqlModify = sqlOptional.orElse(new Date());
            return esModify.before(sqlModify);
        }).orElse(true);

    }

    public Optional<Date> loadUpdateTimeByKey(Object key) {
        String sql = String
            .format("select * from %s where %s=#{id}", changeSource.getTable(), changeSource.getPrimaryKey());
        Map row = mysqlRepository.queryForMap(parseSql(sql, key));
        return Optional.ofNullable(row)
            .map(columns -> (Date) columns.get(changeSource.getUpdateTimeField()));
    }


}
