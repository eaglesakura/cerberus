package com.eaglesakura.android.rx;

import android.content.Context;

import com.eaglesakura.util.LogUtil;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, packageName = BuildConfig.APPLICATION_ID, sdk = 21)
public abstract class CiJUnitTester {

    private Context mContext;

    public Context getContext() {
        return mContext;
    }

    private void initializeLogger() {
        ShadowLog.stream = System.out;
        LogUtil.setOutput(true);
        LogUtil.setLogger(new LogUtil.Logger() {
            @Override
            public void i(String msg) {
                try {
                    StackTraceElement[] trace = new Exception().getStackTrace();
                    StackTraceElement elem = trace[Math.min(trace.length - 1, 2)];
                    System.out.println("I " + String.format("%s[%d] : %s", elem.getFileName(), elem.getLineNumber(), msg));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void d(String msg) {
                try {
                    StackTraceElement[] trace = new Exception().getStackTrace();
                    StackTraceElement elem = trace[Math.min(trace.length - 1, 2)];
                    System.out.println("D " + String.format("%s[%d] : %s", elem.getFileName(), elem.getLineNumber(), msg));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Before
    public void onSetup() {
        mContext = RuntimeEnvironment.application;
        initializeLogger();
    }
}
