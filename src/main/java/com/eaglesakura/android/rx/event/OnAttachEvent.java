package com.eaglesakura.android.rx.event;

import com.eaglesakura.android.rx.LifecycleEvent;
import com.eaglesakura.android.rx.LifecycleState;

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
