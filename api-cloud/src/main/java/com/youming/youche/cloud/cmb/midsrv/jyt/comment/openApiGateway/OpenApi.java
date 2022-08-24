package com.youming.youche.cloud.cmb.midsrv.jyt.comment.openApiGateway;

import com.alibaba.fastjson.JSON;
import okhttp3.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 通过Okhttp3进行网络访问的工具类
 *
 * */
public class OpenApi {
    private static final String APPID = "appid";
    private static final String TIMESTAMP = "timestamp";
    private static final String SIGN = "sign";
    private static final String APISIGN = "apisign";
    private static final String VERIFY = "verify";
    private static final String VERSION = "version";
    private static final MediaType JSONHeader = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClient client = new OkHttpClient();
    private static OkHttpClient clientHttps = new OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .sslSocketFactory(sslSocketFactory(), x509TrustManager())
            .hostnameVerifier((String var1, SSLSession var2) -> true).build();

    public static OkHttpClient getClient () {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
//                    忽略证书验证，根据安全性需要选择
                    .sslSocketFactory(sslSocketFactory(), x509TrustManager())
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify (String s, SSLSession sslSession) {
                            return true;
                        }
                    })
                    .build();
            return client;
        } catch (Exception e) {
            throw new RuntimeException("Http客户端构造失败");
        }

    }

    /**
     * 通过AppSecret加签方式和get方式进行连接
     * @param url 地址
     * @param headMap 包括appid、timestamp、sign、apisign（加签后的key-value）、verify的map
     * @param queryParam 包括查询的所有入参的map
     * */
    public static String httpGetByAppSecret(String url, Map<String, String> headMap, Map<String, Object> queryParam) throws IOException {
        if (!headMap.containsKey(APPID) || !headMap.containsKey(TIMESTAMP) || !headMap.containsKey(SIGN)
                || !headMap.containsKey(APISIGN) || !headMap.containsKey(VERIFY)){
            return null;
        }
        Request.Builder builder = new Request.Builder();
        if (queryParam != null && !queryParam.isEmpty()){
            StringBuilder sb = new StringBuilder(url + "?");
            //装载请求参数
            for (Map.Entry<String, Object> entry : queryParam.entrySet()){
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            url = sb.substring(0, sb.length() - 1);
        }
        Request request = builder
                .url(url)
                .addHeader(APPID, headMap.get(APPID))
                .addHeader(TIMESTAMP,headMap.get(TIMESTAMP))
                .addHeader(SIGN, headMap.get(SIGN))
                .addHeader(APISIGN,headMap.get(APISIGN))
                .addHeader(VERIFY,headMap.get(VERIFY))
                .addHeader(VERSION, headMap.get(VERSION))
                .build();
        Response response = clientHttps.newCall(request).execute();
        if (response.isSuccessful()){
            return response.body().string();
        }
        return null;
    }

    /**
     * 通过AppSecret和post方式提交
     * @param url 地址
     * @param headMap 包括appid、timestamp、sign、apisign（加签后的key-value）、verify的map
     * @param params 查询的入参
     * */
    public static String httpPostByAppSecret(String url, Map<String, String> headMap, Map<String, Object> params) throws IOException {
        if (!headMap.containsKey(APPID) || !headMap.containsKey(TIMESTAMP) || !headMap.containsKey(SIGN)
                || !headMap.containsKey(APISIGN) || !headMap.containsKey(VERIFY)){
            return null;
        }
        RequestBody body = RequestBody.create(JSONHeader, JSON.toJSONString(params));
        Request request = new Request.Builder()
                .url(url)
                .addHeader(APPID, headMap.get(APPID))
                .addHeader(TIMESTAMP,headMap.get(TIMESTAMP))
                .addHeader(SIGN, headMap.get(SIGN))
                .addHeader(APISIGN,headMap.get(APISIGN))
                .addHeader(VERIFY,headMap.get(VERIFY))
                .addHeader(VERSION, headMap.get(VERSION))
                .post(body)
                .build();
        Response response = clientHttps.newCall(request).execute();
        if (response.isSuccessful()){
            return response.body().string();
        }
        System.out.println(response);
        return null;
    }

    /**
     * 通过AppSecret和put方式提交
     * @param url 地址
     * @param headMap 包括appid、timestamp、sign、apisign（加签后的key-value）、verify的map
     * @param params 传递的参数
     * */
    public static String httpPutByAppSecret(String url, Map<String, String> headMap, Map<String, Object> params) throws Exception {
        if (!headMap.containsKey(APPID) || !headMap.containsKey(TIMESTAMP) || !headMap.containsKey(SIGN)
                || !headMap.containsKey(APISIGN) || !headMap.containsKey(VERIFY)){
            return null;
        }
        RequestBody body = RequestBody.create(JSONHeader, JSON.toJSONString(params));
        Request request = new Request.Builder()
                .url(url)
                .addHeader(APPID, headMap.get(APPID))
                .addHeader(TIMESTAMP,headMap.get(TIMESTAMP))
                .addHeader(SIGN, headMap.get(SIGN))
                .addHeader(APISIGN,headMap.get(APISIGN))
                .addHeader(VERIFY,headMap.get(VERIFY))
                .addHeader(VERSION, headMap.get(VERSION))
                .put(body)
                .build();
        Response response = clientHttps.newCall(request).execute();
        if (response.isSuccessful()){
            return response.body().string();
        }
        return null;
    }

    /**
     * 通过AppSecret和delete方式提交
     * @param url 地址
     * @param headMap 包括appid、timestamp、sign、apisign（加签后的key-value）、verify的map
     * @param params 传递的参数
     * */
    public static String httpDeleteByAppSecret(String url, Map<String, String> headMap, Map<String, Object> params) throws Exception {
        if (!headMap.containsKey(APPID) || !headMap.containsKey(TIMESTAMP) || !headMap.containsKey(SIGN)
                || !headMap.containsKey(APISIGN) || !headMap.containsKey(VERIFY)){
            return null;
        }
        RequestBody body = RequestBody.create(JSONHeader, JSON.toJSONString(params));
        Request request = new Request.Builder()
                .url(url)
                .addHeader(APPID, headMap.get(APPID))
                .addHeader(TIMESTAMP,headMap.get(TIMESTAMP))
                .addHeader(SIGN, headMap.get(SIGN))
                .addHeader(APISIGN,headMap.get(APISIGN))
                .addHeader(VERIFY,headMap.get(VERIFY))
                .addHeader(VERSION, headMap.get(VERSION))
                .delete(body)
                .build();
        Response response = clientHttps.newCall(request).execute();
        if (response.isSuccessful()){
            return response.body().string();
        }
        return null;
    }

    public static X509TrustManager x509TrustManager(){
        X509TrustManager x509TrustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted (X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted (X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers () {
                return new X509Certificate[]{};
            }
        };
        return x509TrustManager;
    }



    public static SSLSocketFactory sslSocketFactory(){
        SSLSocketFactory sslSocketFactory = null;
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            TrustManager[] trustAllCerts = new TrustManager[]{x509TrustManager()};
            sslContext.init(null, trustAllCerts, new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslSocketFactory;
    }
}
