package com.fansqz.mirpc.framework.registry.zookeeper;

import com.fansqz.mirpc.framework.constants.LoadBalanceTypeEnum;
import com.fansqz.mirpc.framework.loadbalance.LoadBalance;
import com.fansqz.mirpc.framework.loadbalance.LoadBalancerFactory;
import com.fansqz.mirpc.framework.protocol.dto.RpcRequest;
import com.fansqz.mirpc.framework.registry.ServiceDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author fzw
 * zk实现服务发现
 * @date 2022-09-01 10:25
 */
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {

    private final LoadBalance loadBalance;

    public ZkServiceDiscovery() {
        //todo:读取配置文件选择负载均衡策略
        loadBalance = LoadBalancerFactory.getLoadBalance(LoadBalanceTypeEnum.RANDOM.getCode());
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        //获取获取服务名
        String rpcServiceName = rpcRequest.getRpcServiceName();
        //获取该服务名下注册的所有服务地址
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        //负载均衡选择一个服务
        String targetServiceUtl = loadBalance.selectServiceAddress(serviceUrlList, rpcRequest);
        if (targetServiceUtl == null) {
            throw new RuntimeException("没有可用服务");
        }
        log.info("成功找到服务[{}]",targetServiceUtl);
        String[] socketAddressArray = targetServiceUtl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host,port);

    }

}
