package com.youming.youche.record.domain.tenant;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 车队与车辆关系表
 * </p>
 *
 * @author Terry
 * @since 2021-12-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TenantVehicleRel extends BaseDomain {

    private static final long serialVersionUID = 1L;


//    private Long relId;

    /**
     * 车辆编号
     */
    private Long vehicleCode;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 归属人
     */
    private Long userId;

    /**
     * 车牌号码，冗余
     */
    private String plateNumber;


    /**
     * 操作员id
     */
    private Long opId;

    /**
     * 默认1
     */
    private Integer verifySys;

    /**
     * 用户归属的二级组织
     */
    private Long orgId;

    /**
     * 用户归属的顶级组织
     */
    private Long rootOrgId;

    /**
     * 审核原因
     */
    private String verifyReason;

    /**
     * 审核时间
     */
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime auditDate;

    /**
     * 用户是否接单:1不接单，2接单
     */
    private Integer isWorking;

    /**
     * 管理人员备注
     */
    private String managerRemark;

    /**
     * 车辆类别
     */
    private Integer vehicleClass;

    /**
     * 结算时间
     */
    private Integer settlementTime;

    /**
     * 路歌返回信息
     */
    private String retResult;

    /**
     * 路歌接口调用时间
     */
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime retDate;

    /**
     * 路歌返回不通过原因:根据数字位数标识含义，每位数字含义参见
     */
    private String retDateil;

    /**
     * 归属大区ID
     */
    private Long attachedRootOrgId;

    /**
     * 归属省份
     */
    private Long attachedProvince;

    /**
     * 归属地区
     */
    private Long attachedRegion;

    /**
     * 归属地
     */
    private String attachedAddr;

    /**
     * 空载油耗(升/百公里)
     */
    private Long loadEmptyOilCost;

    /**
     * 载重油耗(升/百公里)
     */
    private Long loadFullOilCost;

    /**
     * 归属运营部门
     */
    private Long attachedRootOrgTwoId;

    /**
     * 是否使用车辆油耗
     */
    private Integer isUseCarOilCost;

    /**
     * 上一单单号
     */
    private Long beforeOrder;

    /**
     * 当前单号
     */
    private Long currOrder;

    /**
     * 归属人
     */
    private String attachedMan;

    /**
     * 归属人ID
     */
    private String attachedManId;

    /**
     * 归属人手机号
     */
    private String attachedManPhone;

    /**
     * 合同图片ID
     */
    private Long contractPicId;

    /**
     * 合同图片URL
     */
    private String contractPicUrl;

    /**
     * 车辆图片是否通过
     */
    private Integer vehiclePictureState;

    /**
     * 行使证是否通过
     */
    private Integer drivingLicenseState;

    /**
     * 是否共享出去
     */
    private Integer shareFlg;

    /**
     * 来源租户，用于自有司机迁移
     */
    private Long sourceTenantId;

    /**
     * 状态 默认1
     */
    private Integer state;

    /**
     * 认证状态：1-未认证 2-已认证 3-认证失败
     */
    private Integer authState;

    /**
     * 审核内容
     */
    private String auditContent;

    /**
     * 车辆图片
     */
    private String vehiclePicture;

    /**
     * 行驶证
     */
    private String drivingLicense;

    /**
     * 驾驶证
     */
    private String adriverLicense;

    /**
     * 运营证
     */
    private String operCerti;

    /**
     * 行驶证编号
     */
    private String drivingLicenseSn;

    /**
     * 行驶证副本(图片保存编号)
     */
    private String adriverLicenseCopy;

    /**
     * 是否显示审核按钮
     */
    private Integer isAuth;

    /**
     * 联系人id
     */
    private Long linkUserId;

    /**
     * 司机id
     */
    private Long driverUserId;

    /**
     * 账单接收人手机号
     */
    private String billReceiverMobile;

    /**
     * 账单接收人用户编号
     */
    private Long billReceiverUserId;

    /**
     * 账单接收人姓名
     */
    private String billReceiverName;

    /**
     * 招商合同文件图片
     */
    private String investContractFilePicUrl;
    private Long investContractFilePic;

    /**
     * 挂靠合同文件图片
     */
    private String attachContractFilePicattach;

    /**
     * 司机创建时间
     */
    @JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime driverCTime;


}
