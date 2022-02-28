package com.lhl.aop.method;

import com.lhl.aop.annotations.StatisticalTimeAnnotation;
import com.lhl.aop.annotations.ThreadAnnotation;
import com.lhl.aop.annotations.TimeOut;

import java.lang.reflect.Method;

public class AnnotationMethod extends AbsMethod {

    public AnnotationMethod(Method method) {
        super();
        if (method.isAnnotationPresent(ThreadAnnotation.class)) {
            ThreadAnnotation threadAnnotation = method.getAnnotation(ThreadAnnotation.class);
            thread = threadAnnotation.thread();
            period = threadAnnotation.period();
            delay = threadAnnotation.delay();
            timeUnit = threadAnnotation.timeUnit();
        }
        if (method.isAnnotationPresent(TimeOut.class))
            timeout = method.getAnnotation(TimeOut.class).timeout();
        if (method.isAnnotationPresent(StatisticalTimeAnnotation.class))
            statistical = true;
        Class c = method.getReturnType();
        if (c.isAssignableFrom(boolean.class) || c.isAssignableFrom(Boolean.class))
            cancel = true;
    }
}
