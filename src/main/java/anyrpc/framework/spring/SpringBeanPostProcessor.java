package anyrpc.framework.spring;

import anyrpc.framework.annotation.RpcService;
import anyrpc.framework.protocol.netty.client.NettyRpcClient;
import anyrpc.framework.protocol.netty.client.RpcRequestTransport;
import anyrpc.framework.provider.RpcServiceConfig;
import anyrpc.framework.provider.ServiceProvider;
import anyrpc.framework.provider.impl.ZkServiceProvider;
import anyrpc.framework.utils.SingletonFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
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

}
