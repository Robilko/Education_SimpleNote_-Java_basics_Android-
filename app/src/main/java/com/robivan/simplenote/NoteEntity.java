package com.robivan.simplenote;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class NoteEntity implements Parcelable {
    private String id;
    private String title;
    private String noteText;
    private String createDate;
    private long date;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("d-MM-y", Locale.getDefault());

    public NoteEntity(String title, String noteText, long date) {
        this.title = title;
        this.noteText = noteText;
        this.date = date;
        createDate = dateFormat.format(date);
    }

    protected NoteEntity(Parcel in) {
        id = in.readString();
        title = in.readString();
        noteText = in.readString();
        date = in.readLong();
        createDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(noteText);
        dest.writeLong(date);
        dest.writeString(createDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NoteEntity> CREATOR = new Creator<NoteEntity>() {
        @Override
        public NoteEntity createFromParcel(Parcel in) {
            return new NoteEntity(in);
        }

        @Override
        public NoteEntity[] newArray(int size) {
            return new NoteEntity[size];
        }
    };

    public String getId(){
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getNoteText() {
        return noteText;
    }

    public String getCreateDate() {
        return createDate;
    }

    public long getDate() {
        return date;
    }

    public static long getCurrentDate() {
        return Calendar.getInstance().getTimeInMillis();
    }


}
