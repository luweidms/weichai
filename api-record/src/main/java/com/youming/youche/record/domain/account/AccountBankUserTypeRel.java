package com.youming.youche.record.domain.account;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 银行卡跟用户类型的关系表
 * </p>
 *
 * @author Terry
 * @since 2021-12-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AccountBankUserTypeRel extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 用户id
     */
    private Long userId;

    /**
     * 银行卡信息表的主键
     */
    private Long bankRelId;

    /**
     * 银行卡号
     */
    private String acctNo;

    /**
     * 操作类型：1 表示可以修改 2 表示可以查看
     */
    private Integer opType;

    /**
     * 账户类型：0 个人 1 对公
     */
    private Integer bankType;

    /**
     * 用户类型：1 员工 2 服务商 3 司机 6 超管 7 收款人
     */
    private Integer userType;

    /**
     * 是否默认账户 0 否  1 是
     */
    private Integer isDefault;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 更新时间
     */
    private LocalDateTime updateDate;

    /**
     * 操作人id
     */
    private Long opId;


}
