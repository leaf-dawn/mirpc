package anyrpc.framework.loadbalance;

import anyrpc.framework.loadbalance.loadbalancer.ConsistentHashLoadBalance;
import anyrpc.framework.loadbalance.loadbalancer.RandomLoadBalance;

/**
 * @author fzw
 * 用于获取balancer
 * @date 2022-09-06 16:05
 */
public class LoadBalancerFactory {


    public static LoadBalance getLoadBalance(byte loadBalanceCode) {
        switch (loadBalanceCode) {
            case 0X01:
                return new RandomLoadBalance();
            case 0X02:
                return new ConsistentHashLoadBalance();
            default:
                throw new RuntimeException("没有该压缩工具");
        }

    }
}
