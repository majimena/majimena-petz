package org.majimena.petz.domain.common;

import org.majimena.framework.domain.EnumDataType;

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
