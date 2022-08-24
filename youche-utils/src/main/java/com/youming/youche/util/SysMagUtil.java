package com.youming.youche.util;

import org.apache.commons.lang.RandomStringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 系统管理工具类
 *
 * @author liuzj
 *
 */
public class SysMagUtil {

    private SysMagUtil() {
    }

    //common层的工具类不能查表
//	public static Object getObjById(Class cla, Object id) {
//		if (id instanceof Integer) {
//			return SysContexts.getEntityManager().get(cla, (Integer) id);
//		}
//		if (id instanceof Long) {
//			return SysContexts.getEntityManager().get(cla, (Long) id);
//		}
//		return null;
//	}

    public static String getRandomCode(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    public static String getRandomNumber(int length){
        return RandomStringUtils.randomNumeric(length);
    }

    public  static String Md5(String plainText) {
        StringBuffer buf = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }
}
