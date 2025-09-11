package com.shawjie.mods.infrastructure;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Ordered {

    int value() default Integer.MAX_VALUE;
}
