package com.fansqz.mirpc.example;


import com.fansqz.mirpc.framework.client.NettyRpcClient;
import com.fansqz.mirpc.framework.protocol.dto.RpcRequest;
import com.fansqz.mirpc.framework.protocol.dto.RpcResponse;

import java.io.*;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.zip.GZIPOutputStream;

public class Client {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        NettyRpcClient client = new NettyRpcClient();
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(Service.class.getCanonicalName())
                .methodName("HelloWorld")
                .parameters(new Object[]{"MyRpc"})
                .requestId("111")
                .paramTypes(new Class[]{String.class}).build();
        CompletableFuture<RpcResponse<Object>> answer =  client.sendRpcRequest(rpcRequest);
        String t = (String) answer.get().getData();
        System.out.println(t);
    }
}
