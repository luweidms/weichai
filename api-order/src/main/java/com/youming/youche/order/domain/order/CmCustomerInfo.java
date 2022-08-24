package com.youming.youche.order.domain.order;

    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 客户信息表/客户档案表
    * </p>
* @author CaoYaJie
* @since 2022-03-21
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class CmCustomerInfo extends BaseDomain {

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
     * 联系人电话
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
    private Integer reciveTime;

            /**
            * 对账日（天）
            */
    private Integer recondTime;

            /**
            * 开票期限（天）
            */
    private Integer invoiceTime;

            /**
            * 收款期限（天）
            */
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
            * 认证状态：1-未认证  2-已认证 3-认证失败
            */
    private Integer authState;

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
    private Integer reconciliationTime;

            /**
            * 对账期限月
            */
    private Integer reconciliationMonth;

            /**
            * 对账期限日
            */
    private Integer reconciliationDay;

            /**
            * 回单期限月
            */
    private Integer reciveMonth;

            /**
            * 回单期限日
            */
    private Integer reciveDay;

            /**
            * 开票期限月
            */
    private Integer invoiceMonth;

            /**
            * 开票期限日
            */
    private Integer invoiceDay;

            /**
            * 收款期限月
            */
    private Integer collectionMonth;

            /**
            * 收款期限日
            */
    private Integer collectionDay;


}
