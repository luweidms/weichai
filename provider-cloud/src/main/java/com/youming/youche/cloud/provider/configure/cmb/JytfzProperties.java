package com.youming.youche.cloud.provider.configure.cmb;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Description: OpenApi平台的常数
 * Author:
 * Date:
 * Version:v1.0
 */
@Component
@ConfigurationProperties(prefix = "jytfz.open-api")
public class JytfzProperties {

    private String appUrl;
    private String appId;
    private String appSecret;
    private String verifyMethod = "SHA-256";
    private String verifyType = "SHA256Verify";
    private String secretKey;
    private String version;
    private String platformNo;
    private String bnkNo;

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public String getVerifyMethod() {
        return verifyMethod;
    }

    public void setVerifyMethod(String verifyMethod) {
        this.verifyMethod = verifyMethod;
    }

    public String getVerifyType() {
        return verifyType;
    }

    public void setVerifyType(String verifyType) {
        this.verifyType = verifyType;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPlatformNo() {
        return platformNo;
    }

    public void setPlatformNo(String platformNo) {
        this.platformNo = platformNo;
    }

    public String getBnkNo() {
        return bnkNo;
    }

    public void setBnkNo(String bnkNo) {
        this.bnkNo = bnkNo;
    }
}
