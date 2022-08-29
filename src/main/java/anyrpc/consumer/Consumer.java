package anyrpc.consumer;

import anyrpc.framework.protocol.ProxyFactory;
import anyrpc.provider.service.HelloService;

/**
 * @author fzw
 * 服务消费者
 * @date 2022-08-28 21:36
 */
public class Consumer {
    public static void main(String[] args) {
        //代理
        HelloService helloService = ProxyFactory.getProxy(HelloService.class);
    }

}
