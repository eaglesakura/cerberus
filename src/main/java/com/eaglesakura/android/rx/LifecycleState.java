package com.eaglesakura.android.rx;


/**
 * Activity/Fragmentのライフサイクル状態を示す
 */
public enum LifecycleState {
    /**
     * Newされたばかり
     */
    NewObject,

    /**
     * OnCreateが完了した
     */
    OnCreated,

    /**
     * OnStartが完了した
     */
    OnStarted,

    /**
     * OnResumeが完了した
     */
    OnResumed,

    /**
     * OnPauseが完了した
     */
    OnPaused,

    /**
     * OnStopが完了した
     */
    OnStopped,

    /**
     * OnDestroyが完了した
     */
    OnDestroyed,
}
