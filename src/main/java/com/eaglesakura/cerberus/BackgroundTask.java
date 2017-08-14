package com.eaglesakura.cerberus;

import com.eaglesakura.cerberus.error.TaskException;
import com.eaglesakura.cerberus.error.TaskCanceledException;
import com.eaglesakura.cerberus.error.TaskTimeoutException;

import android.support.annotation.NonNull;

import java.io.InterruptedIOException;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.disposables.Disposable;


/**
 * 非同期実行されるタスクを管理する
 *
 * タスクは StateLinkQueue によってコールバック管理され、必要に応じてコールバックの保留やキャンセルが行われる。
 */
public class BackgroundTask<T> {

    /**
     * コールバック管理
     */
    PendingCallbackQueue mCallbackQueue;

    /**
     * 外部から指定されたキャンセルチェック
     */
    Set<Signal> mCancelSignals = new HashSet<>();

    /**
     * 受信したエラー
     */
    private Exception mError;

    /**
     * 戻り値
     */
    private T mResult;

    /**
     * 現在の処理ステート
     */
    State mState = State.Building;

    /**
     * 購読データ
     */
    Disposable mSubscription;

    /**
     * コールバック対象を指定する
     * <p>
     * デフォルトはFire And Forget
     */
    CallbackTime mCallbackTime = CallbackTime.FireAndForget;

    /**
     * 完了時処理を記述する
     */
    private BackgroundTask.Action1<T> mCompletedCallback;

    /**
     * キャンセル時の処理を記述する
     */
    private BackgroundTask.Action0<T> mCancelCallback;

    /**
     * エラー時の処理を記述する
     */
    private BackgroundTask.ErrorAction<T> mErrorCallback;

    /**
     * 最終的に必ず呼び出される処理
     */
    private BackgroundTask.Action0 mFinalizeCallback;

    /**
     * チェーン実行されるタスク
     */
    @Deprecated
    private BackgroundTaskBuilder mChainTask;

    /**
     * タスク名
     */
    String mName = "Task::" + getClass().getName();

    /**
     * デフォルトのタイムアウト指定
     */
    @Deprecated
    private long mTimeoutMs = 1000 * 60 * 60;

    public enum State {
        /**
         * タスクを生成中
         */
        Building,

        /**
         * まだ実行されていない
         */
        Pending,

        /**
         * タスクが実行中
         */
        Running,

        /**
         * 完了
         */
        Finished,
    }

    BackgroundTask() {
    }

    /**
     * 現在のタスク状態を取得する
     */
    public State getState() {
        return mState;
    }

    /**
     * 戻り値を取得する
     */
    public T getResult() {
        return mResult;
    }

    /**
     * 指定時間だけウェイトをかける。
     * <p>
     * 途中でキャンセルされた場合は例外を投げて終了される
     */
    public void waitTime(long timeMs) throws TaskException {
        if (timeMs <= 0) {
            return;
        }

        final long START_TIME = System.currentTimeMillis();
        while ((System.currentTimeMillis() - START_TIME) < timeMs) {
            try {
                Thread.sleep(1);
            } catch (Exception e) {
            }

            if (isCanceled()) {
                throw new TaskCanceledException();
            }
        }
    }

