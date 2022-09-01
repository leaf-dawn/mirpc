package anyrpc.framework.registry;

import java.net.InetSocketAddress;

/**
 * @author fzw
 * 服务注册中心
 * @date 2022-09-01 10:05
 */
public interface ServiceRegistry {

    /**
     * 服务注册
     * @param serviceName 服务名
     * @param inetSocketAddress 服务地址
     */
    void registerService(String serviceName, InetSocketAddress inetSocketAddress);
}
