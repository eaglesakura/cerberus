package com.eaglesakura.cerberus.lambda;

/**
 * ラムダ式 / メソッド参照を使いやすくするためのUtil
 * @param <T>
 */
public interface Action1<T> {
    void action(T it);
}
