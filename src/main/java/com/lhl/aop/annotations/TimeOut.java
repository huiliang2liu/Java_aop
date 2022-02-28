package com.lhl.aop.annotations;


import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TimeOut {
    long timeout() default 15000;
}
