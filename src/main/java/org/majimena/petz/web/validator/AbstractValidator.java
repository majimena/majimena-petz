package org.majimena.petz.web.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Created by todoken on 2015/06/25.
 */
public abstract class AbstractValidator<T> implements Validator {

    private Class<T> beanClass;

    public AbstractValidator() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            this.beanClass = (Class<T>) pt.getActualTypeArguments()[0];
        } else {
            throw new IllegalArgumentException("Generics is not found.");
        }
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return beanClass.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (errors.hasErrors()) {
            return;
        }
        validate(Optional.ofNullable((T) target), errors);
    }

    protected abstract void validate(Optional<T> target, Errors errors);

}
