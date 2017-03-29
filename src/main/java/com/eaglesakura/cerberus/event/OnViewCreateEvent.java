package com.eaglesakura.cerberus.event;

import com.eaglesakura.cerberus.LifecycleEvent;
import com.eaglesakura.cerberus.LifecycleState;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public class OnViewCreateEvent implements LifecycleEvent {
    @NonNull
    final LayoutInflater inflater;

    @Nullable
    final ViewGroup container;

    @Nullable
    final Bundle savedInstanceState;

    public OnViewCreateEvent(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        this.container = container;
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    public LifecycleState getState() {
        return LifecycleState.OnViewCreated;
    }

    @NonNull
    public LayoutInflater getInflater() {
        return inflater;
    }

    @Nullable
    public ViewGroup getContainer() {
        return container;
    }

    @Nullable
    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }
}
