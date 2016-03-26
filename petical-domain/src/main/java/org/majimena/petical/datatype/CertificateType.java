package org.majimena.petical.datatype;

/**
 * 支払方法.
 */
public enum CertificateType implements EnumDataType {

    PREVENTION("予防接種証明書"),

    RABID("狂犬病予防注射証"),

    OTHER("その他");

    private String name;

    CertificateType(String name) {
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

    public boolean is(CertificateType value) {
        return this == value;
    }
}
