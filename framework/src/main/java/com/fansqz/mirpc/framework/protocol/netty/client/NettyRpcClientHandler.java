package com.fansqz.mirpc.framework.protocol.netty.client;

import com.fansqz.mirpc.framework.constants.CompressTypeEnum;
import com.fansqz.mirpc.framework.constants.SerializationTypeEnum;
import com.fansqz.mirpc.framework.protocol.netty.constants.RpcConstants;
import com.fansqz.mirpc.framework.protocol.netty.dto.RpcMessage;
import com.fansqz.mirpc.framework.protocol.netty.dto.RpcResponse;
import com.fansqz.mirpc.framework.utils.SingletonFactory;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author fzw
 * netty客户端处理
 * @date 2022-09-19 15:55
 */
@Slf4j
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<RpcMessage> {

    private final RequestManager requestManager;

    private final ChannelManager channelManager;

    public NettyRpcClientHandler() {
        requestManager = SingletonFactory.getInstance(RequestManager.class);
        channelManager = SingletonFactory.getInstance(ChannelManager.class);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {
        try {
            log.info("client receive message:[{}]", msg);
            byte messageType = msg.getMessageType();
            if (messageType == RpcConstants.MessageType.HEARTBEAT_RESPONSE_TYPE) {
                log.info("heart [{}]", msg.getData());
            } else if (messageType == RpcConstants.MessageType.RESPONSE_TYPE) {
                RpcResponse<Object> rpcResponse = (RpcResponse<Object>) msg.getData();
                //设置该请求为完成
                requestManager.complete(rpcResponse);
            }
        }finally {
            //手动释放msg，返回结果以后，msg不应该继续保留使用
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //封装心跳事件，发送rpc心跳包
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent)evt).state();
            if (state == IdleState.WRITER_IDLE) {
                log.info("发送心跳包[{}]", ctx.channel().remoteAddress());
//                Channel channel = channelManager.get(((InetSocketAddress)ctx.channel().remoteAddress()));
                Channel channel = ctx.channel();
                RpcMessage rpcMessage = new RpcMessage();
                rpcMessage.setCodec(SerializationTypeEnum.PROTOSTUFF.getCode());
                rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());
                rpcMessage.setMessageType(RpcConstants.MessageType.HEARTBEAT_REQUEST_TYPE);
                rpcMessage.setData("ping");
                //io完成以后，如果操作失败，则关闭channel
                channel.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx,evt);
        }
    }


    /**
     * 发生异常关闭channel
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("client catch exception; ", cause);
        cause.printStackTrace();
        ctx.close();
    }
}
