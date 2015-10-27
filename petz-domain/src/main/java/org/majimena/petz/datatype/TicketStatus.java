package org.majimena.petz.datatype;

import java.util.function.Consumer;

/**
 * チケットのステータス.
 */
public enum TicketStatus implements EnumDataType {

    RESERVED("予約中"),

    RECEIPTED("受付済"),

    DOING("診察中"),

    PAYMENT("支払未"),

    COMPLETED("完了"),

    CANCEL("キャンセル");

    private String name;

    TicketStatus(String name) {
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

    public boolean is(TicketStatus status) {
        return this == status;
    }

    public void is(TicketStatus status, Consumer<TicketStatus> proc) {
        if (is(status)) {
            proc.accept(status);
        }
    }

    public TicketStatus next() {
        switch (this) {
            case RESERVED:
                return RECEIPTED;
            case RECEIPTED:
                return DOING;
            case DOING:
                return PAYMENT;
            case CANCEL:
                return CANCEL;
            default:
                return COMPLETED;
        }
    }
}
