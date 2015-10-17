package org.majimena.petz.datatype;

import java.util.function.Consumer;

/**
 * Created by todoken on 2015/10/04.
 */
public enum ScheduleStatus implements EnumDataType {

    RESERVED("予約中"),

    RECEIPTED("受付済"),

    COMPLETED("完了");

    private String name;

    ScheduleStatus(String name) {
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

    public boolean is(ScheduleStatus status) {
        return this == status;
    }

    public void is(ScheduleStatus status, Consumer<ScheduleStatus> proc) {
        if (is(status)) {
            proc.accept(status);
        }
    }

    public ScheduleStatus next() {
        if (this == RESERVED) {
            return RECEIPTED;
        }
        return COMPLETED;
    }
}
