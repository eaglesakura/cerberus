package com.eaglesakura.android.rx.error;

public class RxTaskException extends Exception {
    public RxTaskException() {
    }

    public RxTaskException(String detailMessage) {
        super(detailMessage);
    }

    public RxTaskException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public RxTaskException(Throwable throwable) {
        super(throwable);
    }
}
