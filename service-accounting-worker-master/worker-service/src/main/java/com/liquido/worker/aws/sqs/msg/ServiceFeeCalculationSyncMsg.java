package com.liquido.worker.aws.sqs.msg;

import java.util.List;

import com.liquido.worker.pojo.dto.DwSyncTransactionDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceFeeCalculationSyncMsg {

    private Long syncId;

    private List<DwSyncTransactionDto> bos;

}
