package com.eaglesakura.android.rx.event;

import com.eaglesakura.android.rx.LifecycleEvent;
import com.eaglesakura.android.rx.LifecycleState;

import android.os.Bundle;
import android.support.annotation.Nullable;

public class OnCreateEvent implements LifecycleEvent {
    @Nullable
    final Bundle mBundle;

    public OnCreateEvent(Bundle bundle) {
        mBundle = bundle;
    }

    @Override
    public LifecycleState getState() {
        return LifecycleState.OnCreated;
    }

    @Nullable
    public Bundle getBundle() {
        return mBundle;
    }
}
