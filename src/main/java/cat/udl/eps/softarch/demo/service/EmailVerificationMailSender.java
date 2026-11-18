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

/** Sends the verification link (US-AUT-07) once the transaction that issued it has committed. */
@Component
public class EmailVerificationMailSender {
    static final String SUBJECT = "Verify your email address";

    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationMailSender.class);
    private final JavaMailSender sender;
    private final String frontendUrl;
    private final String from;

    public EmailVerificationMailSender(JavaMailSender sender,
            @Value("${email-verification.frontend-url}") String frontendUrl,
            @Value("${email-verification.from}") String from) {
        this.sender = sender;
        this.frontendUrl = frontendUrl;
        this.from = from;
    }

    @Async
    @TransactionalEventListener
    public void send(EmailVerificationMail event) {
        try {
            String link = UriComponentsBuilder.fromUriString(frontendUrl)
                    .queryParam("token", event.token()).build().toUriString();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(event.email());
            message.setSubject(SUBJECT);
            message.setText("Use this link within 24 hours to verify your email address: " + link
                    + "\n\nIf it expires, you can request a new one from the login page.");
            sender.send(message);
        } catch (RuntimeException exception) {
            logger.error("Unable to send email verification", exception);
        }
    }
}
