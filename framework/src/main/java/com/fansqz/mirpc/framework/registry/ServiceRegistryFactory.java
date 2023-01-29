package com.fansqz.mirpc.framework.registry;

import com.fansqz.mirpc.framework.registry.zookeeper.ZkServiceDiscovery;
import com.fansqz.mirpc.framework.registry.zookeeper.ZkServiceRegistry;

/**
 * @author fzw
 * 用于获取服务注册和服务发现
 * @date 2022-09-08 15:37
 */
public class ServiceRegistryFactory {

    public static ServiceDiscovery getServiceDiscovery(byte code) {
        switch (code) {
            case 0X01:
                return new ZkServiceDiscovery();
            default:
                throw new RuntimeException("仅支持zookeeper做服务发现");
        }
    }

    public static ServiceRegistry getServiceRegistry(byte code) {
        switch (code) {
            case 0X01:
                return new ZkServiceRegistry();
            default:
                throw new RuntimeException("仅支持zookeeper做服务注册");
        }
    }

}
