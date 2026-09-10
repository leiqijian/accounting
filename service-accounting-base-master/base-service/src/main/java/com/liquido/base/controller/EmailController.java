package com.liquido.base.controller;


import javax.validation.Valid;

import com.liquido.base.api.EmailApi;
import com.liquido.base.pojo.vo.EmailAttachmentMessageVo;
import com.liquido.base.pojo.vo.EmailMessageVo;
import com.liquido.base.service.EmailService;
import com.liquido.core.mvc.dto.ResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class EmailController implements EmailApi {

    private final EmailService emailService;

    @Override
    @PostMapping("/base/email/plain-text-message/send")
    public ResponseDto<String> sendPlainTextMessage(@Valid @RequestBody final EmailMessageVo vo) {
        emailService.sendPlainTextMessage(vo);
        return ResponseDto.success("Email sent successfully");
    }

    @Override
    @PostMapping("/base/email/html-text-message/send")
    public ResponseDto<String> sendHtmlTextMessage(@Valid @RequestBody final EmailMessageVo vo) {
        emailService.sendHtmlTextMessage(vo);
        return ResponseDto.success("Email sent successfully");
    }

    @Override
    @PostMapping("/base/email/with-attachment-message/send")
    public ResponseDto<String> sendWithAttachmentMessage(
            @Valid @RequestBody final EmailAttachmentMessageVo vo) {
        emailService.sendWithAttachmentMessage(vo);
        return ResponseDto.success("Email sent successfully");
    }
}
