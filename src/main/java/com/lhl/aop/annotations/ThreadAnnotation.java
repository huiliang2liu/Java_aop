package com.lhl.aop.annotations;

import com.lhl.aop.AnnotationLoadClass;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ThreadAnnotation {

    int thread() default AnnotationLoadClass.DEFAULT;

    long delay() default 0;

    long period() default 3000;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

}
