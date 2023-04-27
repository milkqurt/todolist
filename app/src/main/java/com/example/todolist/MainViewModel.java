package com.example.todolist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private NoteDatabase database;

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = NoteDatabase.getInstance(application);
    }

    public LiveData<List<Note>> getNotes() {
        return database.notesDao().getNotes();
    }

    public void remove(Note note) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                database.notesDao().remove(note.getId());
            }
        });
        thread.start();
    }
}
