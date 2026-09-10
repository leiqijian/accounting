package com.liquido.base.pojo.vo;

import java.util.List;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BatchCreatePayoutPaymentConfigVo {

    @NotEmpty
    List<CreatePayoutPaymentConfigVo> configItems;

}
