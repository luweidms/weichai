package com.youming.youche.cloud.cmb.midsrv.jyt.comment.bizUtils;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description 签名工具类
 * @auther
 * @date 2019/10/24
 **/
public class SignUtils {

    private static final Logger logger = LoggerFactory.getLogger(SignUtils.class);

    private static final String SHA256_HMAC = "HmacSHA256";

    /**
     * 生成签名
     *
     * @param data 数据
     * @param key 密钥
     * @return 签名结果
     */
    private static String generateSignature(final Map<String, Object> data, String key)  throws Exception {
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder sb = new StringBuilder();
        for (String k: keyArray) {
            if (data.get(k) == null || "".equals(data.get(k))) {
                continue;
            }
            sb.append(k).append("=").append(generateString(data.get(k))).append("&");
        }
        String signBefore = sb.deleteCharAt(sb.length()-1).toString();
        String signAfter = SHA256_HMAC(signBefore, key);
        logger.info("待加签字符串:{},key:{},加签后字符串:{}", signBefore, key, signAfter);
        return signAfter;
    }

    /**
     * value的排序，组成字符串
     * */
    private static String generateString(Object value){
        if (value instanceof Map){
            Map<String, Object> map = JSONObject.parseObject(JSONObject.toJSONString(value));
            Set<String> keySet = map.keySet();
            String[] keyArray = keySet.toArray(new String[0]);
            Arrays.sort(keyArray);
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            for (String k: keyArray) {
                if (map.get(k) == null || "".equals(map.get(k))) {
                    continue;
                }
                sb.append("\"").append(k).append("\"").append(":").append(generateString(map.get(k))).append(",");
            }
            return sb.deleteCharAt(sb.length()-1).append("}").toString();
        }else if (value instanceof List){
            List<Object> list = JSONObject.parseArray(JSONObject.toJSONString(value));
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            list.forEach(item ->
                    sb.append(generateString(item)).append(",")
            );
            return sb.deleteCharAt(sb.length()-1).append("]").toString();
        }
        return JSONObject.toJSONString(value);
    }

    /**
     * 生成签名
     *
     * @param data 数据
     * @param key 密钥
     * @return 签名结果
     */
    public static String generateSign(final Map<String, Object> data, String key)  throws Exception {
        return generateSignature(data, key);
    }

    /**
     * 生成字符串的SHA256签名
     *
     * @param message 消息
     * @param secret 密钥
     * @return 处理后的字符串
     */
    private static String SHA256_HMAC(String message, String secret) throws Exception {
        Mac sha256_HMAC = Mac.getInstance(SHA256_HMAC);
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SHA256_HMAC);
        sha256_HMAC.init(secretKey);
        byte[] bytes = sha256_HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8));
        return byteArrayToHexString(bytes);
    }

    /**
     * 字节数组转字符串
     *
     * @param
     * @return
     */
    private static String byteArrayToHexString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        String temp;
        for (int i = 0; b != null && i < b.length; i++) {
            temp = Integer.toHexString(b[i] & 0XFF);
            if (temp.length() == 1) {
                sb.append('0');
            }
            sb.append(temp);
        }
        return sb.toString().toLowerCase();
    }

}
