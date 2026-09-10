package com.liquido.base.controller.lark;

import java.util.Collection;
import javax.validation.Valid;

import com.liquido.base.api.LarkMessageApi;
import com.liquido.base.pojo.vo.lark.LarkBathMessageVo;
import com.liquido.base.pojo.vo.lark.LarkMessageVo;
import com.liquido.base.service.LarkMessageService;
import com.liquido.core.mvc.dto.ResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class LarkMessageController implements LarkMessageApi {

    private final LarkMessageService larkMessageService;

    @Override
    @PostMapping("/base/lark/message/send")
    public ResponseDto<Void> sendLarkMessage(@Valid @RequestBody final LarkMessageVo vo) {
        larkMessageService.sendLarkMessage(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/base/lark/message/bath-send")
    public ResponseDto<Void> bathSendLarkMessage(@Valid @RequestBody final LarkBathMessageVo vo) {
        larkMessageService.bathSendLarkMessage(vo);
        return ResponseDto.success();
    }

    @Override
    @PostMapping("/base/lark/list-message/bath-send")
    public ResponseDto<Void> bathSendListLarkMessage(
            @RequestBody final Collection<LarkBathMessageVo> list) {
        larkMessageService.bathSendListLarkMessage(list);
        return ResponseDto.success();
    }

}
