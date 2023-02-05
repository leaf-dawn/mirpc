package com.mirpc.learn.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
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
            bootstrap.group(bossGroup,workerGroup) //设置两个线程组
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,128)//设置线程队列的到连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE,true) //设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //给pipeline设置处理器
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //这里可以用一个集合管理SocketChannel，需要推送消息时，可以推到各个channel对应的NioEventLooop
                            //的taskQueue或scheduleTaskQueue
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    });//给我们的workerGroup设置的EventLoop对应的管道设置处理器
            System.out.println("服务器 is ready ...");
            //绑定电口并且同步，生成一个ChannelFuture对象
            //启动服务器，（并绑定端口）
            ChannelFuture cf = bootstrap.bind(6667).sync();

            //给cf注册监听器，监控我们关心的事情
            cf.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (cf.isSuccess()) {
                        System.out.println("监听端口6667成功");
                    }else {
                        System.out.println("监听端口6667失败");
                    }
                }
            });
            //对关闭通道进行监听
            cf.channel().closeFuture().sync();
        }finally {
            //最后优雅关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }
}
