package com.fansqz.mirpc.framework.utils.treadpool;

import lombok.Data;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author fzw
 * 线程池配置工具
 * @date 2022-09-10 15:17
 */

@Data
public class CustomThreadPoolConfig {

    private static final int DEFAULT_CODE_POOL_SIZE = 10;

    private static final int DEFAULT_MAXIMUM_POOL_SIE_SIZE = 100;

    private static final int DEFAULT_KEEP_ALIVE_TIME = 1;

    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;

    /** 默认拥塞队列容量 */
    private static final int DEFAULT_BLOCKING_QUEUE_CAPACITY = 100;

    /**
     * 可配置参数
     */

    /** 线程池大小 */
    private int corePoolSize = DEFAULT_CODE_POOL_SIZE;

    private int maximumPoolSize = DEFAULT_MAXIMUM_POOL_SIE_SIZE;

    private long keepAliveTime = DEFAULT_KEEP_ALIVE_TIME;

    private TimeUnit timeUnit = DEFAULT_TIME_UNIT;

    /** 阻塞队列 */
    private BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(DEFAULT_BLOCKING_QUEUE_CAPACITY);
}
