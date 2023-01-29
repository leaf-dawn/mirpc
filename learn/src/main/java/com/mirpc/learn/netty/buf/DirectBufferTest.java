package main.java.com.mirpc.learn.netty.buf;

import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

/**
 * @author fzw
 * 直接内存和堆内存
 * @date 2022-09-17 16:45
 */
public class DirectBufferTest {

    public static void main(String[] args) {
        //申请堆内存
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);
        //申请直接内存
        //容易内存泄漏，但是可以减少拷贝次数
        ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(1024 * 1024);
    }
}
