package com.example.notepad.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes")
public class Note {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String content;

    // زمان ایجاد و آخرین ویرایش
    public long createdAt;
    public long updatedAt;

    public Note() {}

    public Note(String title, String content, long createdAt, long updatedAt) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
