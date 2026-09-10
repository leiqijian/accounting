package com.liquido.base.enums;

import java.util.Arrays;

import com.liquido.core.common.exception.CommonExceptionCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessStrategyEnum {

    /**====================PAY-IN==============================================================*/
    /**
     * formula: amount = +amount + (tradeAmount * fx) - fee - tax
     */
    PAY_IN_SETTLED(
            TransactionTypeCodeEnum.PAY_IN,
            DirectionTypeEnum.SETTLED,
            // transaction amount positive
            AmountPonEnum.POSITIVE,
            // fee or tax negative
            AmountPonEnum.NEGATIVE,
            0
    ),

    /**
     * formula: amount = +amount - (tradeAmount * fx) - fee - tax
     */
    PAY_IN_REFUND(
            TransactionTypeCodeEnum.PAY_IN,
            DirectionTypeEnum.REFUND,
            // transaction amount negative
            AmountPonEnum.NEGATIVE,
            // fee or tax negative
            AmountPonEnum.NEGATIVE,
            1
    ),

    /**
     * formula: amount = +amount - (tradeAmount * fx) - fee - tax
     */
    PAY_IN_CHARGE_BACK(
            TransactionTypeCodeEnum.PAY_IN,
            DirectionTypeEnum.CHARGE_BACK,
            // transaction amount negative
            AmountPonEnum.NEGATIVE,
            // fee or tax negative
            AmountPonEnum.NEGATIVE,
            2
    ),

    /**
     * formula: amount = +amount + fee + tax
     */
    PAY_IN_CHARGE_BACK_REJECTED(
            TransactionTypeCodeEnum.PAY_IN,
            DirectionTypeEnum.CHARGE_BACK_REJECTED,
            // transaction amount positive
            AmountPonEnum.POSITIVE,
            // fee or tax positive
            AmountPonEnum.POSITIVE,
            3
    ),


    /**====================PAY-OUT==============================================================*/
    /**
     * formula: amount = -amount - (tradeAmount * fx) - fee - tax
     */
    PAY_OUT_SETTLED(
            TransactionTypeCodeEnum.PAY_OUT,
            DirectionTypeEnum.SETTLED,
            // transaction amount negative
            AmountPonEnum.NEGATIVE,
            // fee or tax negative
            AmountPonEnum.NEGATIVE,
            0
    ),

    /**
     * formula: amount = -amount + (tradeAmount * fx) + fee + tax
     * scene: create a new refund order
     */
    PAY_OUT_REFUND(
            TransactionTypeCodeEnum.PAY_OUT,
            DirectionTypeEnum.REFUND,
            // transaction amount positive
            AmountPonEnum.POSITIVE,
            // fee or tax positive
            AmountPonEnum.POSITIVE,
            1
    ),

    /**
     * formula: amount = -amount + (tradeAmount * fx) + fee + tax
     * scene: Refund of original transaction order
     */
    PAY_OUT_REJECTED(
            TransactionTypeCodeEnum.PAY_OUT,
            DirectionTypeEnum.REJECTED,
            // transaction amount positive
            AmountPonEnum.POSITIVE,
            // fee or tax positive
            AmountPonEnum.POSITIVE,
            2
    ),

    /**
     * formula: amount = -fee - tax
     * scene: co payout rejected & No principal, only fees and tax which fee on fee.
     */
    PAY_OUT_REJECTED_DEBIT(
            TransactionTypeCodeEnum.PAY_OUT,
            DirectionTypeEnum.REJECTED_DEBIT,
            // transaction amount positive
            AmountPonEnum.NEGATIVE,
            // fee or tax positive
            AmountPonEnum.NEGATIVE,
            0
    ),


    /**====================MARKET_PLACE=========================================================*/
    /**
     * formula: amount = -amount - (tradeAmount * fx) - fee - tax
     */
    MARKET_PLACE_SETTLED(
            TransactionTypeCodeEnum.MARKET_PLACE_ORDERS,
            DirectionTypeEnum.SETTLED,
            // transaction amount negative
            AmountPonEnum.NEGATIVE,
            // fee or tax negative
            AmountPonEnum.NEGATIVE,
            0
    ),

    /**
     * formula: amount = -amount + (tradeAmount * fx) + fee + tax
     */
    MARKET_PLACE_REFUND(
            TransactionTypeCodeEnum.MARKET_PLACE_ORDERS,
            DirectionTypeEnum.REFUND,
            // transaction amount positive
            AmountPonEnum.POSITIVE,
            // fee or tax  positive
            AmountPonEnum.POSITIVE,
            1
    ),

    ;

    private final TransactionTypeCodeEnum transactionType;
    private final DirectionTypeEnum directionType;
    private final AmountPonEnum amountPon;
    private final AmountPonEnum feePon;

    /**
     * execution priority,
     * The smaller the value of the same transaction type(PAY-IN/PAY-OUT), the higher the priority.
     */
    private final int priority;

    public static BusinessStrategyEnum parse(final String name) {

        return Arrays.stream(BusinessStrategyEnum.values())
                .filter(tmp -> tmp.name().equals(name))
                .findFirst()
                .orElse(null);
    }

    public static BusinessStrategyEnum parse(final TransactionTypeCodeEnum transactionType,
                                             final DirectionTypeEnum directionType) {
        return Arrays.stream(BusinessStrategyEnum.values())
                .filter(item -> item.getTransactionType() == transactionType
                        && item.getDirectionType() == directionType)
                .findFirst()
                .orElseThrow(() -> CommonExceptionCode.ENUM_PARSE_ERROR.exception(String.format(
                        "transactionType=%s, directionType=%s", transactionType, directionType)));
    }

    public static BusinessStrategyEnum parse(final String transactionType,
                                             final String directionType) {
        return Arrays.stream(BusinessStrategyEnum.values())
                .filter(item -> item.getTransactionType().getCode().equals(transactionType)
                        && item.getDirectionType().getCode().equals(directionType))
                .findFirst()
                .orElseThrow(() -> CommonExceptionCode.ENUM_PARSE_ERROR.exception(String.format(
                        "transactionType=%s, directionType=%s", transactionType, directionType)));
    }
}
