package org.majimena.petical.common.utils;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by todoken on 2015/08/02.
 */
public class BeanFactoryUtils {

    public static <F, T> T copyNonNullProperties(F orig, T dest) {
        return copyNonNullProperties(orig, dest, new String[]{});
    }

    public static <F, T> T copyNonNullProperties(F orig, T dest, String... excludes) {
        PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(orig);
        for (PropertyDescriptor descriptor : descriptors) {
            String name = descriptor.getName();
            boolean isExcludeName = false;
            for (String exclude : excludes) {
                if (StringUtils.equals(name, exclude)) {
                    isExcludeName = true;
                    break;
                }
            }
            if ("class".equals(name) || isExcludeName) {
                continue;
            }
            copyNonNullProperty(orig, dest, name);
        }
        return dest;
    }

    public static <F, T> void copyNonNullProperty(F orig, T dest, String name) {
        if (PropertyUtils.isReadable(orig, name) && PropertyUtils.isWriteable(orig, name)) {
            try {
                Object value = PropertyUtils.getSimpleProperty(orig, name);
                if (value != null) {
//                    if (value instanceof String && StringUtils.isEmpty((String) value)) {
//                        return;
//                    }
                    BeanUtils.copyProperty(dest, name, value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
