package netty.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author fzw
 * @date 2022-08-12 15:25
 */
public class NettyClient {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup eventExecutors = new NioEventLoopGroup();

        //创建客户端启动对象
        //不是ServerBootstrap而是Bootstrap
        Bootstrap bootstrap = new Bootstrap();
        try {
            //设置参数
            bootstrap.group(eventExecutors) //线程组
                    .channel(NioSocketChannel.class) //设置客户端管道
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new NettyClientInitializer());
                        }
                    });
            System.out.println("客户端 ok...");
            //启动客户端去连接服务端
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8888).sync();
            //给关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        }finally {
            eventExecutors.shutdownGracefully();
        }
    }
}
