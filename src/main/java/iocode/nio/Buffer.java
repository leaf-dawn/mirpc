package iocode.nio;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.RandomAccess;

/**
 * @author fzw
 * @date 2022-08-07 15:41
 */
public class Buffer {

    @Test
    public void test() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(64);
        for (int i = 0 ; i < 64; i++) {
            byteBuffer.put((byte)i);
        }
        //读取
        byteBuffer.flip();
        for (int i = 0; i < 32; i++){
            System.out.println(byteBuffer.get());
        }
        //设置buffer只读
        ByteBuffer readOnlyBuffer = byteBuffer.asReadOnlyBuffer();
        for (int i = 0; i < 32; i++) {
            System.out.println(readOnlyBuffer.get());
        }
    }

    /**
     * 可以让文件直接再内存（堆外内存）修改
     * @throws FileNotFoundException
     */
    @Test
    public void mappedByteBufferTest() throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("d://xuexi.txt","rw");
        //获取通道
        FileChannel channel = randomAccessFile.getChannel();
        //使用读写模式
        // 0 :可以直接修改的起始位置
        // 5 ：映射道内存的大小，即将xuexi.txt的多少个字节映射到内存
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE,0,5);
        mappedByteBuffer.put(0,(byte)'H');
        mappedByteBuffer.put(3,(byte)'9');

        //关闭
        randomAccessFile.close();
    }

}
