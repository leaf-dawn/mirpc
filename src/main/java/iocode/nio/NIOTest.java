package iocode.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author fzw
 * NIO是面向buffer编程的
 * @date 2022-08-05 15:41
 */

class NIOService {

    public static void main(String[] args) throws IOException {

        //创建serverSocketChannel - > ServerSocket
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //得到一个Selector对象
        Selector selector = Selector.open();

        //绑定端口
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        //设置为非阻塞
        serverSocketChannel.configureBlocking(false);

        //把serverSocketChannel注册到selector,关注的事件为OP_ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //循环等待客户端连接
        while (true) {
            //等待1秒，看看有没有事件发生
            if (selector.select(1000) == 0) {
                //无事件发生
                System.out.println("服务器等待1秒，无连接");
                continue;
            }
            //通过selectionKeys可以反向获取通道
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey selectionKey = keyIterator.next();
                //如果是创建连接
                if (selectionKey.isAcceptable()) {
                    //如果是OP_ACCEPT,有客户端连接
                    //给该客户端生成socketChannel
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    //设置为非阻塞
                    socketChannel.configureBlocking(false);
                    //将socketChannel注册到selector,关注读事件
                    //同时关联一个Buffer
                    socketChannel.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }
                //读事件
                if (selectionKey.isReadable()) {
                    //通过key 方向获取channel
                    SocketChannel channel = (SocketChannel)selectionKey.channel();
                    //获取该channel关联的buffer
                    ByteBuffer buffer = (ByteBuffer)selectionKey.attachment();
                    channel.read(buffer);
                    System.out.println("读取到的数据是" + new String(buffer.array()));
                }
                //手动移除selectionKey,防止重复操作
                keyIterator.remove();
            }
        }

    }
}


class NIOClient {
    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        //设置非阻塞
        socketChannel.configureBlocking(false);
        //提供ip和端口
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1",6666);
        //连接服务器
        if (!socketChannel.connect(inetSocketAddress)) {
            while (!socketChannel.finishConnect()) {
                System.out.println("连接需要事件，客户端不会阻塞");
            }
        }
        //连接成功,发送数据
        String str = "hello";
        //wrap,根据字节数组大小来创建buffer大小，并放进去
        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());
        socketChannel.write(buffer);
        System.in.read();
    }
}
