package com.tecnocampus.tecnohostal.application.dto;

import com.tecnocampus.tecnohostal.domain.Guest;

public record GuestDTO(
        String id,
        String name,
        String email,
        String phone,
        String vipLevel
) {
    public static GuestDTO from(Guest guest) {
        return new GuestDTO(guest.getId(), guest.getName(), guest.getEmail(),
                guest.getPhone(), guest.getVipLevel().name());
    }
}
