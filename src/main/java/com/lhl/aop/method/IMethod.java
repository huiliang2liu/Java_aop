package com.lhl.aop.method;

import java.util.concurrent.TimeUnit;

public interface IMethod {
    boolean isStatistical();

    int thread();

    long delay();

    long period();

    TimeUnit timeUnit();

    boolean isCancel();

    boolean isTimeout(long time);
}
