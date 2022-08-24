package com.youming.youche.record.vo.driver;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: DoAddOBMDriver
 * @Package: com.youming.youche.record.vo.driver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/17 14:54
 * @company:
 */
@Data
public class DoAddOBMDriver implements Serializable {

    /**
     * 手机号
     */
    private String loginAcct;

    /**
     * 名称
     */
    private String linkman;

    /**
     * 身份证号
     */
    private String identification;

    /**
     * 驾驶证号
     */
    private String adriverLicenseSn;

    /**
     * 准驾车型
     */
    private String vehicleType;

    /**
     *驾驶证有效期开始
     */
    private String driverLicenseTime;

    /**
     *驾驶证有效期结束
     */
    private String driverLicenseExpiredTime;

    /**
     *从业资格证有效期开始
     */
    private String qcCertiTime;


    /**
     *从业资格证有效期结束
     */
    private String qcCertiExpiredTime;


    /**
     *头像id
     */
    private String userPrice;

    /**
     *头像地址
     */
    private String userPriceUrl;

    /**
     *身份证z地址
     */
    private String idenPictureFront;

    /**
     *身份证zid
     */
    private String idenPictureFrontUrl;


    /**
     *身份证反面
     */
    private String idenPictureBack;


    /**
     *身份证反面id
     */
    private String idenPictureBackUrl;

    /**
     *驾驶证正面id
     */
    private String adriverLicenseOriginal;


    /**
     *驾驶证正面地址
     */
    private String adriverLicenseOriginalUrl;


    /**
     *驾驶证f面id
     */
    private String adriverLicenseDuplicate;

    /**
     *驾驶证f面id
     */
    private String adriverLicenseDuplicateUrl;


    /**
     *从业资格
     * 证
     */
    private String qcCerti;


    /**
     *从业资格
     * 证
     */
    private String qcCertiUrl;



    /**
     *路哥运输
     * 协议
     */
    private String lugeAgreement;



    /**
     *路哥运输
     * 协议
     */
    private String lugeAgreementUrl;


    private Long userId;


}
