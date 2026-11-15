package cat.udl.eps.softarch.demo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PasswordChangedMailSender {
    private static final Logger logger = LoggerFactory.getLogger(PasswordChangedMailSender.class);
    private final JavaMailSender sender;
    private final String from;

    public PasswordChangedMailSender(JavaMailSender sender, @Value("${password-reset.from}") String from) {
        this.sender = sender;
        this.from = from;
    }

    @Async
    @TransactionalEventListener
    public void send(PasswordChangedMail event) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(event.email());
            message.setSubject("Password changed");
            message.setText("Your account password has been changed.");
            sender.send(message);
        } catch (RuntimeException exception) {
            logger.error("Unable to send password change email", exception);
        }
    }
}
