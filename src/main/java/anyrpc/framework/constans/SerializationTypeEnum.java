package anyrpc.framework.constans;

import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * @author fzw
 * 序列化相关的枚举
 * @date 2022-08-30 19:08
 */

@Getter
@AllArgsConstructor
public enum SerializationTypeEnum {
    //kyro
    KYRO((byte)0x01,"kyro"),
    PROTOSTUFF((byte)0x02,"protostuff"),
    HESSIAN((byte)0x03,"hessian");

    private final byte code;

    private final String name;

    public static String getName(byte code) {
        for (SerializationTypeEnum c : SerializationTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

}
