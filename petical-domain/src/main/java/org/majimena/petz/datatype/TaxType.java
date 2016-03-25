package org.majimena.petz.datatype;

/**
 * Created by todoken on 2015/07/26.
 */
public enum TaxType implements EnumDataType {

    INCLUSIVE("内税"),
    EXCLUSIVE("外税");

    private String name;

    TaxType(String name) {
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

    public boolean is(TaxType type) {
        return this == type;
    }
}
