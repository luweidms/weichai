package com.youming.youche.record.dto.violation;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hzx
 * @description 车辆违章回参
 * @date 2022/1/19 21:56
 */
@Data
public class ViolationDto implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 记录id
     */
    private Long recordId;
    /**
     * 违章时间
     */
    private String violationTime;
    /**
     * 官方违章编码
     */
    private String violationCode;
    /**
     * 违章城市名
     */
    private String locationName;
    /**
     * 违章地址
     */
    private String violationAddress;
    /**
     * 罚款金额
     */
    private String violationFine;
    /**
     * 滞纳金
     */
    private String overdueFine;
    /**
     * 违章扣分
     */
    private String violationPoint;
    /**
     * 违章类型
     */
    private String category;
    /**
     * 违章文书号
     */
    private String violationWritNo;
    /**
     * 违章公私类型（1、公；2、私）
     */
    private Integer type;
    private String typeName;
    /**
     * 司机主键
     */
    private String userId;
    /**
     * 车牌号码
     */
    private String plateNumber;
    /**
     * 司机姓名
     */
    private String linkMan;
    /**
     * 手机号码
     */
    private String mobilePhone;
    /**
     * 违章记录状态（0、未处理；1、处理中；2、已完成）
     */
    private Integer recordState;
    /**
     * 违章原因
     */
    private String violationReason;
    /**
     * 违章订单ID主键
     */
    private String violationOrderId;
    /**
     * 违章缴费单号
     */
    private String violationOrderNum;
    /**
     * 违章订单状态 1待审核->2待付款->3处理中->4已完成/5已撤销/6已退单/7失效/8审核不通过
     */
    private Integer orderState;
    private String orderStateName;
    /**
     * 除了配置的服务商以外的报价数目
     */
    private String offerCount;

    /**
     * 除了配置的服务商以外的报价数目
     */
    private Boolean isOffer;

}
