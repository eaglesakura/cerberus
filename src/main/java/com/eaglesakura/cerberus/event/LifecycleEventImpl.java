package com.eaglesakura.cerberus.event;

import com.eaglesakura.cerberus.LifecycleEvent;
import com.eaglesakura.cerberus.LifecycleState;

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
