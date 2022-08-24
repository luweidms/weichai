package com.youming.youche.record.domain.violation;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 违章列表
 * </p>
 *
 * @author Terry
 * @since 2022-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ViolationRecord extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 车辆主键
     */
    private Long vehicleCode;

    /**
     * 车牌号码
     */
    private String plateNumber;

    /**
     * 车牌种类(01、大型汽车；15:、挂车)
     */
    private String plateType;

    /**
     * 司机主键
     */
    private Long userId;

    /**
     * 司机姓名
     */
    private String linkMan;

    /**
     * 手机号码
     */
    private String mobilePhone;

    /**
     * 订单ID（不是缴费单，是平台的运输单）
     */
    private Long orderId;

    /**
     * 车队ID
     */
    private Long tenantId;

    /**
     * 归属车队名字
     */
    private String tenantName;

    /**
     * 违章公私类型（1、公；2、私）
     */
    private Integer type;

    /**
     * 车架号
     */
    private String vinNo;

    /**
     * 发动机号
     */
    private String engineNo;

    /**
     * 违章时间
     */
    private String violationTime;

    /**
     * 违章地址
     */
    private String violationAddress;

    /**
     * 违章原因
     */
    private String violationReason;

    /**
     * 罚款金额
     */
    private Long violationFine;

    /**
     * 滞纳金
     */
    private Long overdueFine;

    /**
     * 违章扣分
     */
    private Integer violationPoint;

    /**
     * 官方违章编码
     */
    private String violationCode;

    /**
     * 违章文书号
     */
    private String violationWritNo;

    /**
     * 违章类型（1、电子眼；2、现场单；3、已处理未缴费）
     */
    private Integer categoryId;

    /**
     * 违章类型
     */
    private String category;

    /**
     * 违章城市编码
     */
    private Integer locationid;

    /**
     * 违章城市名
     */
    private String locationName;

    /**
     * 操作人ID
     */
    private Long opId;

    /**
     * 是否经验短信通知
     */
    private Boolean alreadyNotify;

    /**
     * 第一次app推送时间
     */
    private String appNotifyDate;

    /**
     * 违章记录状态（0、未处理；1、处理中；2、已完成）
     */
    private Integer recordState;

    /**
     * 违章ID
     */
    private String violationId;

    /**
     * 认罚时间
     */
    private String renfaTime;

    /**
     * 是否需要驾驶证号码 1表示需要  0表示不需要
     */
    private String driveNo;

    /**
     * 现场单获取类型:1手动添加
     */
    private Integer accessType;

    /**
     * 罚单图片ID
     */
    private Long fineImageId;

    /**
     * 罚单图片URL
     */
    private String fineImageUrl;


}
