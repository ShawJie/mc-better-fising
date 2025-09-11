package com.shawjie.mods.infrastructure;

import com.google.common.collect.Comparators;
import com.shawjie.mods.action.CallbackAction;
import com.shawjie.mods.event.FishCatchingEvent;
import com.shawjie.mods.event.PlayerPickupItemEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Supplier;

public class ActionProcessRegister {

    private final List<Event<?>> localEvents = List.of(
        FishCatchingEvent.EVENT, PlayerPickupItemEvent.EVENT
    );

    private final Map<Class<?>, List<SingletonCallbackActionSupplier>> actionInstanceCache = new HashMap<>();
    private final AnnotationAwareOrderComparator actionComparator = new AnnotationAwareOrderComparator();

    public ActionProcessRegister(Class<? extends ModInitializer> initializerClass) {
        Optional.ofNullable(initializerClass.getAnnotation(EnableAction.class))
            .map(EnableAction::classes)
            .ifPresent(this::processActions);
    }

    private void processActions(Class<? extends CallbackAction>[] actionClasses) {
        for (Class<? extends CallbackAction> actionClass : actionClasses) {
            SingletonCallbackActionSupplier supplier = new SingletonCallbackActionSupplier(() -> instanceObject(actionClass));
            for (Class<?> interfaceClass : actionClass.getInterfaces()) {
                if (interfaceClass != CallbackAction.class &&
                    interfaceClass.getSimpleName().endsWith("Event")) {
                    actionInstanceCache.compute(
                        interfaceClass, (k, v) -> {
                            List<SingletonCallbackActionSupplier> suppliers = v;
                            if (suppliers == null) {
                                suppliers = new ArrayList<>();
                            }
                            suppliers.add(supplier);
                            return suppliers;
                        }
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

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void dispatcherEventListener() {
        for (Event localEvent : localEvents) {
            Object invoker = localEvent.invoker();
            Type[] targetEvent = invoker.getClass().getGenericInterfaces();
            if (targetEvent.length == 1) {
                List<SingletonCallbackActionSupplier> suppliers = actionInstanceCache.get((Class<?>) targetEvent[0]);
                if (suppliers != null) {
                    List<CallbackAction> actions = new ArrayList<>(suppliers.size());
                    for (SingletonCallbackActionSupplier supplier : suppliers) {
                        actions.add(supplier.get());
                    }

                    actions.sort(actionComparator);
                    actions.forEach(localEvent::register);
                }
            }
        }
    }

    private static class SingletonCallbackActionSupplier implements Supplier<CallbackAction> {

        private volatile CallbackAction actionInstance;
        private final Supplier<CallbackAction> newInstanceAction;

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

    private class AnnotationAwareOrderComparator implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {
            return findOrderFromAnnotation(o1) - findOrderFromAnnotation(o2);
        }

        private Integer findOrderFromAnnotation(Object obj) {
            Class<?> optClazz = obj.getClass();
            Ordered annotation = optClazz.getAnnotation(Ordered.class);
            return annotation == null ? Integer.MAX_VALUE : annotation.value();
        }
    }
}
