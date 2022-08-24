package com.youming.youche.cloud.provider.service.cmb;

import com.youming.youche.cloud.cmb.midsrv.jyt.comment.bizUtils.SignUtils;
import com.youming.youche.cloud.cmb.midsrv.jyt.comment.enums.HttpMethod;
import com.youming.youche.cloud.cmb.midsrv.jyt.comment.openApiGateway.OpenApi;
import com.youming.youche.cloud.cmb.midsrv.jyt.entity.JytfzClient;
import com.youming.youche.cloud.provider.configure.cmb.JytfzProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Description: OpenApi访问的service
 * Author:
 * Date:
 * Version:v1.0
 */
public class OpenApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiService.class);

    @Autowired
    private JytfzProperties jytfzProperties;

    public OpenApiService() {
    }

    public OpenApiService(JytfzProperties jytfzProperties) {
        this.jytfzProperties = jytfzProperties;
    }

    public JytfzProperties getJytfzProperties() {
        return jytfzProperties;
    }

    public void setJytfzProperties(JytfzProperties jytfzProperties) {
        this.jytfzProperties = jytfzProperties;
    }

    /**
     * 对接openapi平台中的接口
     * @param url 地址
     * */
    public String OpenApiClient(String url, JytfzClient param, HttpMethod httpMethod) throws Exception {
        return OpenApiClient(url, param.formMapParams(), httpMethod.getMethod());
    }

    /**
     * 对接openapi平台中的接口
     * @param url 地址
     * @param queryParams 请求参数map
     * */
    public String OpenApiClient(String url, Map<String, Object> queryParams, String httpMethod) throws Exception {

        Map<String, String> header = forHeaderMap(queryParams);
        String result;
        if (HttpMethod.POST.getMethod().equals(httpMethod)) {
            result = OpenApi.httpPostByAppSecret(url, header, queryParams);
        } else if (HttpMethod.PUT.getMethod().equals(httpMethod)) {
            result = OpenApi.httpPutByAppSecret(url, header, queryParams);
        } else if (HttpMethod.DELETE.getMethod().equals(httpMethod)) {
            result = OpenApi.httpDeleteByAppSecret(url, header, queryParams);
        } else {
            result = OpenApi.httpGetByAppSecret(url, header, queryParams);
        }

        return result;
    }

    /**
     * 形成的头部信息
     * */
    public Map<String, String> forHeaderMap(Map<String, Object> paramMap) throws Exception {
        //head里面的sign是通过业务参数加签得来的
        String sign = SignUtils.generateSign(paramMap, jytfzProperties.getSecretKey());

        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        Map<String, String> map = new HashMap<>(6);
        map.put("appid", jytfzProperties.getAppId());
        map.put("secret", jytfzProperties.getAppSecret());
        map.put("sign", sign);
        map.put("timestamp", timestamp);
        try {
            map.put("apisign", formApiSign(map));
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        map.put("verify", jytfzProperties.getVerifyType());
        map.put("version", jytfzProperties.getVersion());
        map.remove("secret");
        return map;
    }

    /**
     * 形成签名摘要
     * @param map 带有appid、secret、sign、timestamp签名的map
     * */
    public String formApiSign(Map<String, String> map) throws NoSuchAlgorithmException {
        if (!map.containsKey("appid") || !map.containsKey("secret") || !map.containsKey("sign") || !map.containsKey("timestamp")) {
            return "加签头参数不完整";
        }
        List<String> list = new ArrayList<>();
        list.add("appid=" + map.get("appid"));
        list.add("secret=" + map.get("secret"));
        list.add("sign=" + map.get("sign"));
        list.add("timestamp=" + map.get("timestamp"));
        //进行SHA256做摘要算法
        MessageDigest messageDigest;
        messageDigest = MessageDigest.getInstance(jytfzProperties.getVerifyMethod());
        messageDigest.update(StringUtils.join(list, "&").getBytes(StandardCharsets.UTF_8 ));
        byte[] bytes = messageDigest.digest();
        return bytesToHexString(bytes);
    }

    /**
     * 将字节数组转换成16进制字符串
     * @param bytes 字节数组
     * */
    private String bytesToHexString(byte[] bytes){
        StringBuffer sb = new StringBuffer(bytes.length);
        String temp;
        for(int i = 0; i < bytes.length; i++){
            temp = Integer.toHexString(0xFF & bytes[i]);
            if (temp.length() < 2){
                sb.append(0);
            }
            sb.append(temp);
        }
        return sb.toString();
    }

    /**
     * 分批次写入信息到文件中
     * @param parent 本地保存文件的目录
     * @param localFileName 本地保存文件目录下的文件名，建议*.zip的格式
     * @param downloadUrl 文件下载地址
     * @param remoteFileName 远程下载文件的文件名
     *
     * */
    public String saveContent2File(String parent, String localFileName, String downloadUrl, String remoteFileName, String platformNo) throws Exception {
        File file = new File(parent + File.separator + localFileName);
        if (file.exists()){
            file.delete();
        }
        Map<String, Object> param = new HashMap<>();
        param.put("platformNo", platformNo);
        param.put("clearFilename", remoteFileName);
        downloadUrl = downloadUrl + "?platformNo=" + platformNo + "&" + "clearFilename=" + remoteFileName.replace("+", "%2B");
        // 获取连接
        HttpsURLConnection conn = getConnectionByUrl(downloadUrl, param);
        if (null == conn) {
            LOGGER.error("下载文件失败，下载路径：{}", downloadUrl);
            return null;
        }

        try (InputStream in = conn.getInputStream();
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(parent + localFileName, true))) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
        }
        return parent +  File.separator + localFileName;
    }

    /**
     * 根据路径获取连接
     *
     * @param url  路径
     * @return
     * @throws Exception
     */
    private HttpsURLConnection getConnectionByUrl(String url, Map<String, Object> params) throws Exception {
        Map<String, String> header = forHeaderMap(params);
        URL downloadUrl = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) downloadUrl.openConnection();
        conn.setSSLSocketFactory(OpenApi.sslSocketFactory());
        conn.setHostnameVerifier((String var1, SSLSession var2) -> true );
        for (String key : header.keySet()){
            conn.setRequestProperty(key, header.get(key));
        }
        conn.setConnectTimeout(2000);
        conn.setReadTimeout(10000);
        //HTTP Status-Code 302: Temporary Redirect.
        while (conn.getResponseCode() == HttpsURLConnection.HTTP_MOVED_TEMP) {
            conn.disconnect();
            downloadUrl = new URL(conn.getHeaderFields().get("Location").get(0));
            conn = (HttpsURLConnection) downloadUrl.openConnection();
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(10000);
        }
        //HTTP Status-Code 200: OK.
        if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
            LOGGER.error("下载文件失败！返回码为：{}，返回信息为：{}", conn.getResponseCode(), conn.getResponseMessage());
            return null;
        }
        return conn;
    }

}
