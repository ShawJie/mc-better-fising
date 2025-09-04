package com.shawjie.mods.ticker;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Fabric-based task scheduler that executes delayed tasks synchronized with client ticks.
 * Provides thread-safe access to MinecraftClient instance during task execution.
 *
 * @author shawjie
 */
public class PriorityFabricTicker implements ClientTickEvents.StartTick {

    private static final LinkedList<DelayedTask> tasks = new LinkedList<>();
    private static final ThreadLocal<MinecraftClient> rollingClient = new ThreadLocal<>();

    private static boolean inRolling;
    private static final LinkedList<DelayedTask> rollingTemporaryTask = new LinkedList<>();
    
    /**
     * Schedule a task to be executed after a specified delay.
     * 
     * @param task the task to execute
     * @param delayTicks the number of ticks to wait before execution
     */
    public static void scheduleTask(Runnable task, int delayTicks) {
        LinkedList<DelayedTask> delayedTasks = inRolling ? rollingTemporaryTask : tasks;
        delayedTasks.addLast(new DelayedTask(task, delayTicks));
    }

    /**
     * Get the current MinecraftClient instance available during task execution.
     * 
     * @return the current client instance, or null if called outside tick context
     */
    public static MinecraftClient getClient() {
        return rollingClient.get();
    }

    @Override
    public void onStartTick(MinecraftClient client) {
        if (tasks.isEmpty()) {
            return;
        }

        try {
            inRolling = true;
            rollingClient.set(client);
            ListIterator<DelayedTask> iterator = tasks.listIterator();
            while (iterator.hasNext()) {
                DelayedTask task = iterator.next();
                if (--task.remainingTicks <= 0) {
                    task.execute();
                    iterator.remove();
                }
            }
        } finally {
            rollingClient.remove();

            inRolling = false;
            if (!rollingTemporaryTask.isEmpty()) {
                tasks.addAll(rollingTemporaryTask);
                rollingTemporaryTask.clear();
            }
        }
    }

    /**
     * Internal class representing a delayed task with tick countdown.
     */
    private static class DelayedTask {
        final Runnable task;
        int remainingTicks;
        
        DelayedTask(Runnable task, int ticks) {
            this.task = task;
            this.remainingTicks = ticks;
        }
        
        void execute() {
            task.run();
        }
    }
}