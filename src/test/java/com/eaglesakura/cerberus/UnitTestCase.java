package com.eaglesakura.cerberus;

import com.eaglesakura.android.AndroidSupportTestCase;

import org.robolectric.annotation.Config;

@Config(constants = BuildConfig.class, packageName = BuildConfig.APPLICATION_ID, sdk = 23)
public abstract class UnitTestCase extends AndroidSupportTestCase {
}
