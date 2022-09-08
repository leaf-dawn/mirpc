package anyrpc.framework.provider.impl;

import anyrpc.framework.constants.ServiceRegistryEnum;
import anyrpc.framework.provider.RpcServiceConfig;
import anyrpc.framework.provider.ServiceProvider;
import anyrpc.framework.registry.ServiceRegistry;
import anyrpc.framework.registry.ServiceRegistryFactory;

import javax.xml.ws.Service;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fzw
 * zk服务提供者
 * @date 2022-09-08 12:13
 */
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

    }

    @Override
    public Object getService(String rpcServiceName) {
        return null;
    }

    @Override
    public void publiService(RpcServiceConfig rpcServiceConfig) {

    }
}
