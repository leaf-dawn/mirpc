package anyrpc.framework.protocol;

import anyrpc.framework.register.RemoteMapRegister;
import anyrpc.framework.register.URL;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import static anyrpc.framework.register.RemoteMapRegister.get;

/**
 * @author fzw
 * 代理工厂
 * @date 2022-08-28 22:25
 */
public class ProxyFactory {

    public static <T> T getProxy(Class interfaceClass){

        Object proxyInstance = Proxy.newProxyInstance(ProxyFactory.class.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Invocation invocation = new Invocation(interfaceClass.getName(),method.getName(),
                        method.getParameterTypes(),args);
                //从注册中心获取url
                List<URL> urls = RemoteMapRegister.get(interfaceClass.getName());
                //通过负载均衡获取一个
                //发送数据，获取结果
                return null;
            }
        });
        return (T)proxyInstance;

    }
}
