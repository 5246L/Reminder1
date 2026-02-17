package org.example.reminder1.controller;

import lombok.RequiredArgsConstructor;
import org.example.reminder1.service.TelegramService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/telegram")
@RequiredArgsConstructor
public class TelegramController {

    private final TelegramService telegramService;

    @GetMapping("/updates")
    public String getUpdates() {
        return telegramService.getUpdates();
    }
}
