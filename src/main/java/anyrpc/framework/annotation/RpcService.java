package anyrpc.framework.annotation;

import java.lang.annotation.*;

/**
 * @author fzw
 * Rpc服务提供注解，添加该注解，则是rpc服务
 * @date 2022-09-22 11:12
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
//https://www.jianshu.com/p/7f54e7250be3
@Inherited
public @interface RpcService {

    String version() default "";

    String group() default "";
}
