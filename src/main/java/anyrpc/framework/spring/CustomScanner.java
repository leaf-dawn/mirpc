package anyrpc.framework.spring;

import jdk.internal.org.objectweb.asm.tree.AnnotationNode;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;

/**
 * @author fzw
 * 包扫描
 * @date 2022-09-22 10:48
 */
public class CustomScanner extends ClassPathBeanDefinitionScanner {

    public CustomScanner(BeanDefinitionRegistry registry,Class<? extends Annotation> annotype){
        super(registry);
        super.addIncludeFilter(new AnnotationTypeFilter(annotype));
    }
    public CustomScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }
}
