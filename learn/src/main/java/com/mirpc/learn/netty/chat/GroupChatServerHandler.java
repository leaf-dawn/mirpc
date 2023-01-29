package main.java.com.mirpc.learn.netty.chat;

import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;

/**
 * @author fzw
 * 处理器
 * @date 2022-08-13 21:48
 */
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    /** 定义一个全局channel组，管理所有channel
    GlobalEventExecutor.INSTANCE 是一个全局的事件执行器，为单例 */
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**    handlerAdded表示连接建立，一旦连接，第一个被执行  */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //将该客户端加入聊天的信息推送给其他在线客户端
        //该方法会向channel中所有的channel遍历，并发消息
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 加入聊天");
        channelGroup.add(channel);
    }


    /** 表示channel处于活动状态，提示xx上线 */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "上线");
    }

    /** channel处于不活动状态，提示离线 */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+"离线");
    }

    /** 断开连接时，推送消息给其他用户*/
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() +"离开");
        //不需要channelGroup.remote(channel)，断开连接时，自己会remote
    }

    /** 将channel加到channelGroup */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        //获取当前channel
        Channel channel = ctx.channel();
        //这时，遍历channelCroup，根据不同情况，发送不同消息
        channelGroup.forEach(ch->{
            if (channel != ch) {
                ch.writeAndFlush("[客户端]" + channel.remoteAddress()+"发送了消息：" + msg + "\n");
            } else {
                ch.writeAndFlush("消息发送成功：" + msg);
            }
        });
    }
}
