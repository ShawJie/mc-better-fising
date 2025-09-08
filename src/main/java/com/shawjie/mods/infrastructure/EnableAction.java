package com.shawjie.mods.action;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableAction {

    Class<? extends CallbackAction>[] classes() default {};
}
