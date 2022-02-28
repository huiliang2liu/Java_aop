package com.lhl.aop.method;

import com.lhl.aop.LoadClass;

import java.util.concurrent.TimeUnit;

public class AbsMethod implements IMethod {
    protected boolean statistical = false;
    protected int thread = LoadClass.DEFAULT;
    protected long delay = 1000;
    protected long period = 1000;
    protected TimeUnit timeUnit = TimeUnit.MINUTES;
    protected boolean cancel = false;
    protected long timeout = 0;

    @Override
    public boolean isStatistical() {
        return statistical;
    }

    @Override
    public int thread() {
        return thread;
    }

    @Override
    public long delay() {
        return delay;
    }

    @Override
    public long period() {
        return period;
    }

    @Override
    public TimeUnit timeUnit() {
        return timeUnit;
    }

    @Override
    public boolean isCancel() {
        return cancel;
    }

    @Override
    public boolean isTimeout(long time) {
        if (timeout <= 0)
            return false;
        return time > timeout;
    }
}
