package com.jie.virtual_thread.controller;

import com.jie.virtual_thread.service.SimulatedService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 虚拟线程控制器
 *
 * @author jie
 */
@RestController
@RequestMapping("/api/virtual-thread")
public class VirtualThreadDemoController {

    @Resource
    private SimulatedService simulatedService;


    private static final int TASK_COUNT = 1000;

    /**
     * 演示使用虚拟线程执行器处理多个任务
     */
    @GetMapping("/executor")
    public Map<String, Object> virtualThreadExecutor() throws Exception {
        List<String> results = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        // 使用@Async直接提交任务
        List<CompletableFuture<String>> futures = new ArrayList<>();
        for (int i = 0; i < TASK_COUNT; i++) {
            final String taskId = String.valueOf(i);
            futures.add(simulatedService.processTaskAsync(taskId));
        }

        // 等待所有任务完成并收集结果
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        for (CompletableFuture<String> future : futures) {
            results.add(future.get());
        }

        long endTime = System.currentTimeMillis();
        return Map.of(
                "耗时(毫秒)", endTime - startTime,
                "结果数量", results.size()
        );
    }

    /**
     * 使用普通线程池处理多个任务
     */
    @GetMapping("/platform-thread")
    public Map<String, Object> platformThreadExecutor() throws Exception {
        List<String> results = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        // 创建固定大小的普通线程池
        try (ExecutorService executor = new ThreadPoolExecutor(
                // 核心线程数
                100,
                // 最大线程数`
                100,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(TASK_COUNT),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        )) {
            // 提交10000个任务
            List<Future<String>> futures = new ArrayList<>();
            for (int i = 0; i < TASK_COUNT; i++) {
                final String taskId = String.valueOf(i);
                futures.add(executor.submit(() -> simulatedService.processTask(taskId)));
            }

            // 获取所有任务的结果
            for (Future<String> future : futures) {
                results.add(future.get());
            }
        }

        long endTime = System.currentTimeMillis();
        return Map.of(
                "耗时(毫秒)", endTime - startTime,
                "结果数量", results.size()
        );
    }

    /**
     * 不使用线程直接串行处理
     */
    @GetMapping("/sequential")
    public Map<String, Object> sequentialProcessing() {
        List<String> results = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        // 串行处理10000个任务
        for (int i = 0; i < TASK_COUNT; i++) {
            String taskId = String.valueOf(i);
            results.add(simulatedService.processTask(taskId));
        }

        long endTime = System.currentTimeMillis();
        return Map.of(
                "耗时(毫秒)", endTime - startTime,
                "结果数量", results.size()
        );
    }
} 