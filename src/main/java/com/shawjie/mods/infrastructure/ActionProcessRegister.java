package com.shawjie.mods.action;

import com.shawjie.mods.event.FishCatchingEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class ActionProcessRegister {

    private final List<Event<?>> localEvents = List.of(
        FishCatchingEvent.EVENT
    );

    private final Map<Class<?>, SingletonCallbackActionSupplier> actionInstanceCache = new HashMap<>();

    public ActionProcessRegister() {
        Class<?> modeInitializeClazz = detectInitializer();
        if (modeInitializeClazz == null) {
            return;
        }

        EnableAction enableActionAnno = modeInitializeClazz.getAnnotation(EnableAction.class);
        for (Class<? extends CallbackAction> enableAction : enableActionAnno.classes()) {
            Type[] genericInterfaces = enableAction.getGenericInterfaces();
            for (Type genericInterface : genericInterfaces) {
                if (genericInterface.equals(CallbackAction.class)) {
                    continue;
                }
                if (genericInterface.getTypeName().endsWith("Event") && genericInterface instanceof Class<?>) {
                    actionInstanceCache.putIfAbsent(
                        (Class<?>) genericInterface,
                        new SingletonCallbackActionSupplier(() -> instanceObject(enableAction))
                    );
                }
            }
        }
    }

    private CallbackAction instanceObject(Class<? extends CallbackAction> clazz) {
        try {
            Constructor<? extends CallbackAction> emptyConstructor = clazz.getDeclaredConstructor();
            return emptyConstructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Class<?> detectInitializer() {
        try {
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if ("onInitialize".equals(stackTraceElement.getMethodName())) {
                    Class<?> actionClazz = Class.forName(stackTraceElement.getClassName());
                    if (ModInitializer.class.isAssignableFrom(actionClazz)) {
                        return actionClazz;
                    }
                }
            }
        } catch (ClassNotFoundException ignored) {}
        return null;
    }

    @SuppressWarnings("rawtypes")
    public void dispatcherEventListener() {
        for (Event localEvent : localEvents) {
            Object invoker = localEvent.invoker();
            Type[] targetEvent = invoker.getClass().getGenericInterfaces();
            if (targetEvent.length == 1) {
                Optional.ofNullable(actionInstanceCache.get((Class<?>) targetEvent[0]))
                    .map(SingletonCallbackActionSupplier::get)
                    .ifPresent(localEvent::register);
            }
        }
    }

    private static class SingletonCallbackActionSupplier implements Supplier<CallbackAction> {

        private CallbackAction actionInstance;
        private Supplier<CallbackAction> newInstanceAction;

        public SingletonCallbackActionSupplier(Supplier<CallbackAction> newInstanceAction) {
            this.newInstanceAction = newInstanceAction;
        }

        @Override
        public CallbackAction get() {
            if (actionInstance == null) {
                synchronized (this) {
                    if (actionInstance == null) {
                        actionInstance = newInstanceAction.get();
                    }
                }
            }
            return actionInstance;
        }
    }
}
