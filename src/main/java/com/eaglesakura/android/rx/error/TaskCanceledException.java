package com.eaglesakura.android.rx.error;

public class TaskCanceledException extends RxTaskException {
    public TaskCanceledException() {
    }

    public TaskCanceledException(String detailMessage) {
        super(detailMessage);
    }

    public TaskCanceledException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public TaskCanceledException(Throwable throwable) {
        super(throwable);
    }
}
