package com.youming.youche.record.domain.vehicle;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 车辆心愿线路表
 * </p>
 *
 * @author Terry
 * @since 2021-12-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class VehicleObjectLineVer extends BaseDomain {

    private static final long serialVersionUID = 1L;


    //@TableId(value = "his_id", type = IdType.AUTO)
    private Long hisId;

    /**
     * 车辆编号
     */
    private Long vehicleCode;

    /**
     * 线路类型(line_type)
     */
    private Integer lineType;

    /**
     * 出发省
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer sourceProvince;

    /**
     * 出发市
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer sourceRegion;

    /**
     * 出发区
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer sourceCounty;

    /**
     * 目的省
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer desProvince;

    /**
     * 目的市
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer desRegion;

    /**
     * 目的区
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer desCounty;

    /**
     * 推送标志(0：不推送（缺省） 1:推送)
     */
    private Integer pushFlag;

    /**
     * 推送时间(最后一次推送时间)
     */
    private LocalDateTime pushDate;

    /**
     * 车辆类型
     */
    private String vehicleStatus;

    /**
     * 吨位
     */
    private String vehicleLoad;

    /**
     * 车长
     */
    private String vehicleLength;

    /**
     * 货物类型
     */
    private String goodsType;

    /**
     * 期望时间
     */
    private LocalDateTime expectedTime;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 承运价
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long carriagePrice;

    private Long tenantId;

    /**
     * 操作人ID
     */
    private Long opId;

    /**
     * 修改操作人
     */
    private Long updateOpId;

    /**
     * 是否有效 0无效 1有效
     */
    @TableField("STS")
    private Integer sts;

    /**
     * 0不可用 1可用 9被移除
     */
    private Integer verState;

    /**
     * 车辆主表历史id
     */
    private Long vehicleHisId;

    /**
     * 是否审批成功 1-是,3-自动审核通过
     */
    private Integer isAuthSucc;

    /**
     * 起始省
     */
    @TableField(exist = false)
    private  String sourceProvinceName;

    /**
     * 起始市
     */
    @TableField(exist = false)
    private  String  sourceRegionName;

    /**
     * 起始区、县
     */
    @TableField(exist = false)
    private  String sourceCountyName;

    /**
     * 目的省
     */
    @TableField(exist = false)
    private  String desProvinceName;

    /**
     * 目的市
     */
    @TableField(exist = false)
    private  String desRegionName;

    /**
     * 目的区、县
     */
    @TableField(exist = false)
    private  String desCountyName;
}
