package cat.udl.eps.softarch.demo.steps;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@TestConfiguration
public class RecoveryTestConfiguration {
    @Bean
    @Primary
    MutableClock recoveryClock() {
        return new MutableClock();
    }

    @Bean
    @Primary
    TestMailbox recoveryMailbox() {
        return new TestMailbox();
    }

    static class MutableClock extends Clock {
        private volatile Instant now = Instant.parse("2026-01-01T00:00:00Z");
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return now; }
        void advanceSeconds(long seconds) { now = now.plusSeconds(seconds); }
    }

    static class TestMailbox extends JavaMailSenderImpl {
        final BlockingQueue<SimpleMailMessage> messages = new LinkedBlockingQueue<>();
        @Override public void send(SimpleMailMessage message) { messages.add(message); }
    }
}
