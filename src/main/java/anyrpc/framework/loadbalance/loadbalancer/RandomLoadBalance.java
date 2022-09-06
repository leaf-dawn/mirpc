package anyrpc.framework.loadbalance.loadbalancer;

import anyrpc.framework.loadbalance.AbstractLoadBalance;
import anyrpc.framework.protocol.netty.dto.RpcRequest;

import java.util.List;
import java.util.Random;

/**
 * @author fzw
 * 随机选则策略
 * @date 2022-09-06 15:15
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
