package main.java.com.mirpc.learn.netty.tcp;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author fzw
 * @date 2022-08-16 10:23
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //获取pipeline
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new MessageEncoder()); //添加编码器
        pipeline.addLast(new NettyClientHandler());
    }
}
