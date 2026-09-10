package com.liquido.statement.aws.sqs.msg;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimerAutoTransferOutMsg implements Serializable {
    private static final long serialVersionUID = 1L;

    private String msg;

}
