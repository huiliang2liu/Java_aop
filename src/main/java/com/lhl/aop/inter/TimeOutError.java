package com.lhl.aop.inter;

import java.lang.reflect.Method;

public interface TimeOutError {
    void timeOut(Object object, Method method, long time);
}
