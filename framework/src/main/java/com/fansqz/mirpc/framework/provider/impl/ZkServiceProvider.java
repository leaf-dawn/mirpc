package com.fansqz.mirpc.framework.provider.impl;

import com.fansqz.mirpc.framework.constants.RpcNetConstants;
import com.fansqz.mirpc.framework.constants.ServiceRegistryEnum;
import com.fansqz.mirpc.framework.provider.RpcServiceConfig;
import com.fansqz.mirpc.framework.provider.ServiceProvider;
import com.fansqz.mirpc.framework.registry.ServiceRegistry;
import com.fansqz.mirpc.framework.registry.ServiceRegistryFactory;
import lombok.extern.slf4j.Slf4j;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fzw
 * zk服务提供者,本地注册
 * @date 2022-09-08 12:13
 */
@Slf4j
public class ZkServiceProvider implements ServiceProvider {

    private final Map<String, Object> serviceMap;

    private final Set<String> registeredService;

    private final ServiceRegistry serviceRegistry;

    public ZkServiceProvider() {
        serviceMap = new ConcurrentHashMap<>();
        registeredService = ConcurrentHashMap.newKeySet();
        serviceRegistry = ServiceRegistryFactory.getServiceRegistry(ServiceRegistryEnum.ZOOKEEPER.getCode());
    }

    @Override
    public void addService(RpcServiceConfig rpcServiceConfig) {
        String rpcServiceName = rpcServiceConfig.getRpcServiceName();
        //本地注册服务
        if (registeredService.contains(rpcServiceName)) {
            return;
        }
        registeredService.add(rpcServiceName);
        serviceMap.put(rpcServiceName, rpcServiceConfig.getService());
        log.info("添加服务：");

    }

    @Override
    public Object getService(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (Objects.isNull(service)) {
            throw new RuntimeException("服务获取失败");
        }
        return service;
    }

    @Override
    public void publishService(RpcServiceConfig rpcServiceConfig) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            //进行本地注册
            this.addService(rpcServiceConfig);
            //进行服务注册
            serviceRegistry.registerService(rpcServiceConfig.getRpcServiceName(), new InetSocketAddress(host, RpcNetConstants.PORT));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
