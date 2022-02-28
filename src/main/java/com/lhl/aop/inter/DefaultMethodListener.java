package com.lhl.aop.inter;


import com.lhl.aop.method.IMethod;

import java.lang.reflect.Method;

public interface DefaultMethodListener {

    void invoke(Object o, Method superMethod, Method method, Object[] args, IMethod lhlMethod,
                       TimeOutError timeOutError, InvokeError invokeError, StatisticalTime statisticalTime);
}
