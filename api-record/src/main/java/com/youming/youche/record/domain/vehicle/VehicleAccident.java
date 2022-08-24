package com.youming.youche.record.domain.vehicle;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author hzx
 * @description 车辆事故
 * @date 2022/1/15 16:07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class VehicleAccident extends BaseDomain {

    /**
     * 主键
     */
    private Long id;

    /**
     * 车辆id
     */
    private Long vehicleId;

    /**
     * 车牌号
     */
    private String vehicleCode;

    /**
     * 事故状态 （1、已登记、2、已维修、3、已核赔）
     */
    private String accidentStatus;
    @TableField(exist = false)
    private String accidentStatusName;

    /**
     * 报案日期
     */
    private String reportDate;

    /**
     * 部门id
     */
    private Long orgId;

    /**
     * 用户id
     */
    private Long userDataId;

    /**
     * 事故描述
     */
    private String accidentDescription;

    /**
     * 相关照片
     */
    private String relatedPhotos;

    /**
     * 保险公司
     */
    private String insuranceCompany;

    /**
     * 被保险人
     */
    private String insured;

    /**
     * 理赔金额
     */
    private String claimAmount;

    /**
     * 理赔时间
     */
    private String claimDate;

    /**
     * 出险日期
     */
    private String accidentDate;

    /**
     * 出险地点
     */
    private String accidentPlace;

    /**
     * 事故原因
     */
    private String accidentCause;

    /**
     * 事故类型
     */
    private String accidentType;
    @TableField(exist = false)
    private String accidentTypeName;

    /**
     * 本车车损
     */
    private String vehicleDamage;

    /**
     * 对方车损
     */
    private String otherDamage;

    /**
     * 道路损款
     */
    private String roadLoss;

    /**
     * 是否物损
     */
    @TableField(exist = false)
    private String materialDamage;
    @TableField(exist = false)
    private String materialDamageName;

    /**
     * 是否伤人
     */
    private String wounding;
    @TableField(exist = false)
    private String woundingName;

    /**
     * 事故司机
     */
    private String accidentDriver;
    /**
     * 主驾
     */
    private String mainDriver;

    /**
     * 车辆品牌型号
     */
    @TableField(exist = false)
    private String brandModel;

    /**
     * 车辆品牌
     */
    @TableField(exist = false)
    private String brandName;

    /**
     * 车辆型号
     */
    @TableField(exist = false)
    private String brandType;

    /**
     * 归属部门
     */
    @TableField(exist = false)
    private String orgName;

    /**
     * 车队id
     */
    private Long tenantId;

    @TableField(exist = false)
    private String createDate;

    public String getAccidentTypeName() {
        if ("1".equals(accidentType)) {
            accidentTypeName = "直行事故";
        } else if ("2".equals(accidentType)) {
            accidentTypeName = "追尾事故";
        } else if ("3".equals(accidentType)) {
            accidentTypeName = "超车事故";
        } else if ("4".equals(accidentType)) {
            accidentTypeName = "左转弯事故";
        } else if ("5".equals(accidentType)) {
            accidentTypeName = "右转变事故";
        } else if ("6".equals(accidentType)) {
            accidentTypeName = "窄道事故";
        } else if ("7".equals(accidentType)) {
            accidentTypeName = "弯道事故";
        } else if ("8".equals(accidentType)) {
            accidentTypeName = "坡道事故";
        } else if ("9".equals(accidentType)) {
            accidentTypeName = "会车事故";
        } else if ("10".equals(accidentType)) {
            accidentTypeName = "超车事故";
        } else if ("11".equals(accidentType)) {
            accidentTypeName = "停车事故";
        }
        return accidentTypeName;
    }

    public String getMaterialDamageName() {
        if ("1".equals(materialDamage)) {
            materialDamageName = "是";
        } else if ("0".equals(materialDamage)) {
            materialDamageName = "否";
        }
        return materialDamageName;
    }

    public String getWoundingName() {
        if ("1".equals(wounding)) {
            woundingName = "是";
        } else if ("0".equals(wounding)) {
            woundingName = "否";
        }
        return woundingName;
    }

    public String getAccidentStatusName() {
        if ("1".equals(accidentStatus)) {
            this.accidentStatusName = "已登记";
        } else if ("2".equals(accidentStatus)) {
            this.accidentStatusName = "已维修";
        } else if ("3".equals(accidentStatus)) {
            this.accidentStatusName = "已核赔";
        }
        return accidentStatusName;
    }

    public String getBrandModel() {
        return brandModel;
    }

    public void setBrandModel(String brandModel) {
        this.brandModel = brandName + "" + brandType;
    }
}
