package com.liquido.statement.pojo.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryAccountUploadTradeDataVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    private LocalDateTime localDateTime;

    @NotEmpty
    private List<Long> merchantIds;
}
