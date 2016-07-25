package com.eaglesakura.android.rx;

/**
 *
 */
@Deprecated
public class RxTaskBuilder<T> extends BackgroundTaskBuilder<T> {
    @Deprecated
    public RxTaskBuilder(SubscriptionController subscriptionController) {
        super(subscriptionController);
    }

    @Deprecated
    public RxTaskBuilder(BackgroundTaskBuilder parent, PendingCallbackQueue subscriptionController) {
        super(parent, subscriptionController);
    }
}
