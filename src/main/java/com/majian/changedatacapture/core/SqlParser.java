package com.majian.changedatacapture.core;

import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class SqlParser {
    public static String parseSql(String template, Object key) {
        String keyStr = parseParam(key);
        return StringUtils.replacePattern(template, "\\#\\{\\w*\\}", keyStr);
    }

    private static String parseParam(Object key) {
        if (Objects.equals(String.class, key.getClass())) {
            return String.format("'%s'", key);
        } else {
            return String.valueOf(key);
        }
    }

}
