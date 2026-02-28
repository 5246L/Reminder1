package org.example.reminder1.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthSuccessResponse {
    private String message;
    private String name;
    private String email;
}
