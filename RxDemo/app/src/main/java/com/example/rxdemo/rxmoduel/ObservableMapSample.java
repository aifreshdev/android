package com.example.rxdemo.rxmoduel;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ObservableMapSample {

    private final String TAG = "ObservableMapSample";
    public static ObservableMapSample sInstance;

    static {
        sInstance = new ObservableMapSample();
    }

    public Observable<String> executeMap(){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                for(int i=0;i<20;i++) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext("num " + i);
                    }
                }

                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).map(new Function<String, String>() {
            @Override
            public String apply(String s) throws Exception {
                return "apply " + s;
            }
        });
    }

    public Observer<String> getObserver(){
        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, "onSubscribe: ");
            }

            @Override
            public void onNext(String s) {
                Log.i(TAG, "onNext: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.i(TAG, "onError: ");
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete ");
            }
        };
    }
}
