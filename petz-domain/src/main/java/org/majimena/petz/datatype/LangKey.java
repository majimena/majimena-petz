package org.majimena.petz.datatype;

import java.io.Serializable;
import java.util.Locale;

/**
 * サポートする言語.
 */
public enum LangKey implements EnumDataType, Serializable {
    /**
     * 英語.
     */
    ENGLISH(Locale.US),

    /**
     * 日本語.
     */
    JAPANESE(Locale.JAPAN);

    private Locale locale;

    LangKey(Locale locale) {
        this.locale = locale;
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getValue() {
        return name();
    }
}
