package com.lhl.aop;

import com.lhl.aop.inter.*;
import com.lhl.aop.method.IMethod;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public abstract class LoadClass implements StatisticalTime, TimeOutError, InvokeError {
    public static final int DEFAULT = 0;
    public static final int IO = 1;
    public static final int CUP = 2;
    public static final int SINGLE = 3;
    public static final int DELAY = 4;
    public static final int PERIOD = 5;
    private ClassLoader classLoader;
    private ExecutorService io;
    private ExecutorService cpu;
    private ExecutorService single;
    private ScheduledExecutorService scheduled;
    private Map<Class, Object> instanceMap = new HashMap<>();
    private InvokeError error;
    private Map<Object, TimeOutError> timeOutErrorMap = new HashMap<>();
    private Map<Object, StatisticalTime> statisticalTimeMap = new HashMap<>();
    private DefaultMethodListener loadClassListener;


    public LoadClass() {
        classLoader = LoadClass.class.getClassLoader();
        io = Executors.newCachedThreadPool(new AopThreadFactory("io"));
        cpu = Executors.newFixedThreadPool(Runtime.getRuntime()
                .availableProcessors(), new AopThreadFactory("cpu"));
        single = Executors.newSingleThreadExecutor(new AopThreadFactory("single"));
        scheduled = Executors.newScheduledThreadPool(2, new AopThreadFactory("scheduled"));
    }


    public <T> T newInstance(Class<? extends T> parent, String name, ClassLoadingStrategy strategy) {
        try {
            return (T) loadClass(parent, name, strategy).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setDefaultMethodListener(DefaultMethodListener loadClassListener) {
        this.loadClassListener = loadClassListener;
    }

    public <T> T getInstance(Class<? extends T> parent, ClassLoadingStrategy strategy) {
        if (!instanceMap.containsKey(parent))
            synchronized (parent) {
                if (!instanceMap.containsKey(parent))
                    instanceMap.put(parent, newInstance(parent, parent.getName() + "$LHL", strategy));
            }
        return (T) instanceMap.get(parent);
    }

    public void setError(InvokeError error) {
        this.error = error;
    }

    public <T> Class<T> loadClass(Class<? extends T> parent, String name, ClassLoadingStrategy strategy) {
        try {
            return (Class<T>) classLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
        }
        synchronized (this) {
            DynamicType.Builder builder = new ByteBuddy().subclass(parent);
            Method[] methods = parent.getDeclaredMethods();
            if (methods != null) {
                Map<Method, IMethod> methodMap = new HashMap<>();
                for (Method method : methods) {
                    IMethod im = method2method(method);
                    if (im == null)
                        continue;
                    methodMap.put(method, im);
                }
                Interceptor interceptor = new Interceptor(this, this,
                        this, methodMap, this);
                for (Method method : methods)
                    builder = builder.method(ElementMatchers.named(method.getName())).intercept(MethodDelegation.to(interceptor));
            }
            if (strategy != null)
                return builder.name(name).make().load(classLoader, strategy).getLoaded();
            return builder.name(name).make().load(classLoader, ClassLoadingStrategy.Default.WRAPPER).getLoaded();
        }
    }

    protected abstract IMethod method2method(Method method);


    void runOtherThread(Object o, Method superMethod, Method method, Object[] args, IMethod lhlMethod,
                        TimeOutError timeOutError, InvokeError invokeError, StatisticalTime statisticalTime) {
        switch (lhlMethod.thread()) {
            case CUP:
                cpu.submit(new AopRunnable(o, superMethod, method, args, lhlMethod, timeOutError, invokeError, statisticalTime));
                break;
            case IO:
                io.submit(new AopRunnable(o, superMethod, method, args, lhlMethod, timeOutError, invokeError, statisticalTime));
                break;
            case SINGLE:
                single.submit(new AopRunnable(o, superMethod, method, args, lhlMethod, timeOutError, invokeError, statisticalTime));
                break;
            case DELAY:
                scheduled.schedule(new AopRunnable(o, superMethod, method, args, lhlMethod, timeOutError, invokeError, statisticalTime), lhlMethod.delay(), lhlMethod.timeUnit());
                break;
            case PERIOD:
                new ScheduledRunnable(o, superMethod, method, args, lhlMethod, timeOutError, invokeError, statisticalTime, scheduled);
                break;
            default:
                defaultThread(o, superMethod, method, args, lhlMethod, timeOutError, invokeError, statisticalTime);
                break;
        }
    }

    protected void defaultThread(Object o, Method superMethod, Method method, Object[] args, IMethod lhlMethod,
                                 TimeOutError timeOutError, InvokeError invokeError, StatisticalTime statisticalTime) {
        if (loadClassListener != null)
            loadClassListener.invoke(o, superMethod, method, args, lhlMethod, timeOutError, invokeError, statisticalTime);
    }

    public void destroy() {
        cpu.shutdownNow();
        io.shutdownNow();
        single.shutdownNow();
        scheduled.shutdownNow();
        instanceMap.clear();
        timeOutErrorMap.clear();
        statisticalTimeMap.clear();
        error = null;
    }

    public void registerTimeOutError(Object o, TimeOutError error) {
        if (o == null || error == null)
            return;
        timeOutErrorMap.put(o, error);
    }

    public void unRegisterTimeOutError(Object o) {
        timeOutErrorMap.remove(o);
    }

    public void registerStatisticalTime(Object o, StatisticalTime error) {
        if (o == null || error == null)
            return;
        statisticalTimeMap.put(o, error);
    }

    public void unRegisterStatisticalTime(Object o) {
        statisticalTimeMap.remove(o);
    }

    @Override
    public void error(Throwable error) {
        if (this.error != null)
            this.error.error(error);
        else
            error.printStackTrace();
    }

    public Method changeMethod(Object o, Method method, Object[] args) {
        return method;
    }


    @Override
    public void time(Object object, Method method, long time) {
        if (statisticalTimeMap.containsKey(object))
            statisticalTimeMap.get(object).time(object, method, time);
    }

    @Override
    public void timeOut(Object object, Method method, long time) {
        if (timeOutErrorMap.containsKey(object))
            timeOutErrorMap.get(object).timeOut(object, method, time);
    }
}
