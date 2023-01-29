package com.fansqz.mirpc.framework.serialize.kyro;

import com.fansqz.mirpc.framework.serialize.Serializer;
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

    /** kryos非线程安全*/
    private static final ThreadLocal<Kryo> kryos = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            // configure kryo instance, customize settings
            return kryo;
        };
    };


    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {
             Kryo kryo = kryos.get();
             kryo.writeObject(output,obj);
             kryos.remove();
             return output.getBuffer();
        } catch (IOException e) {
            throw new SerializationException("kery序列化失败");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryos.get();
            Object o = kryo.readObject(input,clazz);
            kryos.remove();
            return clazz.cast(o);
        } catch (IOException e) {
            throw new SerializationException("kery反序列化失败");
        }
    }
}
