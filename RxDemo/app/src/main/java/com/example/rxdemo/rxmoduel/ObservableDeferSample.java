package com.example.rxdemo.rxmoduel;

import android.util.Log;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ObservableDeferSample {

    private String TAG = "ObservableDeferSample";
    public String name = "JACK";

    /**
     * https://blog.danlew.net/2015/07/23/deferring-observable-code-until-subscription-in-rxjava/
     * call a new Observable
     */
    public Observable<String> valueObservable(){
        return Observable.defer(new Callable<ObservableSource<? extends String>>() {
            @Override
            public ObservableSource<? extends String> call() throws Exception {
                return Observable.just(name);
            }
        });
    }

    public Observer<String> getValueObserver(){
        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                Log.i(TAG, "Name " + s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }

    public Observable<String> createObservable(final String newType){
        return Observable.defer(new Callable<ObservableSource<? extends String>>() {
            @Override
            public ObservableSource<? extends String> call() throws Exception {
                return Observable.just(newType);
            }
        });
    }
}
