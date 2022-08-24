package com.youming.youche.order.domain.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class BusiSubjectsRel extends BaseDomain {

    private static final long serialVersionUID = 1L;


    private Integer bookMold;

    private String bookType;

    /**
     * 业务ID
     */
    private Long businessId;

    /**
     * 业务类型
     */
    private Long businessType;

    /**
     * 优惠科目
     */
    private Long disSubjectsId;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 收入支出标识，用于志鸿账户的流水信息1支出2收入0不记录流水
     */
    private Integer rootInoutType;

    /**
     * 规则类型(1非固定值、2固定值、3比例、4实现类)
     */
    private Integer ruleType;

    /**
     * 规则值
     */
    private String ruleValue;

    /**
     * 顺序ID
     */
    private Integer sortid;

    /**
     * 状态（0无效、1有效）
     */
    private Integer status;

    /**
     * 业务科目编号
     */
    private Long subjectsId;

    /**
     * 车队id
     */
    private Long tenantId;

    /**
     * 可用金额单位
     */
    @TableField(exist = false)
    private Long amountFee;

    /**
     * 科目名称
     */
    @TableField(exist = false)
    private String subjectsName;

    /**
     * 科目类型
     */
    @TableField(exist = false)
    private Integer subjectsType;

    /**
     * 利润
     */
    @TableField(exist = false)
    private Long income;

    /**
     * 返现金额
     */
    @TableField(exist = false)
    private Long backIncome;

    /**
     * 对方用户ID
     */
    @TableField(exist = false)
    private Long otherId;

    /**
     * 对方用户
     */
    @TableField(exist = false)
    private String otherName;

    @TableField(exist = false)
    private Long incomeTenantId;

    @TableField(exist = false)
    private long poundageFee;//未到期手续费

}
