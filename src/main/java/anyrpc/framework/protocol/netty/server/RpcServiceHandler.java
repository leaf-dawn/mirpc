package anyrpc.framework.protocol.netty.server;

import anyrpc.framework.protocol.netty.dto.RpcRequest;
import anyrpc.framework.provider.ServiceProvider;
import anyrpc.framework.provider.impl.ZkServiceProvider;
import anyrpc.framework.utils.SingletonFactory;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;


/**
 * @author fzw
 * rpc处理handler

 * @date 2022-09-12 22:46
 */
@Slf4j
public class RpcServiceHandler {

    private final ServiceProvider serviceProvider;

    public RpcServiceHandler() {
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProvider.class);
    }

    /**
     *接收rpc请求数据，执行以后，并返回结果
     * @param rpcRequest rpcRequest
     * @return 运行结果
     */
    public Object handler(RpcRequest rpcRequest) {
        //获取service
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        Object result;
        //获取要执行方法
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("service[{}] 执行方法[{}]成功", rpcRequest.getRpcServiceName(),rpcRequest.getMethodName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
