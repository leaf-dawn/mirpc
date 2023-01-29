package anyrpc.framework.protocol.netty.client;

import anyrpc.framework.constants.CompressTypeEnum;
import anyrpc.framework.constants.SerializationTypeEnum;
import anyrpc.framework.constants.ServiceRegistryEnum;
import anyrpc.framework.protocol.netty.codec.RpcMessageDecoder;
import anyrpc.framework.protocol.netty.codec.RpcMessageEncoder;
import anyrpc.framework.protocol.netty.constants.RpcConstants;
import anyrpc.framework.protocol.netty.dto.RpcMessage;
import anyrpc.framework.protocol.netty.dto.RpcRequest;
import anyrpc.framework.protocol.netty.dto.RpcResponse;
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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import netty.tcp.NettyClientHandler;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author fzw
 *  https://blog.csdn.net/weixin_31080716/article/details/113089494
 *  是否要实现断开重连
 * rpc客户端
 * @date 2022-09-13 19:50
 */

@Slf4j
public class NettyRpcClient {

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

    @SneakyThrows
    public CompletableFuture<RpcResponse<Object>> sendRpcRequest(RpcRequest rpcRequest) {
        //发送数据
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        //获取地址地址对应channel
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest);
        Channel channel = getChannel(inetSocketAddress);
        if (channel.isActive()) {
            //添加future
            requestManager.put(rpcRequest.getRequestId(), resultFuture);
            //设置rpcMessage并发送
            RpcMessage rpcMessage = RpcMessage.builder().data(rpcRequest)
                    .codec(SerializationTypeEnum.HESSIAN.getCode())
                    .compress(CompressTypeEnum.GZIP.getCode())
                    .messageType(RpcConstants.MessageType.REQUEST_TYPE).build();
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future ->{
                //如果成功，则发送发送成功信息
                if (future.isSuccess()) {
                    log.info("client send message: [{}]", rpcMessage);
                } {
                    //失败则结束该请求
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("send failed:", future.cause());
                }
            });
            return resultFuture;
        } else {
            throw new  IllegalStateException();
        }
    }

    /**
     * todo：这里是否同步化就可以了
     * 进行连接到服务端
     * @param inetSocketAddress ：网络地址
     * @return : 连接的socket
     */
    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
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


    /**
     * 根据地址获取channel，如果没有channel就进行连接
     * @param inetSocketAddress 网路信息
     * @return channel
     */
    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelManager.get(inetSocketAddress);
        if (channel == null) {
            channel = doConnect(inetSocketAddress);
            channelManager.set(inetSocketAddress, channel);
        }
        return channel;
    }
}
