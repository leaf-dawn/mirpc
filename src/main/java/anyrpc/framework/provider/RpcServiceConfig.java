package anyrpc.framework.provider;

import lombok.Data;

/**
 * @author fzw
 * 服务对象
 * @date 2022-09-08 12:06
 */
@Data
public class RpcServiceConfig {

    /** 服务版本 */
    private String version = "";

    /** 一个接口如果有多个实现类，区分 */
    private String group = "";

    /** 目标服务 */
    private Object service;
}