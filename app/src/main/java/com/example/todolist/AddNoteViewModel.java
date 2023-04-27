package com.example.todolist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class AddNoteViewModel extends AndroidViewModel {

    private NoteDatabase database;
    private MutableLiveData<Boolean> shouldCloseScreen = new MutableLiveData<>();

    public AddNoteViewModel(@NonNull Application application) {
        super(application);
        database = NoteDatabase.getInstance(application);
    }

    public LiveData<Boolean> getShouldCloseScreen() {
        return shouldCloseScreen;
    }

    public void saveNote(Note note) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                database.notesDao().add(note);
                shouldCloseScreen.postValue(true);
            }
        });
        thread.start();
    }
}
