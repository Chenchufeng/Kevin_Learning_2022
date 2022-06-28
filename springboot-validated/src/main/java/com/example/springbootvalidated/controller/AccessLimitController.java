package com.example.springbootvalidated.controller;

import com.example.springbootvalidated.annoation.AccessLimit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: Kevin
 * @Date: 2022/6/28 10:22
 * @Description:
 */
@RestController
@RequestMapping("access")
@Slf4j
public class AccessLimitController {
    @GetMapping("/test")
    @AccessLimit(maxCount = 5,seconds = 60)
    public String limit(HttpServletRequest request){
        log.error("Access Limit Test");
        return "限流测试";
    }

}