package com.youming.youche.record.dto.trailer;

import lombok.Data;

import java.io.Serializable;
@Data
public class TrailerGuaPeiZhiDto implements Serializable {
    private Integer id;
    /**
     * 挂车牌号类型
     */
    private String licenceType;
    /**
     * 车辆品牌
     */
    private String brandModel;
    /**
     * 资产名称
     */

    private String trailerNumber;
    /**
     * 车辆数量
     */
    private int count;
    /**
     * 车辆状态
     */
    private String states;
    /**
     * 车辆性质
     */
    private String xingzhi;
    /**
     * 资产净值
     */
    private Long zcjzze;
    /**
     * 剩余折旧期数
     */
    private Integer syzjqsze;

    /**
     * 资产单月折旧
     */
    private Long zcdyzjze;
    /**
     * 车型
     */
    private String trailer_status;
    /**
     * 品牌型号
     */
    private String ppxh;
    /**
     * '材质 1复合板、2铁柜、3铝柜、4冷柜、5其他、6未定',
     */
    private Integer trailerMaterial;
    /**
     * 容积
     */
    private Double trailerVolume;
    /**
     * 发动机号
     */
    private String engineNo;
    /**
     * 车架号
     */
    private String vinNo;

    /**
     * 车长
     */
    private String vehicleLengthName;

    /**
     * 车辆类型 / 车型
     */
    private String vehicleInfo;

}
