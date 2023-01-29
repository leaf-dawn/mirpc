package com.fansqz.mirpc.framework.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fzw
 * 负载均衡选择
 * @date 2022-09-06 15:58
 */
@Getter
@AllArgsConstructor
public enum LoadBalanceTypeEnum {

    //random
    RANDOM((byte)0x01,"random"),
    CONSISTENT_HASH((byte)0x02,"consistentHash");

    private final byte code;

    private final String name;

    public static String getName(byte code) {
        for (LoadBalanceTypeEnum c : LoadBalanceTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

}
