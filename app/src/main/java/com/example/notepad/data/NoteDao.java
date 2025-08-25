package com.example.notepad.data;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;

@Dao
public interface NoteDao {
    @Insert  void insert(Note note);
    @Update  void update(Note note);
    @Delete  void delete(Note note);

    @Query("DELETE FROM notes")
    void deleteAll();

    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    LiveData<List<Note>> getAllOrderByCreated();

    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    LiveData<List<Note>> getAllOrderByUpdated();
}


