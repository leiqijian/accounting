package com.liquido.worker.aws.sqs.msg;

import java.util.List;

import com.liquido.worker.pojo.dto.DwSyncShopifyDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceShopifySyncMsg {

    private List<DwSyncShopifyDto> dtoList;

}
