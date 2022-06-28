package com.example.springbootvalidated.interceptor;

import com.example.springbootvalidated.annoation.AccessLimit;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Kevin
 * @Date: 2022/6/28 09:55
 * @Description: 访问拦截器
 * 限流的思路
 * <p>
 * 通过路径:ip的作为key，访问次数为value的方式对某一用户的某一请求进行唯一标识
 * 每次访问的时候判断key是否存在，是否count超过了限制的访问次数
 * 若访问超出限制，则应response返回msg:请求过于频繁给前端予以展示
 */
@Component
@Slf4j
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Handler 是否为 HandlerMethod 实例
        try {
            if (handler instanceof HandlerMethod) {
                // 强转
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                // 获取方法
                Method method = handlerMethod.getMethod();
                // 是否有AccessLimit注解
                if (!method.isAnnotationPresent(AccessLimit.class)) {
                    return true;
                }
                // 获取注解内容信息
                AccessLimit accessLimit = method.getAnnotation(AccessLimit.class);
                if (accessLimit == null) {
                    return true;
                }
                int maxCount = accessLimit.maxCount();
                int seconds = accessLimit.seconds();
                // 存储key
                String key = request.getRemoteAddr() + ":" + request.getContextPath() + ":" + request.getServletPath();
                // 已经访问的次数
                Integer count = (Integer)redisTemplate.opsForValue().get(key);
                log.info("已经访问的次数:" + count);
                if (null==count || -1 == count){
                    redisTemplate.opsForValue().set(key,1,seconds, TimeUnit.SECONDS);
                    return true;
                }
                if (count<maxCount){
                    redisTemplate.opsForValue().increment(key);
                    return true;
                }
                if (count>maxCount){
                    log.warn("请求过于频繁请稍后再试");
                    return false;
                }
            }
        } catch (Exception e) {
            log.warn("请求过于频繁请稍后再试");
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}