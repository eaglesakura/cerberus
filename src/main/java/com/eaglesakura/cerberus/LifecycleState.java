package com.eaglesakura.cerberus;


/**
 * Activity/Fragmentのライフサイクル状態を示す
 */
public enum LifecycleState {
    /**
     * Newされたばかり
     */
    NewObject,

    /**
     * OnCreateViewが完了した
     */
    OnAttach,

    /**
     * OnCreateViewが完了した
     */
    OnViewCreated,

    /**
     * OnCreateが完了した
     */
    OnCreated,

    /**
     * OnStartが完了した
     */
    OnStarted,

    /**
     * インスタンス状態の保存を行う
     */
    OnRestoreInstanceState,

    /**
     * OnResumeが完了した
     */
    OnResumed,

    /**
     * インスタンス状態の保存を行う
     */
    OnSaveInstanceState,

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
    OnViewDestroyed,

    /**
     * OnDestroyが完了した
     */
    OnDestroyed,
}
