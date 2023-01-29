package com.fansqz.mirpc.framework.loadbalance.loadbalancer;

import com.fansqz.mirpc.framework.loadbalance.AbstractLoadBalance;
import com.fansqz.mirpc.framework.protocol.dto.RpcRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.TreeMap;

/**
 * @author fzw
 * consisten hash负载均衡策略
 * todo:未写完，有待研究
 * https://github.com/apache/dubbo/blob/2d9583adf26a2d8bd6fb646243a9fe80a77e65d5/dubbo-cluster/src/main/java/org/apache/dubbo/rpc/cluster/loadbalance/ConsistentHashLoadBalance.java
 * @date 2022-09-06 15:18
 */
public class ConsistentHashLoadBalance extends AbstractLoadBalance {

    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        return null;
    }

    static class ConsistenHashSelector {
        //虚拟调用者
        private final TreeMap<Long, String> virtulInvokers;
        //身份hash码
        private final int identityHashCode;

        ConsistenHashSelector(List<String> invokers, int replicaNumber, int identityHashCode) throws NoSuchAlgorithmException {
            this.virtulInvokers = new TreeMap<>();
            this.identityHashCode = identityHashCode;

            for (String invoker : invokers) {
                for (int i = 0; i < replicaNumber / 4; i++) {
                    byte[] digest = md5(invoker + i);
                    for (int h = 0; h < 4; h++) {
                        long m = hash(digest,h);
                    }
                }
            }
        }
    }


    /** md5加密 */
    static byte[] md5(String key) throws NoSuchAlgorithmException {
        MessageDigest md;
        md = MessageDigest.getInstance("MD5");
        byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
        md.update(bytes);
        return md.digest();
    }

    static long hash(byte[] digest, int idx) {
        return ((long) (digest[3 + idx * 4] & 255) << 24 | (long) (digest[2 + idx * 4] & 255) << 16 | (long) (digest[1 + idx * 4] & 255) << 8 | (long) (digest[idx * 4] & 255)) & 4294967295L;
    }



}
