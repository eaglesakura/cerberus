package com.eaglesakura.cerberus.event;

import com.eaglesakura.cerberus.LifecycleEvent;
import com.eaglesakura.cerberus.LifecycleState;

import android.os.Bundle;
import android.support.annotation.NonNull;

public class OnSaveEvent implements LifecycleEvent {
    @NonNull
    final Bundle mBundle;

    public OnSaveEvent(Bundle bundle) {
        mBundle = bundle;
    }

    @Override
    public LifecycleState getState() {
        return LifecycleState.OnSaveInstanceState;
    }

    @NonNull
    public Bundle getBundle() {
        return mBundle;
    }
}