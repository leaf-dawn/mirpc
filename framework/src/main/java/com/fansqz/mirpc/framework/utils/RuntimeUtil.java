package com.fansqz.mirpc.framework.utils;

/**
 * @author fzw
 * runtime相关工具包
 * @date 2022-09-11 16:36
 */
public class RuntimeUtil {

    /**
     * 获取cpu核数
     * @return :cpu核数
     */
    public static int cpus() {
        return Runtime.getRuntime().availableProcessors();
    }
}
