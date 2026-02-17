package org.example.reminder1.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void testSendReminder_Success() {
        String to = "test@example.com";
        String title = "Тестовое напоминание";
        String description = "Описание напоминания";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendReminder(to, title, description);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendReminder_CheckMessageContent() {
        String to = "test@example.com";
        String title = "Встреча";
        String description = "Обсудить проект";

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendReminder(to, title, description);

        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage capturedMessage = messageCaptor.getValue();

        assertNotNull(capturedMessage);
        assertEquals(to, capturedMessage.getTo()[0]);
        assertEquals("🔔 Напоминание: " + title, capturedMessage.getSubject());
        assertEquals(description, capturedMessage.getText());
    }

    @Test
    void testSendReminder_ThrowsException() {
        String to = "test@example.com";
        String title = "Тест";
        String description = "Описание";

        doThrow(new RuntimeException("SMTP server unavailable"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            emailService.sendReminder(to, title, description);
        });

        assertEquals("Не удалось отправить Email", exception.getMessage());

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testSendReminder_NullRecipient() {
        assertThrows(IllegalArgumentException.class, () -> {
            emailService.sendReminder(null, "Тест", "Описание");
        });
    }

    @Test
    void testSendReminder_BlankRecipient() {
        assertThrows(IllegalArgumentException.class, () -> {
            emailService.sendReminder("   ", "Тест", "Описание");
        });
    }
}
