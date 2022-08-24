package com.youming.youche.record.domain.cm;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 客户线路经停点表
 * </p>
 *
 * @author 向子俊
 * @since 2022-01-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CmCustomerLineSubway extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 线路ID
     */
    private Long lineId;

    /**
     * 客户编号
     */
    private Long customerId;

    /**
     * 目的省份ID
     */
    private Integer desProvince;

    /**
     * 目的市编码ID
     */
    private Integer desCity;

    /**
     * 目的县区ID
     */
    private Integer desCounty;

    /**
     * 目的地经度
     */
    private String desEnd;

    /**
     * 目的地纬度
     */
    private String desNand;

    /**
     * 目的地详细地址
     */
    private String desAddress;

    /**
     * 经停顺序
     */
    private Integer seq;

    /**
     * 文字描述目的地址，不填写默认使用百度定位地址
     */
    private String navigatDesLocation;

    /**
     * 到达时限（小时）
     */
    private Float arriveTime;

    /**
     * 公里数
     */
    private Integer mileageNumber;

    /**
     * 省份名称
     */
    @TableField(exist = false)
    private String desProvinceName;

    /**
     * 市名称
     */
    @TableField(exist = false)
    private String desRegionName;

    /**
     * 区域名称
     */
    @TableField(exist = false)
    private String desCountyName;

}
