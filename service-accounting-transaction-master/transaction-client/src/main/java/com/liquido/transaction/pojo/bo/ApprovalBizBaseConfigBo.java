package com.liquido.transaction.pojo.bo;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalBizBaseConfigBo {

    private List<ApprovalCostConfigBo> costConfig;

}
