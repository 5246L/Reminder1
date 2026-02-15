package org.example.reminder1.scheduler;

import org.example.reminder1.entity.Reminder;
import org.example.reminder1.entity.User;
import org.example.reminder1.service.EmailService;
import org.example.reminder1.service.ReminderService;
import org.example.reminder1.service.TelegramService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderSchedulerTest {

    @Mock
    private ReminderService reminderService;

    @Mock
    private EmailService emailService;

    @Mock
    private TelegramService telegramService;

    @InjectMocks
    private ReminderScheduler reminderScheduler;

    private User testUser;
    private Reminder testReminder;

    @BeforeEach
    void setUp() {
        // Создать тестового пользователя
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@example.com");
        testUser.setTelegramChatId(123456789L);

        // Создать тестовое напоминание
        testReminder = new Reminder();
        testReminder.setId(1L);
        testReminder.setTitle("Встреча");
        testReminder.setDescription("Обсудить проект");
        testReminder.setRemind(LocalDateTime.now());
        testReminder.setNotified(false);
        testReminder.setUser(testUser);
    }

    @Test
    void testSendReminders_NoReminders() {
        // Тест: нет напоминаний для отправки
        when(reminderService.getRemindersToSend(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        reminderScheduler.checkAndSendReminders();

        // Проверить что сервисы не вызывались
        verify(emailService, never()).sendReminder(anyString(), anyString(), anyString());
        verify(telegramService, never()).sendReminder(anyLong(), anyString(), anyString());
        verify(reminderService, never()).updateReminder(any(Reminder.class));
    }

    @Test
    void testSendReminders_WithEmailAndTelegram() {
        // Тест: отправка напоминания с email и telegram
        when(reminderService.getRemindersToSend(any(LocalDateTime.class)))
                .thenReturn(List.of(testReminder));

        doNothing().when(emailService).sendReminder(anyString(), anyString(), anyString());
        doNothing().when(telegramService).sendReminder(anyLong(), anyString(), anyString());
        when(reminderService.updateReminder(any(Reminder.class))).thenReturn(testReminder);

        reminderScheduler.checkAndSendReminders();

        // Проверить что email отправлен
        verify(emailService, times(1)).sendReminder(
                eq("user@example.com"),
                eq("Встреча"),
                eq("Обсудить проект")
        );

        // Проверить что telegram отправлен
        verify(telegramService, times(1)).sendReminder(
                eq(123456789L),
                eq("Встреча"),
                eq("Обсудить проект")
        );

        // Проверить что напоминание помечено как отправленное
        verify(reminderService, times(1)).updateReminder(argThat(reminder ->
                reminder.getNotified() == true
        ));
    }

    @Test
    void testSendReminders_OnlyEmail() {
        // Тест: отправка только на email (нет telegram)
        testUser.setTelegramChatId(null);

        when(reminderService.getRemindersToSend(any(LocalDateTime.class)))
                .thenReturn(List.of(testReminder));

        doNothing().when(emailService).sendReminder(anyString(), anyString(), anyString());
        when(reminderService.updateReminder(any(Reminder.class))).thenReturn(testReminder);

        reminderScheduler.checkAndSendReminders();

        // Проверить что email отправлен
        verify(emailService, times(1)).sendReminder(anyString(), anyString(), anyString());

        // Проверить что telegram НЕ отправлен
        verify(telegramService, never()).sendReminder(anyLong(), anyString(), anyString());

        // Проверить что напоминание помечено как отправленное
        verify(reminderService, times(1)).updateReminder(any(Reminder.class));
    }

    @Test
    void testSendReminders_OnlyTelegram() {
        // Тест: отправка только в telegram (нет email)
        testUser.setEmail(null);

        when(reminderService.getRemindersToSend(any(LocalDateTime.class)))
                .thenReturn(List.of(testReminder));

        doNothing().when(telegramService).sendReminder(anyLong(), anyString(), anyString());
        when(reminderService.updateReminder(any(Reminder.class))).thenReturn(testReminder);

        reminderScheduler.checkAndSendReminders();

        // Проверить что email НЕ отправлен
        verify(emailService, never()).sendReminder(anyString(), anyString(), anyString());

        // Проверить что telegram отправлен
        verify(telegramService, times(1)).sendReminder(anyLong(), anyString(), anyString());

        // Проверить что напоминание помечено как отправленное
        verify(reminderService, times(1)).updateReminder(any(Reminder.class));
    }

    @Test
    void testSendReminders_EmailFails() {
        // Тест: ошибка при отправке email (telegram должен отправиться)
        when(reminderService.getRemindersToSend(any(LocalDateTime.class)))
                .thenReturn(List.of(testReminder));

        doThrow(new RuntimeException("SMTP error")).when(emailService)
                .sendReminder(anyString(), anyString(), anyString());
        doNothing().when(telegramService).sendReminder(anyLong(), anyString(), anyString());
        when(reminderService.updateReminder(any(Reminder.class))).thenReturn(testReminder);

        reminderScheduler.checkAndSendReminders();

        // Проверить что попытка отправить email была
        verify(emailService, times(1)).sendReminder(anyString(), anyString(), anyString());

        // Проверить что telegram всё равно отправился
        verify(telegramService, times(1)).sendReminder(anyLong(), anyString(), anyString());

        // Проверить что напоминание всё равно помечено как отправленное
        verify(reminderService, times(1)).updateReminder(any(Reminder.class));
    }

    @Test
    void testSendReminders_TelegramFails() {
        // Тест: ошибка при отправке в telegram (email должен отправиться)
        when(reminderService.getRemindersToSend(any(LocalDateTime.class)))
                .thenReturn(List.of(testReminder));

        doNothing().when(emailService).sendReminder(anyString(), anyString(), anyString());
        doThrow(new RuntimeException("Telegram API error")).when(telegramService)
                .sendReminder(anyLong(), anyString(), anyString());
        when(reminderService.updateReminder(any(Reminder.class))).thenReturn(testReminder);

        reminderScheduler.checkAndSendReminders();

        // Проверить что email отправился
        verify(emailService, times(1)).sendReminder(anyString(), anyString(), anyString());

        // Проверить что попытка отправить telegram была
        verify(telegramService, times(1)).sendReminder(anyLong(), anyString(), anyString());

        // Проверить что напоминание всё равно помечено как отправленное
        verify(reminderService, times(1)).updateReminder(any(Reminder.class));
    }

    @Test
    void testSendReminders_MultipleReminders() {
        // Тест: обработка нескольких напоминаний
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");
        user2.setTelegramChatId(987654321L);

        Reminder reminder2 = new Reminder();
        reminder2.setId(2L);
        reminder2.setTitle("Звонок");
        reminder2.setDescription("Позвонить клиенту");
        reminder2.setRemind(LocalDateTime.now());
        reminder2.setNotified(false);
        reminder2.setUser(user2);

        when(reminderService.getRemindersToSend(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(testReminder, reminder2));

        doNothing().when(emailService).sendReminder(anyString(), anyString(), anyString());
        doNothing().when(telegramService).sendReminder(anyLong(), anyString(), anyString());
        when(reminderService.updateReminder(any(Reminder.class))).thenReturn(testReminder);

        reminderScheduler.checkAndSendReminders();

        // Проверить что email отправлен дважды
        verify(emailService, times(2)).sendReminder(anyString(), anyString(), anyString());

        // Проверить что telegram отправлен дважды
        verify(telegramService, times(2)).sendReminder(anyLong(), anyString(), anyString());

        // Проверить что оба напоминания помечены как отправленные
        verify(reminderService, times(2)).updateReminder(any(Reminder.class));
    }
}