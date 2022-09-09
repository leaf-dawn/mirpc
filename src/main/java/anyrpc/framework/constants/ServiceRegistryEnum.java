package anyrpc.framework.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fzw
 * 服务注册中心选择
 * @date 2022-09-08 15:38
 */
@Getter
@AllArgsConstructor
public enum ServiceRegistryEnum {

    //random
    ZOOKEEPER((byte)0x01,"zookeeper");

    private final byte code;

    private final String name;
}
