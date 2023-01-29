package anyrpc.example.simple;

import anyrpc.framework.protocol.netty.client.NettyRpcClient;
import anyrpc.framework.protocol.netty.dto.RpcRequest;

public class Client {
    public static void main(String[] args) {
        NettyRpcClient client = new NettyRpcClient();
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName("Service")
                .methodName("HelloWorld")
                .parameters(new Object[]{"MyRpc"})
                .paramTypes(new Class[]{String.class}).build();
        client.sendRpcRequest(rpcRequest);
    }
}
