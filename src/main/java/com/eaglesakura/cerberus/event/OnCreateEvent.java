package com.eaglesakura.cerberus.event;

import com.eaglesakura.cerberus.LifecycleEvent;
import com.eaglesakura.cerberus.LifecycleState;

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
