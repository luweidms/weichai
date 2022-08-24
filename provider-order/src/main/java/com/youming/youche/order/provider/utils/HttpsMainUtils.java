package com.youming.youche.order.provider.utils;

import cn.hutool.crypto.asymmetric.Sign;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DataFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
public class HttpsMainUtils {


    /**
     * 是否dev环境 （是否是开发测试环境） true 开发测试 false (生产环境)
     */
//    public static  boolean isDev  = SysCfgUtil.getCfgBooleanVal("W_S_CTRl", 2);
    public static  boolean isDev  = true;
    /**
     * 是否开启验签
     */
    public static final boolean isSign  = true;

    /**
     * 渠道
     */
    public static final String  channel      = "MYBANK";

    /**
     * 短信验证码
     */
    public static  String  smsCode      = "888888";

    /**
     * 币种
     */
    public static final String  currencyCode = "156";

    /**
     * 成功返回编码
     */
    public static String sucCode = "0000";

    /**
     * 重复交易
     */
    public static String againCode = "4005";

    /**
     * 查询时间范围控制测试
     */
    public static int timeRound = 10000;
    /**
     * 发送验证码类型
     * P_INNER_TRANSFER, 同行转账; P_CROSS_TRANSFER, 跨行转账
     */
    public static String bizName = "P_CROSS_TRANSFER";

    /**
     *
     * 0发起成功 1发起失败 2银行打款失败 3提现成功(打款成功)
     */
    /**发起成功 */
    public static final String respCodeZero = "0";
    /**银行打款失败 */
    public static final String respCodeFail = "2";
    /**提现成功(打款成功) */
    public static final String respCodeSuc = "3";
    /**网络超时 */
    public static final String netTimeOutFail = "4";
    /**提现记录失效*/
    public static final String respCodeInvalid = "5";
    /**不绑卡收款针对司机收款（待收款）**/
    public static final String respCodeCollection = "6";

    //sit环境url   https://fcsupergw.dl.alipaydev.com/open/api/common/request.htm
    public static  String  reqUrl = "https://fcsupergw.dl.alipaydev.com/open/api/common/request.htm";
    static{
        if(!isDev){
            //生产（预发）
//     		 reqUrl = "https://fcopenpre.mybank.cn/open/api/common/request.htm";
            //生产
            reqUrl = "https://fcopen.mybank.cn/open/api/common/request.htm";
            smsCode = "";
        }
    }

    /**
     * 跨行单笔转账接口
     */
    public static String crossLineTransInterface = "ant.ebank.transfer.single.crosslinetrans";

    /**
     * 行内单笔转账接口
     */
    public static String crossLineInterface = "ant.ebank.transfer.single.linetrans";

    /**
     * 发送验证码接口
     */
    public static String sendInterface = "ant.ebank.auth.smscode.send";

    /**
     *单笔业务状态查询
     */
    public static String singlePayQueryInterface = "ant.ebank.transfer.singlepay.query";

    /**
     * 批量交易记录查询
     */
    public static String queryInterface = "ant.ebank.trans.record.query";

    /**
     * 账户余额查询
     */
    public static String balanceQuery = "ant.ebank.acount.balance.query";
    /**
     * 跨行单笔转账不能大于 5万（未提供支行号）
     */
    public static long maxPayMoney = 5000000;


    //	inprocess 处理中
    //	success 成功
    //	failure 失败
    //	reexchange退汇
    /**打款中*/
    public static String inprocess = "inprocess";
    /**打款成功*/
    public static String success = "success";
    /**打款失败*/
    public static String failure = "failure";
    /**退汇*/
    public static String reexchange = "reexchange";

    // 5002	查询结果不存在	未知(U)	（业务整体返回码）
    // 5006	有多条查询结果	未知(U)	（预期查询单笔实际存在多笔时返回此错误码）
    /**5002	查询结果不存在	未知(U)	（业务整体返回码）*/
    public static String STATUS_5002 ="5002";
    /**5006	有多条查询结果	未知(U)	（预期查询单笔实际存在多笔时返回此错误码）*/
    public static String STATUS_5006 ="5006";
    /*查询业务类型*/
    public static String bizTypeList = "01"; //为空默认全部，01: 转账; 02: 贷款;03: 消费;04：结息;05: 理财;06: 同业;07: 票据，英文逗号隔开

