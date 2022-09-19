package anyrpc.framework.registry;

import anyrpc.framework.protocol.netty.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * @author fzw
 * 服务发现
 * @date 2022-09-01 10:10
 */
public interface ServiceDiscovery {

    /**
     * 服务发现
     * @param rpcRequest rpc服务请求
     * @return 服务地址
     */
    InetSocketAddress lookupService(RpcRequest rpcRequest);
}
