package com.liquido.aqueducts.vo.document.metric;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PayinMetricItem {

    private TransactionMetricGroupId _id;
    private long count;
    private long sumAmount;

}
