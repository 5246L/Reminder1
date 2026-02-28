package org.example.reminder1.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.reminder1.entity.Reminder;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendReminder(String to, String title, String description) {

        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("Email получателя не может быть пустым");
        }
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(to);
            simpleMailMessage.setSubject("🔔 Напоминание: " + title);
            simpleMailMessage.setText(description);


            mailSender.send(simpleMailMessage);
            log.info("Успешно отправлено на Email {}", to);

        } catch (Exception e) {
            log.error("Ошибка отправки Email: {}", e.getMessage());
            throw new RuntimeException("Не удалось отправить Email", e);
        }
    }

    public boolean SendReminderIfPossible(Reminder reminder) {
        if (reminder.getUser().getEmail() == null) return false;
        try {
            sendReminder(
                    reminder.getUser().getEmail(),
                    reminder.getTitle(),
                    reminder.getDescription()
            );
            log.info("Email отправлен: {}", reminder.getUser().getEmail());
            return true;
        } catch (Exception e) {
            log.error("Ошибка Email для ID {}: {}", reminder.getId(), e.getMessage());
            return false;
        }
    }
}
