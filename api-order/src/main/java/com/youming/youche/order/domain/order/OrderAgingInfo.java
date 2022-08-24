package com.youming.youche.order.domain.order;

    import java.time.LocalDateTime;

    import com.baomidou.mybatisplus.annotation.TableField;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 时效罚款表
    * </p>
* @author liangyan
* @since 2022-03-22
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OrderAgingInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 订单离台时间
            */
    private LocalDateTime orderStartDate;

            /**
            * 订单到达时间
            */
    private LocalDateTime orderArriveDate;

            /**
            * 操作人
            */
    private Long opId;

            /**
            * 操作人名称
            */
    private String opName;

            /**
            * 订单号
            */
    private Long orderId;

            /**
            * 到达时限
            */
    private Long arriveTime;

            /**
            * 罚款金额
            */
    private Long finePrice;

            /**
            * 说明
            */
    private String remark;

            /**
            * 审核状态
            */
    private Integer auditSts;

            /**
            * 审核通过时间
            */
    private LocalDateTime auditDate;

            /**
            * 租户ID
            */
    private Long tenantId;

            /**
            * 时限要求
            */
    private Long arriveHour;

            /**
            * 责任人
            */
    private Long userId;

            /**
            * 责任人名称
            */
    private String userName;

            /**
            * 责任人手机
            */
    private String userPhone;

            /**
            * 线路节点
            */
    private String lineNode;

            /**
            * 始发市
            */
    private Integer sourceRegion;

            /**
            * 到达市
            */
    private Integer desRegion;

            /**
            * 起始纬度
            */
    private String nand;

            /**
            * 目的纬度
            */
    private String nandDes;

            /**
            * 起始经度
            */
    private String eand;

            /**
            * 目的经度
            */
    private String eandDes;

            /**
            * 抵扣金额
            */
    private Long deductionFee;

            /**
            * 到付抵扣金额
            */
    private Long arriveDeductionFee;

            /**
            * 尾款抵扣金额
            */
    private Long finalDeductionFee;

    @TableField(exist=false)
    private String agingStsName;
    @TableField(exist=false)
    private Integer agingSts;
    @TableField(exist=false)
    private String auditStateName;
    /**
     * 审核状态
     */
    @TableField(exist=false)
    private String auditStsName;

    public String getAuditStsName() {
        if(this.auditSts == null){
            this.auditStsName = "";
        }else if(this.auditSts == 0){
            this.auditStsName = "待审核";
        }else if(this.auditSts == 1){
            this.auditStsName = "审核中";
        }else if(this.auditSts == 2){
            this.auditStsName = "审核不通过";
        }else if(this.auditSts == 3){
            this.auditStsName = "已完成";
        }else if(this.auditSts == 8){
            this.auditStsName = "取消";
        }
        return auditStsName;
    }

    public void setAuditStsName(String auditStsName) {
        this.auditStsName = auditStsName;
    }
}
