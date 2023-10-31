package com.example.tina;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class CalendarEvent {
    private String title;
    private String description;
    private String date;

    public CalendarEvent() {
        // Construtor vazio necessário para o Firebase
    }

    public CalendarEvent(String title, String description, String date) {
        this.title = Nome;
        this.description = descricao;
        this.date = dataEvento;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
