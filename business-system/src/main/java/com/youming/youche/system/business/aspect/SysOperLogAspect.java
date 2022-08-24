package com.youming.youche.system.business.aspect;


import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.aspect.SysOperatorLog;
import com.youming.youche.system.domain.SysOperLog;
import org.apache.dubbo.config.annotation.DubboReference;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 系统日志：切面处理类
 */
//@Aspect
//@Component
public class SysOperLogAspect {

//    private final Logger logger = LoggerFactory.getLogger(SysOperLogAspect.class);

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @Resource
    protected HttpServletRequest request;

    // 定义切点 @Pointcut
    // 在注解的位置切入代码
    @Pointcut("@annotation( com.youming.youche.system.aspect.SysOperatorLog)")
    public void logPoinCut() {
    }


    @AfterReturning("logPoinCut()")
    public void saveSysLog(JoinPoint joinPoint) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取切入点所在的方法
        Method method = signature.getMethod();


// 获取操作
        SysOperatorLog myLog = method.getAnnotation(SysOperatorLog.class);
//        if (myLog != null) {
//            String value = myLog.value();
//            reportLog.setOperation(value);// 保存获取的操作
//            logger.info("业务id：" + myLog.busId());
//            logger.info("备注：" + myLog.comment());
//            logger.info("业务编码：" + myLog.code());
//            logger.info("操作类型：" + myLog.type());
//        }
        SysOperLog sysOperLog = new SysOperLog();
//        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String header = request.getHeader("User-Agent");
/*        UserAgent ua = UserAgentUtil.parse(header);

        ua.getBrowser().toString();//Chrome
        ua.getVersion();//14.0.835.163
        ua.getEngine().toString();//Webkit
        ua.getEngineVersion();//535.1
        ua.getOs().toString();//Windows 7
        ua.getPlatform().toString();//Windows*/
//        Object[] args = joinPoint.getArgs();
//        // 将参数所在的数组转换成json
//        JSON parse = JSONUtil.parse(args);
//        String params = parse.toString();
        sysOperLog.setOpName(accessToken);
        sysOperLog.setBusiCode(myLog.code().getCode());
        sysOperLog.setBusiName(myLog.code().getName());
        sysOperLog.setBusiId(myLog.busId());
        sysOperLog.setOperType(myLog.type().getCode());
        sysOperLog.setOperTypeName(myLog.type().getName());
        sysOperLog.setOperComment(myLog.comment());
        sysOperLog.setChannelType(header);

//        if (tenantId != null && tenantId.length == 1) {
//            sysOperLog.setTenantId(tenantId[0]);
//        }
        sysOperLogService.save(sysOperLog,accessToken);

    }
}
