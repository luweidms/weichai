package com.youming.youche.order.domain.order;

    import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author CaoYaJie
* @since 2022-03-21
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderTransitLineInfoVer extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 地址
     */
    private String address;
    /**
     * 运行时效
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
     * 县区
     */
    private Integer county;
    /**
     * 行驶距离
     */
    private Long distance;
    /**
     * 起始经度
     */
    private String eand;
    /**
     * 是否修改
     */
    private Integer isUpdate;
    /**
     * 地址纬度
     */
    private String nand;
    /**
     * 文字描述目的地址，不填写默认使用百度定位地址
     */
    private String navigatLocation;
    /**
     * 操作员id
     */
    private Long opId;
    /**
     * 订单号
     */
    private Long orderId;
    /**
     * 经停点类型 1:装货地 2:卸货点
     */
    private Integer pointType;
    /**
     * 省份
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
     * 车队id
     */
    private Long tenantId;
    /**
     * 省份名称
     */
    @TableField(exist=false)
    private String provinceName;
    /**
     * 地区名称
     */
    @TableField(exist=false)
    private String regionName;
    /**
     * 县名称
     */
    @TableField(exist=false)
    private String countyName;


}
