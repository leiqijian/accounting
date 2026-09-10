package com.liquido.aqueducts.vo.document.metric;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class MarketPlaceMetricItem {

    private TransactionMetricGroupId _id;
    private long count;
    private List<String> amountList;

}
