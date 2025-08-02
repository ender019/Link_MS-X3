package com.unknown.link.dtos;

import java.time.LocalDateTime;

public record GUserDTO(
        String id,
        String username,
        String email,
        String description,
        String avatar,
        LocalDateTime reg_date
) {}
