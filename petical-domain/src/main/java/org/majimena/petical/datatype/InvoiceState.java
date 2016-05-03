package org.majimena.petical.datatype;

/**
 * インヴォイスのステータス.
 */
public enum InvoiceState implements EnumDataType {

    NULL(""),

    NOT_PAID("未払い"),

    PAID("支払済"),

    CANCEL("キャンセル");

    private String name;

    InvoiceState(String name) {
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

    public boolean is(InvoiceState status) {
        return this == status;
    }
}
