package com.liquido.core.common.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import sun.misc.Unsafe;

public class DynamicEnumUtil {

    public static <T extends Enum<?>> T addEnum(final Class<T> enumClass,
                                                final String name) throws Exception {
        return addEnum(enumClass, name, Map.of());
    }

    /**
     * add a new enum in the enumClass
     *
     * @param enumClass the enum class
     * @param name      the enum name
     * @param args      all instance field value order by declare
     */
    public static <T extends Enum<?>> T addEnum(final Class<T> enumClass,
                                                final String name,
                                                final Object... args) throws Exception {
        final Field[] instanceFields = Arrays.stream(enumClass.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .toArray(Field[]::new);
        final Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < instanceFields.length; i++) {
            final Field field = instanceFields[i];
            map.put(field.getName(), args[i]);
        }

        return addEnum(enumClass, name, map);
    }

    /**
     * add a new enum in the enumClass
     *
     * @param enumClass the enum class
     * @param name      the enum name
     * @param args      field name - value map
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<?>> T addEnum(final Class<T> enumClass,
                                                final String name,
                                                final Map<String, Object> args) throws Exception {
        final Constructor<?> constructor = Unsafe.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        final Unsafe unsafe = (Unsafe) constructor.newInstance();
        final T enumValue = (T) unsafe.allocateInstance(enumClass);

        final Field ordinalField = Enum.class.getDeclaredField("ordinal");
        makeAccessible(ordinalField);
        final Enum<?>[] values = (Enum<?>[]) enumClass.getMethod("values").invoke(null);
        ordinalField.setInt(enumValue, values.length);

        final Field nameField = Enum.class.getDeclaredField("name");
        makeAccessible(nameField);
        nameField.set(enumValue, name);

        for (final Map.Entry<String, Object> entry : args.entrySet()) {
            final Field field = enumClass.getDeclaredField(entry.getKey());
            makeAccessible(field);
            field.set(enumValue, entry.getValue());
        }

        registerValue(enumValue);

        return enumValue;
    }

    private static void registerValue(final Enum<?> enumValue) throws Exception {
        final Field valuesField = enumValue.getClass().getDeclaredField("$VALUES");
        makeAccessible(valuesField);
        final Enum<?>[] oldValues = (Enum<?>[]) valuesField.get(null);
        final Object newValues = Array.newInstance(enumValue.getClass(), oldValues.length + 1);
        for (int i = 0; i < oldValues.length; i++) {
            Array.set(newValues, i, oldValues[i]);
        }
        Array.set(newValues, oldValues.length, enumValue);
        valuesField.set(null, newValues);

        final Field enumConstantsField = Class.class.getDeclaredField("enumConstants");
        makeAccessible(enumConstantsField);
        enumConstantsField.set(enumValue.getClass(), null);

        final Field enumConstantDirectory = Class.class.getDeclaredField("enumConstantDirectory");
        makeAccessible(enumConstantDirectory);
        enumConstantDirectory.set(enumValue.getClass(), null);
    }

    private static void makeAccessible(final Field field) throws Exception {
        field.setAccessible(true);
        final Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
    }
}
