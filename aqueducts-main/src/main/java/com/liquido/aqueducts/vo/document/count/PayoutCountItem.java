package com.liquido.aqueducts.vo.document.count;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PayoutCountItem {

    private TransactionCountGroupId _id;
    private int count;

}
