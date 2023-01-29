package main.java.com.mirpc.learn.netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import org.junit.Test;

import java.nio.charset.Charset;

/**
 * @author fzw
 * buf测试
 * @date 2022-08-13 16:52
 */
public class NettyByteBufferTest {

    @Test
    public void test1() {
        //创建一个byteBuf
        //byteBuf里面包含一个大小为10的byte数组
        ByteBuf byteBuf = Unpooled.buffer(10);
        for (int i= 0; i < 10; i++){
            byteBuf.writeByte(i);
        }

        System.out.println("capacity=" + byteBuf.capacity());
        //输出
        //不需要反转
        for (int i = 0; i < byteBuf.capacity(); i++) {
            System.out.println(byteBuf.readByte());
        }

    }

    @Test
    public void test2() {
        ByteBuf byteBuf = Unpooled.copiedBuffer("hello,world!", CharsetUtil.UTF_8);
        //使用相关的方法
        if (byteBuf.hasArray()) {
            byte[] content = byteBuf.array();
            //将content转为字符串
            System.out.println(new String(content, Charset.forName("utf8")));

            System.out.println("byteBuf="+byteBuf);

            System.out.println(byteBuf.arrayOffset());
            System.out.println(byteBuf.readerIndex());
            System.out.println(byteBuf.writerIndex());
            System.out.println(byteBuf.capacity());

            int len = byteBuf.readableBytes();
            System.out.println("可读取的字节数:"+len);

            //按照范围读取
            System.out.println(byteBuf.getCharSequence(0,4,CharsetUtil.UTF_8));
            System.out.println(byteBuf.getCharSequence(4,6,CharsetUtil.UTF_8));
        }
    }
}
