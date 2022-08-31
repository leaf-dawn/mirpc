package anyrpc.framework.compress;

/**
 * @author fzw
 * 压缩相关
 * @date 2022-08-31 12:19
 */
public interface Compress {

    /**
     * 压缩
     * @param bytes 需要压缩bytes
     * @return 压缩后bytes
     */
    byte[] compress(byte[] bytes);


    /**
     * 解压
     * @param bytes 需要解压的bytes
     * @return 原bytes
     */
    byte[] decompress(byte[] bytes);

}
