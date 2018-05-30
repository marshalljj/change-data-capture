package com.majian.changedatacapture.autoconfig;


import com.majian.changedatacapture.core.stream.BinlogProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@KafkaListener(topics = "${binlog.kafka.topic}", containerFactory = "binlogContainerFactory")
public class BinlogListener {

    private BinlogProcessor binlogProcessor;

    public BinlogListener(BinlogProcessor binlogProcessor) {
        this.binlogProcessor = binlogProcessor;
    }

    @KafkaHandler
    public void onBinlog(byte[] message) {
        try {
            binlogProcessor.process(message);
        } catch (Exception e) {
            log.error("binlog 处理失败", e);
        }

    }
}
