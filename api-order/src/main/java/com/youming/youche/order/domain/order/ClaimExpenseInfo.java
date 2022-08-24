package com.youming.youche.order.domain.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 车管报销表
 * </p>
 * @author liangyan
 * @since 2022-03-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ClaimExpenseInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 报销编号
     */
    private String specialExpenseNum;

    /**
     * 用户ID
     */
    private Long userInfoId;

    /**
     * 用户手机
     */
    private String userInfoPhone;

    /**
     * 用户名
     */
    private String userInfoName;

    /**
     * 大区
     */
    private Long bigRootId;

    /**
     * 运营区
     */
    private Long rootOrgId;

    /**
     * 项目部
     */
    private Long orgId;

    /**
     * 一级类目
     */
    private Long stairCategory;

    /**
     * 一级类目名
     */
    private String stairCategoryName;

    /**
     * 二级类目
     */
    private Long secondLevelCategory;

    /**
     * 二级类目名
     */
    private String secondLevelCategoryName;

    /**
     * 申请时间
     */
    private LocalDateTime appDate;

    /**
     * 借款原因
     */
    private String appReason;

    /**
     * 金额(单位分)
     */
    private Long amount;

    /**
     * 开户行
     */
    private String bankName;

    /**
     * 省份
     */
    private String province;

    /**
     * 地市
     */
    private String city;

    /**
     * 账号
     */
    private String accNo;

    /**
     * 帐户名
     */
    private String accName;

    /**
     * 状态（0待处理，1区总通过,2区总驳回,3财务通过(完成),4财务驳回,5取消）
     */
    private Integer state;

    /**
     * 审批部门
     */
    private Long verifyOrgId;

    /**
     * 审批人（审批人为空则为部门审批）
     */
    private Long verifyOpId;

    /**
     * 流程ＩＤ
     */
    private Long nodeId;

    /**
     * 审批时间
     */
    private LocalDateTime verifyDate;

    /**
     * 审批说明
     */
    private String verifyNotes;

    /**
     * 车牌ID
     */
    private Long vehicleCode;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 银行支行
     */
    private String bankBranch;

    /**
     * 订单编号
     */
    private Long orderId;

    /**
     * 报销人姓名
     */
    private String borrowName;

    /**
     * 报销人手机
     */
    private String borrowPhone;

    /**
     * 报销人ID
     */
    private Long borrowUserId;

    /**
     * 报销类型:1 车管 2 司机
     */
    private Integer expenseType;

    /**
     * 支付id
     */
    private Long flowId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 事故时间
     */
    private LocalDateTime accidentDate;

    /**
     * 出险时间
     */
    private LocalDateTime insuranceDate;

    /**
     * 事故类型
     */
    private Integer accidentType;

    /**
     * 事故原因
     */
    private Integer accidentReason;

    /**
     * 责任划分
     */
    private Integer dutyDivide;

    /**
     * 事故司机
     */
    private Integer accidentDivide;

    /**
     * 保险公司
     */
    private String insuranceFirm;

    /**
     * 理赔金额
     */
    private Long insuranceMoney;

    /**
     * 事故说明
     */
    private String accidentExplain;

    /**
     * 过磅重量
     */
    private Long weightFee;

    /**
     * 司机姓名
     */
    private String carOwnerName;

    /**
     * 司机手机
     */
    private String carPhone;

    /**
     * 报案号
     */
    private String reportNumber;

    /**
     * 收款账户类型：0私人账户；1对公账户
     */
    private Integer bankType;

    /**
     * 是否需要开票 0无需 1需要
     */
    private Integer isNeedBill;

    /**
     * 对私收款虚拟账户
     */
    private String collectAcctId;

    /**
     * 保险生效时间
     */
    private LocalDateTime insuranceEffectiveDate;

    /**
     * 付款方类型
     */
    private Integer payUserType;

    /**
     * 收款方类型
     */
    private Integer receUserType;

    /**
     * 报销记录最后审核人id
     */
    private Long verifyUserInfoId;

    /**
     * 报销记录最后审核人姓名
     */
    private String verifyUserName;

    /**
     * 业务流水ID
     */
    private Long expenseId;

    /**
     * 状态
     */
    private Integer sts;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户手机
     */
    private String userPhone;

    /**
     * 借支记录最后核销人id
     */
    private Long verifyUserId;


    @TableField(exist=false)
    private List<String> fileUrl;//文件url
    @TableField(exist=false)
    private List<String> fileName;//
    @TableField(exist=false)
    private List<Long> fileId;//文件id

    @TableField(exist=false)
    private String stsString;

    @TableField(exist=false)
    private String accidentTypeName;
    @TableField(exist=false)
    private String accidentReasonName;

    @TableField(exist=false)
    private String dutyDivideName;
    @TableField(exist=false)
    private String accidentDivideName;

    @TableField(exist=false)
    private  String orgName;

}
