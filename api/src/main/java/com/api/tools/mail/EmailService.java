package com.api.tools.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EmailService {
    private final Environment env;
    private final JavaMailSender emailSender;

    @Autowired
    public EmailService(Environment env, JavaMailSender emailSender) {
        this.env = env;
        this.emailSender = emailSender;
    }
    public boolean sendSimpleMail(String to, String subject, String text) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(env.getProperty("email.from", String.class, "support@cash-manager.live"));
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

