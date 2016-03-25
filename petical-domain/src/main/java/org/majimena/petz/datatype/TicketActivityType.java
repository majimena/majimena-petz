package org.majimena.petz.datatype;

/**
 * チケットアクティビティタイプ.
 */
public enum TicketActivityType implements EnumDataType {

    CHANGE_STATE("ステートの変更"),

    MODIFY_RESERVATION("予約変更"),

    EXAMINATION("診察");

    private String name;

    TicketActivityType(String name) {
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

    public boolean is(TicketActivityType value) {
        return this == value;
    }
}
