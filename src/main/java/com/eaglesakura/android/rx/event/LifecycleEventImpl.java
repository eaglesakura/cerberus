package com.eaglesakura.android.rx.event;

import com.eaglesakura.android.rx.LifecycleEvent;
import com.eaglesakura.android.rx.LifecycleState;

import android.support.annotation.NonNull;

public class LifecycleEventImpl implements LifecycleEvent {
    @NonNull
    final LifecycleState mState;

    public LifecycleEventImpl(@NonNull LifecycleState state) {
        mState = state;
    }

    @Override
    public LifecycleState getState() {
        return mState;
    }
}
