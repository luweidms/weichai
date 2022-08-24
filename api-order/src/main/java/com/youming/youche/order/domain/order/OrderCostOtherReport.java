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
 *
 * @author xxx
 * @since 2022-03-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderCostOtherReport extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 卡号
     */
    private String cardNo;
    /**
     * 审核备注
     */
    private String auditRemark;
    /**
     * 消费金额，单位分
     */
    private Long consumeFee;

    @TableField(exist = false)
    private String consumeFeeStr;
    /**
     * 状态名称
     */
    @TableField(exist = false)
    private String stateName;
    /**
     * 消费数量
     */
    private Long consumeNum;
    /**
     * 附件1ID
     */
    private Long fileId1;
    /**
     * 附件2ID
     */
    private Long fileId2;
    /**
     * 附件3ID
     */
    private Long fileId3;
    /**
     * 附件4ID
     */
    private Long fileId4;
    /**
     * 附件5ID
     */
    private Long fileId5;
    /**
     * 文件1url
     */
    private String fileUrl1;
    /**
     * 文件2url
     */
    private String fileUrl2;
    /**
     * 文件3url
     */
    private String fileUrl3;
    /**
     * 文件4url
     */
    private String fileUrl4;
    /**
     * 文件5url
     */
    private String fileUrl5;
    /**
     * 是否可以审核 0否 1是 枚举SysStaticDataEnum.IS_AUTH
     */
    private Integer isAudit;
    /**
     * 订单ID
     */
    private Long orderId;
    /**
     * 单价，单位分
     */
    private Long priceUnit;
    /**
     * 主表ID
     */
    private Long relId;
    /**
     * 备注
     */
    private String remark;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 提交时间
     */
    private LocalDateTime subTime;
    /**
     * 提交人
     */
    private Long subUserId;
    /**
     * 提交人名称
     */
    private String subUserName;
    /**
     * 租户ID
     */
    private Long tenantId;
    /**
     * 消费类型ID
     */
    private Long typeId;
    /**
     * 消费类型名称
     */
    private String typeName;
    /**
     * 司机ID
     */
    private Long userId;

    /**
     * 加油升数
     */
    private Double oilRise;

    /**
     * 付款方式 1:油卡 2:现金 3:ETC卡
     */
    private Integer paymentWay;

    /**
     * 数据类型 1油费 2其他
     */
    private Integer tableType;

    /**
     * 油耗里程
     */
    private Long oilMileage;

    @TableField(exist = false)
    private String  oilMileageStr;

    /**
     * 临时油站名称
     */
    private String oilProductName;

    /**
     * 部门id
     */
    private Long orgId;


}
