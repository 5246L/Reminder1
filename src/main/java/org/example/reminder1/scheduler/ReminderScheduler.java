package org.example.reminder1.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
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

    @Scheduled(cron = "${app.scheduler.cron}")
    @SchedulerLock(name = "checkAndSendReminders", lockAtMostFor = "55s")
    public void checkAndSendReminders() {
        log.info("Проверка упоминаний...");
        List<Reminder> reminders = reminderService.getRemindersToSend(LocalDateTime.now());
        log.info("Найдено напоминаний {}", reminders.size());

        for (Reminder reminder : reminders) {
            boolean sent = emailService.SendReminderIfPossible(reminder)
                        | telegramService.sendReminderIfPossible(reminder);

            if (sent) {
                reminderService.markAsNotified(reminder);
            } else {
                log.warn("Напоминание ID {} не отправлено ни в один канал", reminder.getId());
            }
        }
    }
}