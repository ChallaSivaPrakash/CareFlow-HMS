package com.careflow.backend.dto;

import java.time.LocalDateTime;

public record ChatMessage(
    String sender,
    String role,
    String content,
    LocalDateTime timestamp
) {}
