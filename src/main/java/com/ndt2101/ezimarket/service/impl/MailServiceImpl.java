package com.ndt2101.ezimarket.service.impl;

import com.ndt2101.ezimarket.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void send(String subject, String content, String sendTo, Boolean isHtmlFormat) throws MessagingException {
        if (isHtmlFormat == null) {
            isHtmlFormat = false;
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setSubject(subject);
        helper.setText(content, isHtmlFormat);
        helper.setTo(sendTo);

        mailSender.send(mimeMessage);
    }
}
