package com.eaglesakura.cerberus;


/**
 * Activity/Fragmentのライフサイクル状態を示す
 */
public enum LifecycleState {
    /**
     * Newされたばかり
     */
    NewObject,
    OnAttach,
    OnCreateView,
    OnCreate,
    OnActivityResult,
    OnStart,
    OnRestoreInstanceState,
    OnResume,
    OnCreateOptionsMenu,
    OnSaveInstanceState,
    OnPause,
    OnStop,
    OnDestroyView,
    OnDestroy,
}
