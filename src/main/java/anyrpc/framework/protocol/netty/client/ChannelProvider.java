package anyrpc.framework.protocol.netty.client;


import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fzw
 * 使用map保存管道，提交查找速度
 * todo: 易发生内存泄漏， channel已经失效，但是一直没有使用这个channel
 * @date 2022-09-13 19:55
 */

@Slf4j
public class ChannelProvider {

    private final Map<String, Channel> channelMap;

    public ChannelProvider() {
        this.channelMap = new ConcurrentHashMap<>();
    }

    /**
     * 获取连接还有效的channel，如果channel关闭的话，需要移除
     * todo:这里建议在close哪里
     * @param inetSocketAddress ：
     * @return channel
     */
    public Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        //获取channel后需要判断是否还有效，如果没有校就直接去除
        if (channelMap.containsKey(key)) {
            Channel channel = channelMap.get(key);
            if (channel == null || !channel.isActive()) {
                channelMap.remove(key);
                return null;
            }
            return channel;
        }
        return null;
    }

    /**
     * 添加一个channel
     * @param inetSocketAddress :
     * @param channel :
     */
    public void set(InetSocketAddress inetSocketAddress, Channel channel) {
        String key = inetSocketAddress.toString();
        channelMap.put(key, channel);
    }


    /**
     * 移除一个channel
     * 在channel的连接断开时使用
     * @param inetSocketAddress ：
     */
    public void remove(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        channelMap.remove(key);
        log.info("remove channel [{}]", inetSocketAddress.getHostName());
    }

}
