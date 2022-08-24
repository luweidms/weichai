package com.youming.youche.order.domain.order;

    import java.time.LocalDateTime;

    import com.baomidou.mybatisplus.annotation.TableField;
    import com.youming.youche.commons.base.BaseDomain;
    import com.youming.youche.order.annotation.SysStaticDataInfoDict;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

    import static com.youming.youche.conts.EnumConsts.SysStaticDataAL.SYS_CITY;
    import static com.youming.youche.conts.EnumConsts.SysStaticDataAL.SYS_DISTRICT;
    import static com.youming.youche.conts.EnumConsts.SysStaticDataAL.SYS_PROVINCE;

/**
* <p>
    * 订单途径表
    * </p>
* @author CaoYaJie
* @since 2022-03-17
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderTransitLineInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;


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
    @SysStaticDataInfoDict(dictDataSource = SYS_CITY)
    private Integer region;

            /**
            * 省
            */
    @SysStaticDataInfoDict(dictDataSource = SYS_PROVINCE)
    private Integer province;

            /**
            * 县
            */
    @SysStaticDataInfoDict(dictDataSource = SYS_DISTRICT)
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
            * 排序id
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
            * 操作员id
            */
    private Long opId;

            /**
            * 租户id
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

    @TableField(exist=false)
    private String provinceName;
    @TableField(exist=false)
    private String regionName;
    @TableField(exist=false)
    private String countyName;



}
