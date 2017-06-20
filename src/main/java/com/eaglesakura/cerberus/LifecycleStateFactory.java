package com.eaglesakura.cerberus;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

/**
 * 現在のライフサイクル状態の保持・生成を行う
 *
 * LifecycleOwnerとリンクし、自動的に管理される。
 */
class LifecycleStateFactory {

    LifecycleStateDump mCurrentState = new LifecycleStateDump(null, 0);

    private LifecycleStateFactory() {

    }

    public LifecycleStateDump getCurrentState() {
        synchronized (this) {
            return mCurrentState;
        }
    }

    private void onNextState(Lifecycle.Event event) {
        synchronized (this) {
            mCurrentState = new LifecycleStateDump(event, mCurrentState.getStateChangeCount() + 1);
        }
    }

    public static LifecycleStateFactory newInstance(LifecycleOwner owner) {
        Lifecycle lifecycle = owner.getLifecycle();

        LifecycleStateFactory result = new LifecycleStateFactory();
        LifecycleObserver observer = new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
            public void onCreate() {
//                Log.d("Event", "Call/onCreate");
                result.onNextState(Lifecycle.Event.ON_CREATE);
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_START)
            public void onStart() {
//                Log.d("Event", "Call/onStart");
                result.onNextState(Lifecycle.Event.ON_START);
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            public void onResume() {
//                Log.d("Event", "Call/onResume");
                result.onNextState(Lifecycle.Event.ON_RESUME);
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            public void onPause() {
//                Log.d("Event", "Call/onPause");
                result.onNextState(Lifecycle.Event.ON_PAUSE);
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
            public void onStop() {
//                Log.d("Event", "Call/onStop");
                result.onNextState(Lifecycle.Event.ON_STOP);
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            public void onDestroy() {
//                Log.d("Event", "Call/onDestroy");
                result.onNextState(Lifecycle.Event.ON_DESTROY);
                lifecycle.removeObserver(this);
            }
        };
        lifecycle.addObserver(observer);

        return result;
    }
}
