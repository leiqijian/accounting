package com.liquido.statement.service;

public class GrowthRateTest {

    public static void main(String[] args) {
        System.out.println(growthRatePow1(5000.0, 0.25, 10));
        System.out.println(growthRatePow2(5000.0, 0.25, 10));


        System.out.println(String.format("%03d", 10));
    }

    /**
     * @param init 初始值
     * @param rate 增速率
     * @param n    第n年后
     * @return
     */
    private static double growthRatePow1(double init, double rate, int n) {
        return init * Math.pow((1 + rate), n);
    }

    /**
     * @param init 初始值
     * @param rate 增速率
     * @param n    第n年后
     * @return
     */
    private static double growthRatePow2(double init, double rate, int n) {
        double value = init;
        for (int i = 0; i < n; i++) {
            value = value * (1 + rate);
        }
        return value;
    }

}
