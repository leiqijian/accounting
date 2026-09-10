package com.liquido.base.constant.dynamic;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.AttributeConverter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.StringUtils;

/**
 * This is the common base class of all dynamic constant types. Subclass can
 * declare its own constants that must be public, static, final, and must define
 * an {@link AttributeConverter} for {@link DynamicConstantSerializer}
 * {@link DynamicConstantDeserializer}.
 *
 * <PRE>
 * {@code
 * public class Example extends DynamicConstant<String> {
 * <p>
 * public static final Example EXAMPLE = new Example("EXAMPLE");
 * <p>
 * public static class Converter implements AttributeConverter<Example, String> {
 * ...
 * }
 * }
 * }
 * </PRE>
 *
 * @param <T> the code type
 */
@JsonSerialize(using = DynamicConstantSerializer.class)
@JsonDeserialize(using = DynamicConstantDeserializer.class)
public abstract class DynamicConstant<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private final T code;

    protected DynamicConstant(final T code) {
        this.code = Objects.requireNonNull(code);
    }

    /**
     * Find the constant definition according to the code, and then
     * convert to constant instance
     *
     * @param constantClass constant class
     * @param code          code value
     *
     * @return the constant instance, return null if definition not found
     */
    public static <C extends DynamicConstant<T>, T> C parse(
            final Class<C> constantClass,
            final T code) {
        return DynamicConstantSupport.parse(constantClass, code);
    }

    /**
     * Find the constant definition according to the code, if not found,
     * register the definition, and then convert to constant instance
     *
     * @param constantClass constant class
     * @param code          code value
     * @param name          constant definition identifier
     * @param properties    instance attribute values defined in constantClass
     *                      in the order of declaration
     *
     * @return the constant instance
     */
    public static <C extends DynamicConstant<T>, T> C parse(
            final Class<C> constantClass,
            final T code,
            final String name,
            final Object... properties) {
        return DynamicConstantSupport.parse(constantClass, code, name, properties);
    }

    public static <C extends DynamicConstant<String>> C parseIfNotBlank(
            final Class<C> constantClass,
            final String code) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return parse(constantClass, code);
    }

    public static <C extends DynamicConstant<String>> C parseIfNotBlank(
            final Class<C> constantClass,
            final String code,
            final String name,
            final Object... properties) {
        if (StringUtils.isBlank(code)) {
            return null;
        }
        return parse(constantClass, code, name, properties);
    }

    public static <C extends DynamicConstant<T>, T> List<C> values(final Class<C> clazz) {
        return DynamicConstantSupport.values(clazz);
    }

    public final T getCode() {
        return code;
    }

    @Override
    public final boolean equals(final Object obj) {
        return this == obj;
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return code.toString();
    }
}
