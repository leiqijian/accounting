package com.liquido.base.service.impl;


import java.io.File;
import java.util.Objects;
import javax.mail.internet.MimeMessage;

import com.liquido.base.pojo.vo.EmailAttachmentMessageVo;
import com.liquido.base.pojo.vo.EmailBaseVo;
import com.liquido.base.pojo.vo.EmailMessageVo;
import com.liquido.base.pojo.vo.FileBytesVo;
import com.liquido.base.service.EmailService;
import com.liquido.core.common.utils.BeanCopierUtil;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSenderImpl javaMailSender;

    @Value("${spring.mail.from}")
    private String mailFrom;

    @Async
    @Override
    @SneakyThrows
    public void sendPlainTextMessage(final EmailMessageVo vo) {
        JavaMailSender emailSender = getJavaMailSender(vo);
        final SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(vo.getTo().toArray(new String[0]));
        if (CollectionUtils.isNotEmpty(vo.getCc())) {
            message.setCc(vo.getCc().toArray(new String[0]));
        }

        message.setSubject(vo.getSubject());
        message.setText(vo.getContent());
        emailSender.send(message);
    }

    @Async
    @Override
    @SneakyThrows
    public void sendHtmlTextMessage(final EmailMessageVo vo) {
        JavaMailSender emailSender = getJavaMailSender(vo);
        final MimeMessage message = emailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");
        helper.setFrom(mailFrom);
        helper.setTo(vo.getTo().toArray(new String[0]));
        if (CollectionUtils.isNotEmpty(vo.getCc())) {
            helper.setCc(vo.getCc().toArray(new String[0]));
        }
        helper.setSubject(vo.getSubject());
        helper.setText(vo.getContent(), true);
        emailSender.send(message);
    }

    @Async
    @Override
    public void sendWithAttachmentMessage(final EmailAttachmentMessageVo vo) {
        JavaMailSender emailSender = getJavaMailSender(vo);
        final MimeMessage message = emailSender.createMimeMessage();
        try {
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(vo.getTo().toArray(new String[0]));
            if (CollectionUtils.isNotEmpty(vo.getCc())) {
                helper.setCc(vo.getCc().toArray(new String[0]));
            }

            helper.setSubject(vo.getSubject());
            helper.setText(vo.getContent(), vo.getHtml());
            // add attachments file
            if (ObjectUtils.isNotEmpty(vo.getPathToAttachment())) {
                for (final String pathToAttachment : vo.getPathToAttachment()) {
                    final FileSystemResource file =
                            new FileSystemResource(new File(pathToAttachment));
                    helper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
                }
            }
            // add attachment byte
            if (ObjectUtils.isNotEmpty(vo.getFileBytesList())) {
                for (final FileBytesVo fileBytesVo : vo.getFileBytesList()) {
                    helper.addAttachment(fileBytesVo.getFileName(),
                            new ByteArrayResource(fileBytesVo.getBytes()));
                }
            }
            emailSender.send(message);
        } catch (Exception e) {
            log.error("send attachment message fail:", e);
        }
    }


    private JavaMailSender getJavaMailSender(final EmailBaseVo vo) {
        if (Objects.isNull(vo)
                || (Objects.isNull(vo.getMailHost()) && Objects.isNull(vo.getMailPort())
                && Objects.isNull(vo.getMailUsername())
                && Objects.isNull(vo.getMailPassword()))) {
            return javaMailSender;
        }

        final JavaMailSenderImpl customMailSender = new JavaMailSenderImpl();
        BeanCopierUtil.copyProperties(javaMailSender, customMailSender);

        if (StringUtils.isNotBlank(vo.getMailHost())) {
            customMailSender.setHost(vo.getMailHost());
        }
        if (Objects.nonNull(vo.getMailPort()) && vo.getMailPort() > 0) {
            customMailSender.setPort(vo.getMailPort());
        }
        if (StringUtils.isNotBlank(vo.getMailUsername())) {
            customMailSender.setUsername(vo.getMailUsername());
        }
        if (StringUtils.isNotBlank(vo.getMailPassword())) {
            customMailSender.setPassword(vo.getMailPassword());
        }
        return customMailSender;
    }
}
