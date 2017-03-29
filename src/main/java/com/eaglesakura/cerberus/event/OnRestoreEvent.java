package com.eaglesakura.cerberus.event;

import com.eaglesakura.cerberus.LifecycleEvent;
import com.eaglesakura.cerberus.LifecycleState;

import android.os.Bundle;
import android.support.annotation.NonNull;

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
