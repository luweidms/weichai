package com.youming.youche.order.domain.order;

    import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
<<<<<<< HEAD
* @author CaoYaJie
* @since 2022-03-21
=======
* @author qyf
* @since 2022-03-19
>>>>>>> origin/feature/order
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderTransitLineInfoH extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 邮寄地址
     */
    private String address;
    /**
     * 到达时限
     */
    private Float arriveTime;
    /**
     * 靠台时间
     */
    private LocalDateTime carDependDate;
    /**
     * 实际离台时间
     */
    private LocalDateTime carStartDate;
    /**
     * 县
     */
    private Integer county;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 行驶距离
     */
    private Long distance;
    /**
     * 经度
     */
    private String eand;
    /**
     * 完成时间
     */
    private LocalDateTime endDate;
    /**
     * 出发地纬度
     */
    private String nand;
    /**
     * 文字描述目的地址，不填写默认使用百度定位地址
     */
    private String navigatLocation;
    /**
     * 操作人
     */
    private Long opId;
    /**
     * 订单ID
     */
    private Long orderId;
    /**
     * 经停点类型 1:装货地 2:卸货点
     */
    private Integer pointType;
    /**
     * 省
     */
    private Integer province;
    /**
     * 地区
     */
    private Integer region;
    /**
     * 顺序ID
     */
    private Integer sortId;
    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;



}
