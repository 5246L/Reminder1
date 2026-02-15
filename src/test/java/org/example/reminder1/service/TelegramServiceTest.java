package org.example.reminder1.service;

import org.example.reminder1.dto.TelegramSendMessageRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TelegramService telegramService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(telegramService, "botToken", "test-bot-token");
    }

    @Test
    void testSendReminder_Success() {

        Long chatId = 123456789L;
        String title = "Тестовое напоминание";
        String description = "Описание";

        ResponseEntity<String> responseEntity = new ResponseEntity<>(
                "{\"ok\":true,\"result\":{\"message_id\":123}}",
                HttpStatus.OK
        );

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        telegramService.sendReminder(chatId, title, description);

        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void testSendReminder_CheckRequestBody() {
        // Arrange
        Long chatId = 987654321L;
        String title = "Встреча";
        String description = "В 15:00";

        ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"ok\":true}", HttpStatus.OK);

        ArgumentCaptor<HttpEntity<TelegramSendMessageRequest>> entityCaptor =
                ArgumentCaptor.forClass(HttpEntity.class);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(responseEntity);

        telegramService.sendReminder(chatId, title, description);

        verify(restTemplate).exchange(
                anyString(),
                eq(HttpMethod.POST),
                entityCaptor.capture(),
                eq(String.class)
        );

        HttpEntity<TelegramSendMessageRequest> capturedEntity = entityCaptor.getValue();
        TelegramSendMessageRequest requestBody = capturedEntity.getBody();

        assertNotNull(requestBody);
//        assertEquals(chatId, requestBody.getChatId());
        assertTrue(requestBody.getText().contains(title));
        assertTrue(requestBody.getText().contains(description));
        assertTrue(requestBody.getText().contains("🔔"));

        HttpHeaders headers = capturedEntity.getHeaders();
        assertEquals(MediaType.APPLICATION_JSON, headers.getContentType());
    }

    @Test
    void testSendReminder_NullChatId() {
        // Arrange
        Long chatId = null;
        String title = "Тест";
        String description = "Описание";

        telegramService.sendReminder(chatId, title, description);

        verify(restTemplate, never()).exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(Class.class)
        );
    }

    @Test
    void testSendReminder_TelegramApiError() {

        Long chatId = 123456789L;
        String title = "Тест";
        String description = "Описание";

        doThrow(new RuntimeException("400 Bad Request: chat_id is empty"))
                .when(restTemplate).exchange(
                        anyString(),
                        eq(HttpMethod.POST),
                        any(HttpEntity.class),
                        eq(String.class)
                );

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            telegramService.sendReminder(chatId, title, description);
        });

        assertEquals("Не удалось отправить Telegram", exception.getMessage());

        verify(restTemplate, times(1)).exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void testGetUpdates() {

        String mockResponse = "{\"ok\":true,\"result\":[{\"update_id\":123}]}";

        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenReturn(mockResponse);

        String result = telegramService.getUpdates();

        assertNotNull(result);
        assertTrue(result.contains("ok"));
        verify(restTemplate, times(1)).getForObject(anyString(), eq(String.class));
    }
}