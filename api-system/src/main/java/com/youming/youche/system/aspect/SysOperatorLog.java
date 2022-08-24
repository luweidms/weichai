package com.youming.youche.system.aspect;




import com.youming.youche.commons.constant.SysOperLogConst;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解类
 * @author terry
 */
@Target(ElementType.METHOD) //注解放置的目标位置,METHOD是可注解在方法级别上
@Retention(RetentionPolicy.RUNTIME) //注解在哪个阶段执行
public  @interface SysOperatorLog {

    String value() default "";
//    String value() default "";
//    String value() default "";
//    String value() default "";

    SysOperLogConst.BusiCode code();
    long busId() default 0;
    SysOperLogConst.OperType type();
    String comment() default "";
}
