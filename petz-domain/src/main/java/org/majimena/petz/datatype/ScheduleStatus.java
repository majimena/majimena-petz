package org.majimena.petz.datatype;

/**
 * Created by todoken on 2015/10/04.
 */
public enum ScheduleStatus implements EnumDataType {

    RESERVED("予約");

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
}
