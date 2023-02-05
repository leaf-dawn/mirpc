package com.fansqz.mirpc.framework.server;

import com.fansqz.mirpc.framework.protocol.codec.RpcMessageDecoder;
import com.fansqz.mirpc.framework.protocol.codec.RpcMessageEncoder;
import com.fansqz.mirpc.framework.provider.RpcServiceConfig;
import com.fansqz.mirpc.framework.provider.ServiceProvider;
import com.fansqz.mirpc.framework.provider.impl.ZkServiceProvider;
import com.fansqz.mirpc.framework.utils.CustomShutdownHook;
import com.fansqz.mirpc.framework.utils.RuntimeUtil;
import com.fansqz.mirpc.framework.utils.SingletonFactory;
import com.fansqz.mirpc.framework.utils.treadpool.ThreadPoolFactoryUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

import java.util.concurrent.TimeUnit;

/**
 * @author fzw
 * 服务端
 * @date 2022-08-29 20:22
 */
public class NettyRpcServer {

    private final ServiceProvider serviceProvider = SingletonFactory.getInstance(ZkServiceProvider.class);

    public void registerService(RpcServiceConfig rpcServiceConfig) {
        serviceProvider.publishService(rpcServiceConfig);
    }

    public static void start(int port) {
        //添加程序关闭的钩子
        CustomShutdownHook.getShutdownHook().addShutdownHook();
        NioEventLoopGroup boosGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        DefaultEventExecutorGroup eventExecutorGroup = new DefaultEventExecutorGroup(
                RuntimeUtil.cpus() * 2,
                ThreadPoolFactoryUtil.createThreadFactory("service-handler-group",false)
        );
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(boosGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //开启nagle
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    //开启tcp心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //设置tcp三次握手请求队列最大长度
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //对入站\出站事件进行日志记录
                    .handler(new LoggingHandler((LogLevel.INFO)))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //30 秒之内没有收到客户端请求的话就关闭连接
                            //idleStateHandler是处理空闲状态的处理器
                            // long readerIdleTime: 表示多长事件没有读，就会发送一个心跳检测是否连接状态
                            // log writerIdleTime: 表示多长事件没有写，就发送一个心跳检测是否连接状态
                            //long allIdleTime: 表示有多长时间没有读写，发送一个心跳检测是否连接状态
                            //如果IdleStateEvent触发候，就会传递给管道的下一个handler
                            //通过调用handler的userEventTiggered
                            pipeline.addLast(new IdleStateHandler(30, 0, 0 , TimeUnit.SECONDS));
                            pipeline.addLast(new RpcMessageDecoder());
                            pipeline.addLast(new RpcMessageEncoder());
                            pipeline.addLast(eventExecutorGroup, new NettyRpcServerHandler());
                        }
                    });
            //绑定端口，同步等待
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            //同步等待关闭
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
