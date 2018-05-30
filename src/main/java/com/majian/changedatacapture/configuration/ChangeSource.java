package com.majian.changedatacapture.configuration;

import lombok.Data;

@Data
public class ChangeSource {
    private String table;
    private String primaryKey;
    private String rootKey;
    private String updateTimeField;
}
