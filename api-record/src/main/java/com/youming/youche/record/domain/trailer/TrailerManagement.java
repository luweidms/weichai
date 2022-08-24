package com.youming.youche.record.domain.trailer;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 挂车管理
 * </p>
 *
 * @author Terry
 * @since 2022-01-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class TrailerManagement extends BaseDomain {

    private static final long serialVersionUID = 1L;
    /**
     *
     */
    @TableField(exist = false)
    private String trailerMaterialName;

    /**
     *0新增 1修改
     */
    private Integer trailerSate;

    /**
     * 挂车牌
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String trailerNumber;

    /**
     * 车长
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String trailerLength;

    /**
     * 车型
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String trailerStatus;

    /**
     * 吨位
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String trailerLoad;

    /**
     * 容积
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String trailerVolume;

    /**
     * 审核时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String authDate;

    /**
     * 状态 1为在途 2为在台
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer isState;

    /**
     * 操作人
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long opId;

    /**
     * 租户
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long tenantId;

    /**
     * 地址纬度
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String nand;

    /**
     * 地址经度
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String eand;

    /**
     * 始发省
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer sourceProvince;

    /**
     * 始发市
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer sourceRegion;

    /**
     * 始发县区
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer sourceCounty;

    /**
     * 到达省
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer desProvince;

    /**
     * 到达市
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer desRegion;

    /**
     * 到达县区
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer desCounty;

    /**
     * 起始地
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String source;

    /**
     * 目的地
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String des;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String carVersion;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String brandModel;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String operCerti;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String vinNo;

    /**
     * 宽
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String trailerWide;

    /**
     * 高
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String trailerHigh;

    /**
     * 桥数 1单桥、2双桥
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer trailerBridgeNumber;

    /**
     * 材质 1复合板、2铁柜、3铝柜、4冷柜、5其他、6未定
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer trailerMaterial;

    /**
     * 登记日期
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String registationTime;

    /**
     * 图片url
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String trailerPictureUrl;

    /**
     * 图片
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long trailerPictureId;

    /**
     * 登记证号
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String registrationNumble;

    /**
     * 行驶证有效期
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String vehicleValidityTime;

    /**
     * 营运证有效期
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String operateValidityTime;

    /**
     * 有效期 1:运营证 2：行驶证
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer validityTime;

    /**
     * 审核状态  0-待审核，1-审核通过，2-审核拒绝
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer isAutit;

    /**
     * 行驶证到期时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate vehicleValidityExpiredTime;

    /**
     * 运营证到期时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate operateValidityExpiredTime;

    /**
     * 运营证图片url
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String operCertiUrl;

    /**
     * 0 闲置 1禁用
     */
    private Short idle;


    @TableField(exist = false)
    private String sourceProvinceName;
    @TableField(exist = false)
    private String sourceRegionName;
    @TableField(exist = false)
    private String sourceCountyName;
    @TableField(exist = false)
    private String stateName;

}
