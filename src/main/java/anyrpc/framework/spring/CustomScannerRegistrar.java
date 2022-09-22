package anyrpc.framework.spring;

import anyrpc.framework.annotation.RpcScan;
import com.sun.xml.internal.ws.wsdl.writer.document.Import;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

/**
 * @author fzw
 * 用来动态注入bean的
 * 把用RpcService的注解的bean注册到spring中
 * 关于ImportBeanDefinitionRegistrar https://zhuanlan.zhihu.com/p/91461558
 * 不会请看： https://www.php.cn/manual/view/21527.html spring中文手册
 * @date 2022-09-20 20:43
 */
public class CustomScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private static final String RPC_BASE_PACKAGE_ATTRIBUTE_NAME = "basePackage";

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        //resourceLoaderAware用来添加resourceLoader的引用的。spring调用setResourceLoader接口设置
       this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        //获取注解的属性basePackage参数值
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(RpcScan.class.getName()));
        String[] rpcScanBasePackages = new String[]{};
        if (annotationAttributes != null) {
            rpcScanBasePackages = annotationAttributes.getStringArray(RPC_BASE_PACKAGE_ATTRIBUTE_NAME);
        }
        //如果没有传递，则在该注解所注释的启动类所在的包下
        if (rpcScanBasePackages.length == 0) {
            rpcScanBasePackages = new String[]{ClassUtils.getPackageName(annotationMetadata.getClassName())};
        }
        //用于扫描rpcService注解
        CustomScanner customScanner = new CustomScanner(registry,RpcScan.class);
        if (resourceLoader != null) {
            customScanner.setResourceLoader(resourceLoader);
        }
        //对这些包进行扫描
        int rpcServiceCount = customScanner.scan(rpcScanBasePackages);
    }
}
