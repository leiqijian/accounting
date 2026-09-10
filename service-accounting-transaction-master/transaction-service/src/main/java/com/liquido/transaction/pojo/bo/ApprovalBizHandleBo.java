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
public class ApprovalBizHandleBo<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean handleFlag;

    private T successStatus;

    private T failStatus;

    private String message;

    public static <T> ApprovalBizHandleBo<T> success() {
        return ApprovalBizHandleBo.<T>builder().handleFlag(true).build();
    }


    public static <T> ApprovalBizHandleBo<T> success(final T status) {
        ApprovalBizHandleBo<T> bo = success();
        bo.setSuccessStatus(status);
        return bo;
    }

    public static <T> ApprovalBizHandleBo<T> error(final T status, final String errorMessage) {
        return ApprovalBizHandleBo.<T>builder()
                .handleFlag(false)
                .failStatus(status)
                .message(errorMessage).build();
    }
}
