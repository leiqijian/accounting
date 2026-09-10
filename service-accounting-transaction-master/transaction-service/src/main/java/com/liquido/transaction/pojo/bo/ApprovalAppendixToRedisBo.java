package com.liquido.transaction.pojo.bo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalAppendixToRedisBo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String temporaryFileUrl;

    private String fileName;
}
