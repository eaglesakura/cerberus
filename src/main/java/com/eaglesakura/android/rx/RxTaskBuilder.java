package com.eaglesakura.android.rx;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 *
 */
public class RxTaskBuilder<T> {
    final SubscriptionController mSubscription;

    Observable<T> mObservable;

    /**
     * 標準ではプロセス共有スレッドで実行される
     */
    SubscribeTarget mThreadTarget = SubscribeTarget.GlobalParallels;

    /**
     * Task
     */
    RxTask<T> mTask = new RxTask<>();

    public RxTaskBuilder(SubscriptionController subscriptionController) {
        mSubscription = subscriptionController;
        mTask.mSubscription = mSubscription;
    }

    /**
     * 処理対象のスレッドを指定する
     */
    public RxTaskBuilder<T> subscribeOn(SubscribeTarget target) {
        mThreadTarget = target;
        return this;
    }

    /**
     * コールバック対象のタイミングを指定する
     */
    public RxTaskBuilder<T> observeOn(ObserveTarget target) {
        mTask.mObserveTarget = target;
        return this;
    }

    /**
     * ユーザのキャンセルチェックを有効化する
     */
    public RxTaskBuilder<T> cancelSignal(RxTask.CancelSignal signal) {
        mTask.mUserCancelSignal = signal;
        return this;
    }

    /**
     * 処理にタイムアウトを付与する
     */
    public RxTaskBuilder<T> timeout(long timeoutMs) {
        mObservable.timeout(timeoutMs, TimeUnit.MILLISECONDS);
        return this;
    }

    /**
     * 非同期処理を指定する
     */
    public RxTaskBuilder<T> async(RxTask.Async<T> subscribe) {
        mObservable = Observable.create((Subscriber<? super T> it) -> {
            synchronized (mTask) {
                try {
                    mTask.mState = RxTask.State.Running;

                    it.onNext(subscribe.call(mTask));
                    it.onCompleted();
                } catch (Throwable e) {
                    it.onError(e);
                }
            }
        })
                .subscribeOn(mSubscription.getThreadController().getScheduler(mThreadTarget))
                .observeOn(AndroidSchedulers.mainThread());
        return this;
    }

    /**
     * Observableを直接更新する
     */
    public RxTaskBuilder<T> update(final Action1<Observable<T>> callback) {
        callback.call(mObservable);
        return this;
    }

    /**
     * 戻り値からの処理を記述する
     */
    public RxTaskBuilder<T> completed(RxTask.Action1<T> callback) {
        mTask.completed(callback);
        return this;
    }

    /**
     * エラーハンドリングを記述する
     */
    public RxTaskBuilder<T> failed(RxTask.ErrorAction<T> callback) {
        mTask.failed(callback);
        return this;
    }

    /**
     * キャンセル時のハンドリングを設定する
     */
    public RxTaskBuilder<T> canceled(RxTask.Action0<T> callback) {
        mTask.canceled(callback);
        return this;
    }

    /**
     * 終了時の処理を記述する
     */
    public RxTaskBuilder<T> finalized(RxTask.Action0<T> callback) {
        mTask.finalized(callback);
        return this;
    }

    /**
     * セットアップを完了し、処理を開始する
     */
    public RxTask<T> start() {
        mTask.mState = RxTask.State.Pending;
        // キャンセルを購読対象と同期させる
        mTask.mSubscribeCancelSignal = (task) -> mSubscription.isCanceled(mTask.mObserveTarget);

        final Subscription subscribe = mObservable.subscribe(
                // next = completeed
                next -> {
                    mTask.setResult(next);
                },
                // error
                error -> {
                    mTask.setError(error);
                }
        );

        // 購読対象に追加
        mSubscription.add(subscribe);
        return mTask;
    }
}
