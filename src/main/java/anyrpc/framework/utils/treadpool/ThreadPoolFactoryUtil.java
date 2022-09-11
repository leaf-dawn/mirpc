package anyrpc.framework.utils.treadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author fzw
 * 线程池工具，用于统一管理所有线程池
 * @date 2022-09-10 14:35
 */

@Slf4j
public final class ThreadPoolFactoryUtil {
    /**
     * 线程池map，通过threadNamePrefix来区分不同线程池
     */
    private static final Map<String, ExecutorService> THREAD_POOLS = new ConcurrentHashMap<>();

    /** 构造函数私有化 */
    private ThreadPoolFactoryUtil() {

    }

    /** 对createCustomThreadPoolIfAbsent进行重载，默认配置，默认非守护线程 */
    public static ExecutorService createCustomThreadPoolIfAbsent(String threadNamePrefix) {
        CustomThreadPoolConfig customThreadPoolConfig = new CustomThreadPoolConfig();
        return createCustomThreadPoolIfAbsent(customThreadPoolConfig, threadNamePrefix, false);
    }

    /** 对createCustomThreadPoolIfAbsent进行重载，默认非守护线程 */
    public static ExecutorService createCustomThreadPoolIfAbsent(CustomThreadPoolConfig customThreadPoolConfig,
                                                                 String threadNamePrefix) {
        return createCustomThreadPoolIfAbsent(customThreadPoolConfig, threadNamePrefix, false);
    }

    /**
     * 根据自定义配置创建线程池
     * 如果threadNamePrefix中存在的话，直接返回，不存在则创建
     * @param customThreadPoolConfig 线程池配置
     * @param threadNamePrefix 线程名称前缀
     * @param daemon 是否是守护线程
     * @return 线程池对象
     */
    public static ExecutorService createCustomThreadPoolIfAbsent(CustomThreadPoolConfig customThreadPoolConfig,
                                                                 String threadNamePrefix, Boolean daemon) {

        ExecutorService threadPool = THREAD_POOLS.computeIfAbsent(threadNamePrefix,
                k -> createThreadPool(customThreadPoolConfig, threadNamePrefix, daemon));
        //如果被shutdown就再创建一个, todo:isTerminated是否多余
        if (threadPool.isShutdown() || threadPool.isTerminated()) {
            THREAD_POOLS.remove(threadNamePrefix);
            threadPool = createThreadPool(customThreadPoolConfig, threadNamePrefix, daemon);
            THREAD_POOLS.put(threadNamePrefix, threadPool);
        }
        return threadPool;
    }

    /**
     * 创建一个线程池
     * @param customThreadPoolConfig 线程池配置
     * @param threadNamePrefix 线程名称前缀
     * @param daemon 该线程池里线程是否是守护线程
     * @return 线程池
     */
    private static ExecutorService createThreadPool(CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix, Boolean daemon) {
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(customThreadPoolConfig.getCorePoolSize(),
                customThreadPoolConfig.getMaximumPoolSize(),
                customThreadPoolConfig.getKeepAliveTime(),
                customThreadPoolConfig.getTimeUnit(),
                customThreadPoolConfig.getWorkQueue(),
                threadFactory);
    }

    /**
     * 创建一个线程工厂
     * @param threadNamePrefix 创建线程名称的前缀
     * @param daemon 是否是守护线程
     * @return 线程工厂
     */
    public static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if (Objects.nonNull(threadNamePrefix)) {
            if (Objects.nonNull(daemon)) {
                return new ThreadFactoryBuilder()
                        .setNameFormat(threadNamePrefix + "-%d")
                        .setDaemon(daemon)
                        .build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }

    /**
     * 关闭管理的所有线程池
     */
    public static void shutDownAllThreadPool() {
        log.info("调用关闭所有线程池方法");
        THREAD_POOLS.entrySet().parallelStream().forEach(entry -> {
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            log.info("正在关闭线程池【{}】【{}】", entry.getKey(), executorService.isTerminated());
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                    if (!executorService.awaitTermination(60,TimeUnit.SECONDS)) {
                        log.error("线程池无法关闭");
                    }
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        });
    }

}
