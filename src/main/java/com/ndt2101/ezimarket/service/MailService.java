package com.ndt2101.ezimarket.service;

import javax.mail.MessagingException;

public interface MailService {
    void send(String subject, String content, String sendTo, Boolean isHtmlFormat) throws MessagingException;
}
