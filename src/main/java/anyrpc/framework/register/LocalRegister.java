package anyrpc.framework.register;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fzw
 * 本地注册
 * @date 2022-08-28 22:01
 */
public class LocalRegister {

    private static Map<String, Class> map = new HashMap<String,Class>();

    public static void register(String interfaceName, Class implClass){
        map.put(interfaceName, implClass);
    }

    public static Class get(String interfaceName){
        return map.get(interfaceName);
    }
}
