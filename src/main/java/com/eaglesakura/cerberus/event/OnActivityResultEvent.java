package com.eaglesakura.cerberus.event;

import com.eaglesakura.cerberus.LifecycleEvent;
import com.eaglesakura.cerberus.LifecycleState;

import android.content.Intent;

/**
 * OnActivityResult
 */
public class OnActivityResultEvent implements LifecycleEvent {
    int mRequestCode;

    int mResult;

    Intent mData;

    public OnActivityResultEvent(int requestCode, int result, Intent data) {
        mRequestCode = requestCode;
        mResult = result;
        mData = data;
    }

    public int getRequestCode() {
        return mRequestCode;
    }

    public int getResult() {
        return mResult;
    }

    public Intent getData() {
        return mData;
    }

    @Override
    public LifecycleState getState() {
        return LifecycleState.OnActivityResult;
    }
}
