package com.fansqz.mirpc.framework.utils.config;

/**
 * @author fzw
 * 配置相关的枚举
 * @date 2022-09-05 11:44
 */
public class ConfigConstants {

     public static class RpcConfig {
         //rpc配置位置
        public final static String RPC_CONFIG_PATH = "rpc.properties";
        //zookeeper地址
        public final static String ZK_ADDRESS = "rpc.zookeeper.address";
        // rpc协议端口
        public final static String RPC_PROTOCOL_PORT = "rpc.protocol.port";
    }

    public static class DefaultConfig {

        /** rpc 协议默认端口 */
        public static final int DEFAULT_RPC_PROTOCOL_PORT = 13888;

        /** ZK的默认地址 */
        public static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";
    }

}
