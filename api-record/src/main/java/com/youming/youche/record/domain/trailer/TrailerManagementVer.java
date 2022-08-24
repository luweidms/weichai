package com.youming.youche.record.domain.trailer;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
public class TrailerManagementVer extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 挂车编号
     */
    private Long trailerId;

    /**
     * 挂车牌
     */
    private String trailerNumber;

    /**
     * 车长
     */
    private String trailerLength;

    /**
     * 车型
     */
    private String trailerStatus;

    /**
     * 吨位
     */
    private String trailerLoad;

    /**
     * 容积
     */
    private String trailerVolume;

    private Integer isState;

    /**
     * 操作人
     */
    private Long opId;

    /**
     * 租户
     */
    private Long tenantId;

    /**
     * 地址纬度
     */
    private String nand;

    /**
     * 地址经度
     */
    private String eand;

    /**
     * 始发省
     */
    private Integer sourceProvince;

    /**
     * 始发市
     */
    private Integer sourceRegion;

    /**
     * 始发县区
     */
    private Integer sourceCounty;

    /**
     * 到达省
     */
    private Integer desProvince;

    /**
     * 到达市
     */
    private Integer desRegion;

    /**
     * 到达县区
     */
    private Integer desCounty;

    /**
     * 起始地
     */
    private String source;

    /**
     * 目的地
     */
    private String des;

    /**
     * 版本
     */
    private String craVersion;

    /**
     * 品牌型号
     */
    private String brandModel;

    /**
     * 运营证图片
     */
    private String operCerti;

    /**
     * 车架号
     */
    private String vinNo;

    /**
     * 宽
     */
    private String trailerWide;

    /**
     * 高
     */
    private String trailerHigh;

    /**
     * 桥数 1单桥、2双桥
     */
    private Integer trailerBridgeNumber;

    /**
     * 材质 1复合板、2铁柜、3铝柜、4冷柜、5其他、6未定
     */
    private Integer trailerMaterial;
    /**
     *
     */
    @TableField(exist = false)
    private String trailerMaterialName;

    /**
     * 登记日期
     */
    private String registrationTime;

    /**
     * 图片url
     */
    private String trailerPictureUrl;

    /**
     * 图片
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
     * 营运证有效期
     */
    private String operateValidityTime;

    /**
     * 有效期 1:运营证 2：行驶证
     */
    private Integer validityTime;

    /**
     * 修改操作人
     */
    private Long updateOpId;

    /**
     * 是否审核 1为是 0为否
     */
    private Integer isAutit;

    /**
     * 行驶证到期时间
     */
    private String vehicleValidityExpiredTime;

    /**
     * 运营证到期时间
     */
    private String operateValidityExpiredTime;

    /**
     * 0 闲置 1禁用
     */
    private Short idle;

    /**
     * 运营证图片url
     */
    private String operCertiUrl;

    /**
     * 来源省份名称
     */
    @TableField(exist = false)
    private String sourceProvinceName;

    /**
     * 源市名称
     */
    @TableField(exist = false)
    private String sourceRegionName;

    /**
     * 源县名
     */
    @TableField(exist = false)
    private String sourceCountyName;


}
