package com.majian.changedatacapture.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Node {

    private String name;
    private String sql;
    private boolean multi;
    private List<Field> fields;
    private List<Node> children;

    public Node() {
        this.children = new ArrayList<>();
        this.fields = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Field> getFields() {
        if (fields == null) {
            return Collections.emptyList();
        }
        return fields;
    }



}
