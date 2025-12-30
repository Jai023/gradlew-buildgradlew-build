package io.turboshift.util;

import io. turboshift.TurboShift;

import java.util.concurrent.*;

public class AsyncTaskManager {
    private static ExecutorService workerPool;
    private static ScheduledExecutorService scheduledPool;
    private static boolean initialized = false;
    
    /**
     * Initialize thread pools
     */
    public static void initialize() {
        if (initialized) {
            TurboShift.LOGGER. warn("AsyncTaskManager already initialized");
            return;
        }
        
        int processors = Runtime.getRuntime().availableProcessors();
        int workerThreads = Math.max(2, Math.min(processors - 1, 8));
        
        // Create worker thread pool
        workerPool = new ThreadPoolExecutor(
                2, // Core pool size
                workerThreads, // Max pool size
                60L, TimeUnit.SECONDS, // Keep-alive time
                new LinkedBlockingQueue<>(200), // Work queue
                new ThreadFactory() {
                    private int counter = 0;
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r, "TurboShift-Worker-" + counter++);
                        thread.setDaemon(true);
                        thread.setPriority(Thread.NORM_PRIORITY - 1);
                        return thread;
                    }
                },
                new ThreadPoolExecutor. CallerRunsPolicy() // Rejection policy
        );
        
        // Create scheduled thread pool
        scheduledPool = Executors.newScheduledThreadPool(2, r -> {
            Thread thread = new Thread(r, "TurboShift-Scheduler");
            thread.setDaemon(true);
            thread. setPriority(Thread.NORM_PRIORITY - 1);
            return thread;
        });
        
        initialized = true;
        TurboShift.LOGGER.info("✓ Async Task Manager initialized with {} worker threads", workerThreads);
    }
    
    /**
     * Submit a task to be executed asynchronously
     */
    public static Future<?> submitTask(Runnable task) {
        ensureInitialized();
        return workerPool.submit(task);
    }
    
    /**
     * Submit a callable task
     */
    public static <T> Future<T> submitTask(Callable<T> task) {
        ensureInitialized();
        return workerPool. submit(task);
    }
    
    /**
     * Schedule a task with a delay
     */
    public static ScheduledFuture<?> scheduleTask(Runnable task, long delay, TimeUnit unit) {
        ensureInitialized();
        return scheduledPool.schedule(task, delay, unit);
    }
    
    /**
     * Schedule a repeating task
     */
    public static ScheduledFuture<? > scheduleRepeatingTask(Runnable task, long initialDelay, long period, TimeUnit unit) {
        ensureInitialized();
        return scheduledPool.scheduleAtFixedRate(task, initialDelay, period, unit);
    }
    
    /**
     * Shutdown all thread pools
     */
    public static void shutdown() {
        if (!initialized) {
            return;
        }
        
        TurboShift.LOGGER. info("Shutting down async task manager...");
        
        if (workerPool != null && !workerPool.isShutdown()) {
            workerPool.shutdown();
            try {
                if (!workerPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    workerPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                workerPool. shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        if (scheduledPool != null && !scheduledPool.isShutdown()) {
            scheduledPool.shutdown();
            try {
                if (!scheduledPool. awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduledPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduledPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        initialized = false;
        TurboShift.LOGGER.info("✓ Async task manager shutdown complete");
    }
    
    /**
     * Check if manager is initialized
     */
    private static void ensureInitialized() {
        if (!initialized) {
            throw new IllegalStateException("AsyncTaskManager not initialized!  Call initialize() first.");
        }
    }
    
    /**
     * Get active worker count
     */
    public static int getActiveWorkerCount() {
        if (!initialized || !(workerPool instanceof ThreadPoolExecutor)) {
            return 0;
        }
        return ((ThreadPoolExecutor) workerPool).getActiveCount();
    }
    
    /**
     * Get queue size
     */
    public static int getQueueSize() {
        if (!initialized || !(workerPool instanceof ThreadPoolExecutor)) {
            return 0;
        }
        return ((ThreadPoolExecutor) workerPool).getQueue().size();
    }
}