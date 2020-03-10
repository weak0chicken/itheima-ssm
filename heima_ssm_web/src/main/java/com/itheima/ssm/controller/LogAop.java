package com.itheima.ssm.controller;

import com.itheima.ssm.domain.SysLog;
import com.itheima.ssm.service.ISysLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.Security;
import java.util.Date;

@Component
@Aspect
public class LogAop {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private ISysLogService sysLogService;

    private Date visitTime; //开始访问时间
    private Class clazz; //访问的类
    private Method method; //访问的方法

    @Before("execution(* com.itheima.ssm.controller.*.*(..))")
    public void doBefore(JoinPoint jp) throws NoSuchMethodException {
        visitTime = new Date(); //当前时间就是开始访问时间

        //获取具体执行的方法的Method对象
        clazz = jp.getTarget().getClass(); //具体要访问的类
        String methodName = jp.getSignature().getName(); //获取访问的方法的名称
        Object[] args = jp.getArgs(); //获取参数

        if(args==null || args.length==0) {
            method = clazz.getMethod(methodName);  //只能获取无参数的方法
        }else{
            Class[] classArgs = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                classArgs[i] = args[i].getClass();
            }
            method = clazz.getMethod(methodName,classArgs);
        }
    }

    @After("execution(* com.itheima.ssm.controller.*.*(..))")
    public void doAfter(JoinPoint jp) throws Exception {
        long time = new Date().getTime() - visitTime.getTime();   //获取访问时长

        //获取url
        String url = "";
        if(clazz != null && method != null && clazz != LogAop.class) {

            //1.获取类上的@RequestMapping("/orders")
            RequestMapping classAnnotation = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
            if(classAnnotation != null) {
                String[] classValue = classAnnotation.value();
                //2.获取方法上的@RequestMapping("/xxx")
                RequestMapping methodAnnotation = method.getAnnotation(RequestMapping.class);
                if(methodAnnotation != null) {
                    String[] methodValue = methodAnnotation.value();
                    url = classValue[0] + methodValue[0];
                }
            }
        }


        //获取访问的ip
        String ip = request.getRemoteAddr();

        //获取访问者
        /*SecurityContext context = SecurityContextHolder.getContext(); //1-1通过SecurityContextHolder获取Spring Security的环境对象context*/
        SecurityContext context = (SecurityContext) request.getSession().getAttribute("SPRING_SECURITY_CONTEXT"); //1-2通过request获取Security环境对象context
        User user = (User) context.getAuthentication().getPrincipal(); //2.根据环境对象获取当前操作对象,即user
        String username = user.getUsername();  //3.获取操作者名称

        //将日志信息封装到SysLog对象
        SysLog syslog = new SysLog();
        syslog.setExecutionTime(time);   //执行时长
        syslog.setIp(ip);   //访问ip地址
        syslog.setMethod("[类名] "+clazz.getName() + "[方法名] " + method.getName());
        syslog.setUrl(url);
        syslog.setUsername(username);
        syslog.setVisitTime(visitTime);

        //调用Service完成数据库的日志操作
        sysLogService.save(syslog);
    }


}
