package main.java.com.mirpc.learn.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

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
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //基于http协议,使用http编码和解码
                            pipeline.addLast(new HttpServerCodec());
                            //以块方式写，添加chunkedWriteHandler处理器
                            pipeline.addLast(new ChunkedWriteHandler());
                            /*
                             *http数据传输过程是分段，httpObjectAggregator，就是将多端聚合
                             * 当浏览器发送大量数据时，就会发出多次请求
                             */
                            pipeline.addLast(new HttpObjectAggregator(8190));
                            /*
                             * 对于websocket，是以（frame）发送的
                             *  可以看到有webSocketFrame下有6个子类
                             * 请求时：ws://localhost:7000/hello  表示请求的uri
                             * webSocketServerProtocolHandler核心功能实时将http协议升级为ws协议，保持长连接
                             */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello"));
                            //自定义的handler
                            pipeline.addLast(new MyTestWebSocketFrameHandler());
                        }
                    });

            //启动
            ChannelFuture channelFuture = serverBootstrap.bind(7777).sync();

            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
