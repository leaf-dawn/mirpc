package anyrpc.framework.provider;

/**
 * @author fzw
 * 服务提供者
 * @date 2022-09-08 12:04
 */
public interface ServiceProvider {

    /**
     * 添加服务
     * @param rpcServiceConfig :
     */
    void addService(RpcServiceConfig rpcServiceConfig);

    /**
     * 获取服务
     * @param rpcServiceName ：
     * @return :
     */
    Object getService(String rpcServiceName);

    /**
     * 服务发布
     * @param rpcServiceConfig :
     */
    void publiService(RpcServiceConfig rpcServiceConfig);
}
