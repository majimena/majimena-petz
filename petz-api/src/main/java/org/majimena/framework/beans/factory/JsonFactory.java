package org.majimena.framework.beans.factory;

/**
 * Created by k.todoroki on 2015/08/08.
 */
public interface JsonFactory {

    <T> String to(T object);

    <T> T from(String json, Class<T> clazz);

}
