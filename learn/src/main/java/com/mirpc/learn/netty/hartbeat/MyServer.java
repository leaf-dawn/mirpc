package main.java.com.mirpc.learn.netty.hartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;

/**
 * @author fzw
 * 心跳机制服务端
 * @date 2022-08-13 23:31
 */
public class MyServer {

    public static void main(String[] args) throws InterruptedException {
        //创建事件循环组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<ServerSocketChannel>() {
                        @Override
                        protected void initChannel(ServerSocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //idleStateHandler是处理空闲状态的处理器
                            // long readerIdleTime: 表示多长事件没有读，就会发送一个心跳检测是否连接状态
                            // log writerIdleTime: 表示多长事件没有写，就发送一个心跳检测是否连接状态
                            //long allIdleTime: 表示有多长时间没有读写，发送一个心跳检测是否连接状态
                            //如果IdleStateEvent触发候，就会传递给管道的下一个handler
                            //通过调用handler的userEventTiggered
                            pipeline.addLast(new IdleStateHandler(3,5,7, TimeUnit.SECONDS));
                            //加入一个对空闲检测进一步处理的handler(自定义)
                            pipeline.addLast(new MyServerHandler());

                        }
                    });

            //启动
            ChannelFuture channelFuture = serverBootstrap.bind(7000).sync();

            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
