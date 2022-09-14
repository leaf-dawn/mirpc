package anyrpc.framework.protocol.netty.client;

import anyrpc.framework.protocol.netty.dto.RpcRequest;

/**
 * @author fzw
 *
 * @date 2022-09-14 20:38
 */
public interface RpcRequestTransport {
    /**
     * 发送rpc请求，获取结果
     * @param rpcRequest rpc请求
     * @return 结果
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
