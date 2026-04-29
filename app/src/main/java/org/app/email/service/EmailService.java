package org.app.email.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;


    @Async
    public void sendTemporaryPassword(String toEmail, String fullName, String tempPassword) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject("Ваш временный пароль — Общежитие");
            message.setText(
                    "Здравствуйте, " + fullName + "!\n\n"
                            + "Ваша учётная запись в приложении общежития создана.\n\n"
                            + "Ваш временный пароль: " + tempPassword + "\n\n"
                            + "При первом входе вам будет предложено сменить пароль.\n\n"
                            + "С уважением,\nАдминистрация приложения Pleasant Routine"
            );
            mailSender.send(message);
            log.info("Temporary password email sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage(), e);
        }
    }

    @Async
    public void sendPasswordChangedNotification(String toEmail, String fullName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject("Пароль успешно изменён — Общежитие");
            message.setText(
                    "Здравствуйте, " + fullName + "!\n\n"
                            + "Ваш пароль был успешно изменён.\n"
                            + "Если это были не вы, немедленно обратитесь к администратору.\n\n"
                            + "С уважением,\nАдминистрация приложения Pleasant Routine"
            );
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send password changed email to {}: {}", toEmail, e.getMessage(), e);
        }
    }
}