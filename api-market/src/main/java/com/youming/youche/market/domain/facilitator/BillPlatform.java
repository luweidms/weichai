package com.youming.youche.market.domain.facilitator;

    import java.math.BigDecimal;
    import java.time.LocalDateTime;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 票据平台表
    * </p>
* @author CaoYaJie
* @since 2022-02-14
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class BillPlatform extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 用户编号
            */
    private Long userId;

            /**
            * 平台名称
            */
    private String platName;

            /**
            * 联系人
            */
    private String linkMan;

            /**
            * 身份证号
            */
    private String certNo;

            /**
            * 联系电话
            */
    private String linkPhone;

            /**
            * 地方杂税成本
            */
    private Long localTaxRate;

            /**
            * 其他成本
            */
    private Long otherTaxRate;

            /**
            * 平台提成
            */
    private Long platformFeeRate;

            /**
            * 票据成本
            */
    private String haCost;

            /**
            * 服务费计算公式
            */
    private String serviceFeeFormula;

            /**
            * 发票抬头(企业名称)
            */
    private String billHead;

            /**
            * 税号(社会信用代码)
            */
    private String taxNum;

            /**
            * 最大可开票金额
            */
    private Long maxBillAmount;

            /**
            * 银行编号
            */
    private Integer bankCode;

            /**
            * 银行名称
            */
    private String bankName;

            /**
            * 银行账户
            */
    private String bankAcctNo;

            /**
            * 开票联系电话
            */
    private String billLinkPhone;

            /**
            * 注册省编号
            */
    private Integer registProvinceId;

            /**
            * 注册省名称
            */
    private String registProvinceName;

            /**
            * 注册市编号
            */
    private Integer registCityId;

            /**
            * 注册市名称
            */
    private String registCityName;

            /**
            * 注册县/区编号
            */
    private Integer registDistrictId;

            /**
            * 注册县/区名称
            */
    private String registDistrictName;

            /**
            * 企业注册详细地址
            */
    private String registAddress;

            /**
            * 邮寄省编号
            */
    private Integer postProvinceId;

            /**
            * 邮寄省名称
            */
    private String postProvinceName;

            /**
            * 邮寄市编号
            */
    private Integer postCityId;

            /**
            * 邮寄市名称
            */
    private String postCityName;

            /**
            * 邮寄县/区编号
            */
    private Integer postDistrictId;

            /**
            * 邮寄县/区名称
            */
    private String postDistrictName;

            /**
            * 邮寄详细地址
            */
    private String postAddress;

            /**
            * 收件人
            */
    private String receiver;

            /**
            * 收件人手机号
            */
    private String receiverMobile;

            /**
            * 自有车运费收款账户类型1-车队账户，2-司机账户
            */
    private Integer payAcctType;

            /**
            * 系统前缀
            */
    private String sysPre;

            /**
            * 状态 0-无效，1-有效
            */
    private Integer state;

            /**
            * 托运协议模板
            */
    private String templateUrl;

            /**
            * 最大补单时长(小时)
            */
    private BigDecimal maxReinputOrderTime;

            /**
            * 运输时速（公里/小时）
            */
    private BigDecimal travelSpeed;

            /**
            * 位置采集（个/分钟）
            */
    private Integer locationGather;

            /**
            * 最小运费（元/公里）
            */
    private BigDecimal minTravelFee;

            /**
            * 最大运费（元/公里）
            */
    private BigDecimal maxTravelFee;

            /**
            * 预付比例
            */
    private BigDecimal prepayScale;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 修改时间
     */
    private LocalDateTime updateDate;


}
