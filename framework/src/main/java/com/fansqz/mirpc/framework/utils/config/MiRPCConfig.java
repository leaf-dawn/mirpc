package com.fansqz.mirpc.framework.utils.config;

import java.util.Properties;

// 配置读取位置
public class MiRPCConfig {


    // zookeeper地址
    public static String ZOOKEEPER_ADDRESS;

    // rpc协议端口
    public static int RPC_PROTOCOL_PORT;

    static {
        // 读取properties配置
        Properties properties = PropertiesFileUitl.readPropertiesFile(ConfigConstants.RpcConfig.RPC_CONFIG_PATH);
        // 读取zookeeper
        if (properties == null || properties.getProperty(ConfigConstants.RpcConfig.ZK_ADDRESS) == null) {
            ZOOKEEPER_ADDRESS = ConfigConstants.DefaultConfig.DEFAULT_ZOOKEEPER_ADDRESS;
        }else {
            ZOOKEEPER_ADDRESS = properties.getProperty(ConfigConstants.RpcConfig.ZK_ADDRESS);
        }
        // 读取rpc协议端口
        if (properties == null || properties.getProperty(ConfigConstants.RpcConfig.RPC_PROTOCOL_PORT) == null) {
            RPC_PROTOCOL_PORT = ConfigConstants.DefaultConfig.DEFAULT_RPC_PROTOCOL_PORT;
        }else {
            RPC_PROTOCOL_PORT = Integer.parseInt(properties.getProperty(ConfigConstants.RpcConfig.RPC_PROTOCOL_PORT));
        }
    }
}
