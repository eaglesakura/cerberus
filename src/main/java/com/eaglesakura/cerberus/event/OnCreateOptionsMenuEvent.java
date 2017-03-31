package com.eaglesakura.cerberus.event;

import com.eaglesakura.cerberus.LifecycleEvent;
import com.eaglesakura.cerberus.LifecycleState;

import android.view.Menu;
import android.view.MenuInflater;


public class OnCreateOptionsMenuEvent implements LifecycleEvent {
    Menu menu;
    MenuInflater inflater;

    public OnCreateOptionsMenuEvent(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        this.inflater = inflater;
    }

    public Menu getMenu() {
        return menu;
    }

    public MenuInflater getInflater() {
        return inflater;
    }

    @Override
    public LifecycleState getState() {
        return LifecycleState.OnCreateOptionsMenu;
    }
}
