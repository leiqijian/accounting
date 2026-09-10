package com.liquido.base.api;

import javax.validation.Valid;

import com.liquido.base.pojo.vo.EmailAttachmentMessageVo;
import com.liquido.base.pojo.vo.EmailMessageVo;
import com.liquido.core.mvc.dto.ResponseDto;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface EmailApi {

    @PostMapping("/base/email/plain-text-message/send")
    ResponseDto<String> sendPlainTextMessage(@Valid @RequestBody EmailMessageVo vo);

    @PostMapping("/base/email/html-text-message/send")
    ResponseDto<String> sendHtmlTextMessage(@Valid @RequestBody EmailMessageVo vo);

    @PostMapping("/base/email/with-attachment-message/send")
    ResponseDto<String> sendWithAttachmentMessage(@Valid @RequestBody EmailAttachmentMessageVo vo);

}
