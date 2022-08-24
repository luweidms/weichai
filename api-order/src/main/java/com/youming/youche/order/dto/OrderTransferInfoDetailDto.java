package com.youming.youche.order.dto;

import com.youming.youche.order.annotation.SysStaticDataInfoDict;
import com.youming.youche.order.domain.order.OrderTransitLineInfo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import static com.youming.youche.conts.EnumConsts.SysStaticDataAL.SYS_CITY;
import static com.youming.youche.conts.EnumConsts.SysStaticDataAL.SYS_PROVINCE;

/**
 * @author: luona
 * @date: 2022/5/18
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class OrderTransferInfoDetailDto implements Serializable {
    private String sourceRegion;
    private String desRegion;
    /**
     * 订单号
     */
    private Long orderId;
    /**
     * 车队名称
     */
    private String tenantName;
    /**
     * 联系人手机
     */
    private String linkPhone;
    /**
     * 靠台时间
     */
    private LocalDateTime dependTime;
    /**
     * 到达时限
     */
    private Float arriveTime;
    private String source;
    private String des;
    private Long distance;
    private String goodsName;
    private Float weight;
    private Float square;
    private String vehicleLengh;
    @SysStaticDataInfoDict(dictDataSource = "VEHICLE_STATUS@ORD")
    private Integer vehicleStatus;
    private String localUserName;
    private String localPhone;
    private Long insuranceFee;
    private Long preCashFee;
    private Long preOilVirtualFee;
    private Long preOilFee;
    private Long preEtcFee;
    private Long finalFee;
    private Long totalFee;
    private String plateNumber;
    private String carDriverMan;
    private String carDriverPhone;
    private Long sourceId;
    /**
     * 承运方公里数
     */
    private Integer mileageNumber;
    private List<OrderTransitLineInfo> transitLineInfos;
    private String reciveAddr;
    /**
     * 省份id
     */
    private Integer provinceId;
    private Integer cityId;
    /**
     * 省份
     */
    private String provinceName;
    /**
     * 市
     */
    private String cityName;
    private String eand;
    private String nand;
    private String eandDes;
    private String nandDes;
    /**
     * 始发省份
     */
    @SysStaticDataInfoDict(dictDataSource = SYS_PROVINCE)
    private Integer sourceProvince;
    /**
     * 到达时限
     */
    private LocalDateTime arriveTimeStr;
    @SysStaticDataInfoDict(dictDataSource = SYS_CITY)
    private Integer sourceRegionId;

}
