package org.crud.data.utils;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Utilities.
 */
public final class EntityUtil {

    private EntityUtil() {}

    /**
     * Fills 'to' object fields by using 'from' object fields based
     * on matching of field's names. It uses reflection.
     * If name or type of a field are different, it must be handled
     * by custom way.
     *
     * @param from object 'from' which data is extracted
     * @param to object 'to' which data is filled
     * @param <R> type of object 'from'
     * @param <L> type of object 'to'
     * @return filled 'to' object
     */
    public static <R, L> L fillBlindly(R from, L to) {
        Objects.requireNonNull(from, "Argument 'from' must not be null");
        Objects.requireNonNull(to, "Argument 'to' must not be null");
        for (Field fromField : from.getClass().getDeclaredFields()) {
            try {
                String fromFieldName = fromField.getName();
                fromField.setAccessible(true);
                Object fromFieldValue = fromField.get(from);
                Field toField = getObjectFieldByName(to, fromFieldName);
                if (toField != null) {
                    toField.setAccessible(true);
                    toField.set(to, fromFieldValue);
                }
            } catch (Exception e) {
                // ignore this field and continue
            }
        }
        return to;
    }

    private static Field getObjectFieldByName(Object o, String fieldName) {
        for (Field field : o.getClass().getDeclaredFields()) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }
}
