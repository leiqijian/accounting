package com.liquido.worker.events;

import java.util.List;
import javax.persistence.Convert;

import com.liquido.base.enums.CountryCodeEnum;
import com.liquido.base.enums.TransactionTypeCodeEnum;
import com.liquido.worker.pojo.bo.TaskFeeCalculationSettleBo;
import com.liquido.worker.pojo.entity.TaskFeeCalculation;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public interface TaskFeeCalculationEvent {

    @Getter
    class FeePublishSqsEvent extends ApplicationEvent {

        private static final long serialVersionUID = 1L;

        private final TaskFeeCalculationSettleBo bo;

        public FeePublishSqsEvent(final TaskFeeCalculationSettleBo bo) {
            super(bo);
            this.bo = bo;
        }

    }

    @Getter
    class FeeRerunSecondCalculateEvent extends ApplicationEvent {

        private static final long serialVersionUID = 1L;

        private final String merchantCode;

        @Convert(converter = CountryCodeEnum.Convert.class)
        private final CountryCodeEnum countryCode;

        @Convert(converter = TransactionTypeCodeEnum.Convert.class)
        private final TransactionTypeCodeEnum transactionTypeCode;

        private final List<Long> secondCalculateIds;

        public FeeRerunSecondCalculateEvent(
                final String merchantCode, final CountryCodeEnum countryCode,
                final TransactionTypeCodeEnum transactionTypeCode,
                final List<Long> secondCalculateIds) {
            super(secondCalculateIds);
            this.merchantCode = merchantCode;
            this.countryCode = countryCode;
            this.transactionTypeCode = transactionTypeCode;
            this.secondCalculateIds = secondCalculateIds;
        }

    }

    @Getter
    class RefundPublishSqsEvent extends ApplicationEvent {

        private static final long serialVersionUID = 1L;

        private final TaskFeeCalculation data;

        public RefundPublishSqsEvent(final TaskFeeCalculation data) {
            super(data);
            this.data = data;
        }

    }

}
