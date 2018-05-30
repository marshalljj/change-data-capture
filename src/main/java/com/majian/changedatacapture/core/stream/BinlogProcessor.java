package com.majian.changedatacapture.core.stream;

import com.majian.changedatacapture.core.binlog.Entry;
import com.majian.changedatacapture.core.binlog.EntryType;
import com.majian.changedatacapture.core.binlog.EventType;
import com.majian.changedatacapture.core.binlog.Header;
import com.majian.changedatacapture.core.binlog.Message;
import com.majian.changedatacapture.core.binlog.RowChange;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class BinlogProcessor {

    private static final Set<EventType> validEvents = EnumSet.of(EventType.INSERT, EventType.UPDATE);
    private List<StreamProcessor> streamProcessors;

    public BinlogProcessor(
        List<StreamProcessor> streamProcessors) {
        this.streamProcessors = streamProcessors;
    }

    public void process(byte[] message) {
        try {
            Message canalMessage = Message.parseFrom(message);
            Entry entry = canalMessage.getEntry();
            if (!(entry.getEntryType() == EntryType.ROWDATA)) {
                return;
            }
            Header header = entry.getHeader();
            EventType eventType = header.getEventType();
            log.info("receive binlog: table={},eventType={}", header.getTableName(), eventType);
            if (!validEvents.contains(eventType)) {
                return;
            }
            for (StreamProcessor streamProcessor : streamProcessors) {
                if (StringUtils.equalsIgnoreCase(streamProcessor.tableName(), header.getTableName())) {
                    RowChange rowChange = RowChange.parseFrom(entry.getStoreValue());
                    streamProcessor.processEachChange(rowChange);
                }
            }
        } catch (Exception e) {
            log.error("解析binlog 失败", e);
        }

    }

}
