package anyrpc.framework.utils;

import anyrpc.framework.constants.RpcNetConstants;
import anyrpc.framework.registry.zookeeper.CuratorUtils;
import anyrpc.framework.utils.treadpool.ThreadPoolFactoryUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @author fzw
 * 关机钩子，用于做关机后的处理
 * @date 2022k-09-10 14:22
 */

@Slf4j
public class ShutdownHook {

    private static final ShutdownHook SHUTDOWN_HOOK = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return SHUTDOWN_HOOK;
    }

    /**
     * 添加关机的钩子
     */
    public void addShutdownHook() {
        log.info("添加关机钩子");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(),
                        RpcNetConstants.PORT);
                CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), inetSocketAddress);
            } catch (UnknownHostException e) {
                log.error("无法识别主机id，导致无法注销zk的注册信息:{}",e.getMessage());
            }
            ThreadPoolFactoryUtil.shutDownAllThreadPool();
        }));
    }
}