    /**
     * awaitを行い、結果を捨てる
     */
    @Deprecated
    public boolean safeAwait(long timeoutMs) {
        try {
            await(timeoutMs);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * awaitを行い、結果を捨てる
     */
    @Deprecated
    public void safeAwait() {
        try {
            await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 処理待ちを行い、結果を取得する。
     * 内部でスピンロックするため、明示的にスピンロックを抜けるためには引数cancelCallbackを使用する
     */
    public T await(@NonNull PendingCallbackQueue.CancelCallback cancelCallback) throws Exception {
        while (!isFinished() && !cancelCallback.isCanceled()) {
            if (isCanceled()) {
                throw new TaskCanceledException();
            }

            try {
                Thread.sleep(1);
            } catch (Exception e) {
            }
        }
        throwIfError();

        if (!isFinished()) {
            throw new TaskTimeoutException();
        }

        return mResult;
    }

    /**
     * 処理待ちを行う
     * <p>
     * timeout等やコールバックと同居するため、実装はただのwaitである。
     */
    @Deprecated
    public T await(long timeoutMs) throws Exception {
        final long START_TIME = System.currentTimeMillis();
        while (!isFinished() && ((System.currentTimeMillis() - START_TIME) < timeoutMs)) {
            if (isCanceled()) {
                throw new TaskCanceledException();
            }

            try {
                Thread.sleep(1);
            } catch (Exception e) {
            }
        }

        throwIfError();

        if (!isFinished()) {
            throw new TaskTimeoutException();
        }

        return mResult;
    }

    /**
     * 処理の完了待ちを行う
     */
    @Deprecated
    public T await() throws Exception {
        return await(mTimeoutMs);
    }

    /**
     * エラー内容を取得する
     */
    public Exception getError() {
        return mError;
    }

    /**
     * エラーを持っていたら投げる
     */
    public void throwIfError() throws Exception {
        if (mError != null) {
            throw mError;
        }
    }

    /**
     * タスクがキャンセルされていたら例外を投げて処理を強制終了する
     */
    public void throwIfCanceled() throws TaskCanceledException {
        if (isCanceled()) {
            throw new TaskCanceledException();
        }
    }

    private boolean hasCanceledError() {
        if (mError == null) {
            return false;
        }

        return (mError instanceof TaskCanceledException)
                || (mError instanceof InterruptedException)
                || (mError instanceof InterruptedIOException);
    }

    /**
     * タスクがキャンセル状態であればtrue
     */
    public boolean isCanceled() {
        if (hasCanceledError()) {
            return true;
        }

        // キャンセルシグナルに一つでも引っかかったらtrue
        for (Signal signal : mCancelSignals) {
            if (signal.is(this)) {
                return true;
            }
        }

        return false;
    }

    public boolean isFinished() {
        synchronized (this) {
            return mState == State.Finished;
        }
    }

    public boolean hasError() {
        synchronized (this) {
            return mError != null;
        }
    }

    private void handleCanceled() {
        if (mCancelCallback == null) {
            return;
        }
        mCancelCallback.call(this);
    }

    private void handleCompleted(T next) {
        if (mCompletedCallback == null) {
            return;
        }

        mCompletedCallback.call(next, this);
    }

    @Deprecated
    private void handleChain() {
        // 連続実行タスクが残っているなら、チェーンで実行を開始する
        if (mChainTask != null) {
            mChainTask.start();
            mChainTask = null;
        }
    }

    private void handleFailed(Exception error) {
        // タスクがキャンセルされた場合
        if (mErrorCallback == null) {
            return;
        }

        mErrorCallback.call(error, this);
    }

    private void handleFinalize() {
        if (mFinalizeCallback == null) {
            return;
        }
        mFinalizeCallback.call(this);
    }


    BackgroundTask<T> completed(Action1<T> completedCallback) {
        synchronized (this) {
            mCompletedCallback = completedCallback;

            if (mState == BackgroundTask.State.Finished) {
                // タスクが終わってしまっているので、ハンドリングも行う
                if (!isCanceled() && !hasError()) {
                    handleCompleted(getResult());
                }
            }

            return this;
        }
    }

    BackgroundTask<T> canceled(Action0<T> cancelCallback) {
        synchronized (this) {
            mCancelCallback = cancelCallback;
            if (mState == BackgroundTask.State.Finished && isCanceled()) {
                handleCanceled();
            }
            return this;
        }
    }

    BackgroundTask<T> failed(ErrorAction<T> errorCallback) {
        synchronized (this) {
            mErrorCallback = errorCallback;
            if (mState == BackgroundTask.State.Finished) {
                // タスクが終わってしまっているので、ハンドリングする
                if (!isCanceled() && hasError()) {
                    mCallbackQueue.run(mCallbackTime, () -> {
                        handleFailed(getError());
                    });
                }
            }
            return this;
        }
    }

    BackgroundTask<T> finalized(Action0 finalizeCallback) {
        synchronized (this) {
            mFinalizeCallback = finalizeCallback;
            if (mState == State.Finished) {
                mCallbackQueue.run(mCallbackTime, () -> {
                    handleFinalize();
                });
            }
            return this;
        }
    }

    void setResult(T result) {
        synchronized (this) {
            mState = State.Finished;
            mResult = result;
        }

        mCallbackQueue.run(mCallbackTime, () -> {

            if (isCanceled()) {
                handleCanceled();
            } else {
                try {
                    handleCompleted(result);
                    handleFinalize();
                    // 次のタスクを実行開始する
                    handleChain();
                } catch (Exception error) {
                    // Completed処理に失敗した
                    mResult = null;
                    setError(error);
                }
            }
        });
    }

    void setError(Exception error) {
        synchronized (this) {
            mState = State.Finished;
            mError = error;
        }

        mCallbackQueue.run(mCallbackTime, () -> {
            if (isCanceled()) {
                handleCanceled();
            } else {
                handleFailed(error);
                handleFinalize();
            }
        });
    }

    /**
     * 非同期処理を記述する
     */
    public interface Async<T> {
        T call(BackgroundTask<T> task) throws Exception;
    }

    /**
     * コールバックを記述する
     */
    public interface Action0<T> {
        void call(BackgroundTask<T> task);
    }

    /**
     * 非同期処理後のコールバックを記述する
     */
    public interface Action1<T> {
        void call(T it, BackgroundTask<T> task);
    }

    /**
     * 非同期処理後のコールバックを記述する
     */
    public interface ErrorAction<T> {
        void call(Exception it, BackgroundTask<T> task);
    }

    /**
     * 各種チェック用のコールバック関数
     * <p>
     * cancel()メソッドを呼び出すか、このコールバックがisCanceled()==trueになった時点でキャンセル扱いとなる。
     */
    public interface Signal<T> {
        /**
         * キャンセルする場合はtrueを返す
         */
        boolean is(BackgroundTask<T> task);
    }

}
