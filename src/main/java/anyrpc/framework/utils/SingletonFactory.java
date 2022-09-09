package anyrpc.framework.utils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fzw
 * 单例工厂
 * @date 2022-09-09 15:36
 */
public class SingletonFactory {

    /** 用于存储单例对象 */
    private static final Map<String, Object> OBJECT_MAP = new ConcurrentHashMap<>();

    /** 获取一个单例对象 */
    public static <T> T getInstance(Class<T> clazz) {
        if (Objects.isNull(clazz)) {
            throw new IllegalArgumentException();
        }
        String key = clazz.toString();
        if (OBJECT_MAP.containsKey(key)) {
            return clazz.cast(OBJECT_MAP.get(key));
        }else {
            return clazz.cast(OBJECT_MAP.computeIfAbsent(key, k -> {
                try {
                    return clazz.newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(),e);
                }
            }));
        }
    }
}
