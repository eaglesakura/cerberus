package com.eaglesakura.android.rx;

import com.eaglesakura.android.rx.error.TaskCanceledException;

import rx.Observable;

/**
 *
 */
public class RxTask<T> {

    /**
     * 外部から指定されたキャンセルチェック
     */
    CancelSignal mUserCancelSignal;

    /**
     * 購読対象からのキャンセルチェック
     */
    CancelSignal mSubscribeCancelSignal;

    /**
     * 受信したエラー
     */
    Throwable mError;

    /**
     * 戻り値
     */
    T mResult;

    State mState = State.Building;

    /**
     * コールバック対象を指定する
     *
     * デフォルトはFire And Forget
     */
    ObserveTarget mObserveTarget = ObserveTarget.FireAndForget;

    /**
     * 処理本体
     */
    Observable<T> mObservable;

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
     * 処理の完了待ちを行う
     */
    public T await() throws Throwable {
        return mObservable.toBlocking().first();
    }

    /**
     * エラー内容を取得する
     */
    public Throwable getError() {
        return mError;
    }

    /**
     * エラーを持っていたら投げる
     */
    public void throwIfError() throws Throwable {
        if (mError != null) {
            throw mError;
        }
    }

    /**
     * タスクがキャンセル状態であればtrue
     */
    public boolean isCanceled() {
        if (mUserCancelSignal != null && mUserCancelSignal.isCanceled(this)) {
            return true;
        }

        if (mSubscribeCancelSignal != null & mSubscribeCancelSignal.isCanceled(this)) {
            return true;
        }

        if (mError != null && (mError instanceof TaskCanceledException)) {
            return true;
        }

        return false;
    }

    /**
     * 非同期処理を記述する
     */
    public interface Async<T> {
        T call(RxTask<T> task) throws Throwable;
    }

    /**
     * コールバックを記述する
     */
    public interface Action0<T> {
        void call(RxTask<T> task);
    }

    /**
     * 非同期処理後のコールバックを記述する
     */
    public interface Action1<T> {
        void call(T it, RxTask<T> task);
    }


    /**
     * 非同期処理後のコールバックを記述する
     */
    public interface ErrorAction<T> {
        void call(Throwable it, RxTask<T> task);
    }

    /**
     * キャンセルチェック用のコールバック
     * <p/>
     * cancel()メソッドを呼び出すか、このコールバックがisCanceled()==trueになった時点でキャンセル扱いとなる。
     */
    public interface CancelSignal<T> {
        /**
         * キャンセルする場合はtrueを返す
         */
        boolean isCanceled(RxTask<T> task);
    }

}
