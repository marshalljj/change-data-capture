package com.majian.changedatacapture.core;

import com.google.common.collect.Lists;
import java.util.List;

public class DefaultRowMapper implements RowMapper{

    @Override
    public List<Row> map(Row row) {
        return Lists.newArrayList(row);
    }
}
