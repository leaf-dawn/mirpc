package anyrpc.framework.protocol;

import anyrpc.framework.register.URL;

import java.util.List;
import java.util.Random;

/**
 * @author fzw
 * 负载均衡算法
 * @date 2022-08-29 10:27
 */
public class LoadBalance {

    /**
     * 随机策略
     */
    public static URL random(List<URL> list) {
        Random random = new Random();
        int n = random.nextInt(list.size());
        return list.get(n);
    }


}
