package com.example.notepad.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;

public class NoteRepository {
    private final NoteDao dao;

    public NoteRepository(Application app){
        dao = AppDatabase.get(app).noteDao();
    }

    public LiveData<List<Note>> getAllOrderByCreated(){ return dao.getAllOrderByCreated(); }
    public LiveData<List<Note>> getAllOrderByUpdated(){ return dao.getAllOrderByUpdated(); }

    public void insert(Note n){ AppDatabase.dbExecutor.execute(() -> dao.insert(n)); }
    public void update(Note n){ AppDatabase.dbExecutor.execute(() -> dao.update(n)); }
    public void delete(Note n){ AppDatabase.dbExecutor.execute(() -> dao.delete(n)); }
    public void deleteAll(){ AppDatabase.dbExecutor.execute(dao::deleteAll); }
}


