package com.fansqz.mirpc.framework.client;

import com.fansqz.mirpc.framework.protocol.dto.RpcResponse;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fzw
 * 请求管理者，用于管理请求
 * 记录提交请求，如果完成则去除
 * @date 2022-09-13 21:52
 */
public class RequestManager {

    private static final Map<String, CompletableFuture> UNPROCESSED_RESPONSE = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse<Object>> future) {
        UNPROCESSED_RESPONSE.put(requestId, future);
    }

    public void complete(RpcResponse rpcResponse) {
        CompletableFuture future = UNPROCESSED_RESPONSE.remove(rpcResponse.getRequestId());
        if (Objects.nonNull(future)) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }
}
