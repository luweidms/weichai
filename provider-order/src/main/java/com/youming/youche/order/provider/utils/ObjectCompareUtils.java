package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.order.OrderGoods;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderScheduler;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ObjectCompareUtils {


    /**
     * 判断两个新旧对象指定字段是否发生改变
     *
     * @param oldObj
     * @param newObj
     * @param file   需要比较的字段数组
     * @return
     */
    @SuppressWarnings("unused")
    public static boolean isModifyObj(Object oldObj, Object newObj, String[] file) {
        try {
            //oldObj.getClass();
            Field[] objOld = oldObj.getClass().getDeclaredFields();
            Field[] objNew = newObj.getClass().getDeclaredFields();
            if (file != null) {
                List<String> listString = Arrays.asList(file);
                for (int i = 0; i < objOld.length; i++) {
                    Field objFile = objOld[i];
                    String oldFieldName = objFile.getName();
                    //判断字段类型
                    Type type = objFile.getGenericType();
                    //获取字段名
                    String oldfieldNames = Character.toUpperCase(oldFieldName.charAt(0)) + oldFieldName.substring(1, oldFieldName.length());
                    if (oldfieldNames.equals("SerialVersionUID")) {
                        continue;
                    }
                    //获取私有字段的get方法，得到私有属性的值
                    Method oldGetMethod = null;
                    Method newGetMethod = null;
                    try {
                        oldGetMethod = oldObj.getClass().getMethod("get" + oldfieldNames);
                        newGetMethod = newObj.getClass().getMethod("get" + oldfieldNames);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    //设置获取私有属性
                    objFile.setAccessible(true);
                    if (listString.contains(oldFieldName)) {
                        if (type.toString().equals("class java.util.Date")) {
                            Date objOldValue = (Date) oldGetMethod.invoke(oldObj);
                            Date objNewValue = (Date) newGetMethod.invoke(newObj);
                            if ((objOldValue != null && objNewValue != null && objOldValue.getTime() != objNewValue.getTime())
                                    || (objOldValue == null && objNewValue != null)
                                    || (objOldValue != null && objNewValue == null)) {
                                return false;
                            }
                        } else if (type.toString().equals("class java.lang.String")) {
                            String objOldValue = (String) oldGetMethod.invoke(oldObj);
                            String objNewValue = (String) newGetMethod.invoke(newObj);
                            if ((objOldValue != null && objNewValue != null && !objOldValue.equals(objNewValue)
                                    && !(("".equals(objOldValue) && "null".equals(objNewValue)) || ("".equals(objNewValue) && "null".equals(objOldValue)))
                            )
                                    || (objOldValue == null && objNewValue != null && !"".equals(objNewValue))
                                    || (objOldValue != null && !"".equals(objOldValue) && objNewValue == null)) {
                                return false;
                            }
                        } else if (type.toString().equals("class java.lang.Integer")) {
                            String objOldValue = String.valueOf((Integer) oldGetMethod.invoke(oldObj));
                            String objNewValue = String.valueOf((Integer) newGetMethod.invoke(newObj));
                            if ((objOldValue != null && objNewValue != null
                                    && (!(objOldValue.equals("0") && objNewValue.equals("null"))
                                    && !(objNewValue.equals("0") && objOldValue.equals("null"))
                                    && !objOldValue.equals(objNewValue)
                            ))
                                    || (objOldValue == null && objNewValue != null && !objNewValue.equals("0"))
                                    || (objOldValue != null && !objOldValue.equals("0") && objNewValue == null && !objOldValue.equals("0"))) {
                                return false;
                            }
                        } else if (type.toString().equals("class java.lang.Float")) {
                            String objOldValue = String.valueOf((Float) oldGetMethod.invoke(oldObj));
                            String objNewValue = String.valueOf((Float) newGetMethod.invoke(newObj));
                            if ((objOldValue != null && objNewValue != null
                                    && (!(objOldValue.equals("0.0") && objNewValue.equals("null"))
                                    && !(objNewValue.equals("0.0") && objOldValue.equals("null"))
                                    && !objOldValue.equals(objNewValue)
                            ))
                                    || (objOldValue == null && objNewValue != null && !objNewValue.equals("0.0"))
                                    || (objOldValue != null && objNewValue == null && !objOldValue.equals("0.0"))) {
                                return false;
                            }
                        } else if (type.toString().equals("float")) {
                            Float objOldValue = (Float) oldGetMethod.invoke(oldObj);
                            Float objNewValue = (Float) newGetMethod.invoke(newObj);
                            if ((objOldValue != null && objNewValue != null
                                    && (!(objOldValue.equals("0.0") && objNewValue.equals("null"))
                                    && !(objNewValue.equals("0.0") && objOldValue.equals("null"))
                                    && !objOldValue.equals(objNewValue)
                            ))
                                    || (objOldValue == null && objNewValue != null && !objNewValue.equals("0.0"))
                                    || (objOldValue != null && objNewValue == null && !objOldValue.equals("0.0"))) {
                                return false;
                            }
                        } else if (type.toString().equals("int")) {
                            Integer objOldValue = (Integer) oldGetMethod.invoke(oldObj);
                            Integer objNewValue = (Integer) newGetMethod.invoke(newObj);
                            if ((objOldValue != null && objNewValue != null
                                    && (!(objOldValue.equals("0") && objNewValue.equals("null"))
                                    && !(objNewValue.equals("0") && objOldValue.equals("null"))
                                    && !objOldValue.equals(objNewValue)
                            ))
                                    || (objOldValue == null && objNewValue != null && !objNewValue.equals("0"))
                                    || (objOldValue != null && objNewValue == null && !objOldValue.equals("0"))) {
                                return false;
                            }
                        } else if (type.toString().equals("long")) {
                            String objOldValue = String.valueOf((Long) oldGetMethod.invoke(oldObj));
                            String objNewValue = String.valueOf((Long) newGetMethod.invoke(newObj));
                            if ((objOldValue != null && objNewValue != null
                                    && (!(objOldValue.equals("0") && objNewValue.equals("null"))
                                    && !(objNewValue.equals("0") && objOldValue.equals("null"))
                                    && !objOldValue.equals(objNewValue)
                            ))
                                    || (objOldValue == null && objNewValue != null && !objNewValue.equals("0"))
                                    || (objOldValue != null && objNewValue == null && !objOldValue.equals("0"))) {
                                return false;
                            }
                        } else if (type.toString().equals("class java.lang.Long")) {
                            String objOldValue = String.valueOf((Long) oldGetMethod.invoke(oldObj));
                            String objNewValue = String.valueOf((Long) newGetMethod.invoke(newObj));
                            if ((objOldValue != null && objNewValue != null
                                    && (!(objOldValue.equals("0") && objNewValue.equals("null"))
                                    && !(objNewValue.equals("0") && objOldValue.equals("null"))
                                    && !objOldValue.equals(objNewValue)
                            ))
                                    || (objOldValue == null && objNewValue != null && !objNewValue.equals("0"))
                                    || (objOldValue != null && objNewValue == null && !objOldValue.equals("0"))) {
                                return false;
                            }
                        } else if (type.toString().equals("class java.lang.Double")) {
                            String objOldValue = String.valueOf((Double) oldGetMethod.invoke(oldObj));
                            String objNewValue = String.valueOf((Double) newGetMethod.invoke(newObj));
                            if ((objOldValue != null && objNewValue != null
                                    && (!(objOldValue.equals("0.0") && objNewValue.equals("null"))
                                    && !(objNewValue.equals("0.0") && objOldValue.equals("null"))
                                    && !objOldValue.equals(objNewValue)
                            ))
                                    || (objOldValue == null && objNewValue != null && !objNewValue.equals("0.0"))
                                    || (objOldValue != null && objNewValue == null && !objOldValue.equals("0.0"))) {
                                return false;
                            }
                        } else if (type.toString().equals("double")) {
                            String objOldValue = String.valueOf((Double) oldGetMethod.invoke(oldObj));
                            String objNewValue = String.valueOf((Double) newGetMethod.invoke(newObj));
                            if ((objOldValue != null && objNewValue != null
                                    && (!(objOldValue.equals("0.0") && objNewValue.equals("null"))
                                    && !(objNewValue.equals("0.0") && objOldValue.equals("null"))
                                    && !objOldValue.equals(objNewValue)
                            ))
                                    || (objOldValue == null && objNewValue != null && !objNewValue.equals("0.0"))
                                    || (objOldValue != null && objNewValue == null && !objOldValue.equals("0.0"))) {
                                return false;
                            }
                        }
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("数据对比异常");
        }
        return true;
    }


    public void isNotBlankOrderInfo(OrderInfo orderInfo) {
        if (orderInfo != null) {
            if (orderInfo.getOrderType() == null) {
                throw new BusinessException("订单类型不能为空");
            }
            if (orderInfo.getSourceProvince() == null) {
                throw new BusinessException("起始地省份不能为空");
            }
            if (orderInfo.getSourceRegion() == null) {
                throw new BusinessException("起始地城市不能为空");
            }
            if (orderInfo.getDesProvince() == null) {
                throw new BusinessException("目的地省份不能为空");
            }
            if (orderInfo.getDesRegion() == null) {
                throw new BusinessException("目的地城市不能为空");
            }
        } else {
            throw new BusinessException("订单不能为空");
        }
    }

    public void isNotBlankOrderGoods(OrderGoods orderGoods, Integer orderType) {
        if (orderGoods != null) {
            if (orderGoods.getSource() == null) {
                throw new BusinessException("起始地的省市区不能为空");
            }
            if (orderGoods.getDes() == null) {
                throw new BusinessException("目的地省市区不能为空");
            }
            if (OrderConsts.OrderType.FIXED_LINE == orderType) {
                //固定线路
                if (orderGoods.getCustomUserId() == null) {
                    throw new BusinessException("客户id不能为空");
                }
            }
            if (orderGoods.getNand() == null) {
                throw new BusinessException("起始地经纬度不能为空");
            }
            if (orderGoods.getEand() == null) {
                throw new BusinessException("起始地经纬度不能为空");
            }
            if (orderGoods.getNandDes() == null) {
                throw new BusinessException("目的地经纬度不能为空");
            }
            if (orderGoods.getEandDes() == null) {
                throw new BusinessException("目的地经纬度不能为空");
            }
            if (orderGoods.getLocalUser() == null) {
                throw new BusinessException("跟单人员id不能为空");
            }
            if (orderGoods.getLocalPhone() == null) {
                throw new BusinessException("跟单人员电话不能为空");
            }
            if (orderGoods.getLocalUserName() == null) {
                throw new BusinessException("跟单人员名称不能为空");
            }
            if (orderGoods.getCompanyName() == null) {
                throw new BusinessException("客户名称不能为空");
            }
        } else {
            throw new BusinessException("订单货品不能为空");
        }
    }

    public void isNotBlankOrderScheduler(OrderScheduler orderScheduler, Integer appointWay) {
        if (orderScheduler != null) {
            if (orderScheduler.getAppointWay() == null) {
                throw new BusinessException("调度方式不能为空");
            }
            if (orderScheduler.getDependTime() == null) {
                throw new BusinessException("靠台时间不能为空");
            }
            if (com.youming.youche.record.common.OrderConsts.AppointWay.APPOINT_CAR == appointWay) {
                if (orderScheduler.getPlateNumber() == null) {
                    throw new BusinessException("车牌号码不能为空");
                }
                if (orderScheduler.getCarDriverPhone() == null) {
                    throw new BusinessException("司机手机不能为空");
                }
                if (orderScheduler.getCarDriverId() == null) {
                    throw new BusinessException("司机id不能为空");
                }
                if (orderScheduler.getCarDriverMan() == null) {
                    throw new BusinessException("司机名称不能为空");
                }
                if (orderScheduler.getVehicleClass() == null) {
                    throw new BusinessException("车辆类型不能为空");
                }

            } else if (OrderConsts.AppointWay.APPOINT_LOCAL == appointWay) {
                if (orderScheduler.getDispatcherName() == null) {
                    throw new BusinessException("调度员名称不能为空");
                }
                if (orderScheduler.getDispatcherId() == null) {
                    throw new BusinessException("调度员id不能为空");
                }
                if (orderScheduler.getDispatcherBill() == null) {
                    throw new BusinessException("调度员手机不能为空");
                }
            }
        } else {
            throw new BusinessException("订单调度不能为空");
        }
    }

    /**
     * 校验 对应的字段不能为空
     *
     * @param obj
     * @param map
     * @return
     * @throws Exception
     */
    public static boolean isNotBlankNamesMap(Object obj, Map<String, String> map) throws Exception {
        for (String key : map.keySet()) {
            isNotBlankName(obj, key, map.get(key));
        }
        return true;
    }

    /**
     * 判断obj中name字段是否为空
     *
     * @param obj
     * @param name   必须是obj中的字段名，否则跳过不判断
     * @param notice 提示语
     * @return
     * @throws Exception
     */
    public static boolean isNotBlankName(Object obj, String name, String notice) throws Exception {
        if (StringUtils.isNotBlank(name)) {
            Field[] fileds = obj.getClass().getDeclaredFields();
            for (int i = 0; i < fileds.length; i++) {
                if (name.equals(fileds[i].getName())) {
                    Field objFile = fileds[i];
                    String fieldName = objFile.getName();
                    //判断字段类型
                    Type type = objFile.getGenericType();
                    //获取字段名
                    String fieldNames = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1, fieldName.length());
                    //获取私有字段的get方法，得到私有属性的值
                    Method getMethod = null;
                    try {
                        getMethod = obj.getClass().getMethod("get" + fieldNames);
                    } catch (Exception e) {
                        fieldNames = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1, fieldName.length());
                        getMethod = obj.getClass().getMethod("get" + fieldNames);
                    }
                    //设置获取私有属性
                    objFile.setAccessible(true);
                    if (type.toString().equals("class java.lang.String")) {
                        String objOldValue = (String) getMethod.invoke(obj);
                        if (StringUtils.isBlank(objOldValue)) {
                            if (StringUtils.isBlank(notice)) {
                                throw new BusinessException((fieldName + "不能为空"));
                            } else {
                                throw new BusinessException((notice + "不能为空"));
                            }
                        }
                    }
                    if (type.toString().equals("class java.lang.Integer")) {
                        Integer objOldValue = (Integer) getMethod.invoke(obj);
                        if (null == objOldValue || objOldValue < 0) {
                            if (StringUtils.isBlank(notice)) {
                                throw new BusinessException((fieldName + "不能为空"));
                            } else {
                                throw new BusinessException((notice + "不能为空"));
                            }
                        }
                    }
                    if (type.toString().equals("int")) {
                        Integer objOldValue = (Integer) getMethod.invoke(obj);
                        if (null == objOldValue || objOldValue < 0) {
                            if (StringUtils.isBlank(notice)) {
                                throw new BusinessException((fieldName + "不能为空"));
                            } else {
                                throw new BusinessException((notice + "不能为空"));
                            }
                        }
                    }
                    if (type.toString().equals("long")) {
                        Long objOldValue = (Long) getMethod.invoke(obj);
                        if (null == objOldValue || objOldValue < 0) {
                            if (StringUtils.isBlank(notice)) {
                                throw new BusinessException((fieldName + "不能为空"));
                            } else {
                                throw new BusinessException((notice + "不能为空"));
                            }
                        }
                    }
                    if (type.toString().equals("class java.lang.Long")) {
                        Long objOldValue = (Long) getMethod.invoke(obj);
                        if (null == objOldValue || objOldValue < 0) {
                            if (StringUtils.isBlank(notice)) {
                                throw new BusinessException((fieldName + "不能为空"));
                            } else {
                                throw new BusinessException((notice + "不能为空"));
                            }
                        }
                    }
                    if (type.toString().equals("class java.util.Date")) {
                        Date objOldValue = (Date) getMethod.invoke(obj);
                        if (null == objOldValue) {
                            if (StringUtils.isBlank(notice)) {
                                throw new BusinessException((fieldName + "不能为空"));
                            } else {
                                throw new BusinessException((notice + "不能为空"));
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * 判断两个新旧对象指定字段是否发生改变
     * @param oldObj
     * @param newObj
     * @param file 不比较传入的参数 属性名
     * @return
     */
    public static  List<Map<String, Object>> compareFieldsNotIn(Object oldObj,Object newObj,String[] file){
        try {
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            String classNameOld = oldObj.getClass().getSimpleName();
            Field[] objOld = oldObj.getClass().getDeclaredFields();
            //Field[] objNew = newObj.getClass().getDeclaredFields();
            for(int i=0;i<objOld.length;i++){
                Field  objFile=objOld[i];
                String oldFieldName=objFile.getName();
                //判断字段类型
                Type type = objFile.getGenericType();
                //获取字段名
                String oldfieldNames = Character.toUpperCase(oldFieldName.charAt(0))+oldFieldName.substring(1, oldFieldName.length());
                //获取私有字段的get方法，得到私有属性的值
                Method oldGetMethod = null;
                Method newGetMethod = null;
                try {
                    oldGetMethod = oldObj.getClass().getMethod("get"+oldfieldNames);
                    newGetMethod = newObj.getClass().getMethod("get"+oldfieldNames);
                }catch (Exception e) {
                    continue;
                }
                //设置获取私有属性
                objFile.setAccessible(true);
                List<String> isNotCompList = file == null ? new ArrayList<>() : Arrays.asList(file);
                if (!isNotCompList.contains(oldFieldName)) {
                    if (type.toString().equals("class java.util.Date")) {
                        Date objOldValue =(Date)oldGetMethod.invoke(oldObj);
                        Date objNewValue =(Date)newGetMethod.invoke(newObj);
                        if((objOldValue!=null && objNewValue!=null && objOldValue.getTime() != objNewValue.getTime())
                                || (objOldValue==null && objNewValue!=null)
                                || (objOldValue!=null && objNewValue==null)){
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("className", classNameOld);
                            map.put("fieldName", oldFieldName);
                            map.put("newValue", objNewValue);
                            map.put("oldValue", objOldValue);
                            list.add(map);
                        }
                    }else if (type.toString().equals("class java.lang.String")) {
                        String objOldValue =(String)oldGetMethod.invoke(oldObj);
                        String objNewValue =(String)newGetMethod.invoke(newObj);
                        if((objOldValue!=null && objNewValue!=null &&  !objOldValue.equals(objNewValue)
                                && !(("".equals(objOldValue) && "null".equals(objNewValue)) || ("".equals(objNewValue) && "null".equals(objOldValue)))
                        )
                                || (objOldValue==null && objNewValue!=null && !"".equals(objNewValue))
                                || (objOldValue!=null && !"".equals(objOldValue) && objNewValue==null)){
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("className", classNameOld);
                            map.put("fieldName", oldFieldName);
                            map.put("newValue", objNewValue);
                            map.put("oldValue", objOldValue);
                            list.add(map);
                        }
                    }else if (type.toString().equals("class java.lang.Integer")) {
                        String objOldValue =String.valueOf((Integer)oldGetMethod.invoke(oldObj));
                        String objNewValue =String.valueOf((Integer)newGetMethod.invoke(newObj));
                        if((objOldValue!=null &&  objNewValue!=null
                                && (!(objOldValue.equals("0") && objNewValue.equals("null"))
                                && !(objNewValue.equals("0") && objOldValue.equals("null"))
                                && !objOldValue.equals(objNewValue)
                        ))
                                || (objOldValue==null && objNewValue!=null && !objNewValue.equals("0"))
                                || (objOldValue!=null && !objOldValue.equals("0") && objNewValue==null && !objOldValue.equals("0") )){
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("className", classNameOld);
                            map.put("fieldName", oldFieldName);
                            map.put("newValue", objNewValue);
                            map.put("oldValue", objOldValue);
                            list.add(map);
                        }
                    }else if (type.toString().equals("class java.lang.Float")) {
                        String objOldValue =String.valueOf((Float)oldGetMethod.invoke(oldObj));
                        String objNewValue =String.valueOf((Float)newGetMethod.invoke(newObj));
                        if((objOldValue!=null &&  objNewValue!=null
                                && (!(objOldValue.equals("0.0") && objNewValue.equals("null"))
                                && !(objNewValue.equals("0.0") && objOldValue.equals("null"))
                                && !objOldValue.equals(objNewValue)
                        ))
                                || (objOldValue==null && objNewValue!=null  && !objNewValue.equals("0.0"))
                                || (objOldValue!=null  && objNewValue==null && !objOldValue.equals("0.0") )){
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("className", classNameOld);
                            map.put("fieldName", oldFieldName);
                            map.put("newValue", objNewValue);
                            map.put("oldValue", objOldValue);
                            list.add(map);
                        }
                    }else if (type.toString().equals("float")) {
                        Float objOldValue =(Float)oldGetMethod.invoke(oldObj);
                        Float objNewValue =(Float)newGetMethod.invoke(newObj);
                        if((objOldValue!=null &&  objNewValue!=null
                                && (!(objOldValue.equals("0.0") && objNewValue.equals("null"))
                                && !(objNewValue.equals("0.0") && objOldValue.equals("null"))
                                && !objOldValue.equals(objNewValue)
                        ))
                                || (objOldValue==null && objNewValue!=null && !objNewValue.equals("0.0"))
                                || (objOldValue!=null  && objNewValue==null && !objOldValue.equals("0.0") )){
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("className", classNameOld);
                            map.put("fieldName", oldFieldName);
                            map.put("newValue", objNewValue);
                            map.put("oldValue", objOldValue);
                            list.add(map);
                        }
                    }else if (type.toString().equals("int")) {
                        Integer objOldValue =(Integer)oldGetMethod.invoke(oldObj);
                        Integer objNewValue =(Integer)newGetMethod.invoke(newObj);
                        if((objOldValue!=null &&  objNewValue!=null
                                && (!(objOldValue.equals("0") && objNewValue.equals("null"))
                                && !(objNewValue.equals("0") && objOldValue.equals("null"))
                                && !objOldValue.equals(objNewValue)
                        ))
                                || (objOldValue==null && objNewValue!=null  && !objNewValue.equals("0"))
                                || (objOldValue!=null  && objNewValue==null  && !objOldValue.equals("0") )){
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("className", classNameOld);
                            map.put("fieldName", oldFieldName);
                            map.put("newValue", objNewValue);
                            map.put("oldValue", objOldValue);
                            list.add(map);
                        }
                    }else if (type.toString().equals("long")) {
                        String objOldValue =String.valueOf((Long)oldGetMethod.invoke(oldObj));
                        String objNewValue =String.valueOf((Long)newGetMethod.invoke(newObj));
                        if((objOldValue!=null &&  objNewValue!=null
                                && (!(objOldValue.equals("0") && objNewValue.equals("null"))
                                && !(objNewValue.equals("0") && objOldValue.equals("null"))
                                && !objOldValue.equals(objNewValue)
                        ))
                                || (objOldValue==null && objNewValue!=null  && !objNewValue.equals("0"))
                                || (objOldValue!=null && objNewValue==null  && !objOldValue.equals("0") )){
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("className", classNameOld);
                            map.put("fieldName", oldFieldName);
                            map.put("newValue", objNewValue);
                            map.put("oldValue", objOldValue);
                            list.add(map);
                        }
                    }else if (type.toString().equals("class java.lang.Long")) {
                        String objOldValue =String.valueOf((Long)oldGetMethod.invoke(oldObj));
                        String objNewValue =String.valueOf((Long)newGetMethod.invoke(newObj));
                        if((objOldValue!=null &&  objNewValue!=null
                                && (!(objOldValue.equals("0") && objNewValue.equals("null"))
                                && !(objNewValue.equals("0") && objOldValue.equals("null"))
                                && !objOldValue.equals(objNewValue)
                        ))
                                || (objOldValue==null && objNewValue!=null  && !objNewValue.equals("0"))
                                || (objOldValue!=null  && objNewValue==null && !objOldValue.equals("0"))){
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("className", classNameOld);
                            map.put("fieldName", oldFieldName);
                            map.put("newValue", objNewValue);
                            map.put("oldValue", objOldValue);
                            list.add(map);
                        }
                    }else if (type.toString().equals("class java.lang.Double")) {
                        String objOldValue =String.valueOf((Double)oldGetMethod.invoke(oldObj));
                        String objNewValue =String.valueOf((Double)newGetMethod.invoke(newObj));
                        if((objOldValue!=null &&  objNewValue!=null
                                && (!(objOldValue.equals("0.0") && objNewValue.equals("null"))
                                && !(objNewValue.equals("0.0") && objOldValue.equals("null"))
                                && !objOldValue.equals(objNewValue)
                        ))
                                || (objOldValue==null && objNewValue!=null && !objNewValue.equals("0.0") )
                                || (objOldValue!=null && objNewValue==null && !objOldValue.equals("0.0") )){
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("className", classNameOld);
                            map.put("fieldName", oldFieldName);
                            map.put("newValue", objNewValue);
                            map.put("oldValue", objOldValue);
                            list.add(map);
                        }
                    }else if (type.toString().equals("double")) {
                        String objOldValue =String.valueOf((Double)oldGetMethod.invoke(oldObj));
                        String objNewValue =String.valueOf((Double)newGetMethod.invoke(newObj));
                        if((objOldValue!=null &&  objNewValue!=null
                                && (!(objOldValue.equals("0.0") && objNewValue.equals("null"))
                                && !(objNewValue.equals("0.0") && objOldValue.equals("null"))
                                && !objOldValue.equals(objNewValue)
                        ))
                                || (objOldValue==null && objNewValue!=null && !objNewValue.equals("0.0") )
                                || (objOldValue!=null && objNewValue==null && !objOldValue.equals("0.0") )){
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("className", classNameOld);
                            map.put("fieldName", oldFieldName);
                            map.put("newValue", objNewValue);
                            map.put("oldValue", objOldValue);
                            list.add(map);
                        }
                    }

                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
