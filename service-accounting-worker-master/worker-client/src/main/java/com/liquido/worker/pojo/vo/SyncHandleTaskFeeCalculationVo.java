package com.liquido.worker.pojo.vo;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotEmpty;

import com.liquido.worker.pojo.dto.DwSyncTransactionDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncHandleTaskFeeCalculationVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty
    private List<DwSyncTransactionDto> bos;

}
