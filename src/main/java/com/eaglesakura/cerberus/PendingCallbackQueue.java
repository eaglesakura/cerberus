package com.eaglesakura.cerberus;


import com.eaglesakura.cerberus.error.TaskCanceledException;

import android.arch.lifecycle.GenericLifecycleObserver;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 実行対象のスレッドと、コールバック対象のスレッドをそれぞれ管理する。
 * <p>
 * Fragment等と関連付けられ、そのライフサイクルを離れると自動的にコールバックを呼びださなくする。
 */
public class PendingCallbackQueue {

    /**
     * 処理をワンテンポ遅らせるためのハンドラ
     */
    static Handler sHandler = new Handler(Looper.getMainLooper());


    /**
     * 通常処理されるSubscription
     */
    private CompositeDisposable mSubscription = new CompositeDisposable();

    /**
     * 各Observeステートごとの保留タスク管理
     */
    private List<StateController> mStateControllers = new ArrayList<>();

    private ThreadControllerImpl mThreadController = new ThreadControllerImpl();

    private LifecycleStateFactory mStateFactory;

    public PendingCallbackQueue() {
        for (CallbackTime callbackTime : CallbackTime.values()) {
            mStateControllers.add(callbackTime.newStateController());
        }
    }

    /**
     * 現在の状態を取得する
     */
    @NonNull
    LifecycleStateDump getCurrentState() {
        return mStateFactory.getCurrentState();
    }

    ThreadControllerImpl getThreadController() {
        return mThreadController;
    }

    public PendingCallbackQueue bind(LifecycleOwner owner) {
        mStateFactory = LifecycleStateFactory.newInstance(owner);
        owner.getLifecycle().addObserver(new GenericLifecycleObserver() {
            @Override
            public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    mThreadController.dispose();
                    mSubscription.dispose();
                }

                // 保留タスクがあれば流すように促す
                for (StateController ctrl : mStateControllers) {
                    ctrl.onNext(PendingCallbackQueue.this);
                }
            }

            @Override
            public Object getReceiver() {
                return this;
            }
        });
        return this;
    }

    PendingCallbackQueue add(CallbackTime time, Disposable s) {
        if (time != CallbackTime.FireAndForget) {
            mSubscription.add(s);
        }
        return this;
    }

    PendingCallbackQueue remove(Disposable s) {
        mSubscription.remove(s);
        return this;
    }

    private StateController getController(CallbackTime time) {
        return mStateControllers.get(time.ordinal());
    }

    /**
     * @param time      コールしたいタイミング
     * @param dumpState タスク開始時のステート
     */
    public boolean isCanceled(CallbackTime time, LifecycleStateDump dumpState) {
        return getController(time).isCanceled(this, dumpState);
    }

    /**
     * 実行クラスを渡し、処理を行わせる。
     * <p>
     * 実行保留中であれば一旦キューに貯め、resumeのタイミングでキューを全て実行させる。
     */
    public void run(CallbackTime target, Runnable callback) {
        getController(target).run(this, new PendingTask(callback, mStateFactory.getCurrentState()));
    }

    /**
     * 実行クラスを渡し、実行待ちを行う。
     *
     * デッドロック等の事情によりタイムアウトやフリーズの原因になるので、実行には注意すること。
     *
     * @param time      実行条件
     * @param callback  実行内容
     * @param timeoutMs 待ち時間
     */
    @Deprecated
    public void runWithWait(CallbackTime time, Runnable callback, long timeoutMs) throws TimeoutException {
        Object lock = new Object();
        run(time, () -> {
            try {
                callback.run();
            } finally {
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        });
        synchronized (lock) {
            try {
                lock.wait(timeoutMs);
            } catch (Exception e) {
            }
        }
    }

    /**
     * キャンセルコールバックを指定して実行待ちを行う
     */
    public void runWithWait(CallbackTime callbackTime, Runnable callback, @NonNull CancelCallback cancelCallback) throws TaskCanceledException {
        Object[] holder = new Object[1];
        run(callbackTime, () -> {
            try {
                callback.run();
            } finally {
                holder[0] = new Object();
            }
        });

        while (holder[0] == null) {
            try {
                if (cancelCallback.isCanceled()) {
                    throw new TaskCanceledException();
                }
            } catch (Throwable e) {
                throw new TaskCanceledException(e);
            }
        }
    }

    public interface CancelCallback {
        boolean isCanceled() throws Exception;
    }

    static class PendingTask {
        /**
         * タスク本体
         */
        final Runnable mAction;

        /**
         * タスクが発行されたタイミングでのステート
         */
        final LifecycleStateDump mDumpState;

        public PendingTask(Runnable action, LifecycleStateDump dumpState) {
            mAction = action;
            mDumpState = dumpState;
        }

        public void run() {
            mAction.run();
        }

        public LifecycleStateDump getDumpState() {
            return mDumpState;
        }
    }
}
