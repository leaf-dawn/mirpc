package com.fansqz.mirpc.framework.protocol.serialize.protostuff;

import com.fansqz.mirpc.framework.protocol.serialize.Serializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;

import io.protostuff.runtime.RuntimeSchema;
/**
 * @author fzw
 * protostuff序列化工具
 * @date 2022-08-31 11:15
 */
public class ProtostuffSerializer implements Serializer {

    /** 防止每次序列化时都去分配空间 */
    private static final LinkedBuffer  BUFFER = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);


    @Override
    public byte[] serialize(Object obj) {
         Class<?> clazz = obj.getClass();
        Schema schema = RuntimeSchema.getSchema(clazz);
        byte[] bytes;
        try {
            bytes = ProtostuffIOUtil.toByteArray(obj,schema,BUFFER);
        }finally {
            BUFFER.clear();
        }
        return bytes;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }
}
