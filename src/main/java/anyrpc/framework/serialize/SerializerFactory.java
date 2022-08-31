package anyrpc.framework.serialize;

import anyrpc.framework.serialize.hessian.HessianSerializer;
import anyrpc.framework.serialize.kyro.KryoSerializer;
import anyrpc.framework.serialize.protostuff.ProtostuffSerializer;

/**
 * @author fzw
 * 获取序列化工具的工厂
 * @date 2022-08-31 12:08
 */
public class SerializerFactory {

    public static Serializer getSerializer(byte codec) {
        switch (codec) {

            case 0X01:
                return new KryoSerializer();
            case 0x02:
                return new ProtostuffSerializer();
            case 0x03:
                return new HessianSerializer();
            default:
                throw new RuntimeException("没有该序列化工具");
        }

    }
}
