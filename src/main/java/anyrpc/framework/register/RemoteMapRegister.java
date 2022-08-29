package anyrpc.framework.register;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fzw
 * 服务注册中心
 * 本地实现
 * @date 2022-08-28 22:
 */
public class RemoteMapRegister {

    private static Map<String, List<URL>> REGISTER = new HashMap<>();

    public static void register(String interfaceName, URL url) {
        List<URL> list = REGISTER.get(interfaceName);

        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(url);

        REGISTER.put(interfaceName,list);
    }

    /**
     * 从注册中中心里面获取一个url
     * 这个url
     */
    public static List<URL> get(String interfaceName) {
        List<URL> list = REGISTER.get(interfaceName);
        return list;
    }
}
