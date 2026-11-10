package com.tecnocampus.tecnohostal.domain;

import java.util.UUID;

/**
 * Un client de l'hostal. El nivell VIP (STANDARD, SILVER, GOLD) decideix el descompte.
 */
public class Guest {

    private String id = UUID.randomUUID().toString();
    private String name;
    private String email;
    private String phone;
    private VipLevel vipLevel;

    /** Client nou: valida les dades i genera l'id. */
    public Guest(String name, String email, String phone, VipLevel vipLevel) {
        validateName(name);
        validateEmail(email);
        validateVipLevel(vipLevel);
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.vipLevel = vipLevel;
    }

    /** Reconstrueix un client a partir d'una fila de la taula guests. */
    public Guest(String id, String name, String email, String phone, VipLevel vipLevel) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.vipLevel = vipLevel;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Guest name is required");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("A valid guest email is required");
        }
    }

    private void validateVipLevel(VipLevel vipLevel) {
        if (vipLevel == null) {
            throw new IllegalArgumentException("Guest VIP level is required");
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public VipLevel getVipLevel() {
        return vipLevel;
    }
}
