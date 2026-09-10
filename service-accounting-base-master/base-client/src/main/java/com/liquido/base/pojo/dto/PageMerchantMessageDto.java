package com.liquido.base.pojo.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Convert;

import com.liquido.base.enums.MessageSourceTypeEnum;
import com.liquido.base.enums.MessageTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageMerchantMessageDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    @Convert(converter = MessageSourceTypeEnum.Convert.class)
    private MessageSourceTypeEnum messageSourceType;

    @Convert(converter = MessageTypeEnum.Convert.class)
    private MessageTypeEnum messageType;

    private String message;

    private LocalDateTime eventTime;

}
