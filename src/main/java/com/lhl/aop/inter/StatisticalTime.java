package com.lhl.aop.inter;

import java.lang.reflect.Method;

public interface StatisticalTime {
    void time(Object object, Method method, long time);
}
