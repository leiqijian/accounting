//package com.liquido.worker.command;
//
//import com.liquido.base.command.BizBusCommandTypeEnum;
//import com.liquido.core.common.cache.RedisCacheUtil;
//import com.liquido.plugin.commandbus.context.BusCommand;
//import com.liquido.plugin.commandbus.context.BusCommandType;
//import com.liquido.plugin.commandbus.core.BusCommandProcessor;
//import com.liquido.plugin.commandbus.util.ServiceNameHolder;
//import com.liquido.worker.common.Constant;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class MerchantCacheInvalidator implements BusCommandProcessor {
//
//    private final RedisCacheUtil redisCacheUtil;
//
//    @Override
//    public BusCommandType getSupportCommandType() {
//        return BizBusCommandTypeEnum.INVALIDATE_MERCHANT_CACHE;
//    }
//
//    @Override
//    public void processCommand(final BusCommand busCommand) {
//        log.info("invalidating merchant cache of {}", ServiceNameHolder.getCurrentServiceName());
//
//        redisCacheUtil.deleteObject(Constant.CACHE.MERCHANT_INFO_HASH);
//
//    }
//}
