package com.majian.changedatacapture.core.task;

import com.majian.changedatacapture.core.GsonUtils;
import com.majian.changedatacapture.core.Refresher;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskProcessor {

    private ExpiredJudge expiredJudge;
    private Refresher refresher;
    private Filter filter;
    private String name;

    public TaskProcessor(ExpiredJudge expiredJudge, Refresher refresher,
        Filter filter, String name) {
        this.expiredJudge = expiredJudge;
        this.refresher = refresher;
        this.filter = filter;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void scan(TimeRange timeRange) {
        List<Pair> pairs = filter.filterIdByUpdateTimeRange(timeRange);
        pairs.parallelStream()
            .forEach(this::processForEach);

    }

    private void processForEach(Pair pair) {
        try {
            if (expiredJudge.isExpired(pair)) {
                refresher.refresh(pair.getRootKey(), pair.getUpdateTime());
                log.info("binlog compensation completed: {}", GsonUtils.toJson(pair));
            }
        } catch (Exception e) {
            log.error("binlog compensation failed: {}", GsonUtils.toJson(pair), e);
        }
    }







}
