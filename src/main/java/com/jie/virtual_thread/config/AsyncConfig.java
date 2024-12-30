package com.jie.virtual_thread.config;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;  
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.*;

/**
 * 异步配置类
 *
 * @author jie
 */
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

    private static final Logger log = LoggerFactory.getLogger(AsyncConfig.class);

    // 最大并发任务数
    private static final int MAX_CONCURRENT_TASKS = 1000;

    // 任务队列容量
    private static final int QUEUE_CAPACITY = 1000;

    @Override
    @NonNull
    public Executor getAsyncExecutor() {
        log.info("初始化虚拟线程执行器");

        // 创建有界队列
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);

        // 创建虚拟线程池执行器
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                // 核心线程数
                MAX_CONCURRENT_TASKS / 2,
                // 最大线程数
                MAX_CONCURRENT_TASKS,
                // 空闲线程存活时间
                60L, TimeUnit.SECONDS,
                // 工作队列
                workQueue,
                // 虚拟线程工厂
                Thread.ofVirtual().factory(),
                // 拒绝策略：由调用线程执行
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        // 允许核心线程超时
        executor.allowCoreThreadTimeOut(true);

        // 添加线程池监控
        executor.setThreadFactory(new CustomizableThreadFactory("virtual-thread-") {
            @Override
            @NonNull
            public Thread newThread(@NonNull Runnable r) {
                Thread thread = super.newThread(r);
                log.debug("创建新的虚拟线程: {}", thread.getName());
                return thread;
            }
        });

        return executor;
    }

    @Override
    @NonNull
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("异步方法 {} 执行异常, 参数: {}", method.getName(), params, ex);
            // 这里可以添加告警通知等机制
        };
    }

    /**
     * 关闭时的钩子方法，用于优雅关闭线程池
     */
    @PreDestroy
    public void shutdown() {
        log.info("开始关闭虚拟线程执行器");
        if (getAsyncExecutor() instanceof ExecutorService executorService) {
            try {
                // 停止接收新任务
                executorService.shutdown();
                // 等待已有任务完成
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    // 强制关闭
                    executorService.shutdownNow();
                }
                log.info("虚拟线程执行器已关闭");
            } catch (InterruptedException e) {
                log.error("关闭虚拟线程执行器时发生异常", e);
                Thread.currentThread().interrupt();
            }
        }
    }
} 