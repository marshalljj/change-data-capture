package com.majian.changedatacapture.configuration;

import java.util.List;
import lombok.Data;

@Data
public class ProcessorFactoryConfiguration {

    private List<ProcessorConfiguration> processors;

    public void checkValid() {
        processors.forEach(ProcessorConfiguration::checkValid);
    }
}
