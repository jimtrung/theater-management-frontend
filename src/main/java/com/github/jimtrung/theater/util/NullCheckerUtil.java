package com.github.jimtrung.theater.util;

import java.lang.reflect.Field;

public class NullCheckerUtil {
    public static boolean hasNullField(Object obj) {
        try {
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);

                if (field.getName().equalsIgnoreCase("id")) {
                    continue;
                }

                Object value = field.get(obj);
                if (value == null) {
                    return true;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
}
