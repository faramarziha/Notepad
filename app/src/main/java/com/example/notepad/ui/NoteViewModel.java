package com.example.notepad.ui;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.*;
import com.example.notepad.data.Note;
import com.example.notepad.data.NoteRepository;
import java.util.List;

public class NoteViewModel extends AndroidViewModel {

    private final NoteRepository repo;
    private final MediatorLiveData<List<Note>> notes = new MediatorLiveData<>();
    private LiveData<List<Note>> sourceCreated, sourceUpdated;
    private String sortMode = "updated";

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repo = new NoteRepository(application);
        sourceCreated = repo.getAllOrderByCreated();
        sourceUpdated = repo.getAllOrderByUpdated();
        setSortMode(SettingsManager.getSortMode(application));
    }

    public LiveData<List<Note>> getNotes(){ return notes; }

    public void setSortMode(String mode){
        if (mode == null) mode = "updated";
        if (mode.equals(sortMode) && notes.getValue()!=null) return;
        sortMode = mode;
        notes.removeSource(sourceCreated);
        notes.removeSource(sourceUpdated);
        if ("created".equals(mode)){
            notes.addSource(sourceCreated, notes::setValue);
        } else {
            notes.addSource(sourceUpdated, notes::setValue);
        }
    }

    public String getSortMode(){ return sortMode; }

    public void insert(Note n){ repo.insert(n); }
    public void update(Note n){ repo.update(n); }
    public void delete(Note n){ repo.delete(n); }
    public void deleteAll(){ repo.deleteAll(); }
}
