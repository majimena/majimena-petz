package org.majimena.petz.datatypes;

import org.majimena.framework.core.datatypes.EnumDataType;

/**
 * Created by todoken on 2015/07/26.
 */
public enum SexType implements EnumDataType {

    NONE("不明"),
    MALE("オス"),
    FEMALE("メス");

    private String name;

    private SexType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return name();
    }
}
