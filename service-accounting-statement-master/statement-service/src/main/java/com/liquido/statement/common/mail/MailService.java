package com.liquido.statement.common.mail;

import java.util.Map;

public interface MailService {

    void sendAutoPaymentNotifyMail(final String title, final Map<String, Object> params);

}
