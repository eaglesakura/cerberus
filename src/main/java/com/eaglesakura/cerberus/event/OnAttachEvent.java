package com.eaglesakura.cerberus.event;

import com.eaglesakura.cerberus.LifecycleEvent;
import com.eaglesakura.cerberus.LifecycleState;

import android.content.Context;
import android.support.annotation.NonNull;

public class OnAttachEvent implements LifecycleEvent {

    @NonNull
    Context mContext;

    public OnAttachEvent(@NonNull Context context) {
        mContext = context;
    }

    @Override
    public LifecycleState getState() {
        return LifecycleState.OnAttach;
    }

    @NonNull
    public Context getContext() {
        return mContext;
    }
}
