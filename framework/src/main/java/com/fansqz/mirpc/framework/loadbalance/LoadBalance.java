package com.fansqz.mirpc.framework.loadbalance;

import com.fansqz.mirpc.framework.protocol.dto.RpcRequest;

import java.util.List;

/**
 * @author fzw
 * 负载均衡策略接口
 * todo:负载均衡策略通过配置文件选择
 * @date 2022-09-06 14:51
 */
public interface LoadBalance {
    /**
     * 在serviceUrlLst中选择合适的服务地址
      * @param serviceUrlList 服务地址列表
     * @param rpcRequest rpcRequest
     * @return 目标服务地址
     */
    String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest);
}
