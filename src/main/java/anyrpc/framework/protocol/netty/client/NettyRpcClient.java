package anyrpc.framework.protocol.netty.client;

import anyrpc.framework.constants.ServiceRegistryEnum;
import anyrpc.framework.protocol.netty.codec.RpcMessageDecoder;
import anyrpc.framework.protocol.netty.codec.RpcMessageEncoder;
import anyrpc.framework.protocol.netty.dto.RpcRequest;
import anyrpc.framework.registry.ServiceDiscovery;
import anyrpc.framework.registry.ServiceRegistryFactory;
import anyrpc.framework.utils.SingletonFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import netty.tcp.NettyClient;
import netty.tcp.NettyClientHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author fzw
 *  https://blog.csdn.net/weixin_31080716/article/details/113089494
 *  是否要实现断开重连
 * rpc客户端
 * @date 2022-09-13 19:50
 */

@Slf4j
public class NettyRpcClient implements RpcRequestTransport{

    private final ServiceDiscovery serviceDiscovery;

    private final RequestManager requestManager;

    private final ChannelManager channelManager;

    private final Bootstrap bootstrap;

    private final EventLoopGroup eventExecutors;

    public NettyRpcClient() {
        eventExecutors = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventExecutors)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                // 设置连接超时时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //设置心跳包发送
                        pipeline.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        pipeline.addLast(new RpcMessageEncoder());
                        pipeline.addLast(new RpcMessageDecoder());
                        pipeline.addLast(new NettyClientHandler());
                    }
                });
        this.serviceDiscovery = ServiceRegistryFactory.getServiceDiscovery(ServiceRegistryEnum.ZOOKEEPER.getCode());
        this.requestManager = SingletonFactory.getInstance(RequestManager.class);
        this.channelManager = SingletonFactory.getInstance(ChannelManager.class);

    }
    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        return null;
    }

    /**
     * todo：这里是否同步化就可以了
     * 进行连接到服务端
     * @param inetSocketAddress ：网络地址
     * @return : 连接的socket
     */
    public Channel doConnect(InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        //进行连接
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener)future -> {
            if (future.isSuccess()) {
                log.info("the client is connected [{}] successful", inetSocketAddress);
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }
}
