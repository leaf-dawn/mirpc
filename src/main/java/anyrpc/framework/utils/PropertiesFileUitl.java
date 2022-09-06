package anyrpc.framework.utils;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
        try (InputStreamReader inputStreamReader = new InputStreamReader(
                new FileInputStream(rpcConfigPath),StandardCharsets.UTF_8)) {
            properties = new Properties();
            properties.load(inputStreamReader);
        } catch (Exception e) {
            log.error("读取rpc怕配置文件失败[{}]",fileName);
        }
        return properties;
    }
}
