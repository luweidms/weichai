package com.youming.youche.record.domain.cm;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * <p>
 * 客户信息表/客户档案表
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CmCustomerInfo extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 公司名称(全称)
     */
    private String companyName;

    /**
     * 客户简称
     */
    private String customerName;

    /**
     * 公司地址
     */
    private String address;

    /**
     * 联系人名称
     */
    private String lineName;

    /**
     * 联系电话
     */
    private String lineTel;

    /**
     * 联系手机
     */
    private String linePhone;

    /**
     * 市场部门
     */
    private Long saleDaparment;

    /**
     * 发票抬头
     */
    private String lookupName;

    /**
     * 税号
     */
    private String einNumber;

    /**
     * 支付方式(对应静态数据的 code_type= PAY_WAY)
     */
    private Integer payWay;

    /**
     * KA代表
     */
    private String maretSale;

    /**
     * 客户等级(对应静态数据的 code_type= CUSTOMER_LEVEL)
     */
    private String customerLevel;

    /**
     * 回单类型(对应静态数据的 code_type=RECIVE_TYPE)
     */
    private Integer oddWay;

    /**
     * 数据有效状态0：无效，1有效
     */
    private Integer state;

    /**
     * 创建网点ID
     */
    private Long orgId;

    /**
     * 创建租户ID
     */
    private Long tenantId;

    /**
     * 操作人ID
     */
    private Long opId;

    /**
     * 回单期限（天）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer reciveTime;

    /**
     * 对账日（天）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer recondTime;

    /**
     * 开票期限（天）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer invoiceTime;

    /**
     * 收款期限（天）
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer collectionTime;

    /**
     * 结算周期天(这个未用（使用收款期限（天） 字段）)
     */
    private Integer settleCycle;

    /**
     * 用友编码
     */
    private String yongyouCode;

    /**
     * 客户归类
     */
    private String customerCategory;

    /**
     * 时效罚款规则(对应静态数据 code_type=LAST_FEE_RULES 默认为4其他客户)
     */
    private Integer ageFineRule;

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 认证状态：1-未认证 2-已认证 3-认证失败
     */
    private Integer authState;

    /**
     * 弃用
     */
    @TableField(exist = false)
    private Integer status;

    /**
     * 审核内容
     */
    private String auditContent;

    /**
     * 0-不在客户档案展示数据 1-在客户档案展示的数据
     */
    private Integer type;

    /**
     * 0否 1是
     */
    private Integer isAuth;

    /**
     * 回单地址-省
     */
    private Long reciveProvinceId;

    /**
     * 回单地址-市
     */
    private Long reciveCityId;

    /**
     * 回单详细地址
     */
    private String reciveAddress;

    /**
     * 对账期限
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer reconciliationTime;

    /**
     * 对账期限月
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer reconciliationMonth;

    /**
     * 对账期限日
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer reconciliationDay;

    /**
     * 回单期限月
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer reciveMonth;

    /**
     * 回单期限日
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer reciveDay;

    /**
     * 开票期限月
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer invoiceMonth;

    /**
     * 开票期限日
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer invoiceDay;

    /**
     * 收款期限月
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer collectionMonth;

    /**
     * 收款期限日
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer collectionDay;

    /**
     * 电话号码
     */
    @TableField(exist = false)
    private Integer lineNumber;

    /**
     * 静态编码名称
     */
    @TableField(exist = false)
    private String oddWayName;

    /**
     * 静态编码名称
     */
    @TableField(exist = false)
    private String payWayName;

    /**
     * 状态 1、有效 其他、无效
     */
    @TableField(exist = false)
    private String stateName;

    /**
     * 接收省份名称
     */
    @TableField(exist = false)
    private String reciveProvinceName;

    /**
     * 接收城市名称
     */
    @TableField(exist = false)
    private String reciveCityName;

    /**
     * 部门名称
     */
    @TableField(exist = false)
    private String orgName;

    /**
     * 组织名称
     */
    @TableField(exist = false)
    private String saleDaparmentName;


}
