package com.majian.changedatacapture.configuration;


import com.majian.changedatacapture.core.stream.BinlogProcessor;
import com.majian.changedatacapture.core.stream.StreamProcessor;
import com.majian.changedatacapture.core.task.TaskProcessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProcessorFactory {

    private List<StreamProcessor> streamProcessors = new ArrayList<>();
    private List<TaskProcessor> taskProcessors = new ArrayList<>();
    private BinlogProcessor binlogProcessor;

    public Optional<TaskProcessor> getTaskProcessor(String name) {
        return taskProcessors.stream()
            .filter(taskProcessor -> name.equals(taskProcessor.getName()))
            .findFirst();
    }

    public List<TaskProcessor> getTaskProcessors() {
        return taskProcessors;
    }

    public void addStreamProcessor(StreamProcessor streamProcessor) {
        streamProcessors.add(streamProcessor);
    }

    public void addTaskProcessor(TaskProcessor taskProcessor) {
        taskProcessors.add(taskProcessor);
    }

    public BinlogProcessor getBinlogProcessor() {
        if (binlogProcessor == null) {
            binlogProcessor = new BinlogProcessor(streamProcessors);
        }
        return binlogProcessor;
    }



}
