package com.youming.youche.record.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.record.domain.user.UserLineRel;
import com.youming.youche.record.domain.user.UserLineRelVer;
import com.youming.youche.record.domain.user.UserSalaryInfo;
import com.youming.youche.record.domain.user.UserSalaryInfoVer;
import com.youming.youche.util.DateUtil;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * @version:
 * @Title: UserDataInfoVo
 * @Package: com.youming.youche.vo
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @LocalDateTime: 2022/1/5 15:39
 * @company:
 */
@Data
public class UserDataInfoVo implements Serializable {
    private static final long serialVersionUID = 875336885318393540L;
    private static DateTimeFormatter df = DateTimeFormatter.ofPattern(DateUtil.DATE_FORMAT);
    //基础信息
    /**
     * 用户ID
     */
    private long userId;

    /**
     * 来源租户，
     */
    private Long sourceTenantId;

    /**
     * 手机号码
     */
    private String mobilePhone;

    /**
     * 车主（司机）类型（DRIVER_TYPE）
     */
    private Integer carUserType;

    /**
     * 类型名称
     */
    private String carUserTypeName;

    /**
     * 名字
     */
    private String linkman;

    /**
     * 身份证好
     */
    private String identification;

    /**
     * 驾驶证号编号
     */
    private String adriverLicenseSn;

    /**
     * 头像
     */
    private Long userPrice;

    /**
     * 头像地址
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
     * 从业资格证图片ID
     */
    private Long qcCerti;

    /**
     * 从业资格证图片URL
     */
    private String qcCertiUrl;

    /**
     * 工资模式
     */
    private Integer salaryPattern;

    /**
     * 工资模式名称
     */
    private String salaryPatternName;

    /**
     * 薪资
     */
    private Long salary;

    /**
     * 薪资
     */
    private Double salaryDouble;

    /**
     * 补贴天数
     */
    private Long subsidy;

    /**
     * 补贴天数
     */
    private Double subsidyDouble;

    /**
     * 用户补贴信息
     */
    private List<UserSalaryInfo> userSalaryInfoList;

    /**
     * 自有车薪资费用信息
     */
    private List<UserSalaryInfoVer> userSalaryInfoVerList;

    /**
     * 用户线路信息
     */
    private List<UserLineRel> userLineRelList;

    /**
     * 心愿路线
     */
    private List<UserLineRelVer> userLineRelVerList;

    /**
     * 车队名称
     */
    private String tenantName;

    /**
     * 管理员ID
     */
    private String adminUserName;

    /**
     * 手机号码
     */
    private String linkPhone;

    /**
     * 私户账号
     */
    private String privateAcctNo;

    /**
     * 公户账号
     */
    private String corporateAcctNo;

    /**
     * 车辆列表
     */
    private List<Map> vehicleList;

    /**
     * 车辆类型名称
     */
    private String vehicleClassName;

    /**
     * 工资模式
     */
    private int showSalary;

    /**
     * 司机车队关系ID
     */
    private long relId;

    /**
     * 联系人ID
     */
    private Long attachedUserId;

    /**
     * 联系人名称
     */
    private String attachedUserName;

    /**
     * 组织ID
     */
    private Long attachedOrgId;

    /**
     * 组织名称
     */
    private String attachedOrgName;

    /**
     * 线路是否处理
     */
    private boolean showLineVer;

    /**
     * 车辆类型
     */
    private String vehicleType;

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

    /**
     * 驾驶证有效期
     */
    private String driverLicenseTimeStr;

    /**
     * 驾驶证有效期
     */
    private String driverLicenseExpiredTimeStr;

    /**
     * 从业资格证有效期
     */
    private String qcCertiTimeStr;

    /**
     * 从业资格证有效期
     */
    private String qcCertiExpiredTimeStr;

    /**
     * 路歌运输协议
     */
    private Long lugeAgreement;

    /**
     * 路歌运输协议URL
     */
    private String lugeAgreementUrl;

    public String getDriverLicenseTimeStr() {
        if (this.getDriverLicenseTime() != null) {
            setDriverLicenseTimeStr(df.format(this.getDriverLicenseTime()));
        }
        return driverLicenseTimeStr;
    }

    public String getDriverLicenseExpiredTimeStr() {
        if (this.getDriverLicenseExpiredTime() != null) {
            setDriverLicenseExpiredTimeStr(df.format(this.getDriverLicenseExpiredTime()));
        }
        return driverLicenseExpiredTimeStr;
    }

    public String getQcCertiTimeStr() {
        if(this.getQcCertiTime() != null){
            setQcCertiTimeStr(df.format(this.getQcCertiTime()));
        }
        return qcCertiTimeStr;
    }

    public String getQcCertiExpiredTimeStr() {
        if(this.getQcCertiExpiredTime() != null){
            setQcCertiExpiredTimeStr(df.format(this.getQcCertiExpiredTime()));
        }
        return qcCertiExpiredTimeStr;
    }

}
