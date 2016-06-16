package org.majimena.petical.web.api;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Created by todoken on 2015/06/25.
 */
public abstract class AbstractValidator<T extends Serializable> implements Validator {

    private Class<T> beanClass;

    @SuppressWarnings("unchecked")
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

    protected void validate(Optional<T> target, Errors errors) {
        target.ifPresent(t -> validate(t, errors));
    }

    protected void validate(T target, Errors errors) {
    }
}
