package com.example.ttcs_jobwebsite.service;

import com.example.ttcs_jobwebsite.dto.email.EmailDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String emailSender;

    public void sendEmail(EmailDetails emailDetails){
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(emailSender);
            mailMessage.setTo(emailDetails.getRecipient());
            mailMessage.setText(emailDetails.getMessageBody());
            mailMessage.setSubject(emailDetails.getSubject());
            javaMailSender.send(mailMessage);
            log.info("Mail sent successfully");
        }catch (MailException e){
            log.debug("Failure occurred while sending email");
        }
    }
}
