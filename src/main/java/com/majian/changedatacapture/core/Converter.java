package com.majian.changedatacapture.core;

public interface Converter {

    String getName();

    Object convert(Object source);

}
