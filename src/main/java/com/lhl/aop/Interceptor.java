package com.lhl.aop;

import com.lhl.aop.method.IMethod;
import com.lhl.aop.inter.InvokeError;
import com.lhl.aop.inter.StatisticalTime;
import com.lhl.aop.inter.TimeOutError;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.Map;

public class Interceptor {
    private StatisticalTime statisticalTime;
    private TimeOutError timeOutError;
    private Map<Method, IMethod> methodHashMap;
    private boolean hasStatisticalTime;
    private InvokeError invokeError;
    private LoadClass loadClass;

    public Interceptor(LoadClass loadClass, StatisticalTime statisticalTime, TimeOutError timeOutError, Map<Method, IMethod> methodHashMap,
                       InvokeError invokeError) {
        this.timeOutError = timeOutError;
        this.statisticalTime = statisticalTime;
        hasStatisticalTime = statisticalTime != null;
        this.methodHashMap = methodHashMap;
        this.invokeError = invokeError;
        this.loadClass = loadClass;
    }

    @RuntimeType
    public final Object interceptor(@This Object proxy, @Origin Method method,
                                    @SuperMethod Method superMethod, @AllArguments Object[] args) throws Exception {
        return invoke(proxy, method, superMethod, args);
    }

    public Object invoke(Object proxy, Method method,
                         Method superMethod, Object[] args) {
        IMethod threadEnum = methodHashMap.get(method);
        if (threadEnum == null || threadEnum.thread() == AnnotationLoadClass.DEFAULT) {
            long startTime = System.currentTimeMillis();
            Object ret = null;
            try {
                ret = superMethod.invoke(proxy, args);
            } catch (Throwable e) {
                if (invokeError != null)
                    invokeError.error(e);
            }
            long runTime = System.currentTimeMillis() - startTime;
            if (threadEnum.isTimeout(runTime) && timeOutError != null)
                timeOutError.timeOut(proxy, method, runTime);
            if (threadEnum.isStatistical() && statisticalTime != null)
                statisticalTime.time(proxy, method, runTime);
            return ret;
        }
        loadClass.runOtherThread(proxy, superMethod, method, args, threadEnum, timeOutError, invokeError, statisticalTime);
        return null;
    }
}
