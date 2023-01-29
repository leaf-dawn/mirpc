package com.fansqz.mirpc.framework.protocol.netty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;

/**
 * @author fzw
 * rpc请求对象
 * @date 2022-08-30 15:35
 */
@Builder
@Data
@AllArgsConstructor
public class RpcRequest {
    private static final long serialVersionUID = 1905122041952342342L;

    private String requestId;
    /** 接口名称 */
    private String interfaceName;
    /** 调用方法名称 */
    private String methodName;
    /** 参数 */
    private Object[] parameters;
    /** 参数类型 */
    private Class<?>[] paramTypes;
    /** 多版本控制 */
    private String version;
    /** 一个接口有多种实现，可以通过group区分 */
    private String group;

    /** todo：简单相加会导致冲突而出现问题 */
    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }
}
