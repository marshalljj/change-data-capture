package com.majian.changedatacapture.core.stream;

import com.majian.changedatacapture.core.binlog.Column;
import com.majian.changedatacapture.core.binlog.Entry;
import com.majian.changedatacapture.core.binlog.EntryType;
import com.majian.changedatacapture.core.binlog.Header;
import com.majian.changedatacapture.core.binlog.Message;
import com.majian.changedatacapture.core.binlog.RowChange;
import com.majian.changedatacapture.core.binlog.RowData;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

@Slf4j
public class BinlogProcessor  {

    private Refresher refresher;
    private KafkaConsumer<String, byte[]> kafkaConsumer;

    private ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);

    public void close() {
        Thread.currentThread().interrupt();
    }

    public void start() {
        pool.schedule(this::startTask, 5, TimeUnit.SECONDS);
        pool.scheduleAtFixedRate(this::monitorTask, 60, 60, TimeUnit.SECONDS);
    }


    private void startTask() {
        while (!Thread.currentThread().isInterrupted()) {
            ConsumerRecords<String, byte[]> records = kafkaConsumer.poll(100);
            for (ConsumerRecord<String, byte[]> record : records) {
                try {
                    List<RowData> lists = getRowData(record.value());
                    for (RowData rowData : lists) {
                        List<Column> row = rowData.getAfterColumnsList();
                        refresher.refresh(new ColumnRow(row));
                    }

                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }
    }

    private List<RowData> getRowData(byte[] value) {
        Message message = Message.parseFrom(value);
        Entry entry = message.getEntry();
        if (entry.getEntryType() != EntryType.ROWDATA) {
            return Collections.emptyList();
        }
        Header header = entry.getHeader();
        if (!header.getTableName().equals(refresher.tableName())) {
           return Collections.emptyList();
        }

        RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
        return rowChange.getRowDatasList();
    }

    private void monitorTask() {
        //健康检查
    }

}
