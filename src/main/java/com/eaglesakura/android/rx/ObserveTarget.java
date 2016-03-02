package com.eaglesakura.android.rx;

/**
 * 非同期処理のコールバック待ちを行う場所
 */
public enum ObserveTarget {
    /**
     * onResume - onPauseの間のみコールバックを受け付ける
     */
    Forground,

    /**
     * onCreate - onDestroyの間のみコールバックを受け付ける
     */
    Alive,

    /**
     * 常にコールバックを受け付ける
     */
    FireAndForget,
}
