package com.youming.youche.finance.domain.ac;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 申请开票记录表
 * </p>
 *
 * @author hzx
 * @since 2022-04-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ApplyOpenBill extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 记录类型：1主申请记录，2抵扣票记录
     */
    private Integer flowType;

    /**
     * 上级流水号
     */
    private Long parentFlowId;

    /**
     * 申请号
     */
    private String applyNum;

    /**
     * 申请方用户id
     */
    private Long applyUserId;

    /**
     * 申请方
     */
    private String applyUserName;

    /**
     * 订单数
     */
    private Integer orderNum;

    /**
     * 业务金额（单位分）
     */
    private Long busiAmount;

    /**
     * 开票服务费（单位分）
     */
    private Long billServiceAmount;

    /**
     * 开票总金额 = 业务金额 + 开票服务费（单位分）
     */
    private Long openBillAmount;

    /**
     * 票据成本（单位分）
     */
    private Long billCost;

    /**
     * 开票方用户id
     */
    private Long openUserId;

    /**
     * 开票方联系电话
     */
    private String openUserPhone;

    /**
     * 开票方
     */
    private String openUserName;

    /**
     * 抵扣票数量
     */
    private Integer deductionNum;

    /**
     * 发票号,多个发票号 英文逗号分隔(,)
     */
    private String invoiceNumber;

    /**
     * 发票抬头
     */
    private String invoiceTitle;

    /**
     * 税号
     */
    private String dutyParagraph;

    /**
     * 开户银行
     */
    private String bankName;

    /**
     * 银行账户
     */
    private String acctNo;

    /**
     * 联系电话
     */
    private String telPhone;

    /**
     * 注册住所
     */
    private String registeredAddress;

    /**
     * 邮寄地址
     */
    private String mailAddress;

    /**
     * 快递公司
     */
    private String expressCompany;

    /**
     * 快递单号
     */
    private String expressNumber;

    /**
     * 票据类型：1非平台票，2平台票，3油卡消费发票
     */
    private Integer billType;

    /**
     * 申请状态：平台开票：1待支付>2付款中>3开票中>4待确认收票>5已收票，非平台开票,3开票中>4待确认收票>5已收票
     */
    private Integer applyState;

    /**
     * 开票状态：1未开票>2已开票>3开票完成
     */
    private Integer billState;

    /**
     * 主申请方租户id
     */
    private Long tenantId;

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

    /**
     * 同步支付中心状态：null初始化，1成功，2失败
     */
    private Integer synState;

    /**
     * 同步支付中心处理描述
     */
    private String synRemark;

    /**
     * 同步时间
     */
    private LocalDateTime synTime;

    /**
     * 平台提成=服务费 * 票据平台设置的百分比
     */
    private Long platformRoyalty;

    /**
     * 开票率：如6.12%则记成6.12
     */
    private Double billRate;

    /**
     * 数据是否有效：0失效，1有效
     */
    private Integer state;

    /**
     * 托运协议url
     */
    private String consignmentAgreementUrl;

    /**
     * 开票记录编码
     */
    private String invoiceRecordCode;

    /**
     * 核准金额（单位分）
     */
    private Long approvalAmount;

    /**
     * 联系人
     */
    private String contacts;

    /**
     * 核准状态：0未核准，1已核准
     */
    private Integer approvalState;

    /**
     * 平台服务费打款成功时间
     */
    private LocalDateTime payTime;


}
