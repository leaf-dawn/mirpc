package com.fansqz.mirpc.framework.protocol.serialize.kyro;

import com.esotericsoftware.kryo.util.Pool;
import com.fansqz.mirpc.framework.protocol.serialize.Serializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author fzw
 * kryo序列化工具
 * @date 2022-08-30 21:44
 */
public class KryoSerializer implements Serializer {

    /**
     * kryos非线程安全
     */
    private static final Pool<Kryo> kryoPool = new Pool<Kryo>(true, false, 8) {
        protected Kryo create() {
            return new Kryo();
        }
    };

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {
             Kryo kryo = kryoPool.obtain();
             kryo.writeObject(output,obj);
             kryoPool.free(kryo);
             return output.getBuffer();
        } catch (IOException e) {
            throw new SerializationException("kery序列化失败");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoPool.obtain();
            Object o = kryo.readObject(input,clazz);
            kryoPool.free(kryo);
            return clazz.cast(o);
        } catch (IOException e) {
            throw new SerializationException("kery反序列化失败");
        }
    }
}
