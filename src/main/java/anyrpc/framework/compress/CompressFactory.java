package anyrpc.framework.compress;

import anyrpc.framework.compress.gzip.GzipCompress;


/**
 * @author fzw
 * compress工厂，用codec来获取相应的compress
 * @date 2022-08-31 16:58
 */
public class CompressFactory {


    public static Compress getCompress(byte codec) {
        switch (codec) {
            case 0X01:
                return new GzipCompress();
            default:
                throw new RuntimeException("没有该压缩工具");
        }

    }
}
