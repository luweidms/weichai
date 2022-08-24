package com.youming.youche.record.vo.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class CarAuthInfoVo implements Serializable {

    private static final long serialVersionUID = -3382471920411541827L;

    private Long userId; // 用户编号
    private String userName; // 认证用户名
    private String identification; // 身份证
    private String adriverLicenseSn; // 驾驶证
    private String acctNo; // 银行卡号
    private String provinceId; // 开户省Id
    private String cityId; // 开户地市id
    private String branchName; // 开户支行
    private String bankId; // 开户银行id
    private String bankName; // 开户银行
    private String userPrice; // 头像flowId
    private String userPriceUrl; // 头像url
    private Long idenPictureFront; // 身份证正面flowId
    private String idenPictureFrontUrl; // 身份证正面url
    private Long idenPictureBack; // 身份证背面flowId
    private String idenPictureBackUrl; // 身份证背面url
    private Long adriverLicenseOriginal; // 驾驶证正本id
    private String adriverLicenseOriginalUrl; // 驾驶证正本url
    private Long adriverLicenseDuplicate; // 驾驶证副本id
    private String adriverLicenseDuplicateUrl; // 驾驶证副本url
    private Long qcCerti; // 从业资格证
    private String qcCertiUrl;
    private String vehicleType; // 准驾类型
    private Boolean ocrFlag;
    private String driverLicenseTime; // 驾驶证有效期
    private String driverLicenseExpiredTime; // 驾驶证有效期
    private String qcCertiTime; // 从业资格证有效期
    private String qcCertiExpiredTime; // 从业资格证有效期
    private String verifReason; // 审核意见

}
