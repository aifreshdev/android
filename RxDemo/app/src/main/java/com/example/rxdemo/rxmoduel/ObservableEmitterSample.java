package com.example.rxdemo.rxmoduel;

import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ObservableEmitterSample {

    private final static String TAG = "ObservableEmitterSample";

    public static void executeEmitter() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();

                emitter.onNext(4);
                emitter.onNext(5);
                emitter.onNext(6);
                emitter.onComplete();
            }
        }).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "subscribe");
            }

            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "" + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "error");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "complete");
            }
        });
    }

    public static void executeDisposableEmitter(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();

                emitter.onNext(4);
                emitter.onNext(5);
                emitter.onNext(6);
                emitter.onComplete();
            }
        }).subscribe(new Observer<Integer>() {

            private Disposable mSubscribeKiller;

            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "subscribe");
                mSubscribeKiller = d;
            }

            @Override
            public void onNext(Integer value) {
                Log.d(TAG, "onNext : " + value);
                if(value == 3){
                    mSubscribeKiller.dispose();
                    Log.i(TAG, "onNext: dispose()");
                    mSubscribeKiller.isDisposed();
                    Log.i(TAG, "onNext: isDisposed()");
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "error");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "complete");
            }
        });
    }
}
