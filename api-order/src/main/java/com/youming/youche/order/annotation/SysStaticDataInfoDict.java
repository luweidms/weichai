package com.youming.youche.order.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SysStaticDataInfoDict {

    /**
     * 数据dataSource
     *
     * @return
     */
    String dictDataSource();

    /**
     * 返回put到json中的文本key
     * @return
     */
    String dictText() default "";

}
