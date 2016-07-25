package com.eaglesakura.android.rx;

/**
 * 非同期処理のコールバック待ちを行う場所
 */
@Deprecated
public enum ObserveTarget {
    /**
     * onResume - onPauseの間のみコールバックを受け付ける
     */
    Foreground {
        @Override
        public CallbackTime asCallbackTarget() {
            return CallbackTime.Foreground;
        }
    },

    /**
     * 現在の前面処理のみコールバックを受け付ける。
     *
     * onPauseのタイミングで、保留されているキューは全て廃棄される。
     */
    CurrentForeground {
        @Override
        public CallbackTime asCallbackTarget() {
            return CallbackTime.CurrentForeground;
        }
    },

    /**
     * onCreate - onDestroyの間のみコールバックを受け付ける
     */
    Alive {
        @Override
        public CallbackTime asCallbackTarget() {
            return CallbackTime.Alive;
        }
    },

    /**
     * 常にコールバックを受け付ける
     */
    FireAndForget {
        @Override
        public CallbackTime asCallbackTarget() {
            return CallbackTime.FireAndForget;
        }
    };

    public abstract CallbackTime asCallbackTarget();
}
