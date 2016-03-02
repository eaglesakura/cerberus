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
     * 完了時処理を記述する
     */
    RxTask.Action1<T> mCompletedCallback;

    /**
     * エラー時の処理を記述する
     */
    RxTask.ErrorAction<T> mErrorCallback;

    /**
     * 最終的に必ず呼び出される処理
     */
    RxTask.Action0 mFinalizeCallback;

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
            try {
                mTask.mState = RxTask.State.Running;

                it.onNext(subscribe.call(mTask));
                it.onCompleted();
            } catch (Throwable e) {
                it.onError(e);
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
        mCompletedCallback = callback;
        return this;
    }

    /**
     * エラーハンドリングを記述する
     */
    public RxTaskBuilder<T> errored(RxTask.ErrorAction<T> callback) {
        mErrorCallback = callback;
        return this;
    }

    /**
     * 終了時の処理を記述する
     */
    public RxTaskBuilder<T> finalized(RxTask.Action0<T> callback) {
        mFinalizeCallback = callback;
        return this;
    }

    /**
     * セットアップを完了し、処理を開始する
     */
    public RxTask<T> start() {
        mTask.mState = RxTask.State.Pending;
        // キャンセルを購読対象と同期させる
        mTask.mSubscribeCancelSignal = () -> mSubscription.isCanceled(mTask.mObserveTarget);

        final Subscription subscribe = mObservable.subscribe(
                // next = completeed
                next -> {
                    mTask.mResult = next;
                    mTask.mState = RxTask.State.Finished;

                    if (mCompletedCallback != null) {
                        mSubscription.run(mTask.mObserveTarget, () -> {
                            mCompletedCallback.call(next, mTask);
                        });
                    }

                    if (mFinalizeCallback != null) {
                        mSubscription.run(mTask.mObserveTarget, () -> {
                            mFinalizeCallback.call(mTask);
                        });
                    }
                },
                // error
                error -> {
                    mTask.mError = error;
                    mTask.mState = RxTask.State.Finished;

                    if (mErrorCallback != null) {
                        mSubscription.run(mTask.mObserveTarget, () -> {
                            mErrorCallback.call(error, mTask);
                        });
                    }

                    if (mFinalizeCallback != null) {
                        mSubscription.run(mTask.mObserveTarget, () -> {
                            mFinalizeCallback.call(mTask);
                        });
                    }
                }
        );

        // 購読対象に追加
        mSubscription.add(subscribe);
        return mTask;
    }
}
