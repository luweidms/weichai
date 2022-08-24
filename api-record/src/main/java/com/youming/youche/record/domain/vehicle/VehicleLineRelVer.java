package com.youming.youche.record.domain.vehicle;

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
 * 车辆心愿线路关系表
 * </p>
 *
 * @author Terry
 * @since 2021-12-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class VehicleLineRelVer extends BaseDomain {

    private static final long serialVersionUID = 1L;


    //@TableId(value = "his_id", type = IdType.AUTO)
   // private Long hisId;

    /**
     * 关系ID
     */
    private Long relId;

    /**
     * 车辆ID
     */
    private Long vehicleCode;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 线路ID
     */
    private Long lineId;

    /**
     * 线路类型
     */
    private Integer state;

    /**
     * 线路编号
     */
    private String lineCodeRule;

    /**
     * 操作人员
     */
    private Long opId;

    /**
     * 操作时间
     */
    private LocalDateTime opDate;

    /**
     * 修改时间
     */
    private LocalDateTime updateDate;

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
     * 是否审批成功 1-是
     */
    private Integer isAuthSucc;


}
