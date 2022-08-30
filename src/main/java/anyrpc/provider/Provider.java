package anyrpc.provider;
import anyrpc.framework.register.LocalRegister;
import anyrpc.framework.register.RemoteMapRegister;
import anyrpc.framework.register.URL;
import anyrpc.framework.service.HelloService;
import anyrpc.framework.service.impl.HelloServiceImpl;

/**
 * @author fzw
 * 提供者
 * @date 2022-08-28 21:50
 */
public class Provider {

    public static void main(String[] args) {
        //进行本地注册
        LocalRegister.register(HelloService.class.getName(), HelloServiceImpl.class);
        //到注册中心注册
        URL url = new URL("localhost", 8080);
        RemoteMapRegister.register(HelloService.class.getName(), url);


    }
}
