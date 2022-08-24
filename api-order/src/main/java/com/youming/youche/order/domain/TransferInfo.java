package com.youming.youche.order.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author liangyan
* @since 2022-03-14
*/
    @Data
    @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    @TableName("order_transfer_info")
    public class TransferInfo extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;


            /**
            * 接单时间
            */
    private LocalDateTime acceptDate;
    /**
     * 接单车队ID
     */
    private String acceptTenantId;

            /**
            * 接单车队
            */
    private String acceptTenantName;

            /**
            * 司机ID
            */
    private Long carDriverId;

            /**
            * 司机姓名
            */
    private String carDriverMan;

            /**
            * 司机手机
            */
    private String carDriverPhone;

            /**
            * 现金比例
            */
    private Float cashScale;

            /**
            * 到达市
            */
    private String desRegion;

            /**
            * etc使用比例
            */
    private Float etcScale;

            /**
            * 商品名称
            */
    private String goodsName;

            /**
            * 是否清空(0 : 未清空 1 : 已清空)
            */
    private Integer isEmpty;

            /**
            * 油卡使用比例
            */
    private Float oilScale;

            /**
            * 创建人
            */
    private Long opId;

            /**
            * 原始订单号
            */
    private Long orderId;

            /**
            * 接单方的订单状态
            */
    private Integer orderState;

    /**
     * 接单方的订单状态(不确定)
     */
    @TableField(exist=false)
    private String orderStateName;

    /**
            * 车牌号
            */
    private String plateNumber;

            /**
            * 接单的备注的原因
            */
    private String remark;

            /**
            * 催单的次数
            */
    private Integer reminderCount;

            /**
            * 催单的最后一次时间
            */
    private LocalDateTime reminderDate;

            /**
            * 始发市
            */
    private String sourceRegion;

            /**
            * 货物体积
            */
    private Float square;

            /**
            * 中标价
            */
    private Long totalFee;

            /**
            * 转移时间
            */
    private LocalDateTime transferDate;

            /**
            * 转移后订单号
            */
    private Long transferOrderId;

            /**
            * 转单状态(0待接单 1 已接单 2 已拒接 3 已超时)
            */
    private Integer transferOrderState;

    /**
     * 转单状态(0待接单 1 已接单 2 已拒接 3 已超时)
     */
    @TableField(exist=false)
    private String transferOrderStateName;

    public String getTransferOrderStateName() {
        if(this.transferOrderState == 0){
            this.transferOrderStateName = "待接单";
        }else if(this.transferOrderState == 1){
            this.transferOrderStateName = "已接单";
        }else if(this.transferOrderState == 2){
            this.transferOrderStateName = "已拒接";
        }else if(this.transferOrderState == 3){
            this.transferOrderStateName = "已超时";
        }
        return transferOrderStateName;
    }

    public void setTransferOrderStateName(String transferOrderStateName) {
        this.transferOrderStateName = transferOrderStateName;
    }

    /**
            * 转单车队(租户)
            */
    private String transferTenantName;

            /**
            * 转单车队ID
            */
    private Long transferTenantTenantId;

            /**
            * 修改人
            */
    private Long updateOpId;

            /**
            * 车辆长度
            */
    private String vehicleLengh;

            /**
            * 车辆类型
            */
    private Integer vehicleStatus;

            /**
            * 货物重量KG
            */
    private Float weight;

    /**
     * 车辆类别 1自有公司车 2招商挂靠车 3临时外调车 4外来挂靠车 5外调合同车
     */
    @TableField(exist=false)
    private Integer vehicleClass;

    /**
     * 车辆编号
     */
    @TableField(exist=false)
    private Long vehicleCode;


}
