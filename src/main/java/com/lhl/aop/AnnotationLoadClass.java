package com.lhl.aop;


import com.lhl.aop.method.AnnotationMethod;
import com.lhl.aop.method.IMethod;

import java.lang.reflect.Method;

public class AnnotationLoadClass extends LoadClass {
    private static volatile AnnotationLoadClass loadClass;

    public static AnnotationLoadClass getInstance() {
        if (loadClass == null)
            synchronized (AnnotationLoadClass.class) {
                if (loadClass == null)
                    loadClass = new AnnotationLoadClass();
            }
        return loadClass;
    }

    private AnnotationLoadClass() {
        super();
    }

    @Override
    protected IMethod method2method(Method method) {
        return new AnnotationMethod(method);
    }
}
