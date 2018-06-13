package com.majian.changedatacapture.core;

import java.util.List;

public interface RowMapper {

    List<Row> map(Row row);
}
