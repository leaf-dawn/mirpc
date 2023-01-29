package main.java.com.mirpc.learn.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author fzw
 * @date 2022-08-12 22:35
 */
public class TestServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        //向管道加入处理器
        //得到管道
        ChannelPipeline pipeline = socketChannel.pipeline();
        //加入一个netty提供的httpServerCodec codec =>[coder ->decoder]
        //说明：
        //1. netty用的http的编码解码器
        pipeline.addLast("MyHttpServerCodec",new HttpServerCodec());
        //添加一个自定义handler
        pipeline.addLast("MyTestHttpServerHandler",new TestHttpServerHandler());
    }
}
