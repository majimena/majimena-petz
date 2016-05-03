package org.majimena.petical.web.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.majimena.petical.common.exceptions.ApplicationException;
import org.majimena.petical.common.exceptions.ResourceNotFoundException;
import org.majimena.petical.datatype.defs.ID;
import org.majimena.petical.domain.errors.ErrorCode;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

/**
 * エラーオブジェクトを操作するユーティリティ.
 */
public class ErrorsUtils {

    public static void reject(ErrorCode code, Errors errors) {
        errors.reject(code.name());
    }

    public static void rejectIfNotEquals(ErrorCode code, String value1, String value2, Errors errors) {
        if (!StringUtils.equals(value1, value2)) {
            errors.reject(code.name(), "may not be equal values");
        }
    }

    public static void rejectValue(String field, ErrorCode code, Errors errors) {
        errors.rejectValue(field, code.name());
    }

    public static void rejectIfNull(String field, Object value, Errors errors) {
        if (value == null) {
            errors.rejectValue(field, "errors.required");
        }
    }

    public static void rejectIfEmpty(String field, String value, Errors errors) {
        if (StringUtils.isEmpty(value)) {
            errors.rejectValue(field, "errors.required", "may not be empty");
        }
    }

    public static void rejectIfAllNull(Object[] values, Errors errors) {
        for (Object value : values) {
            if (values != null) {
                return;
            }
        }
        errors.reject("errors.required");
    }

    /**
     * バインディングリザルトにエラーがある場合に例外を投げる.
     *
     * @param errors バインディングリザルト
     * @throws BindException エラーがある場合
     */
    public static void throwIfHasErrors(BindingResult errors) throws BindException {
        if (errors != null && errors.hasErrors()) {
            throw new BindException(errors);
        }
    }

    /**
     * 値が空の場合に例外を投げます.
     *
     * @param value チェックする値
     */
    public static void throwIfEmpty(String value) {
        if (StringUtils.isEmpty(value)) {
            throw new ApplicationException(ErrorCode.PTZ_999401);
        }
    }

    /**
     * 値がメールアドレスではない場合に例外を投げます.
     *
     * @param value チェックする値
     */
    public static void throwIfNotMailAddress(String value) {
        if (StringUtils.isNotEmpty(value) && !GenericValidator.isEmail(value)) {
            throw new ApplicationException(ErrorCode.PTZ_999802);
        }
    }

    public static void throwIfNotIdentify(String value) {
        if (StringUtils.isEmpty(value) || value.length() > ID.MAX_LENGTH) {
            throw new IllegalArgumentException("Illegal identify. id=[" + value + "]");
        }
    }

    public static void throwIfNotEqual(String value1, String value2) {
        if (!StringUtils.equals(value1, value2)) {
            throw new ResourceNotFoundException();
        }
    }
}
