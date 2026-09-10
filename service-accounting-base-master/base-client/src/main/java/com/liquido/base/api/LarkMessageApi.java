package com.liquido.base.api;

import java.util.Collection;
import javax.validation.Valid;

import com.liquido.base.pojo.vo.lark.LarkBathMessageVo;
import com.liquido.base.pojo.vo.lark.LarkMessageVo;
import com.liquido.core.mvc.dto.ResponseDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface LarkMessageApi {

    @PostMapping("/base/lark/message/send")
    ResponseDto<Void> sendLarkMessage(@Valid @RequestBody final LarkMessageVo vo);

    @PostMapping("/base/lark/message/bath-send")
    ResponseDto<Void> bathSendLarkMessage(@Valid @RequestBody final LarkBathMessageVo vo);


    @PostMapping("/base/lark/list-message/bath-send")
    ResponseDto<Void> bathSendListLarkMessage(final Collection<LarkBathMessageVo> vo);

}
