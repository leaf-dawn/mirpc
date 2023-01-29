package main.java.com.mirpc.learn.netty.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author fzw
 * 服务端
 * @date 2022-08-11 16:26
 */
public class NettyServer {

    public static void main(String[] args) throws InterruptedException {
        //创建bossGroup和workerGroup
        //1.创建两个线程组，
        //boosGroup只是处理连接请求,真正处理业务，在workerGroup完成
        //bossGroup和workerGroup含有子线程个数: 实际cpu核数 * 2
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //创建服务器端的启动对象，
            ServerBootstrap bootstrap = new ServerBootstrap();
            //使用链式编程
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new NettyServerInitializer());
            //启动服务器，（并绑定端口）
            ChannelFuture cf = bootstrap.bind(8888).sync();

            //对关闭通道进行监听
            cf.channel().closeFuture().sync();
        }finally {
            //最后优雅关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }
}
