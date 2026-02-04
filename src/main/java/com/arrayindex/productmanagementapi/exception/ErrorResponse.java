package com.arrayindex.productmanagementapi.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private String details;

    public ErrorResponse() {}

    public ErrorResponse(int status, String message, LocalDateTime timestamp, String details) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.details = details;
    }

    // Getters and Setters
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}