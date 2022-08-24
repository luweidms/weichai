package com.youming.youche.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    private static final Log log = LogFactory.getLog(MD5.class);
    public static final char[] hexChar = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static MD5 instance = null;

    private MD5() {
    }

    public static MD5 getInstance() {
        if (instance == null) {
            Class var0 = MD5.class;
            synchronized (MD5.class) {
                if (instance == null) {
                    instance = new MD5();
                }
            }
        }

        return instance;
    }

    private String toHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);

        for (int i = 0; i < b.length; ++i) {
            sb.append(hexChar[(b[i] & 240) >>> 4]);
            sb.append(hexChar[b[i] & 15]);
        }

        return sb.toString();
    }

    private byte[] encode(String algorithm, String info) {
        MessageDigest md5 = null;

        try {
            md5 = MessageDigest.getInstance(algorithm);
            byte[] srcBytes = info.getBytes("UTF-8");
            md5.update(srcBytes);
            byte[] resultBytes = md5.digest();
            return resultBytes;
        } catch (NoSuchAlgorithmException var6) {
            if (log.isInfoEnabled()) {
                log.info(var6.getMessage());
            }

            return null;
        } catch (UnsupportedEncodingException var7) {
            if (log.isInfoEnabled()) {
                log.info(var7.getMessage());
            }

            return null;
        }
    }

    public static String eccryptMD5(String code) {
        MD5 md5 = getInstance();
        return md5.toHexString(md5.encode("MD5", code));
    }

    public static String eccryptSHA(String code) {
        MD5 md5 = getInstance();
        return md5.toHexString(md5.encode("SHA", code));
    }

    public static void main(String[] args) {
        String s = "[1428722325004, 59, RNeSRGEOUpv8SbLKg6ysFcrOVSmknZNM, {\"content\":{\"count\":\"10\",\"roadType\":\"2\",\"page\":1,\"userId\":10011750251008},\"uId\":10011750251008,\"inCode\":\"40062\",\"tokenId\":\"RNeSRGEOUpv8SbLKg6ysFcrOVSmknZNM\"}]";
        System.out.println(eccryptMD5("wengxk"));
        System.out.println(eccryptSHA(s));
    }

}
