package com.igg.boot.framework.autoconfigure.tcc.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TccTopic {
    String name();
}
