package com.youming.youche.finance.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 订单途径表
 *
 * @author Terry
 * @since 2021-11-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderTransitLineInfo extends BaseDomain {
    /**
     * 主键
     */
    private Long id;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 地址纬度
     */
    private String nand;

    /**
     * 地址经度
     */
    private String eand;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 文字描述目的地址，不填写默认使用百度定位地址
     */
    private String navigatLocation;

    /**
     * 市
     */
    private Integer region;

    /**
     * 省
     */
    private Integer province;

    /**
     * 县
     */
    private Integer county;

    /**
     * 到达时限
     */
    private Float arriveTime;

    /**
     * 距离
     */
    private Long distance;

    /**
     * 排序ID
     */
    private Integer sortId;

    /**
     * 靠台时间
     */
    private LocalDateTime carDependDate;

    /**
     * 离台时间
     */
    private LocalDateTime carStartDate;

    /**
     * 操作员ID
     */
    private Long opId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 完成时间
     */
    private LocalDateTime endDate;

    /**
     * 经停点类型 1:装货地 2:卸货点
     */
    private Integer pointType;

}
