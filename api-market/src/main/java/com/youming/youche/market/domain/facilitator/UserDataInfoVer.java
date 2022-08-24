package com.youming.youche.market.domain.facilitator;

import java.time.LocalDateTime;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-01-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class UserDataInfoVer extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 支付密码
     */
    private String accPassword;
    /**
     * 驾驶证副本
     */
    private Long adriverLicenseDuplicate;
    /**
     * 驾驶证副本
     */
    private String adriverLicenseDuplicateUrl;
    /**
     * 驾驶证正本URL
     */
    private Long adriverLicenseOriginal;
    /**
     * 驾驶证正本
     */
    private String adriverLicenseOriginalUrl;
    /**
     * 驾驶证号编号
     */
    private String adriverLicenseSn;
    /**
     * 归属部门
     */
    private Long attachedOrgId;
    /**
     * 归属人
     */
    private Long attachedUserId;
    /**
     * 审核人
     */
    private Long authManId;
    /**
     * 平台认证认证1、未认证  2、已认证 3、认证失败
     */
    private Integer authState;
    /**
     * 渠道类型
     */
    private String channelType;
    /**
     * 联系电话
     */
    private String contactNumber;
    /**
     * 创建时间
     */
    private LocalDateTime createDate;
    /**
     * 驾驶证有效期
     */
    private LocalDateTime driverLicenseExpiredTime;
    /**
     * 驾驶证有效期
     */
    private LocalDateTime driverLicenseTime;
    /**
     * 行驶证
     */
    private Long drivingLicense;
    /**
     * 行驶证
     */
    private String drivingLicenseUrl;
    /**
     * 公司EMAIL
     */
    private String email;
    /**
     * 审核状态 0：未审核 1:已审核(枚举HAS_VER_STATE)
     */
    private Integer hasVer;
    /**
     * 证件类型 1、表示身份证 2、表示驾驶证
     */
    private Integer idType;
    /**
     * 身份证图片正面
     */
    private Long idenPictureBack;
    /**
     * 身份证正面路径
     */
    private String idenPictureBackUrl;
    /**
     * 身份证背面
     */
    private Long idenPictureFront;
    /**
     * 身份证路径背面
     */
    private String idenPictureFrontUrl;
    /**
     * 身份证号
     */
    private String identification;

    private Integer isPerfectInfo;
    /**
     * 联系人姓名
     */
    private String linkman;
    /**
     * 登录名
     */
    private String loginName;
    /**
     * 路歌运输协议
     */
    private Long lugeAgreement;
    /**
     * 路歌运输协议url
     */
    private String lugeAgreementUrl;
    /**
     * 联系人手机
     */
    private String mobilePhone;
    /**
     * 修改支付密码时间
     */
    private LocalDateTime modPwdtime;
    /**
     * 公正次数
     */
    private Integer notarizeCount;
    /**
     * 操作id
     */
    private Long opId;
    /**
     * 从业资格证图片ID
     */
    private Long qcCerti;
    /**
     * 从业资格证图片url
     */
    private LocalDateTime qcCertiExpiredTime;
    /**
     * 从业资格证有效期
     */
    private LocalDateTime qcCertiTime;
    /**
     * 从业资格证有效期
     */
    private String qcCertiUrl;
    /**
     * 从业资格证图片ID
     */
    private Long qrCodeId;
    /**
     * 从业资格证图片url
     */
    private String qrCodeUrl;
    /**
     * 标识数据来源，0、表示平台发展1、表示爬虫2、表示百度3、表示自动生成4、表示外购
     */
    private Integer sourceFlag;
    /**
     * 车队id
     */
    private Long tenantId;
    /**
     * 修改时间
     */
    private LocalDateTime updateDate;
    /**
     * 更新操作人
     */
    private Long updateOpId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户头像
     */
    private Long userPrice;
    /**
     * 用户头像路径
     */
    private String userPriceUrl;
    /**
     * 用户类型
     */
    private Integer userType;

//    private String facilitatorType;
    /**
     * 审核数据状态
     */
    private Integer verState;
    /**
     * 审核意见
     */
    private String verifReason;

   // private Long hisId;


}
