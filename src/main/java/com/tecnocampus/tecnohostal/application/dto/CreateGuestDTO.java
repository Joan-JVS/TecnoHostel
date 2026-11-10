package com.tecnocampus.tecnohostal.application.dto;

/** Body de POST /guests. Si no s'envia vipLevel, el client és STANDARD. */
public record CreateGuestDTO(String name, String email, String phone, String vipLevel) {}
