package com.youming.youche.finance.domain.voucher;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 代金券信息表
 * </p>
 *
 * @author hzx
 * @since 2022-04-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class VoucherInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 来源服务商id
     */
    private Long serviceUserId;

    /**
     * 服务商名称
     */
    private String serviceName;

    /**
     * 代金券类型：1油卡
     */
    private Integer voucherType;

    /**
     * 金额（单位分）
     */
    private Long amount;

    /**
     * 已用金额（单位分）
     */
    private Long paidAmount;

    /**
     * 未用金额（单位分）
     */
    private Long noPayAmount;

    /**
     * 代金券归属月份
     */
    private String voucherMonth;

    /**
     * 赠送时间
     */
    private LocalDateTime giveDate;

    /**
     * 截止失效时间
     */
    private LocalDateTime invalidDate;

    /**
     * 代金券来源产品id
     */
    private Long productId;

    /**
     * 使用范围：1中石油,2中石化
     */
    private Integer useRange;

    /**
     * 状态：0无效、1有效、2已过期
     */
    private Integer state;

    /**
     * 代金券归属租户id
     */
    private Long tenantId;

    /**
     * 代金券归属租户名称
     */
    private String tenantName;

    /**
     * 车队账户
     */
    private String tenantBill;

    /**
     * 流水号
     */
    private Long soNbr;

    /**
     * 操作员id
     */
    private Long opId;

    /**
     * 修改操作员id
     */
    private Long updateOpId;

    /**
     * null初始化，1成功，2失败
     */
    private Integer isReport;

    /**
     * 处理描述
     */
    private String reportRemark;

    private LocalDateTime reportTime;


}
