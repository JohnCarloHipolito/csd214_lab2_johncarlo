package com.triosstudent.csd214_lab2_johncarlo;

import java.time.LocalDate;

public class TodoModel {

    private Long id;
    private String description;
    private LocalDate targetDate;
    private String status;

    public TodoModel(Long id, String description, LocalDate targetDate, String status) {
        this.id = id;
        this.description = description;
        this.targetDate = targetDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
