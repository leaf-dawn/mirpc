package com.fansqz.mirpc.example;


import com.fansqz.mirpc.framework.protocol.netty.server.NettyRpcServer;
import com.fansqz.mirpc.framework.provider.RpcServiceConfig;

public class Server {
    public static void main(String[] args) {
        NettyRpcServer server = new NettyRpcServer();
        // 进行服务发布
        RpcServiceConfig config = RpcServiceConfig.builder()
                .service(new ServiceImpl())
                .build();
        server.registerService(config);
        // 启动服务端
        NettyRpcServer.start("localhost", 8080);
    }
}
