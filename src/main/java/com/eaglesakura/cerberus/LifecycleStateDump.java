package com.eaglesakura.cerberus;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;

/**
 *
 */
class LifecycleStateDump {

    /**
     * 現在のステートを管理する
     */
    private final Lifecycle.Event mState;

    /**
     * ステート変更番号
     * 必ずインクリメントされる
     */
    private final int mStateChangeCount;

    LifecycleStateDump(Lifecycle.Event state, int changeNumber) {
        mState = state;
        mStateChangeCount = changeNumber;
    }

    public Lifecycle.Event getState() {
        return mState;
    }

    public int getStateChangeCount() {
        return mStateChangeCount;
    }

    /**
     * オブジェクトが作成状態であればtrue
     */
    public boolean isCreated() {
        return mState.ordinal() >= Lifecycle.Event.ON_CREATE.ordinal();
    }

    /**
     * Foreground状態であればtrue
     */
    public boolean isForeground() {
        return mState == Lifecycle.Event.ON_RESUME;
    }

    /**
     * オブジェクトが廃棄状態であればtrue
     */
    public boolean isDestroyed() {
        return mState == Lifecycle.Event.ON_DESTROY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LifecycleStateDump that = (LifecycleStateDump) o;

        if (mStateChangeCount != that.mStateChangeCount) return false;
        return mState == that.mState;
    }

    @Override
    public int hashCode() {
        int result = mState != null ? mState.hashCode() : 0;
        result = 31 * result + mStateChangeCount;
        return result;
    }
}
