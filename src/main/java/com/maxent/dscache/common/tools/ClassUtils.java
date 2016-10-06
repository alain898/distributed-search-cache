package com.maxent.dscache.common.tools;

import com.google.common.base.Preconditions;

/**
 * Created by alain on 16/8/25.
 */
public class ClassUtils {
    public static <T> Class<T> loadClass(String name, Class<T> superClass) throws Exception {
        Preconditions.checkNotNull(name, "name is null");
        Preconditions.checkNotNull(superClass, "superClass is null");

        Class clazz = Class.forName(name);
        if (superClass.isAssignableFrom(clazz)) {
            return (Class<T>) clazz;
        } else {
            throw new RuntimeException(String.format(
                    "class[%s] is not subclass of superClass[%s]",
                    name, superClass));
        }
    }
}
