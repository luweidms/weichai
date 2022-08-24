package com.youming.youche.market.vo.maintenanceaudit;

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
 * @author CaoYaJie
 * @since 2022-01-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class AccountBankUserTypeRelVo extends BaseDomain {

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

    private Long acctId;

    private String acctName;

    private String bankId;

    private String bankName;

    private String billid;

    private Integer bindUserType;

    private String branchId;

    private String branchName;

    private String businessLicenseNo;

    private String cityName;

    private Long cityid;


    private String districtName;

    private Long districtid;

    private String identification;

    private Integer isCollectAmount;

    private Integer isDefaultAcct;


    private String pinganCollectAcctId;

    private String pinganMoutId;

    private String pinganNoutId;

    private String pinganPayAcctId;

    private String provinceName;

    private Long provinceid;

    private Integer sts;

    private LocalDateTime stsDate;

    private Long tenantId;


    private Long updateOpId;


    private String wechatAccount;

    private Long weqrCodeId;

    private String weqrCodeUrl;


}
