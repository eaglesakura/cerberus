package com.eaglesakura.cerberus;

/**
 * 現在のライフサイクルイベントを示す
 */
public interface LifecycleEvent {
    LifecycleState getState();
}
