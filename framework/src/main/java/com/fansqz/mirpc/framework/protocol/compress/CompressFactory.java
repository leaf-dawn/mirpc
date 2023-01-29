package com.fansqz.mirpc.framework.protocol.compress;

import com.fansqz.mirpc.framework.protocol.compress.gzip.GzipCompress;


/**
 * @author fzw
 * compress工厂，用codec来获取相应的compress
 * @date 2022-08-31 16:58
 */
public class CompressFactory {


    public static Compress getCompress(byte compressCode) {
        switch (compressCode) {
            case 0X01:
                return new GzipCompress();
            default:
                throw new RuntimeException("没有该压缩工具");
        }

    }
}
