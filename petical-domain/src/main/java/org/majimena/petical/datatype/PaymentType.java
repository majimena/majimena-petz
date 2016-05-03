package org.majimena.petical.datatype;

/**
 * 支払方法.
 */
public enum PaymentType implements EnumDataType {

    CASH("現金"),

    CREDIT_CARD("クレジットカード"),

    OTHER("その他");

    private String name;

    PaymentType(String name) {
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

    public boolean is(PaymentType value) {
        return this == value;
    }
}
