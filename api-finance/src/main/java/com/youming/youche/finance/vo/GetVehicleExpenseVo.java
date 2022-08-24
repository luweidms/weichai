package com.youming.youche.finance.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class GetVehicleExpenseVo implements Serializable {


    private static final long serialVersionUID = 6917384911139252710L;

    /**
     * 申请单号
     */
    private String applyNo;

    /**
     * 车牌
     */
    private String plateNumber;

    /**
     * 状态：1待审核，2审核中，3审核通过，4审核不通过
     */
    private Integer state;

    /**
     * 状态：1待审核，2审核中，3审核通过，4审核不通过
     */
    private String stateStr;

    public String getStateStr() {
        if(this.state == null){
            this.stateStr = "待审核";
        }else if(this.state == 1){
            this.stateStr = "待审核";
        }else if(this.state == 2){
            this.stateStr = "审核中";
        }else if(this.state == 3){
            this.stateStr = "审核通过";
        }else if(this.state == 4){
            this.stateStr = "审核不通过";
        }else if(this.state == 8){
            this.stateStr = "已取消";
        }else {
            this.stateStr = "待审核";
        }

        return stateStr;
    }

    public void setStateStr(String stateStr) {
        this.stateStr = stateStr;
    }

    /**
     * 费用类型：1维修，2保养，3加油，4杂费
     */
    private Long type;

    /**
     * 费用类型：1维修，2保养，3加油，4杂费
     */
    private String typeName;
    /**
     * 司机
     */
    private String userName;
    /**
     * 司机手机号
     */
    private String linkPhone;

    /**
     * 申请人id
     */
    private Long applyId;

    /**
     * 申请人姓名
     */
    private String applyName;

    /**
     * 申请时间
     */
    private String applyTime;

    /**
     * 申请金额(分)
     */
    private Long applyAmount;

    /**
     * 申请金额(元)
     */
    private String applyAmountStr;

    /**
     * 描述
     */
    private String introduce;

    /**
     * 费用使用归属日期
     */
    private String expenseTime;

    /**
     * 附件
     */
    private String file;

    /**
     * 归属部门
     */
    private String expenseDepartment;
    /**
     * 支付方式：1:油卡，2：现金
     */
    private Integer paymentType;
    /**
     * 油卡卡号
     */
    private Long oilCardNumber;
    /**
     * 加油里程
     */
    private Long refuelingMileage;
    /**
     * 归属部门id
     */
    private Long orgId;
    /**
     * vehicle_expense关联主键id
     */
    private Long vehicleExpenseId;

    /**
     * 出车仪表公里数(米)
     */
    private Long mileageDepartureInstrument;

    /**
     * 收车仪表公里数(米)
     */
    private Long mileageReceivingInstrument;

    /**
     * 收车 url
     */
    private String endKmUrl;
    /**
     * 出车url
     */
    private String startKmUrl;
    /**
     * 判断当前登录人是否有权限审核，ture 有，false 无
     */
    private Boolean isHasPermission;


}
