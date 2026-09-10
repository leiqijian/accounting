//package com.liquido.base.command;
//
//import com.liquido.plugin.commandbus.context.EnumBusCommandType;
//
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//
//@Getter
//@AllArgsConstructor
//public enum BizBusCommandTypeEnum implements EnumBusCommandType {
//
//    /**
//     * invalidate merchant onboarding biz cache.
//     */
//    INVALIDATE_MERCHANT_CACHE("INVALIDATE_MERCHANT_CACHE"),
//    ;
//
//    private final String code;
//
//    @Override
//    public Class<? extends Enum<? extends EnumBusCommandType>> getEnumClassType() {
//        return BizBusCommandTypeEnum.class;
//    }
//
//}
