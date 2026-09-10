package com.liquido.test.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * testNG测试对象
 *
 * @author qinqz
 * @date 2020-9-7 13:42:34
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestNgParams<T> {

    private String description;

    private T param;

    private int expectCode;

    public static <T> TestNgParams<T> newInstance(String description, T param, int expectCode) {
        return new TestNgParams<T>(description, param, expectCode);
    }
}
