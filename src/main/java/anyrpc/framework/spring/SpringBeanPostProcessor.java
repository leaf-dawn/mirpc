package anyrpc.framework.spring;

import anyrpc.framework.annotation.RpcReference;
import anyrpc.framework.annotation.RpcService;
import anyrpc.framework.protocol.netty.client.NettyRpcClient;
import anyrpc.framework.protocol.netty.client.RpcRequestTransport;
import anyrpc.framework.provider.RpcServiceConfig;
import anyrpc.framework.provider.ServiceProvider;
import anyrpc.framework.provider.impl.ZkServiceProvider;
import anyrpc.framework.spring.proxy.RpcClientProxy;
import anyrpc.framework.utils.SingletonFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 创建bean的时候
 * 1. 将服务端进行注册
 * 2. 将客户端进行代理并注入
 * @author fzw
 * @date 2022-09-22 11:29
 */
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final ServiceProvider serviceProvider;

    private final RpcRequestTransport rpcClient;

    public SpringBeanPostProcessor() {
       this.serviceProvider =  SingletonFactory.getInstance(ZkServiceProvider.class);
       this.rpcClient = new NettyRpcClient();
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        //创建RpcService的bean之前，注册serviceProvider
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            //构建rpcConfig
            RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .service(bean).build();
            serviceProvider.publishService(rpcServiceConfig);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?>  targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field declareField : declaredFields) {
            RpcReference rpcReference = declareField.getAnnotation(RpcReference.class) ;
            if (Objects.nonNull(rpcReference))  {
                RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                        .group(rpcReference.group())
                        .version(rpcReference.version())
                        .build();
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceConfig);
                Object clientProxy = rpcClientProxy.getProxy(declareField.getClass());
                declareField.setAccessible(true);
                try {
                    declareField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
