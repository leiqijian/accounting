package com.liquido.transaction.pojo.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Convert;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.liquido.base.enums.PlatformEnum;
import com.liquido.transaction.enums.DeliveryTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMessageInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    /**
     * Notification channels, multiple selections such as email, system, SMS, etc.
     */
    @NotNull
    @Convert(converter = DeliveryTypeEnum.Convert.class)
    private DeliveryTypeEnum deliveryType;

    private MessageExtendDataVo extendData;

    @NotEmpty
    private List<ReceiverInfoVo> receiverInfos;

    @NotNull
    @Convert(converter = PlatformEnum.Convert.class)
    private PlatformEnum platform;

    private LocalDateTime publishTime;

    private LocalDateTime expireTime;

    /**
     * message create user id
     */
    private Long createUserId;

    private String remark;

}
