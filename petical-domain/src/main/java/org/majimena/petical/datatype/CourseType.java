package org.majimena.petical.datatype;

/**
 * 診察科目.
 */
public enum CourseType implements EnumDataType {

    EXAMINATION("診察料"),

    INSPECTION("検査料"),

    INJECTION("注射料"),

    SCHOOLING("指導料"),

    DOCUMENT("文書料"),

    DISPENSING("調剤料"),

    PHYSIATRICS("物療料"),

    TREATMENT("処置料"),

    ADMISSION("入院料"),

    OPERATION("手術料"),

    ANESTHESIA("麻酔料"),

    BLOOD_TRANSFUSION("輸血料"),

    NECROPSY("剖検料");

    private String name;

    CourseType(String name) {
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

    public boolean is(CourseType value) {
        return this == value;
    }
}
