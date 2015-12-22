package org.majimena.petz.datatype;

/**
 * 支払方法.
 */
public enum PaymentOption implements EnumDataType {

    CASH("現金"),

    CREDIT_CARD("クレジットカード"),

    OTHER("その他");

    private String name;

    PaymentOption(String name) {
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

    public boolean is(PaymentOption value) {
        return this == value;
    }
}
