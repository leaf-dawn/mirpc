package anyrpc.framework.annotation;

import java.lang.annotation.*;

/**
 * @author fzw
 * rpc引入注解，添加到属性上
 * @date 2022-09-22 11:25
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RpcReference {

    String version() default "";

    String group() default "";
}