    //不需要签名的字段集合
    private  static List<String> notSigns = null;
    static{
        notSigns = new ArrayList<String>();
        notSigns.add("partner");
        notSigns.add("function");
        notSigns.add("reqTime");
        notSigns.add("reqMsgId");
        notSigns.add("appid");

    }

  /*  public static String httpsReq(String reqUrl, String param) throws NoSuchAlgorithmException,
            NoSuchProviderException, IOException,
            KeyManagementException {
        URL url = new URL(reqUrl);
        HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
        httpsConn.setHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String paramString, SSLSession paramSSLSession) {
                return true;
            }
        });

        //创建SSLContext对象，并使用我们指定的信任管理器初始化
        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate,
                                           String paramString) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate,
                                           String paramString) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
        sslContext.init(null, new TrustManager[] { tm }, new java.security.SecureRandom());

        //从上述SSLContext对象中得到SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();

        //创建HttpsURLConnection对象，并设置其SSLSocketFactory对象
        httpsConn.setSSLSocketFactory(ssf);

        httpsConn.setDoOutput(true);
        httpsConn.setRequestMethod("POST");
        httpsConn.setRequestProperty("Content-Type", "application/xml;charset=UTF-8");
        httpsConn.setRequestProperty("Content-Length", String.valueOf(param.length()));

        OutputStreamWriter out = new OutputStreamWriter(httpsConn.getOutputStream(), "UTF-8");
        out.write(param);
        out.flush();
        out.close();

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                httpsConn.getInputStream(), "UTF-8"));
        String tempLine = "";
        StringBuffer resultBuffer = new StringBuffer();
        while ((tempLine = reader.readLine()) != null) {
            resultBuffer.append(tempLine).append(System.getProperty("line.separator"));
        }
        return resultBuffer.toString();
    }

    *//**
     * 请求封装并解析返回数据
     *//*
    @SuppressWarnings("rawtypes")
    public static Map httpsReqData(String function, Map<String, String> form, String publicKey, String privateKey) throws Exception{
        String environment = "DEV"; //请求环境
        if(!isDev){
            environment = "PRODUCT";
        }
        XmlUtil xmlUtil = new XmlUtil();
        //封装报文
        String param = xmlUtil.format(form, function);
        logeer.info(environment + ">>接口["+function+"]request>>"+param);
        // privateKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBANKaZf8dXuV+PKlBDsES5xuElAEmtsqEL6oNLtQvqkuzt13ujv3KMQIBMSj+HEwIB2fj8MAHJAW+eUTSpYBNMF/0MOKuvCBzxchazz1vC1vW5xvV4reA+mrrOHse/yfuhurGLwMlBG3LWpGDsASXomfcFBRkaVBCsUJrrtRKOhpzAgMBAAECgYA1/xY1oa8qAgLFloOU5ybP/F8CKI3nQJsD65tdFdeAz1QtRjPtSADmta2ICBbzO3/CmEOqyGno+xwWlDeQvLmw+QfSpwadrkQ7r/5D+Pwin4AYx3G0PJJtNpsacoemDLvK2q3fSz7lj+pAd5WIGCs5GI++XlRUPqfhy8yIUmnWQQJBAPmJGRtV4gcFhVVcQmFHrMm9uPXH72zGh+mX9r5nG4wx8h8cBGIhw8dQWOWkS7PxbJ4S0bKPPpBJ0tpkw3zOc2ECQQDYDxpyjD7Otz4/yT5vnptbjUup56/dnpVV/uZBRL9spg9sHhJaJ4+47q9dR71AVTMRYglktFpS+6N+mxH92fJTAkEAipB+yJcgNBX0vDnHAo3yfPRFSPYFFboIrsYb1g8bVPZHJM9B/9wQAxWyx5I8F2fwkLaSizzP2P+lfnCFvUBHAQJBAMEWVlNyhJrm5mnI5tSIEV1zW+Be3yuXEuzEnXBNCSoHChdclJgfbPEf1nbtqvM4cSgizjRCfcpVetqlqSGNSmMCQQCKDRhLJjlYDFHNknh3kOKo1lrefnBNukUOSR1HpLhikUNSL8f+m44TZPb+x7twkC/zbYaAt2j1+6HnsSPx11VQ";
        if (isSign) {//生产环境需进行rsa签名
            param = XmlSignUtil.sign(param,privateKey);
        }
        //验证自己的 签名是否正确
        // publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDSmmX/HV7lfjypQQ7BEucbhJQBJrbKhC+qDS7UL6pLs7dd7o79yjECATEo/hxMCAdn4/DAByQFvnlE0qWATTBf9DDirrwgc8XIWs89bwtb1ucb1eK3gPpq6zh7Hv8n7obqxi8DJQRty1qRg7AEl6Jn3BQUZGlQQrFCa67USjoacwIDAQAB";
//        if(!XmlSignUtil.verifyOwnXml(param,publicKey)){
//        	logeer.info(environment + ">>接口["+function+"]request验证失败");
//        }else{
//        	logeer.info(environment + ">>接口["+function+"]request验证成功");
//        }

        //发送请求
        String response = httpsReq(HttpsMain.reqUrl, param);
//      logeer.info(environment + ">>接口["+function+"]response 未解析>>"+response);
        if (isSign) {//生产环境需进行rsa验签
            if (!XmlSignUtil.verify(response,publicKey)) {
                throw new Exception(environment + ">>验签失败");
            }
        }
        //解析报文
        Map<String, Object> resMap = xmlUtil.parse(response, function);
        log.info(environment + ">>接口["+function+"]response 解析>>"+resMap);
        Map codeMap = (Map) resMap.get("resultInfo");
        if(codeMap == null){
            throw new BusinessException(environment + ">>查询失败未有key=resultInfo的数据。");
        }

        return resMap;
    }
    *//**
     * 封装简单的 head
     *//*
    public static LinkedHashMap<String, String> getSimpleForm(String function, String partnerId, String appId) {
        LinkedHashMap<String, String> form = new LinkedHashMap<String, String>();
        String appid =  appId; //APPID
        String partner =  partnerId; //partner
        form.put("appid", appid);
        form.put("partner", partner);
        form.put("function", function);
        form.put("reqTime", DateUtil.formatDate(new Timestamp(System.currentTimeMillis()), "yyyy-MM-ddHH:mm:ss.SSS"));
        //reqMsgId每次报文必须都不一样
        form.put("reqMsgId", UUID.randomUUID().toString());
        return form;
    }
*/

