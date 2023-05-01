package com.example.todolist;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.BiConsumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {

    private NoteDatabase database;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<List<Note>> mutableLiveData = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = NoteDatabase.getInstance(application);
    }

    public LiveData<List<Note>> getNotes() {
        return mutableLiveData;
    }

    public void refreshList() {
        Disposable disposable = database.notesDao().getNotes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<Note>, Throwable>() {
                    @Override
                    public void accept(List<Note> notes, Throwable throwable) throws Throwable {
                        mutableLiveData.setValue(notes);
                        throwable.fillInStackTrace();
                    }
                });
        compositeDisposable.add(disposable);
    }

    public void remove(Note note) {
     Disposable disposable = database.notesDao()
              .remove(note.getId())
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(new Action() {
                  @Override
                  public void run() throws Throwable {
                      Log.d("MainViewModel", "note removed");
                      refreshList();
                  }
              });
      compositeDisposable.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }


    //test
//    private Single<List<Note>> getNotesRx() {
//        return Single.fromCallable(new Callable<List<Note>>() {
//            @Override
//            public List<Note> call() throws Exception {
//                return database.notesDao().getNotes();
//            }
//        });
//    }
}
