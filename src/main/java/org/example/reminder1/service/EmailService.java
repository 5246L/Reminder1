package org.example.reminder1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendReminder(String to, String title, String description) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(to);
            simpleMailMessage.setSubject("Напоминание: " + title);
            simpleMailMessage.setText(description);
//            simpleMailMessage.setFrom("levk9594@reminder.com");


            mailSender.send(simpleMailMessage);
            log.info("Успешно отправлено на Email {}", to);

        } catch (Exception e) {
            log.error("Ошибка отправки Email: {}", e.getMessage());
            throw new RuntimeException("Не удалось отправить Email", e);
        }
    }
}
