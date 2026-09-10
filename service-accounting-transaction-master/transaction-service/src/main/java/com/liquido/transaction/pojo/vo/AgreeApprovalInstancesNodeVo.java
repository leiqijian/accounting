package com.liquido.transaction.pojo.vo;

import java.io.Serializable;
import java.util.Map;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgreeApprovalInstancesNodeVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    private String approvalCode;

    @NotBlank
    private String instanceCode;

    private Map<String, String> formMap;

    private Object formData;

}
