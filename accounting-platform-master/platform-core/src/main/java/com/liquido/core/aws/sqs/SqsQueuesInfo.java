package com.liquido.core.aws.sqs;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SqsQueuesInfo {

    private String name;

    private String url;

    private Integer handlerThreadPoolSize;

    private Integer handlerQueueSize;

    private Integer pollThreads;

    private Integer pollWaitTime;

    private Integer pollDelay;

}
