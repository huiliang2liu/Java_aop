package com.lhl.aop;

import com.lhl.aop.method.IMethod;
import com.lhl.aop.inter.InvokeError;
import com.lhl.aop.inter.StatisticalTime;
import com.lhl.aop.inter.TimeOutError;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

class ScheduledRunnable extends AopRunnable {
    private ScheduledFuture future;

    public ScheduledRunnable(Object object, Method superMethod, Method method, Object[] args, IMethod lhlMethod, TimeOutError timeOutError, InvokeError invokeError, StatisticalTime statisticalTime, ScheduledExecutorService scheduled) {
        super(object, superMethod, method, args, lhlMethod, timeOutError, invokeError, statisticalTime);
        future = scheduled.scheduleAtFixedRate(this, lhlMethod.delay(), lhlMethod.period(), lhlMethod.timeUnit());
    }

    @Override
    public void objectNull() {
        super.objectNull();
        future.cancel(false);
        System.out.println("objectNull");
    }

    @Override
    public void cancel(Object o) {
        super.cancel(o);
        if (lhlMethod.isCancel() && (boolean) o)
            future.cancel(false);
    }
}
