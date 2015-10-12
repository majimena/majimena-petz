package org.majimena.petz.datatype;

import java.io.Serializable;

/**
 * サポートする言語.
 */
public enum LangKey implements EnumDataType, Serializable {
    /**
     * 英語.
     */
    ENGLISH("en"),

    /**
     * 日本語.
     */
    JAPANESE("ja");

    private String value;

    LangKey(String value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getValue() {
        return value;
    }
}
