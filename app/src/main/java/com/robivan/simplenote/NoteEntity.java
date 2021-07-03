package com.robivan.simplenote;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class NoteEntity implements Serializable {
    public final String id, title, noteText, createDate;
    public final long date;

    public NoteEntity(String id, String title, String noteText, long date) {
        this.id = id;
        this.title = title;
        this.noteText = noteText;
        this.date = date;
        SimpleDateFormat dateFormat = new SimpleDateFormat("d-MM-y", Locale.getDefault());
        createDate = dateFormat.format(date);
    }

    public static String generateNewId() {
        return UUID.randomUUID().toString();
    }

    public static long getCurrentDate() {
        return Calendar.getInstance().getTimeInMillis();
    }
}
