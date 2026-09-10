package com.liquido.base.service;

import com.liquido.base.pojo.vo.EmailAttachmentMessageVo;
import com.liquido.base.pojo.vo.EmailMessageVo;

public interface EmailService {

    void sendPlainTextMessage(EmailMessageVo vo);

    void sendHtmlTextMessage(EmailMessageVo vo);

    void sendWithAttachmentMessage(EmailAttachmentMessageVo vo);
}
