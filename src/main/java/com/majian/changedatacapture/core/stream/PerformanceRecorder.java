package com.majian.changedatacapture.core.stream;

import com.majian.changedatacapture.core.LogMessage;
import com.majian.changedatacapture.core.repository.ElasticRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PerformanceRecorder {

    private ElasticRepository elasticRepository;
    private boolean enabled;
    private String recordType;

    public PerformanceRecorder(ElasticRepository elasticRepository, boolean enabled, String recordType) {
        this.elasticRepository = elasticRepository;
        this.enabled = enabled;
        this.recordType = recordType;
    }

    public void persist(LogMessage logMessage) {
        if (enabled) {
            elasticRepository.put(recordType, logMessage);
        }
        log.info("{}", logMessage);
    }
}
