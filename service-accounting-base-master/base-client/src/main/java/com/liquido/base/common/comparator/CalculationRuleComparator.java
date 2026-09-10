package com.liquido.base.common.comparator;

import java.util.Comparator;
import java.util.Map;

import lombok.Data;

@Data
public class CalculationRuleComparator implements Comparator<Map<String, String>> {

    private final Map<String, Integer> weights;

    public CalculationRuleComparator(final Map<String, Integer> weights) {
        this.weights = weights;
    }

    @Override
    public int compare(final Map<String, String> map1, final Map<String, String> map2) {
        if (map1 == null && map2 == null) {
            return 0;
        } else if (map1 == null) {
            return -1;
        } else if (map2 == null) {
            return 1;
        }

        // Check if the two maps are equal
        if (map1.equals(map2)) {
            return 0;
        }

        // Record the last minimum weight
        int pw1 = 0;
        int pw2 = 0;

        while (true) {
            // Record the minimum weight for the current iteration
            int weight1 = Integer.MAX_VALUE;
            int weight2 = Integer.MAX_VALUE;

            String key1 = null;
            String key2 = null;

            // Find the smallest weight values that exist in both maps
            for (final String key : weights.keySet()) {
                if (map1.containsKey(key)) {
                    final int w1 = weights.get(key);
                    if (pw1 < w1 && w1 < weight1) {
                        key1 = key;
                        weight1 = w1;
                    }
                }
                if (map2.containsKey(key)) {
                    final int w2 = weights.get(key);
                    if (pw2 < w2 && w2 < weight2) {
                        key2 = key;
                        weight2 = w2;
                    }
                }
            }

            // Compare the smallest weight values
            if (weight1 != weight2) {
                if (weight1 == Integer.MAX_VALUE) {
                    weight1 = 0;
                }
                if (weight2 == Integer.MAX_VALUE) {
                    weight2 = 0;
                }
                return Integer.compare(weight1, weight2);  // Smaller weight comes first
            }

            // When the minimum weights are equal,
            // compare the values of the keys with the minimum weights
            final String value1 = map1.get(key1);
            final String value2 = map2.get(key2);
            if (!value1.equals(value2)) {
                return value1.compareTo(value2);
            }

            pw1 = weight1;
            pw2 = weight2;
        }
    }

}
