package main.java.com.mirpc.learn.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author fzw
 * 1. 我们定义一个handler，需要继续netty规定好
 * @date 2022-08-12 12:01
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 读取数据实际（这里我们可以读取到客户端发送的消息）
     * @param ctx 上下文的对象，含有管道pipeline,通道channel,地址
     * @param msg 客户端发送的数据
     * @throws Exception
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {
//        System.out.println("server ctx = "+ ctx);
//        //将msg转为byteBuf
//        ByteBuf buf = (ByteBuf)msg;
//        System.out.println("客户端发送的数据为:" + buf.toString(CharsetUtil.UTF_8));
//        System.out.println("客户端地址：" + ctx.channel().remoteAddress());
//
//        如果有一个耗时业务,需要异步执行
//
//        //解决方案一:用户自定义普通线程，放进任务队列 taskQueue
//        ctx.channel().eventLoop().execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(10*1000);
//                    ByteBuf buf = (ByteBuf)msg;
//                    System.out.println("客户端数据：" + buf.toString(CharsetUtil.UTF_8));
//                    //响应
//                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端呀1",CharsetUtil.UTF_8));
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        ctx.channel().eventLoop().execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(10*1000);
//                    ByteBuf buf = (ByteBuf)msg;
//                    System.out.println("客户端数据：" + buf.toString(CharsetUtil.UTF_8));
//                    //响应
//                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端呀2",CharsetUtil.UTF_8));
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });


        //方法2：用户自定义定时任务， 该任务提交到 scheduleTaskQueue
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10*1000);
                    ByteBuf buf = (ByteBuf)msg;
                    System.out.println("客户端数据：" + buf.toString(CharsetUtil.UTF_8));
                    //响应
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端呀2",CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },5, TimeUnit.SECONDS);
    }

    /**
     * 读取结束
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //write + flush
        //把数据写道缓存，并加到管道
        //一般讲，我们对这个发送数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello,客户端",CharsetUtil.UTF_8));
    }

    /**
     * 发生异常时
     * 需要关闭通道
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
