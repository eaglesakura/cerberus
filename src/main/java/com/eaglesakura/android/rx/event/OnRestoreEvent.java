package com.eaglesakura.android.rx.event;

import com.eaglesakura.android.rx.LifecycleEvent;
import com.eaglesakura.android.rx.LifecycleState;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class OnRestoreEvent implements LifecycleEvent {
    @NonNull
    final Bundle mBundle;

    public OnRestoreEvent(Bundle bundle) {
        mBundle = bundle;
    }

    @Override
    public LifecycleState getState() {
        return LifecycleState.OnRestoreInstanceState;
    }

    @NonNull
    public Bundle getBundle() {
        return mBundle;
    }
}
