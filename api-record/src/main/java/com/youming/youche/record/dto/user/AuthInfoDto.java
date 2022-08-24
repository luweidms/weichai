package com.youming.youche.record.dto.user;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AuthInfoDto implements Serializable {

    private static final long serialVersionUID = 2861169024282455099L;

    private String loginAcct; // 认证账号
    private String userName; // 认证用户名
    private String identification; // 身份证
    private String adriverLicenseSn; // 驾驶证

    private String acctName; // 持卡人姓名
    private String acctNo; // 银行卡号
    private String provinceName; // 开户省
    private String provinceId; // 开户省Id
    private String cityName; // 开户地市
    private String cityId; // 开户地市id
    private String branchName; // 开户支行
    private String bankId; // 开户银行Id
    private String bankName; // 开户银行

    private Long idenPictureFront; // 身份证正面flowId
    private String idenPictureFrontUrl; // 身份证正面url
    private Long idenPictureBack; // 身份证背面flowId
    private String idenPictureBackUrl; // 身份证背面url
    private Long adriverLicenseOriginal; // 驾驶证正本id
    private String adriverLicenseOriginalUrl; // 驾驶证正本url
    private Long adriverLicenseDuplicate; // 驾驶证副本id
    private String adriverLicenseDuplicateUrl; // 驾驶证副本url
    private Integer firstFlg; // 初次认证标志 0 否  1是
    private Integer state; // 认证状态  1、未认证 2、已认证 3、认证失败，请重新认证  这个需要转换
    private String stateName; // 认证状态  1、未认证 2、已认证 3、认证失败，请重新认证  这个需要转换
    private String verifReason; // 审核失败原因，为空不显示
    private Integer bindCardEver; // 是否绑定过银行卡  1 是   0 否
    private String vehicleType; // 准驾类型

    private Long qcCerti; // 从业资格证图片ID
    private String qcCertiUrl; // 从业资格证图片URL
    private LocalDateTime driverLicenseTime; // 驾驶证有效期
    private LocalDateTime driverLicenseExpiredTime; // 驾驶证有效期
    private LocalDateTime qcCertiTime; // 从业资格证有效期
    private LocalDateTime qcCertiExpiredTime; // 从业资格证有效期

}
