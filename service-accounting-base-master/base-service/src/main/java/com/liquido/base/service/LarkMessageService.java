package com.liquido.base.service;

import java.util.Collection;

import com.liquido.base.pojo.vo.lark.LarkBathMessageVo;
import com.liquido.base.pojo.vo.lark.LarkMessageVo;

public interface LarkMessageService {
    void sendLarkMessage(final LarkMessageVo larkMessageVo);

    void bathSendLarkMessage(final LarkBathMessageVo larkBathMessageVo);

    void bathSendListLarkMessage(final Collection<LarkBathMessageVo> list);
}
