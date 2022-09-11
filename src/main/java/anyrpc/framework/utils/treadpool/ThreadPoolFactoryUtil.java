package anyrpc.framework.utils.treadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author fzw
 * 线程池工具，用于统一管理所有线程池
 * @date 2022-09-10 14:35
 */
public final class ThreadPoolFactoryUtil {
    /**
     * 线程池map，通过threadNamePrefix来区分不同线程池
     */
    private static final Map<String, ExecutorService> THREAD_POOLS = new ConcurrentHashMap<>();

    /** 构造函数私有化 */
    private ThreadPoolFactoryUtil() {

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

}
