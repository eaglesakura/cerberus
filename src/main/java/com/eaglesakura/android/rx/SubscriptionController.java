package com.eaglesakura.android.rx;


/**
 * 実行対象のスレッドと、コールバック対象のスレッドをそれぞれ管理する。
 * <p>
 * Fragment等と関連付けられ、そのライフサイクルを離れると自動的にコールバックを呼びださなくする。
 *
 * @see PendingCallbackQueue
 */
@Deprecated
public class SubscriptionController extends PendingCallbackQueue {
    public SubscriptionController() {
    }
}
