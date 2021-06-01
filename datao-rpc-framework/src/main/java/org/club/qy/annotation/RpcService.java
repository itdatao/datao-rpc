package org.club.qy.annotation;

import java.lang.annotation.*;

/**
 * @Author hht
 * @Date 2021/5/20 10:24
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcService {
    String version() default "";
    String group() default "";

}
