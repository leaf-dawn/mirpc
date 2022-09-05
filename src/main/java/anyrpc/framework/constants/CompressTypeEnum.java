package anyrpc.framework.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fzw
 * 压缩类型
 * @date 2022-08-30 17:09
 */

@Getter
@AllArgsConstructor
public enum CompressTypeEnum {
    //gzip
    GZIP((byte)0x01,"gzip");

    private final byte code;

    private final String name;

    public static String getName(byte code) {
        for (CompressTypeEnum c : CompressTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.getName();
            }
        }
        return null;
    }
}
