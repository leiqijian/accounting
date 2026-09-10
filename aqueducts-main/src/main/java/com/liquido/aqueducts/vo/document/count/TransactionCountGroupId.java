package com.liquido.aqueducts.vo.document.count;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TransactionCountGroupId {
    private String country;
    private String merchant;
    private String product;
}
