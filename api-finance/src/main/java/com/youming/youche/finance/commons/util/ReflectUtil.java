package com.youming.youche.finance.commons.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @version:
 * @Title: ReflectUtil
 * @Package: org.myclouds.common.util
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2021/10/14 15:00
 * @company:
 */
public class ReflectUtil {

	/**
	 * 判断自定义对象是否为空
	 * @param obj
	 * @return true：为空 false：不为空
	 */
	public static boolean isObjectNull(Object obj) {
		if (obj != null) {
			Class<?> objClass = obj.getClass();
			Method[] declaredMethods = objClass.getDeclaredMethods();
			if (declaredMethods.length > 0) {
				int methodCount = 0; // get 方法数量
				int nullValueCount = 0; // 结果为空
				for (Method declaredMethod : declaredMethods) {
					String name = declaredMethod.getName();
					if (name.startsWith("get") || name.startsWith("is")) {
						methodCount += 1;
						try {
							Object invoke = declaredMethod.invoke(obj);
							if (invoke == null || invoke.equals("")) {
								nullValueCount += 1;
							}
						}
						catch (IllegalAccessException | InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
				return methodCount == nullValueCount;
			}
		}
		return true;
	}

}
