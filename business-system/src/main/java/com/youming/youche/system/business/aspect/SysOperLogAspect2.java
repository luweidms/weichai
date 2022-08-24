package com.youming.youche.system.business.aspect;


import cn.hutool.core.map.MapUtil;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.aspect.SysOperatorLog;
import com.youming.youche.system.domain.SysOperLog;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统日志：切面处理类
 */
//@Aspect
//@Component
public class SysOperLogAspect2 {

    private final Logger logger = LoggerFactory.getLogger(SysOperLogAspect.class);

    /**
     * 用来识别属性是否是基本数据类型
     *
     */
    private static String[] types = { "java.lang.Integer", "java.lang.Double", "java.lang.Float", "java.lang.Long",
            "java.lang.Short", "java.lang.Byte", "java.lang.Boolean", "java.lang.Char", "java.lang.String", "int",
            "double", "long", "short", "byte", "boolean", "char", "float" };

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @Resource
    protected HttpServletRequest request;

    // 定义切点 @Pointcut
    // 在注解的位置切入代码
    @Pointcut("@annotation( com.youming.youche.system.aspect.SysOperatorLog)")
    public void logPoinCut() {
    }


    @Around("logPoinCut()")
    public void saveSysLog(ProceedingJoinPoint point)  throws Throwable {

        // 业务需求数据
        Object result = null;
        Signature sig = point.getSignature(); // 方法名称
        MethodSignature msig = null;
        msig = (MethodSignature) sig;
        Object target = point.getTarget();
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        SysOperatorLog annotation = currentMethod.getAnnotation(SysOperatorLog.class);

        String classType = point.getTarget().getClass().getName();
        Class<?> clazz = Class.forName(classType);
        String clazzName = clazz.getName();
        String methodName = point.getSignature().getName();
        String[] paramNames = getFieldsName(this.getClass(), clazzName, methodName); // 返回参数名称数组

        List<Map<String, Object>> logContent = writeLogInfo(paramNames, point); // 返回参数跟值

        // 执行业务前
//        Object objectOld = afterOperation(point, annotation, logContent);
        // 执行修改前的bean
// 执行业务
        result = point.proceed();
        try {
            // 执行业务之后
//            record(point, annotation, currentMethod, objectOld);
        } catch (Exception e) {
            logger.error("日志记录出错!", e);
        }


// 获取操作
//        SysOperatorLog myLog = method.getAnnotation(SysOperatorLog.class);
        SysOperatorLog myLog = annotation;
        SysOperLog sysOperLog = new SysOperLog();
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        String header = request.getHeader("User-Agent");
        sysOperLog.setOpName(accessToken);
        sysOperLog.setBusiCode(myLog.code().getCode());
        sysOperLog.setBusiName(myLog.code().getName());
        sysOperLog.setBusiId(myLog.busId());
        sysOperLog.setOperType(myLog.type().getCode());
        sysOperLog.setOperTypeName(myLog.type().getName());
        sysOperLog.setOperComment(myLog.comment());
        sysOperLog.setChannelType(header);
        sysOperLogService.save(sysOperLog,accessToken);

    }

