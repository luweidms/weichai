package com.youming.youche.record.dto.trailer;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;

@Data
public class DateCostDto implements Serializable {
    private Long id;
    /**
     * 车牌号类型
     */
    private Integer licenceType;

    private String licenceTypeName;
    public String getLicenceTypeName(){
        switch (licenceType){
            case 1:
                this.setLicenceTypeName("整车");
                break;
            case 2:
                this.setLicenceTypeName("拖头");
                break;
            case 3:
                this.setLicenceTypeName("挂车");
                break;
        }
        return this.licenceTypeName;
    }

    /**
     * 车辆品牌
     */
    private String brandModel;
    /**
     * 资产名称
     */
    private String plateNumber;

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
    private String zcjzze;
    /**
     * 剩余折旧期数
     */
    private Integer syzjqsze;

    /**
     * 资产单月折旧
     */
    private String zcdyzjze;
    /**
     * 当前折旧月数
     */
    private Integer dqzjys;
    /**
     * 当前折旧月数
     */
    private String purchaseDate;
    /**
     * 折旧月数
     */
    private Integer depreciatedMonth;
    /**
     * 原价
     */
    private String price;
    /**
     * 残值
     */
    private String residual;
    /**
     * 车型
     */
    private String vehicleLength;
    private String vehicleLengthName;

    /**
     * 车辆类型 / 车型
     */
    private String vehicleInfo;

    /**
     * 车辆类型
     */
    private Integer vehicleStatus;
    private String vehicleStatusName;

    /**
     * 品牌型号
     */
    private String ppxh;

    /**
     * 材质
     */
    private String caizhi;
    /**
     * 容积
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Double rj;

    private String rjName;
    public String getRjName(){
        DecimalFormat df = new DecimalFormat("#");
        if(this.rj !=null) {
            this.setRjName(df.format(rj) + "立方米");
        }
        return this.rjName;
    }
    /**
     * 发动机号
     */
    private String engineNo;
    /**
     * 车架号
     */
    private String vinNo;

public Double getRj(){
    if (rj==null){
        return 0d;
    }
    return rj;
}

}
