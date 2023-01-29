package com.mirpc.learn.iocode.chat;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @author fzw
 * 聊天系统服务端
 * @date 2022-08-09 11:53
 */
public class ChatServer {
    private Selector selector;

    private ServerSocketChannel serverSocketChannel;
    //端口号
    private static final int PORT = 6666;

    public ChatServer () {
        try {
            //初始化
            selector = Selector.open();
            //获取ServerSocketChannel
            serverSocketChannel = ServerSocketChannel.open();
            //设置非阻塞
            serverSocketChannel.configureBlocking(false);
            //绑定端口
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
            //注册到selector
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 监听 */
    public void listen() {
        try {
            while (true) {
                int count = selector.select(2000);
                if (count > 0) {
                    //有事件处理
                    //获取所有事件的selectionKey集合
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        if (key.isAcceptable()) {
                            //有连接事件
                            //获取连接
                            SocketChannel socketChannel = serverSocketChannel.accept();
                            //注册到selector
                            socketChannel.configureBlocking(false);
                            socketChannel.register(selector,SelectionKey.OP_READ);
                            //提示
                            System.out.println(socketChannel.getRemoteAddress() + "上线");
                        }
                        if (key.isReadable()) {
                            //读事件
                            readDate(key);
                        }
                        //删除key,防止重复处理
                        iterator.remove();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** 有数据发过来的逻辑 */
    private void readDate(SelectionKey key) {
        SocketChannel channel = null;
        try  {
            //获取channel
            channel = (SocketChannel)key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            //读取数据到buffer
            int count = channel.read(buffer);
            if(count>0) {
                //有数据
                String msg = new String(buffer.array());
                System.out.println(channel.getRemoteAddress()+": [" + msg +"]转发数据");
                //向其他客户端发消息
                sendInfoToOtherClients(msg,channel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void sendInfoToOtherClients(String msg,SocketChannel myChannel) throws IOException {
        //遍历所有注册到selector上的socketChannel
        for (SelectionKey key : selector.keys()) {
            //通过key取出channel
            Channel targetChannel = key.channel();
            //发送数据,排除发送给自己
            if (targetChannel instanceof SocketChannel && targetChannel != myChannel) {
                //转型
                SocketChannel socketChannel = (SocketChannel) targetChannel;
                //将msg存储到buffer
                ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
                //将buffer数据写入通道
                socketChannel.write(buffer);
            }
        }
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.listen();
    }
}
