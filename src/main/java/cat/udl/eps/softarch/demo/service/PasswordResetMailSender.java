package cat.udl.eps.softarch.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class PasswordResetMailSender {
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetMailSender.class);
    private final JavaMailSender sender;
    private final String frontendUrl;
    private final String from;

    public PasswordResetMailSender(JavaMailSender sender,
            @Value("${password-reset.frontend-url}") String frontendUrl,
            @Value("${password-reset.from}") String from) {
        this.sender = sender;
        this.frontendUrl = frontendUrl;
        this.from = from;
    }

    @Async
    @TransactionalEventListener
    public void send(PasswordResetMail event) {
        try {
            String link = UriComponentsBuilder.fromUriString(frontendUrl)
                    .queryParam("token", event.token()).build().toUriString();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(event.email());
            message.setSubject("Password recovery");
            message.setText("Use this link within one hour to reset your password: " + link);
            sender.send(message);
        } catch (RuntimeException exception) {
            logger.error("Unable to send password recovery email", exception);
        }
    }
}
