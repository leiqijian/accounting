package com.liquido.statement.common.mail;

import java.util.List;
import java.util.Map;

import com.liquido.statement.feign.BaseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RefreshScope
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final BaseService baseService;

    @Value("#{'${statement.payment.payout.notify.mail-to:hua@liquido.cn}'.split(',')}")
    private List<String> mailTo;

    @Async
    @Override
    public void sendAutoPaymentNotifyMail(final String title, final Map<String, Object> params) {
        final String template =
                MailTemplate.getInstance().getMailTemplate(MailTemplate.AUTO_PAYMENT_NOTIFY);
        final StringSubstitutor sb = new StringSubstitutor(params);
        final String content = sb.replace(template);

        baseService.sendMail(title, mailTo, content);
    }
}
