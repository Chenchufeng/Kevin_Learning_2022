package com.example.springbootvalidated.annoation;

import java.lang.annotation.*;

@Inherited
@Documented
@Target({ElementType.METHOD,ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {
    /**
     * 指定second 时间内 API请求次数
     */
    int maxCount() default 5;

    /**
     * 请求次数的指定时间范围  秒数(redis数据过期时间)
     */
    int seconds() default 60;

}
