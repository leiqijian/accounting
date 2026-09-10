package com.liquido.statement.service;

import java.util.Map;
import javax.annotation.Resource;

import com.liquido.core.common.utils.LocalDateTimeUtil;
import com.liquido.core.common.utils.LocalDateUtil;
import com.liquido.statement.common.AbstractTest;
import com.liquido.statement.common.mail.MailService;
import com.liquido.statement.common.mail.MailTemplate;

import com.google.common.collect.Maps;
import org.testng.annotations.Test;

public class TemplateTest extends AbstractTest {
    @Resource
    private MailService mailService;

    @Test(description = "templateTest")
    public void templateTest() throws Exception {
        String template = MailTemplate.getInstance()
                .getMailTemplate("auto-payment-mail-tpl.html");
        System.out.println(template);
    }

    @Test(description = "mailServiceTest")
    public void mailServiceTest() throws Exception {
        String mailTitle = "【" + "DIDI PAY SA DE CV" + "】资金归集测试邮件, "
                +
                LocalDateTimeUtil.nowUtc().toLocalDate()
                        .format(LocalDateUtil.FORMAT_DATE)
                + "(UTC)";

        Map<String, Object> params = Maps.newHashMap();
        params.put("transactionDate", LocalDateTimeUtil.nowUtc()
                .format(LocalDateTimeUtil.FORMAT_DATETIME));
        params.put("referenceNumber", "N123456");
        params.put("amount", 10086.5);
        params.put("totalFeeAmount", 2.35);
        params.put("netAmount", 10086.5);
        params.put("transferStatus", "SETTLED");
        params.put("targetName", "DIDI PAY SA DE CV");
        params.put("targetBankAccountId", "T100010");
        mailService.sendAutoPaymentNotifyMail(mailTitle, params);
    }

}
