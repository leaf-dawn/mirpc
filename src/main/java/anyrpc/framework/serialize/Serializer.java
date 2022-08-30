package anyrpc.framework.serialize;

/**
 * @author fzw
 * 序列化接口
 * @date 2022-08-30 21:10
 */
public interface Serializer {

    /**
     * 序列化
     * @param obj 序列化对象
     * @return 字符数组
     */
    byte[] serialize(Object obj);


    /**
     * 反序列化
     * @param bytes 序列化了的字符数组
     * @param clazz 目标类
     * @param <T> 目标类型
     * @return 反序列化以后的对象
     */
    <T> T deserialize(byte[] bytes,Class<T> clazz);
}