    /**
     * body 进行签名
     * 规则 :业务层签名规则：对除partner、sign本身之外的业务接口字段进行签名，如已无其他业务接口字段则对空字符串签名；如有则按以下文档中业务字段顺序签名。如有字段a，值A，字段b，值B，则签名原串为”a=A||b=B”,||为分隔符。若a字段值为空则签名原串为”a=||b=B”。
     */
/*    public static String httpBodySign(LinkedHashMap<String, String> form,String configFilePath,String certFile,long tenantId)throws Exception{
        StringBuffer buf = new StringBuffer();
        Set<Map.Entry<String,String>> sets = form.entrySet();
        int j = 0;
        for(Map.Entry<String,String> entry : sets){
            j++;
            String key = entry.getKey();
            String value = entry.getValue();
            if(!notSigns.contains(key)){
                buf.append(key + "=" +  (StringUtils.isNotEmpty(value) ? value: "") +  (j != sets.size() ? "||" : ""));
            }
        }
        log.info("接口["+ DataFormat.getStringKey(form, "function")+"]签名之前>>"+buf.toString());

        String sign = Sign.sign(buf.toString(), false,configFilePath,certFile,tenantId);
        //校验 证书是否正确方法
        //Verify.verify(buf.toString(), sign);

        return sign;
    }*/
    /**业务流水 保持为30位*/
    public static String getZeroNum(String flowId) {
        StringBuffer buf = new StringBuffer();
        for(int i=0;i<30-flowId.length();i++){
            buf.append("0");
        }
        buf.append(flowId);
        return buf.toString();
    }

    /**
     *
     * 消费币种的最小货币单位（分）,金额不足15位前补0，如金额为15.00，则000000000001500
     *
     */
    public static String formatMoney(long amount){
        String out="";
        String str = amount+"";
        for(int i=str.length();i<15;i++){
            out+="0";
        }
        return out+str;
    }
}
