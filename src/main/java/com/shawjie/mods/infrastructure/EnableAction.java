package com.shawjie.mods.infrastructure;

import com.shawjie.mods.action.CallbackAction;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableAction {

    Class<? extends CallbackAction>[] classes() default {};
}
