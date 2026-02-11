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

        for(Reminder reminder : reminders) {
            try {
                if(reminder.getUser().getEmail() != null) {
                    emailService.sendReminder(
                            reminder.getUser().getEmail(),
                            reminder.getTitle(),
                            reminder.getDescription()
                    );

                    log.info("Отправлено на Email {}",  reminder.getUser().getEmail());
                }

                if(reminder.getUser().getTelegramChatId() != null) {
                    telegramService.sendReminder(
                            reminder.getUser().getTelegramChatId(),
                            reminder.getTitle(),
                            reminder.getDescription()
                    );

                    log.info("Отправлено в Telegram {}", reminder.getUser().getTelegramChatId());
                }

                reminder.setNotified(true);
                reminderService.updateReminder(reminder);

            }catch (Exception e) {
                log.error("Ошибка отправки напоминания ID {}: {}", reminder.getId(), e.getMessage());
            }
        }

    }
}
