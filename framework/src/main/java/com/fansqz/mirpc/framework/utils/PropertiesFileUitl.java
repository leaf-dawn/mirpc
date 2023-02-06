package com.fansqz.mirpc.framework.utils;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author fzw
 * 配置文件相关工具
 * @date 2022-09-05 12:06
 */
@Slf4j
@NoArgsConstructor
public class PropertiesFileUitl {

    /**
     * 读取配置文件
     * @param fileName 文件相对路径+文件名
     * @return properties
     */
    public static Properties readPropertiesFile(String fileName) {
        //获取项目路径
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        String rpcConfigPath = "";
        if (url != null) {
            rpcConfigPath = url.getPath() + fileName;
        }
        Properties properties = null;
        try (FileReader fileReader = new FileReader(rpcConfigPath)) {
            properties = new Properties();
            properties.load(fileReader);
        } catch (Exception e) {
            log.error("读取rpc配置文件失败[{}]",fileName);
        }
        return properties;
    }
}
