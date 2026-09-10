package com.liquido.worker.aws.sqs.msg;

import java.util.List;

import com.liquido.worker.pojo.dto.DwSyncPaymentLinkDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServicePaymentLinkSyncMsg {

    private List<DwSyncPaymentLinkDto> dtoList;

}
