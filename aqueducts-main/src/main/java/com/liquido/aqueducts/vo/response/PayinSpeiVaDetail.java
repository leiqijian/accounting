package com.liquido.aqueducts.vo.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PayinSpeiVaDetail extends PayinDefaultDetail {
    private SubAccountInfo subAccount;
    private ClabeAccountTransferDetail transferDetail;
}
