package com.youming.youche.record.common;


import com.youming.youche.commons.exception.BusinessException;
import org.apache.maven.surefire.shade.api.org.apache.commons.codec.binary.Base64;

public class EncryPwd {


    /**
     * 密码加密
     *
     * @param pwd
     * @return
     */
    public static String pwdEncryption(String pwdExpress) {
        long first = Math.round((Math.random()*80))+10;
        long last = Math.round(Math.random()*80)+10;
        String pwd=EncryPwd.encode((first+""+pwdExpress+""+last+"{zx}").getBytes());
        return pwd;
    }

    /**
     * 登录密码解密兼容
     *
     * @param pwd
     * @return
     */
    public static String pwdDecryption(String pwdCiphertext) {
        String pwd = new String(EncryPwd.decode(pwdCiphertext));
        if(pwd.indexOf("{zx}")>0){
            pwd=pwd.substring(2, (pwd.length()-6));
        }else{
            pwd=pwdCiphertext;
        }
        return pwd;
    }

    /**
     * 密码解密
     * @param pwdCiphertext
     * @return
     */
    public static String pwdDecryp(String pwdCiphertext) {
        String pwd = new String(EncryPwd.decode(pwdCiphertext));
        try {
            pwd=pwd.substring(2, (pwd.length()-6));
        } catch (Exception e) {
            throw new BusinessException("加密无效");
        }
        return pwd;
    }

    /**
     * base64加密
     *
     * @param bstr
     * @return String
     */
    private static String encode(byte[] bstr) {
        return Base64.encodeBase64String(bstr);
    }

    /**
     * base64解密
     *
     * @param str
     * @return string
     */
    private static byte[] decode(String str) {
        return Base64.decodeBase64(str);
    }


}
