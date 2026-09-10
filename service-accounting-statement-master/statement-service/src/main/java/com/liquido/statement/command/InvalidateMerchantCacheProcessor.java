/*
package com.liquido.statement.command;

import com.liquido.base.command.BizBusCommandTypeEnum;
import com.liquido.plugin.commandbus.context.BusCommand;
import com.liquido.plugin.commandbus.context.BusCommandType;
import com.liquido.plugin.commandbus.core.BusCommandProcessor;
import com.liquido.plugin.commandbus.exception.CommandProcessingException;
import com.liquido.statement.feign.BaseService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class InvalidateMerchantCacheProcessor implements BusCommandProcessor {

    private final BaseService baseService;

    @Override
    public BusCommandType getSupportCommandType() {
        return BizBusCommandTypeEnum.INVALIDATE_MERCHANT_CACHE;
    }

    @Override
    public void processCommand(final BusCommand busCommand) throws CommandProcessingException {
        baseService.cleanInnerMerchantCache();
    }
}
*/
