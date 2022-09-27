package anyrpc.framework.spring.proxy;

import anyrpc.framework.constants.RpcCodeEnum;
import anyrpc.framework.protocol.netty.client.NettyRpcClient;
import anyrpc.framework.protocol.netty.client.RpcRequestTransport;
import anyrpc.framework.protocol.netty.dto.RpcRequest;
import anyrpc.framework.protocol.netty.dto.RpcResponse;
import anyrpc.framework.provider.RpcServiceConfig;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author fzw
 * rpc客户端的代理
 * @date 2022-09-22 11:47
 */

@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private static final String INTERFACE_NAME = "interfaceName";

    private final RpcRequestTransport rpcRequestTransport;
    private final RpcServiceConfig rpcServiceConfig;

    public RpcClientProxy(RpcRequestTransport rpcRequestTransport, RpcServiceConfig rpcServiceConfig) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcServiceConfig = rpcServiceConfig;
    }

    /**
     * 获取代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),new Class<?>[]{clazz},this);
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("invoked method: [{}]", method.getName());
        //构建rpcRequest并发送
        RpcRequest rpcRequest = RpcRequest.builder().methodName(method.getName())
                .parameters(args)
                .interfaceName(method.getDeclaringClass().getName())
                .paramTypes(method.getParameterTypes())
                .requestId(UUID.randomUUID().toString())
                .group(rpcServiceConfig.getGroup())
                .version(rpcServiceConfig.getVersion())
                .build();
        RpcResponse<Object> rpcResponse = null;
        //发送并阻塞获取rpcResponse
        if (rpcRequestTransport instanceof NettyRpcClient) {
            NettyRpcClient nettyRpcClient = (NettyRpcClient) rpcRequestTransport;
            CompletableFuture<RpcResponse<Object>> completableFuture = nettyRpcClient.sendRpcRequest(rpcRequest);
            rpcResponse = completableFuture.get();
        }
        check(rpcResponse,rpcRequest);
        return rpcResponse.getData();
    }



    private void check(RpcResponse rpcResponse, RpcRequest rpcRequest) {
        if (Objects.isNull(rpcResponse)) {
            throw new RuntimeException("服务调用失败：" + rpcRequest.getInterfaceName());
        }
        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RuntimeException("返回结果错误：id匹配 " + rpcRequest.getInterfaceName());
        }
        if (Objects.isNull(rpcResponse.getCode()) || rpcResponse.getCode().equals(RpcCodeEnum.SUCCESS.getCode())) {
            throw new RuntimeException("服务调用失败：" + rpcResponse.getCode());
        }
    }
}
