package com.youming.youche.system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author : [terry]
 * @version : [v1.0]
 * @className : UserDataInfoDto
 * @description : [描述说明该类的功能]
 * @createTime : [2022/4/27 22:17]
 */
@Data
@RequiredArgsConstructor(staticName = "of")
public class UserDataInfoDto implements Serializable {


    private static final long serialVersionUID = 8016576575217269115L;

    /**
     * 联系人姓名
     */
    private String linkman;

    /**
     * 公司EMAIL
     */
    private String email;

    /**
     * 联系电话
     */
    private String contactNumber;

    /**
     * 联系人手机
     */
    private String mobilePhone;

    /**
     * 身份证号码
     */
    private String identification;

    /**
     * 登录名
     */
    private String loginName;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 用户头像
     */
    private Long userPrice;

    /**
     * 用户头像路径
     */
    private String userPriceUrl;

    /**
     * 身份证图片正面
     */
    private Long idenPictureFront;

    /**
     * 身份证正面路径
     */
    private String idenPictureFrontUrl;

    /**
     * 身份证背面
     */
    private Long idenPictureBack;

    /**
     * 身份证路径背面
     */
    private String idenPictureBackUrl;

    /**
     * 渠道类型
     */
    private String channelType;

    /**
     * 行驶证
     */
    private Long drivingLicense;

    /**
     * 行驶证URL
     */
    private String drivingLicenseUrl;

    /**
     * 驾驶证正本URL
     */
    private Long adriverLicenseOriginal;

    /**
     * 驾驶证正本
     */
    private String adriverLicenseOriginalUrl;

    /**
     * 驾驶证副本URL
     */
    private Long adriverLicenseDuplicate;

    /**
     * 驾驶证副本
     */
    private String adriverLicenseDuplicateUrl;

    /**
     * 操作人ID
     */
    private Long opId;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime opDate;

    /**
     * 证件类型 1、表示身份证 2、表示驾驶证
     */
    private Integer idType;

    /**
     * 公正次数
     */
    private Integer notarizeCount;

    private Integer isPerfectInfo;

    /**
     * 二维码图片id
     */
    private Long qrCodeId;

    /**
     * 二维码图片url
     */
    private String qrCodeUrl;

    /**
     * 标识数据来源，0、表示平台发展1、表示爬虫2、表示百度3、表示自动生成4、表示外购
     */
    private Integer sourceFlag;

    /**
     * 从业资格证图片ID
     */
    private Long qcCerti;

    /**
     * 从业资格证图片URL
     */
    private String qcCertiUrl;

    private Long tenantId;

    /**
     * 驾驶证号编号
     */
    private String adriverLicenseSn;

    /**
     * 平台认证认证1、未认证 2、已认证 3、认证失败
     */
    private Integer authState;
    private String  authStateName;

    /**
     * 支付密码
     */
    private String accPassword;

    /**
     * 修改支付密码时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modPwdtime;

    /**
     * 审核状态 0：未审核 1:已审核(枚举HAS_VER_STATE)
     */
    private Integer hasVer;

    /**
     * 审核意见
     */
    private String verifReason;

    /**
     * 审核人
     */
    private Long authManId;

    /**
     * 归属人
     */
    private Long attachedUserId;

    /**
     * 归属部门
     */
    private Long attachedOrgId;

    /**
     * 准驾车型
     */
    private String vehicleType;

    /**
     * 完整性
     */
    private String completeness;

    /**
     * 快速创建标志（0、非快速创建，1、快速创建）
     */
    private Integer quickFlag;

    /**
     * 驾驶证有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime driverLicenseTime;

    /**
     * 驾驶证有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime driverLicenseExpiredTime;

    /**
     * 从业资格证有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime qcCertiTime;

    /**
     * 从业资格证有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime qcCertiExpiredTime;





}

