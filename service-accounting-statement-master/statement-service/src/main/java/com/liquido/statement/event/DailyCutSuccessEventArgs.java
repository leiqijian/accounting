package com.liquido.statement.event;

import java.io.Serializable;
import java.util.List;

import com.liquido.statement.pojo.bo.DailyCutSuccessBo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DailyCut Success Event Args
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyCutSuccessEventArgs implements Serializable {
    private static final long serialVersionUID = 1L;

    private Boolean autoPayment;

    private List<DailyCutSuccessBo> billList;

}
