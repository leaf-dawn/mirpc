package anyrpc.framework.registry.zookeeper;

import anyrpc.framework.constants.ConfigConstants;
import anyrpc.framework.utils.PropertiesFileUitl;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author fzw
 *
 * zookeeper客户端工具
 * todo:通过spring读取配置文件配置
 * @date 2022-09-01 10:27
 */

@Slf4j
public class CuratorUtils {

    private static final int BASE_SLEEP_TIME = 1000;

    private static final int MAX_RETRIED = 3;

    //根路径
    public static final String ZK_REGISTER_ROOT_PATH = "/my-rpc";

    //服务名-地址
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();

    //用于记录所有在zk注册过的服务器的地址
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();


    private static CuratorFramework zkClient;

    //ZK的默认地址
    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";


    /**
     * 构建持久节点,注册
     * 客户端断开时，节点不会移除
     * @param zkClient zk客户端
     * @param path 节点路径
     */
    public static void createPersistentNode(CuratorFramework zkClient, String path) {

        try {
            //检验是否已经注册
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null) {
                log.info("该节点已经被注册【{}】",path);
                return;
            }
            //进行注册
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
            log.info("节点注册成功【{}】",path);
            REGISTERED_PATH_SET.add(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取一个节点以下的节点，及获取一个服务名下对应的所有地址
     * @param zkClient zk客户端
     * @param rpcServiceName rpc服务名称
     * @return 该节点下所有节点
     */
    public static List<String> getChildrenNodes(CuratorFramework zkClient, String rpcServiceName) {
        //查看本地是否含有
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
            return SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }
        //到zk中取
        List<String> result = null;
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        try {
            result = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, result);
            //注册观察者
            registerWatcher(rpcServiceName, zkClient);
        } catch (Exception e) {
            log.error("获取子节点失败{}",servicePath);
        }
        return result;
    }

    /**
     * 取消注册
     * @param zkClient zk客户端
     * @param inetSocketAddress 地址
     */
    public static void clearRegistry(CuratorFramework zkClient, InetSocketAddress inetSocketAddress) {
        //异步删除
        REGISTERED_PATH_SET.stream().parallel().forEach(p->{
            try {
                if (p.endsWith(inetSocketAddress.toString())) {
                    zkClient.delete().forPath(p);
                }
            } catch (Exception e) {
                log.error("取消注册失败{}",p);
            }
        });
        log.error("该地址的所有注册服务已经取消【{}】",REGISTERED_PATH_SET.toString());
    }

    /**
     * 注册监听节点的变化
     * @param rpcServiceName rpc 服务名称
     * @param zkClient zk客户端
     */
    private static void registerWatcher(String rpcServiceName, CuratorFramework zkClient) throws Exception {
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        /**
         * 一个实用程序，它试图将来自 ZK 路径的所有子节点的所有数据保存在本地缓存。
         * 此类 * 将监视 ZK 路径，响应更新/创建/删除事件，下拉数据等。您可以 * 注册一个监听器，当发生更改时会收到通知。</p>
         *重要
         * - 不可能在事务上保持同步。此类的用户必须
         * 为误报和误报做好准备。此外，更新数据时始终使用版本号 * 以避免覆盖另一个进程的更改
         */
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        //进行监听，如果发生变化，进行更新服务名称下地址信息
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddresses);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }


    public static CuratorFramework getZkClient() {
        //检测配置是否配置了zk地址
        Properties properties = PropertiesFileUitl.readPropertiesFile(ConfigConstants.RpcConfig.RPC_CONFIG_PATH);
        //获取配置文件位置
        String zookeeperAddress = null;
        if (properties == null) {
            zookeeperAddress = DEFAULT_ZOOKEEPER_ADDRESS;
        }else {
            zookeeperAddress = properties.getProperty(ConfigConstants.RpcConfig.ZK_ADDRESS);
        }
        //设置重试策略，重试3次
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIED);
        //如果客户端已经启动，直接返回
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }
        //创建客户端
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(zookeeperAddress)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        try {
            //等待30s，直到连接成功
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("连接zk超时");
            }
        } catch (InterruptedException e) {
            log.error("连接zk失败");
        }
        return zkClient;
    }
}
