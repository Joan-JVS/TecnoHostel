package com.tecnocampus.tecnohostal.exception;

/** Cos JSON de totes les respostes d'error. */
public record ErrorResponse(String timestamp, int status, String error, String message) {}
