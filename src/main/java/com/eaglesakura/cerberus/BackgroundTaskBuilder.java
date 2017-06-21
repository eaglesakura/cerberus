package com.eaglesakura.cerberus;

import com.eaglesakura.cerberus.error.TaskCanceledException;

import android.app.Activity;
import android.app.Dialog;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.exceptions.UndeliverableException;

/**
 * 非同期実行タスク用のBuilder
 *
 * 実行対象のThreadは {@link ExecuteTarget} で指定する。デフォルトは {@link ExecuteTarget#GlobalQueue} で処理される。
 * 実行結果は必ずUIThreadでハンドリングされる。
 */
public class BackgroundTaskBuilder<T> {
    final PendingCallbackQueue mController;

    Observable<T> mObservable;

    /**
     * 標準ではプロセス共有スレッドで実行される
     */
    ExecuteTarget mThreadTarget = ExecuteTarget.GlobalParallel;

    /**
     * Task
     */
    BackgroundTask mTask = new BackgroundTask<>();

    /**
     * タスクをスタート済みであればtrue
     */
    boolean mStartedTask = false;

    public BackgroundTaskBuilder(PendingCallbackQueue subscriptionController) {
        mController = subscriptionController;
        mTask.mCallbackQueue = mController;
    }

    /**
     * 処理対象のスレッドを指定する
     */
    public BackgroundTaskBuilder<T> executeOn(ExecuteTarget target) {
        mThreadTarget = target;
        return this;
    }

    /**
     * コールバック対象のタイミングを指定する
     */
    public BackgroundTaskBuilder<T> callbackOn(CallbackTime target) {
        mTask.mCallbackTime = target;
        return this;
    }

    /**
     * ユーザのキャンセルチェックを有効化する
     */
    public BackgroundTaskBuilder<T> cancelSignal(BackgroundTask.Signal signal) {
        mTask.mCancelSignals.add(signal);
        return this;
    }

    public BackgroundTaskBuilder<T> cancelSignal(Activity activity) {
        return cancelSignal(task -> activity == null || activity.isFinishing());
    }

    public BackgroundTaskBuilder<T> cancelSignal(Fragment fragment) {
        return cancelSignal(task -> {
            if (fragment == null) {
                return true;
            }

            FragmentActivity activity = fragment.getActivity();
            return activity == null || activity.isFinishing();
        });
    }

    /**
     * ユーザーのキャンセルチェックをダイアログと同期する
     */
    public BackgroundTaskBuilder<T> cancelSignal(Dialog dialog) {
        return cancelSignal(task -> !dialog.isShowing());
    }

    /**
     * ダイアログに合わせてキャンセルチェックとキャンセル挙動を設定する
     */
    public BackgroundTaskBuilder<T> cancelSignal(Dialog dialog, BackgroundTask.Action0<T> callback) {
        return canceled(callback).
                cancelSignal(task -> !dialog.isShowing());
    }

    /**
     * タスク名を指定する。
     * <p>
     * タスク名はそのままスレッド名として利用される。
     */
    public BackgroundTaskBuilder<T> name(String name) {
        mTask.mName = name;
        return this;
    }

    /**
     * スレッド名を指定する
     * <p>
     * 指定されていない場合は何もしない。
     */
    void bindThreadName() {
        if (mTask.mName == null) {
            return;
        }

        Thread current = Thread.currentThread();
        if (current.equals(Looper.getMainLooper().getThread())) {
            // UIスレッド名は変更できない
            return;
        }

        try {
            Thread.currentThread().setName("RxTask::" + mTask.mName);
        } catch (Exception e) {

        }
    }

    /**
     * 非同期処理を指定する
     */
    public BackgroundTaskBuilder<T> async(BackgroundTask.Async<T> subscribe) {
        mObservable = Observable.create((ObservableEmitter<T> it) -> {
            synchronized (mTask) {
                mTask.mState = BackgroundTask.State.Running;
                bindThreadName();
            }

            //  非同期処理中はロックを外す
            T result;
            try {
                result = subscribe.call((BackgroundTask<T>) mTask);

                if (mTask.isCanceled()) {
                    throw new TaskCanceledException();
                }
            } catch (Exception e) {
                if (!it.isDisposed()) {
                    try {
                        it.onError(e);
                    } catch (UndeliverableException ee) {
                        // disposed!
                    }
                }
                return;
            }

            // 実行完了をコールする
            synchronized (mTask) {
                if (it.isDisposed()) {
                    return;
                }
                it.onNext(result);
                it.onComplete();
            }
        })
                .subscribeOn(mController.getThreadController().getScheduler(mThreadTarget))
                .observeOn(AndroidSchedulers.mainThread());
        return this;
    }

    /**
     * 戻り値からの処理を記述する
     */
    public BackgroundTaskBuilder<T> completed(BackgroundTask.Action1<T> callback) {
        mTask.completed(callback);
        return this;
    }

    /**
     * エラーハンドリングを記述する
     */
    public BackgroundTaskBuilder<T> failed(BackgroundTask.ErrorAction<T> callback) {
        mTask.failed(callback);
        return this;
    }

    /**
     * キャンセル時のハンドリングを設定する
     */
    public BackgroundTaskBuilder<T> canceled(BackgroundTask.Action0<T> callback) {
        mTask.canceled(callback);
        return this;
    }

    /**
     * 終了時の処理を記述する
     */
    public BackgroundTaskBuilder<T> finalized(BackgroundTask.Action0<T> callback) {
        mTask.finalized(callback);
        return this;
    }

    public boolean isStartedTask() {
        return mStartedTask;
    }

    /**
     * セットアップを完了し、処理を開始する
     */
    public BackgroundTask<T> start() {
        // 実行準備する
        mTask.mState = BackgroundTask.State.Pending;
        // キャンセルを購読対象と同期させる

        if (isStartedTask()) {
            // 既にタスクが起動済みのため、再度起動することはできない
            throw new IllegalStateException();
        }

        mStartedTask = true;
        // 開始タイミングをズラす
        mController.sHandler.post(() -> {
            LifecycleStateDump dumpState = mController.getCurrentState();
            BackgroundTask.Signal signal = task -> mController.isCanceled(mTask.mCallbackTime, dumpState);
            mTask.mCancelSignals.add(signal);
            mTask.mSubscription = mObservable.subscribe(
                    // next = completeed
                    next -> {
                        mTask.mSubscription.dispose();
                        mController.remove(mTask.mSubscription);
                        mTask.mSubscription = null;
                        mTask.setResult(next);
                    },
                    // error
                    error -> {
                        mTask.mSubscription.dispose();
                        mController.remove(mTask.mSubscription);
                        mTask.mSubscription = null;
                        if (error instanceof Exception) {
                            mTask.setError(((Exception) error));
                        } else {
                            throw (Error) error;
                        }
                    }
            );

            // 購読対象に追加
            mController.add(mTask.mCallbackTime, mTask.mSubscription);
        });
        return mTask;
    }
}
