package com.example.demo.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 接口日志切面
 * <p>
 * 使用 Spring AOP 无侵入打印每个接口的请求信息：
 * 请求地址、请求方式、入参、出参、接口耗时。
 * <p>
 * 类比前端：axios 请求/响应拦截器，在每个请求前后打印日志。
 * </p>
 */
@Slf4j
@Aspect
@Component
public class ApiLogAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 环绕通知：拦截所有 /api/** 下的 Controller 方法
     * <p>
     * 执行顺序：前置记录 → 执行目标方法 → 后置记录耗时和返回值
     * </p>
     */
    @Around("execution(* com.example.demo.controller..*.*(..))")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取请求信息
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }
        HttpServletRequest request = attributes.getRequest();

        String requestUrl = request.getRequestURI();
        String requestMethod = request.getMethod();
        String clientIp = request.getRemoteAddr();

        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();

        // 获取入参
        Object[] args = joinPoint.getArgs();
        String params;
        try {
            // 过滤掉 HttpServletRequest/Response 等不可序列化的对象
            Object[] serializableArgs = filterSerializableArgs(args, method.getParameterTypes());
            params = objectMapper.writeValueAsString(serializableArgs);
        } catch (Exception e) {
            params = "[参数序列化失败]";
        }

        // 记录请求开始
        long startTime = System.currentTimeMillis();
        log.info("========== 请求开始 ==========");
        log.info("请求地址: {} {}", requestMethod, requestUrl);
        log.info("客户端IP: {}", clientIp);
        log.info("调用方法: {}.{}", className, methodName);
        log.info("请求参数: {}", params);

        // 执行目标方法
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("接口异常: {}.{} 异常信息: {}", className, methodName, throwable.getMessage());
            throw throwable;
        } finally {
            // 计算耗时
            long endTime = System.currentTimeMillis();
            long costTime = endTime - startTime;

            // 记录响应信息
            String responseJson;
            try {
                responseJson = objectMapper.writeValueAsString(result);
                // 截断过长的响应体
                if (responseJson.length() > 500) {
                    responseJson = responseJson.substring(0, 500) + "...[已截断]";
                }
            } catch (Exception e) {
                responseJson = "[响应序列化失败]";
            }
            log.info("响应结果: {}", responseJson);
            log.info("接口耗时: {}ms", costTime);
            log.info("========== 请求结束 ==========\n");
        }

        return result;
    }

    /**
     * 过滤不可序列化的参数类型（HttpServletRequest 等）
     */
    private Object[] filterSerializableArgs(Object[] args, Class<?>[] paramTypes) {
        if (args == null || args.length == 0) {
            return new Object[]{};
        }
        Object[] result = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] != null && isSerializableType(args[i].getClass())) {
                result[i] = args[i];
            } else {
                result[i] = args[i] != null ? args[i].getClass().getSimpleName() : "null";
            }
        }
        return result;
    }

    /**
     * 判断是否为可序列化的类型
     */
    private boolean isSerializableType(Class<?> clazz) {
        String name = clazz.getName();
        return !name.startsWith("jakarta.servlet") &&
                !name.startsWith("org.springframework.web.multipart") &&
                !clazz.isAssignableFrom(HttpServletRequest.class);
    }
}
