package com.liquido.base.pojo.vo.lark;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.LarkRemindRankEnum;
import com.liquido.core.common.logger.SensitiveField;
import com.liquido.core.common.logger.SensitiveType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LarkMessageVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String receiveId;

    @NotNull
    private LarkRemindRankEnum remindRank;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String remark;

    @SensitiveField(SensitiveType.PASSWORD)
    private String appId;

    @SensitiveField(SensitiveType.PASSWORD)
    private String appSecret;
}
