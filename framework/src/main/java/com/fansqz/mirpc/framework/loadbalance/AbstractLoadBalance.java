package com.fansqz.mirpc.framework.loadbalance;

import com.fansqz.mirpc.framework.protocol.dto.RpcRequest;

import java.util.List;

/**
 * @author fzw
 * 负载均衡的抽象类
 * @date 2022-09-06 15:07
 */
public abstract class AbstractLoadBalance implements  LoadBalance{


    @Override
    public String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest) {
        if (serviceUrlList == null || serviceUrlList.size() == 0)  {
            return null;
        }
        if (serviceUrlList.size() == 1) {
            return serviceUrlList.get(0);
        }
        return doSelect(serviceUrlList, rpcRequest);
    }

    /**
     * 负载均衡的具体实现
     * @param serviceAddresses 服务地址列表
     * @param rpcRequest rpcRequest
     * @return 选则的服务地址
     */
    protected abstract String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest);
}
