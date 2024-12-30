package com.jie.virtual_thread.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Service
public class SimulatedService {
    private static final Logger log = LoggerFactory.getLogger(SimulatedService.class);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * 异步处理任务
     */
    @Async
    public CompletableFuture<String> processTaskAsync(String taskId) {
        try {
            log.debug("开始处理任务 {}, 线程: {}", taskId, Thread.currentThread());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://httpbin.org/delay/1"))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            log.debug("完成任务 {}, 状态码: {}, 线程: {}",
                    taskId, response.statusCode(), Thread.currentThread());

            String result = "任务 " + taskId + " 处理者 " + Thread.currentThread() +
                    " 响应状态: " + response.statusCode();
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("任务 {} 执行失败", taskId, e);
            return CompletableFuture.completedFuture("任务 " + taskId + " 失败: " + e.getMessage());
        }
    }

    /**
     * 模拟耗时操作
     *
     * @param taskId 任务ID
     * @return 处理结果
     */
    public String processTask(String taskId) {
        try {
            log.debug("开始处理任务 {}, 线程: {}", taskId, Thread.currentThread());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://httpbin.org/delay/1"))
                    .build();

            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            log.debug("完成任务 {}, 状态码: {}, 线程: {}",
                    taskId, response.statusCode(), Thread.currentThread());

            return "任务 " + taskId + " 处理者 " + Thread.currentThread() +
                    " 响应状态: " + response.statusCode();
        } catch (Exception e) {
            log.error("任务 {} 执行失败", taskId, e);
            return "任务 " + taskId + " 失败: " + e.getMessage();
        }
    }
} 