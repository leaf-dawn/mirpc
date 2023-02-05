package com.mirpc.learn.iocode.nio;
import org.junit.Test;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
/**
 * @author fzw
 * @date 2022-08-06 22:33
 */
public class Channel {

    @Test
    public void test1() throws IOException {
        String str = "hello";
        FileOutputStream fileOutputStream = new FileOutputStream("d://xuexi.txt");
        FileChannel fileChannel = fileOutputStream.getChannel();
        //创建缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //将str放入byteBuffer中
        byteBuffer.put(str.getBytes());
        //转换
        byteBuffer.flip();
        //写入channel
        fileChannel.write(byteBuffer);
        fileOutputStream.close();
    }

    @Test
    public void test2() throws IOException {
        File file = new File("d://xuexi.txt");
        FileInputStream fileInputStream = new FileInputStream(file);
        //获取channel
        FileChannel fileChannel = fileInputStream.getChannel();
        //关联缓冲区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        //读取道buffer
        fileChannel.read(byteBuffer);
        System.out.println(new String(byteBuffer.array()));
        fileInputStream.close();
    }

    @Test
    public void test3()throws IOException {
        FileInputStream fileInputStream = new FileInputStream("d://xuexi.txt");
        FileChannel fileChannel1 = fileInputStream.getChannel();

        FileOutputStream fileOutputStream = new FileOutputStream("d://xuexi2.txt");
        FileChannel fileChannel2 = fileOutputStream.getChannel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        while (true) {
            //重置标志位
            byteBuffer.clear();
            int read = fileChannel1.read(byteBuffer);
            if (read == -1) {
                break;
            }
            //写入
            byteBuffer.flip();
            fileChannel2.write(byteBuffer);
        }

        fileInputStream.close();
        fileOutputStream.close();
    }

    @Test
    public void nioFileChannelTest() throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream("d://xuexi.txt");
        FileOutputStream fileOutputStream = new FileOutputStream("d://xuexi.txt");
        //获取对应filechannel
    }
}