   /* public void record(JoinPoint joinPoint, BussinessLog annotation, Method currentMethod, Object objectOld)
            throws Throwable {

        int type = annotation.type();
        String bussinessName = annotation.value();
        String key = annotation.key();
        String objName = annotation.name();
        String sql = annotation.sql();
        Class obj = annotation.obj();
        Object newInstance = null;
        if (!obj.equals(Object.class)) {
            newInstance = obj.newInstance();
        }
        String classType = joinPoint.getTarget().getClass().getName();
        Class<?> clazz = Class.forName(classType);
        String clazzName = clazz.getName();
        String methodName = joinPoint.getSignature().getName(); // 简单方法名称

        String[] paramNames = getFieldsName(this.getClass(), clazzName, methodName); // 返回参数名称数组

        Map<String, Object> map = new HashMap<>(); // 获取需要的数据参数
        List<Map<String, Object>> logContent = writeLogInfo(paramNames, joinPoint); // 返回参数跟值
        for (Map<String, Object> content : logContent) {
            if (content.containsKey(key)) {
                map.put("id", content.get(key));
            }
            if (content.containsKey(objName)) {
                map.put("name", content.get(objName));
            }

        }
        String msg = ""; // 业务操作记录
        String name = "";
        Long id = null;
        String uuId = null;
        String operation = ""; // 记录操作

        if (StringUtils.isNotBlank((String) map.get("name"))) { // 如果有name字段的名称
            name = (String) map.get("name");
        }

        if (null != map.get("id")) {
            id = (Long) map.get("id");
        }
        String operationNameString = null;

        Object objectNew = null;
        boolean existUpdate = true;
        if (id != null && id != 0L && StringUtils.isNotBlank(sql)) {
            if (obj.equals(Object.class)) {
                operationNameString = jdbcTemplate.queryForObject((sql), String.class, id);
            } else if ((2 == type) && !obj.equals(Object.class)) { // bean修改前后属性
                objectNew = jdbcTemplate.queryForObject((sql), new BeanPropertyRowMapper<>(obj), id);
                if (ToolUtil.isNotEmpty(objectOld) && ToolUtil.isNotEmpty(objectNew)) {

                    CompareObj<Object> compareUtil = new CompareObj<Object>();
                    String contrastObj = compareUtil.contrastObj(objectOld, objectNew);
                    msg = "修改：" + bussinessName + "内容：" + contrastObj;
                    existUpdate = false;
                }
            }
        }

        Long primaryKey = 0L;// 主键值
        if (1 == type) { // 添加
            operation = "添加";
            if (StringUtils.isNotBlank(operationNameString)) {
                msg = operation + "：" + bussinessName + "内容：" + operationNameString;
            } else {
                if (StringUtils.isNotBlank(name)) {
                    msg = operation + "：" + bussinessName + "内容：" + name;
                }else {
                    msg = operation + "：" + bussinessName;
                }
            }
        } else if (2 == type) {// 修改
            operation = "修改";
            if (StringUtils.isNotBlank(operationNameString)) {
                msg = operation + "：" + bussinessName + "内容：" + operationNameString;
            } else if (existUpdate) {
                if (StringUtils.isNotBlank(name)) {
                    msg = operation + "：" + bussinessName + "内容：" + name;
                }else {
                    msg = operation + "：" + bussinessName;
                }
            }

        } else if (3 == type) {// 删除
            operation = "删除";
            if (StringUtils.isNotBlank(operationNameString)) {
                msg = operation + "：" + bussinessName + "内容：" + operationNameString;
            } else {
                msg = operation + "：" + bussinessName;
            }
        }


        // 自定义线程插入数据记录表

    }


    public Object afterOperation(ProceedingJoinPoint point, SysOperatorLog annotation,
                                 List<Map<String, Object>> logContent) throws Throwable {
        String key = annotation.key();
        Class obj = annotation.obj();
        String sql = annotation.sql();
        Map<String, Object> map = new HashMap<>(); // 获取需要的数据参数
        for (Map<String, Object> content : logContent) {
            if (content.containsKey(key)) {
                map.put("id", content.get(key));
            }
        }
        Long id = null;
        Object queryForObject = null;
        if (null != map.get("id")) {
            id = (Long) map.get("id");
        }
        if (2 == annotation.type()) { // 记录修改前
            if (id != null && id != 0L && StringUtils.isNotBlank(sql) && !obj.equals(Object.class)) {
                queryForObject = jdbcTemplate.queryForObject((sql), new BeanPropertyRowMapper<>(obj), id);
            }
        }
        return queryForObject;

    }*/



    /**
     * 得到方法参数的名称
     *
     * @param cls
     * @param clazzName
     * @param methodName
     * @return
     * @throws NotFoundException
     */
    private static String[] getFieldsName(Class cls, String clazzName, String methodName) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        ClassClassPath classPath = new ClassClassPath(cls);
        pool.insertClassPath(classPath);

        CtClass cc = pool.get(clazzName);
        CtMethod cm = cc.getDeclaredMethod(methodName);
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (attr == null) {

        }
        String[] paramNames = new String[cm.getParameterTypes().length];
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        for (int i = 0; i < paramNames.length; i++) {
            paramNames[i] = attr.variableName(i + pos); // paramNames即参数名集合
        }
        return paramNames;
    }


    /**
     * 获取参数及值
     *
     */
    private static List<Map<String, Object>> writeLogInfo(String[] paramNames, JoinPoint joinPoint) throws IllegalArgumentException, IllegalAccessException {
        Object[] args = joinPoint.getArgs();
        boolean clazzFlag = true;
        List<String> asList = Arrays.asList(types); // 基本数据类型
        List<Map<String, Object>> list = new ArrayList<>(); // 保存所有参数
        Map<String, Object> map = new HashMap<>(); // 基本数据参数保存
        Map<String, Object> fieldsValue = new HashMap<>(); // 实体类型参数保存
        for (int k = 0; k < args.length; k++) {
            Object arg = args[k];
//			paramNames[k] // 参数名称
            // 获取对象类型
            String typeName = arg.getClass().getTypeName();
            if (asList.contains(typeName)) {
                // arg 参数值
                map.put(paramNames[k] + "", arg);
            }
            if (clazzFlag) {
                fieldsValue = getFieldsValue(arg);
                if (MapUtil.isNotEmpty(fieldsValue)) {
                    list.add(fieldsValue);// 保存实体对象参数
                }
            }
        }
        if (MapUtil.isNotEmpty(map)) {
            list.add(map);
        }
        return list;
    }


    /**
     * 得到参数的值
     *
     * @param obj
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static Map<String, Object> getFieldsValue(Object obj) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = obj.getClass().getDeclaredFields();
        String typeName = obj.getClass().getTypeName();
        Map<String, Object> map = new HashMap<>();
        for (String t : types) {
            if (t.equals(typeName)){
                return null;
            }
        }
        for (Field f : fields) {
            f.setAccessible(true);
            for (String str : types) {
                if (f.getType().getName().equals(str)) {
                    map.put(f.getName() + "", f.get(obj)); // 保存参数值
                }
            }

        }
        return map;
    }



}
