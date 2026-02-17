package org.example.reminder1.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.reminder1.entity.Reminder;
import org.example.reminder1.service.EmailService;
import org.example.reminder1.service.ReminderService;
import org.example.reminder1.service.TelegramService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderScheduler {
    private final ReminderService reminderService;
    private final EmailService emailService;
    private final TelegramService telegramService;

    @Scheduled(cron = "0 * * * * *")
    public void checkAndSendReminders() {
        log.info("Проверка упоминаний...");
        LocalDateTime now = LocalDateTime.now();
        List<Reminder> reminders = reminderService.getRemindersToSend(now);

        log.info("Найдено напоминаний {}", reminders.size());

        for (Reminder reminder : reminders) {
            boolean emailSent = false;
            boolean telegramSent = false;

            try {
                if (reminder.getUser().getEmail() != null) {
                    emailService.sendReminder(
                            reminder.getUser().getEmail(),
                            reminder.getTitle(),
                            reminder.getDescription()
                    );
                    log.info("Email отправлен: {}", reminder.getUser().getEmail());
                    emailSent = true;
                }
            } catch (Exception e) {
                log.error("Ошибка отправки Email для ID {}: {}", reminder.getId(), e.getMessage());
            }

            try {
                if (reminder.getUser().getTelegramChatId() != null) {
                    telegramService.sendReminder(
                            reminder.getUser().getTelegramChatId(),
                            reminder.getTitle(),
                            reminder.getDescription()
                    );
                    log.info("Telegram отправлен: {}", reminder.getUser().getTelegramChatId());
                    telegramSent = true;
                }
            } catch (Exception e) {
                log.error("Ошибка отправки Telegram для ID {}: {}", reminder.getId(), e.getMessage());
            }

            if (emailSent || telegramSent) {
                try {
                    reminder.setNotified(true);
                    reminderService.updateReminder(reminder);
                    log.info("Напоминание ID {} помечено как отправленное", reminder.getId());
                } catch (Exception e) {
                    log.error("Ошибка обновления notified для ID {}: {}", reminder.getId(), e.getMessage());
                }
            } else {
                log.warn("Напоминание ID {} не отправлено ни в Email, ни в Telegram", reminder.getId());
            }
        }
    }
}
