package org.majimena.petz.web.utils;

import org.apache.commons.lang3.StringUtils;
import org.majimena.petz.common.exceptions.ResourceNotFoundException;
import org.majimena.petz.datatype.defs.ID;
import org.majimena.petz.domain.errors.ErrorCode;
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

    public static void rejectValue(String field, ErrorCode code, Errors errors) {
        errors.rejectValue(field, code.name());
    }

    public static void rejectIfNull(String field, Object value, Errors errors) {
        if (value == null) {
            errors.rejectValue(field, "errors.required");
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
    public static void throwIfHaveErrors(BindingResult errors) throws BindException {
        if (errors != null && errors.hasErrors()) {
            throw new BindException(errors);
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
