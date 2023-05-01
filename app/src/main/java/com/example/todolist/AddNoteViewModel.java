package com.example.todolist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AddNoteViewModel extends AndroidViewModel {

    private NoteDatabase database;
    private MutableLiveData<Boolean> shouldCloseScreen = new MutableLiveData<>();
    private Disposable disposable;
    private CompositeDisposable compositeDisposable = new CompositeDisposable(); // коллекция disposable
    // чтобы не создавать для каддой операции отдельный

    public AddNoteViewModel(@NonNull Application application) {
        super(application);
        database = NoteDatabase.getInstance(application);
    }

    public LiveData<Boolean> getShouldCloseScreen() {
        return shouldCloseScreen;
    }

    public void saveNote(Note note) {
       disposable = database.notesDao().add(note)
               .delay(3, TimeUnit.SECONDS)
               .subscribeOn(Schedulers.io()) //фоновый поток
               .observeOn(AndroidSchedulers.mainThread()) //переключение на главный поток
               .subscribe(new Action() {
           @Override
           public void run() throws Throwable {
               shouldCloseScreen.setValue(true);
           }
       });
       compositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
//        disposable.dispose();
        compositeDisposable.dispose();
        // при потере фокуса данные сохраняться не будут, так можно управлять жизненным циклом потоков
    }
}
