package com.youming.youche.order.aspect;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.order.annotation.SysStaticDataInfoDict;
import com.youming.youche.order.annotation.Translatable;
import com.youming.youche.order.util.ObjConvertUtils;
import com.youming.youche.order.util.ReadisUtilS;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
@Slf4j
public class SysStaticDataInfoAspect {
    @Resource
    private ReadisUtilS readisUtil;
    //翻译后拼接的内容
    private static String DICT_TEXT_SUFFIX = "Name";

    // 定义切点Pointcut 拦截所有对服务器的请求
    @Pointcut("@annotation( com.youming.youche.order.annotation.Dict)")
    public void excudeService() {
    }

    /**
     * 这是触发 excudeService 的时候会执行的
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("excudeService()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        //这是定义开始事件
        long time1 = System.currentTimeMillis();
        //这是方法并获取返回结果
        Object result = pjp.proceed();
        //这是获取到 结束时间
        long time2 = System.currentTimeMillis();
        log.debug("获取JSON数据 耗时：" + (time2 - time1) + "ms");
        //解析开始时间
        long start = System.currentTimeMillis();
        //开始解析（翻译字段内部的值凡是打了 @Dict 这玩意的都会被翻译）
        this.parseDictText(result);
        //解析结束时间
        long end = System.currentTimeMillis();
        log.debug("解析注入JSON数据  耗时" + (end - start) + "ms");
        return result;
    }

    /**
     * 本方法针对返回对象为Result 的IPage的分页列表数据进行动态字典注入
     * 字典注入实现 通过对实体类添加注解@dict 来标识需要的字典内容,字典分为单字典code即可 ，table字典 code table text配合使用与原来jeecg的用法相同
     * 示例为SysUser   字段为sex 添加了注解@Dict(dicCode = "sex") 会在字典服务立马查出来对应的text 然后在请求list的时候将这个字典text，已字段名称加_dictText形式返回到前端
     * 例输入当前返回值的就会多出一个sex_dictText字段
     * {
     * sex:1,
     * sex_dictText:"男"
     * }
     * 前端直接取值sext_dictText在table里面无需再进行前端的字典转换了
     * customRender:function (text) {
     * if(text==1){
     * return "男";
     * }else if(text==2){
     * return "女";
     * }else{
     * return text;
     * }
     * }
     * 目前vue是这么进行字典渲染到table上的多了就很麻烦了 这个直接在服务端渲染完成前端可以直接用
     *
     * @param result
     */
    private void parseDictText(Object result) throws InstantiationException {
        if (result instanceof ResponseResult) {
            //连接controller返回到前端的参数
            ResponseResult pageUtils = (ResponseResult) result;
            //循环查找出来的数据
            Object utilsData = pageUtils.getData();
            if (utilsData instanceof Page) {
                List<JSONObject> items = new ArrayList<>();//如果是个数组的话走这个
                Page data = (Page) utilsData;
                List records = data.getRecords();
                for (Object record : records) {
                    ObjectMapper mapper = new ObjectMapper();
                    String json = "{}";
                    try {
                        //解决@JsonFormat注解解析不了的问题详见SysAnnouncement类的@JsonFormat
                        json = mapper.writeValueAsString(record);
                    } catch (JsonProcessingException e) {
                        log.error("json解析失败" + e.getMessage(), e);
                    }
                    JSONObject item = JSONObject.parseObject(json);

                    //update-begin--Author:scott -- Date:20190603 ----for：解决继承实体字段无法翻译问题------
                    //for (Field field : record.getClass().getDeclaredFields()) {
                    for (Field field : ObjConvertUtils.getAllFields(record)) {
                        //update-end--Author:scott  -- Date:20190603 ----for：解决继承实体字段无法翻译问题------
                        if (field.getAnnotation(SysStaticDataInfoDict.class) != null) {
                            String code = field.getAnnotation(SysStaticDataInfoDict.class).dictDataSource();
                            String text = field.getAnnotation(SysStaticDataInfoDict.class).dictText();
                            //获取当前带翻译的值
                            String key = String.valueOf(item.get(field.getName()));
                            //翻译字典值对应的txt
                            String textValue = translateDictValue(code, key);
                            //  CommonConstant.DICT_TEXT_SUFFIX的值为，是默认值：
                            // public static final String DICT_TEXT_SUFFIX = "_dictText";
                            log.debug(" 字典Val : " + textValue);
//                        log.debug(" __翻译字典字段__ " + field.getName() + DICT_TEXT_SUFFIX + "： " + textValue);
                            //如果给了文本名
                            if (!StringUtils.isEmpty(text)) {
                                item.put(text, textValue);
                            } else {
                                //走默认策略
                                item.put(field.getName() + DICT_TEXT_SUFFIX, textValue);
                            }

                        }
                        //LocalDateTime类型默认转换string格式化日期
                        if (field.getType().getName().equals("java.time.LocalDateTime") && item.get(field.getName()) != null) {
                            Object o = item.get(field.getName());
                            JSONObject jsonObject = (JSONObject) o;
                            String localDateTime = toLocalDateTimeUtil(jsonObject);
                            item.put(field.getName(), localDateTime);
                        }
                    }
                    items.add(item);
                }
                pageUtils.setData(items);
            } else {
                //不是page的话就是对象 对 对象的 type类型进行翻译 并且加上
                // @Translatable 这个注解 表示要翻译该对象中的 对象属性值 配合 @SysStaticDataInfoDict 注解一起使用
                ObjectMapper mapper = new ObjectMapper();
                String json = "{}";
                try {
                    //解决@JsonFormat注解解析不了的问题详见SysAnnouncement类的@JsonFormat
                    json = mapper.writeValueAsString(utilsData);
                } catch (JsonProcessingException e) {
                    log.error("json解析失败" + e.getMessage(), e);
                }
                JSONObject item = JSONObject.parseObject(json);

                //update-begin--Author:scott -- Date:20190603 ----for：解决继承实体字段无法翻译问题------
                //for (Field field : record.getClass().getDeclaredFields()) {
                for (Field field : ObjConvertUtils.getAllFields(utilsData)) {
                    //update-end--Author:scott  -- Date:20190603 ----for：解决继承实体字段无法翻译问题------
                    if (field.getAnnotation(SysStaticDataInfoDict.class) != null) {
                        String code = field.getAnnotation(SysStaticDataInfoDict.class).dictDataSource();
                        String text = field.getAnnotation(SysStaticDataInfoDict.class).dictText();
                        //获取当前带翻译的值
                        String key = String.valueOf(item.get(field.getName()));
                        //翻译字典值对应的txt
                        String textValue = translateDictValue(code, key);
                        //  CommonConstant.DICT_TEXT_SUFFIX的值为，是默认值：
                        // public static final String DICT_TEXT_SUFFIX = "_dictText";
                        log.debug(" 字典Val : " + textValue);
//                        log.debug(" __翻译字典字段__ " + field.getName() + DICT_TEXT_SUFFIX + "： " + textValue);
                        //如果给了文本名
                        if (!StringUtils.isEmpty(text)) {
                            item.put(text, textValue);
                        } else {
                            //走默认策略
                            item.put(field.getName() + DICT_TEXT_SUFFIX, textValue);
                        }
                    } else if (field.getAnnotation(Translatable.class) != null) {

                        if (field.getType().getName().equals("java.util.List")) {
                            List<JSONObject> items = new ArrayList<>();
                            JSONObject itemData1 = null;
                            List o1 = null;
                            field.setAccessible(true);
                            try {
                                o1 = (List) field.get(utilsData);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }

                            if (o1 != null && o1.size() > 0) {
                                for (Object o : o1) {
                                    ObjectMapper mapperData = new ObjectMapper();
                                    String jsonData = "{}";
                                    try {
                                        //解决@JsonFormat注解解析不了的问题详见SysAnnouncement类的@JsonFormat
                                        jsonData = mapperData.writeValueAsString(o);
                                    } catch (JsonProcessingException e) {
                                        log.error("json解析失败" + e.getMessage(), e);
                                    }
                                    itemData1 = JSONObject.parseObject(jsonData);
                                    for (Field field6 : ObjConvertUtils.getAllFields(o)) {
                                        //update-end--Author:scott  -- Date:20190603 ----for：解决继承实体字段无法翻译问题------
                                        if (field6.getAnnotation(SysStaticDataInfoDict.class) != null) {
                                            String code = field6.getAnnotation(SysStaticDataInfoDict.class).dictDataSource();
                                            String text = field6.getAnnotation(SysStaticDataInfoDict.class).dictText();
                                            //获取当前带翻译的值
                                            String key = String.valueOf(itemData1.get(field6.getName()));
                                            //翻译字典值对应的txt
                                            String textValue = translateDictValue(code, key);
                                            //  CommonConstant.DICT_TEXT_SUFFIX的值为，是默认值：
                                            // public static final String DICT_TEXT_SUFFIX = "_dictText";
                                            log.debug(" 字典Val : " + textValue);
//                        log.debug(" __翻译字典字段__ " + field.getName() + DICT_TEXT_SUFFIX + "： " + textValue);
                                            //如果给了文本名
                                            if (!StringUtils.isEmpty(text)) {
                                                itemData1.put(text, textValue);
                                            } else {
                                                //走默认策略
                                                itemData1.put(field6.getName() + DICT_TEXT_SUFFIX, textValue);
                                            }

                                        }
                                        //LocalDateTime类型默认转换string格式化日期
                                        if (field6.getType().getName().equals("java.time.LocalDateTime") && itemData1.get(field6.getName()) != null) {
                                            Object o12 = itemData1.get(field6.getName());
                                            JSONObject jsonObject = (JSONObject) o12;
                                            String localDateTime = toLocalDateTimeUtil(jsonObject);
                                            itemData1.put(field6.getName(), localDateTime);
                                        }
                                    }
                                    items.add(itemData1);
                                }
                                item.put(field.getName(), items);
                            }

                        } else {
                            JSONObject itemData = null;
                            Object o1 = null;
                            field.setAccessible(true);
                            try {
                                o1 = field.get(utilsData);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            ObjectMapper mapperData = new ObjectMapper();
                            String jsonData = "{}";
                            try {
                                //解决@JsonFormat注解解析不了的问题详见SysAnnouncement类的@JsonFormat
                                jsonData = mapperData.writeValueAsString(o1);
                            } catch (JsonProcessingException e) {
                                log.error("json解析失败" + e.getMessage(), e);
                            }
                            itemData = JSONObject.parseObject(jsonData);
                            for (Field field1 : ObjConvertUtils.getAllFields(o1)) {
                                field1.setAccessible(true);
                                if (field1.getAnnotation(SysStaticDataInfoDict.class) != null) {
                                    String code = field1.getAnnotation(SysStaticDataInfoDict.class).dictDataSource();
                                    String text = field1.getAnnotation(SysStaticDataInfoDict.class).dictText();
                                    //获取当前带翻译的值
                                    String key = String.valueOf(itemData.get(field1.getName()));
                                    //翻译字典值对应的txt
                                    String textValue = translateDictValue(code, key);
                                    //  CommonConstant.DICT_TEXT_SUFFIX的值为，是默认值：
                                    // public static final String DICT_TEXT_SUFFIX = "_dictText";
                                    log.debug(" 字典Val : " + textValue);
//                        log.debug(" __翻译字典字段__ " + field.getName() + DICT_TEXT_SUFFIX + "： " + textValue);
                                    //如果给了文本名
                                    if (!StringUtils.isEmpty(text)) {
                                        itemData.put(text, textValue);
                                    } else {
                                        //走默认策略
                                        itemData.put(field1.getName() + DICT_TEXT_SUFFIX, textValue);
                                    }
                                }
                                //LocalDateTime类型默认转换string格式化日期
                                if (field1.getType().getName().equals("java.time.LocalDateTime") && itemData.get(field1.getName()) != null) {
                                    Object o = itemData.get(field1.getName());
                                    JSONObject jsonObject = (JSONObject) o;
                                    String localDateTime = toLocalDateTimeUtil(jsonObject);
                                    itemData.put(field1.getName(), localDateTime);
                                }
                            }
                            item.put(field.getName(), itemData);
                        }
                    }
                    if (field.getType().getName().equals("java.time.LocalDateTime") && item.get(field.getName()) != null) {
                        Object o = item.get(field.getName());
                        JSONObject jsonObject = (JSONObject) o;
                        String localDateTime = toLocalDateTimeUtil(jsonObject);
                        item.put(field.getName(), localDateTime);
                    }
                }
                pageUtils.setData(item);
            }
        }
    }

    private String toLocalDateTimeUtil(JSONObject jsonObject) {
        //年
        String year = jsonObject.getString("year");
        if (StringUtils.isEmpty(year)) {
            year = "0000";
        }
        //月份
        String monthValue = jsonObject.getString("monthValue");
        if (StringUtils.isEmpty(monthValue)) {
            monthValue = "00";
        } else {
            int parseInt = Integer.parseInt(monthValue);
            if (parseInt <= 9) {
                monthValue = "0" + monthValue;
            }
        }
        //日
        String dayOfMonth = jsonObject.getString("dayOfMonth");
        if (StringUtils.isEmpty(dayOfMonth)) {
            dayOfMonth = "00";
        } else {
            int parseInt = Integer.parseInt(dayOfMonth);
            if (parseInt <= 9) {
                dayOfMonth = "0" + dayOfMonth;
            }
        }
        //分
        String minute = jsonObject.getString("minute");
        if (StringUtils.isEmpty(minute)) {
            minute = "00";
        } else {
            int parseInt = Integer.parseInt(minute);
            if (parseInt <= 9) {
                minute = "0" + minute;
            }
        }
        //时
        String hour = jsonObject.getString("hour");
        if (StringUtils.isEmpty(hour)) {
            hour = "00";
        } else {
            int parseInt = Integer.parseInt(hour);
            if (parseInt <= 9) {
                hour = "0" + hour;
            }
        }
        //秒
        String second = jsonObject.getString("second");
        if (StringUtils.isEmpty(second)) {
            second = "00";
        } else {
            int parseInt = Integer.parseInt(second);
            if (parseInt <= 9) {
                second = "0" + second;
            }
        }
        return year + "-" + monthValue + "-" + dayOfMonth + " " + hour + ":" + minute + ":" + second;
    }

    /**
     * 翻译字典文本
     *
     * @param code
     * @param key
     * @return
     */
    private String translateDictValue(String code, String key) {
        //如果key为空直接返回就好了
        if (ObjConvertUtils.isEmpty(key)) {
            return null;
        }
        StringBuffer textValue = new StringBuffer();
        //分割 key 值
        System.out.println(code + ":::::" + key);
        String[] keys = key.split(",");
        //循环 keys 中的所有值
        for (String k : keys) {
            String tmpValue = null;
            log.debug(" 字典 key : " + k);
            if (k.trim().length() == 0) {
                continue; //跳过循环
            }
            tmpValue = readisUtil.getSysStaticData(code, k.trim()).getCodeName();

            if (tmpValue != null) {
                if (!"".equals(textValue.toString())) {
                    textValue.append(",");
                }
                textValue.append(tmpValue);
            }
        }
        //返回翻译的值
        return textValue.toString();
    }

}
