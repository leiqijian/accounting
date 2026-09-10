package com.liquido.base.common.comparator;

import java.util.Comparator;
import java.util.Map;

import lombok.Data;

@Data
public class FeeCodeComparator implements Comparator<String> {

    private final Map<String, Integer> feeCodeWeights;

    public FeeCodeComparator(final Map<String, Integer> feeCodeWeights) {
        this.feeCodeWeights = feeCodeWeights;
    }

    @Override
    public int compare(final String o1, final String o2) {
        final int o1Weight = feeCodeWeights.getOrDefault(o1, Integer.MAX_VALUE);
        final int o2Weight = feeCodeWeights.getOrDefault(o2, Integer.MAX_VALUE);
        if (o1Weight != o2Weight) {
            return Integer.compare(o1Weight, o2Weight);
        } else {
            return o1.compareTo(o2);
        }
    }

}
