package com.youming.youche.record.dto;

import com.youming.youche.record.vo.VehicleLineRelsVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Date:2022/1/21
 */
@Data
public class TrailerManagementDto implements Serializable {

    /**
     * 新增和修改区分标识 1新增 2修改
     */
    private Integer isEdit;

    /**
     * 挂车牌
     */
    private String trailerNumber;

    /**
     * 车型
     */
    private String trailerStatus;

    /**
     * 1 自有,2 自有未过户,3 租赁,4 招商,5 过户其他公司,6 未定
     */
    private Integer trailerOwnerShip;

    private String trailerOwnerShipName;

    /**
     * 材质 1复合板、2铁柜、3铝柜、4冷柜、5其他、6未定
     */
    private Integer trailerMaterial;

    private String TrailerMaterialStr;

    /**
     * 载重
     */
    private String trailerLoad;

    /**
     * 容积
     */
    private String trailerVolume;

    /**
     * 车长
     */
    private String trailerLength;

    /**
     * 车宽
     */
    private String trailerWide;

    /**
     * 车高
     */
    private String trailerHigh;

    /**
     * 状态 1为在途 2为在台
     */
    private Integer isState;

    private String isStateStr;

    /**
     * 起始省份位置
     */
    private Integer sourceProvince;

    /**
     * 起始城市位置
     */
    private Integer sourceRegion;

    /**
     * 始发县区
     */
    private Integer sourceCounty;

    /**
     * 起始地
     */
    private String source;

    /**
     * 目的地
     */
    private String des;

    private String carVersion;

    private String brandModel;

    /**
     * 运营证id
     */
    private String operCerti;

    /**
     * 运营证图片url
     */
    private String operCertiUrl;


    /**
     * 线路信息
     */
    private List<VehicleLineRelsVo> vehicleLineRelsArray;


    /**
     * 车辆图片url
     */
    private String trailerPictureUrl;

    /**
     * 车辆图片id
     */
    private Long trailerPictureId;

    /**
     * 登记证号
     */
    private String registrationNumble;

    /**
     * 行驶证有效期
     */
    private String vehicleValidityTime;

    /**
     * 行驶证到期时间
     */
    private String vehicleValidityExpiredTime;

    /**
     * 营运证有效期
     */
    private String operateValidityTime;

    /**
     * 运营证到期时间
     */
    private String operateValidityExpiredTime;

    /**
     * 审核状态  0-待审核，1-审核通过，2-审核拒绝
     */
    private Integer isAutit;

    /**
     * 租户
     */
    private Long tenantId;

    /**
     * 操作人
     */
    private Long opId;

    /**
     * 归属部门id
     */
    private Long attachedRootOrgTwoId;

    /**
     * 归属人
     */
    private String attachedMan;

    /**
     * 归属人id
     */
    private Long attachedManId;

    /**
     * 购车价格
     */
    private String price;


    /**
     * 残值
     */
    private String residual;

    /**
     * 贷款利息
     */
    private String loanInterest;

    /**
     * 还款期数
     */
    private Integer interestPeriods;

    /**
     * 已还期数
     */
    private Integer payInterestPeriods;

    /**
     * 购买时间
     */
    private String purchaseDate;

    /**
     * 折旧月数
     */
    private Integer depreciatedMonth;

    /**
     * 保险费用
     */
    private Long insuranceFee;

    private String insuranceFeeStr;

    /**
     * 审车费用
     */
    private Long examVehicleFee;

    private String examVehicleFeeStr;


    /**
     * 保养费用
     */
    private Long maintainFee;

    private String maintainFeeStr;

    /**
     * 维修费用
     */
    private Long repairFee;

    private String repairFeeStr;

    /**
     * 轮胎费用
     */
    private Long tyreFee;

    private String tyreFeeStr;

    /**
     * 其他费用
     */
    private Long otherFee;

    private String otherFeeStr;

    /**
     * 年审时间开始时间
     */
    private String annualVeriTime;

    /**
     * 年审时间到期时间
     */
    private String annualVeriExpiredTime;

    /**
     * 季审时间开始时间
     */
    private String seasonalVeriTime;

    /**
     * 季审时间到期时间
     */
    private String seasonalVeriExpiredTime;


    /**
     * 商业险时间开始时间
     */
    private String businessInsuranceTime;

    /**
     * 商业险时间到期时间
     */
    private String businessInsuranceExpiredTime;

    /**
     * 商业险单号
     */
    private String businessInsuranceCode;

    /**
     * 交强险时间开始时间
     */
    private String insuranceTime;

    /**
     * 交强险时间到期时间
     */
    private String insuranceExpiredTime;

    /**
     * 交强险单号
     */
    private String insuranceCode;

    /**
     * 其他险时间开始时间
     */
    private String otherInsuranceTime;

    /**
     * 其他险时间到期时间
     */
    private String otherInsuranceExpiredTime;

    /**
     * 其他险单号
     */
    private String otherInsuranceCode;

    /**
     * 保养周期
     */
    private String maintainDis;

    private String maintainDisStr;

    /**
     * 保养预警公里
     */
    private String maintainWarnDis;

    private String maintainWarnDisStr;

    /**
     * 上次保养时间
     */
    private String prevMaintainTime;

    /**
     * 登记日期
     */
    private String registrationTime;

    /**
     * 失败原因
     */
    private String reasonFailure;

    // 起始位置
    private String trailerSourceRegionStr;

    // 归属人手机号
    private String attachedUserPhone;

    // 线路绑定
    private String lineCodeRules;
    /**
     * 挂车ID
     */
    private Long trailerId;
    /**
     * 地址纬度
     */
    private String nand;
    /**
     * 地址经度
     */
    private String eand;
    /**
     *  desProvince     目的省id
     */
    private Integer desProvince;
    /**
     * 目的城市
     */
    private Integer desRegion;
    /**
     * 到达县
     */
    private Integer desCounty;
    /**
     * 车架号
     */
    private String vinNo;

    private Integer trailerBridgeNumber;
    /**
     * 桥数 1单桥、2双桥
     */
    private Integer validityTime;
    /**
     * 关系主键
     */
    private Long relId;

    /**
     * 所属组织
     */
    private Long attachedRootOrgId;

    private String orgName;
    /**
     * 归属大区
     */
    private String remark;
    /**
     * 状态
     */
    private  Integer state;

}
