package com.liquido.transaction.pojo.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Convert;

import com.liquido.transaction.enums.SystemMessageTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementDto implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Primary key
     */
    private Long id;

    /**
     * FK key
     */
    private Long messageId;

    private String title;

    private String content;

    @Convert(converter = SystemMessageTypeEnum.Convert.class)
    private SystemMessageTypeEnum messageType;

    private LocalDateTime publishTime;

    private Long createdBy;

}
