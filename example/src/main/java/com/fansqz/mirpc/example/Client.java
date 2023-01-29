package com.fansqz.mirpc.example;


import com.fansqz.mirpc.framework.protocol.netty.client.NettyRpcClient;
import com.fansqz.mirpc.framework.protocol.netty.dto.RpcRequest;

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
