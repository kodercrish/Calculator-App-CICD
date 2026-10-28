package com.example.calculator;

/**
 * Error body, e.g. { "status": 400, "error": "Bad Request", "message": "Division by zero is not allowed" }
 */
public record ErrorResponse(int status, String error, String message) {
}
