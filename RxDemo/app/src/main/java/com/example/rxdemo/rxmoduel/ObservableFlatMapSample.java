package com.example.rxdemo.rxmoduel;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ObservableFlatMapSample {

    private String TAG = "ObservableFlatMapSample";
    private CompositeDisposable mDisposable;
    public static ObservableFlatMapSample sInstance;

    static {
        sInstance = new ObservableFlatMapSample();
    }

    public ObservableFlatMapSample() {
        mDisposable = new CompositeDisposable();
    }

    public void executeFlatMap(){
        mDisposable.add(Observable.just("1", "2", "3")
                .flatMap(new Function<String, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(String s) throws Exception {
                        return Observable.just("apply " + s);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Log.i(TAG, "accept: " + s);
                    }
                }));
    }

    public void dispose(){
        mDisposable.isDisposed();
    }
}
