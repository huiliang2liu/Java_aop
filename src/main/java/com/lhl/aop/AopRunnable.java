package com.lhl.aop;

import com.lhl.aop.method.IMethod;
import com.lhl.aop.inter.InvokeError;
import com.lhl.aop.inter.StatisticalTime;
import com.lhl.aop.inter.TimeOutError;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

class AopRunnable implements Runnable {
    private Method method;
    private WeakReference<Object> weakReference;
    private Object[] args;
    private InvokeError invokeError;
    private TimeOutError timeOutError;
    protected IMethod lhlMethod;
    private StatisticalTime statisticalTime;
    private Method superMethod;

    public AopRunnable(Object object, Method superMethod, Method method, Object[] args, IMethod lhlMethod,
                       TimeOutError timeOutError, InvokeError invokeError, StatisticalTime statisticalTime) {
        this.method = method;
        this.weakReference = new WeakReference<>(object);
        this.args = args;
        this.invokeError = invokeError;
        this.timeOutError = timeOutError;
        this.lhlMethod = lhlMethod;
        this.statisticalTime = statisticalTime;
        this.superMethod = superMethod;
    }

    @Override
    public final void run() {
        Object o = weakReference.get();
        if (o == null) {
            objectNull();
            return;
        }
        long startTime = System.currentTimeMillis();
        try {
            cancel(superMethod.invoke(weakReference.get(), args));
        } catch (Throwable e) {
            if (invokeError != null)
                invokeError.error(e);
        }
        long runTime = System.currentTimeMillis() - startTime;
        if (lhlMethod.isTimeout(runTime) && timeOutError != null)
            timeOutError.timeOut(o, method, runTime);
        if (lhlMethod.isStatistical() && statisticalTime != null)
            statisticalTime.time(o, method, runTime);
    }

    public void objectNull() {

    }

    public void cancel(Object o) {

    }
}
