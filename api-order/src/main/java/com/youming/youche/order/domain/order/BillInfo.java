package com.youming.youche.order.domain.order;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
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
 * @author CaoYaJie
 * @since 2022-03-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class BillInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 银行账户
     */
    private String acctNo;

    /**
     * 邮寄地址
     */
    private String address;

    /**
     * 开户银行
     */
    private String bankName;

    /**
     * 是否有开票能力 1、有开票能力，2、没有开票能力
     */
    private Integer billAbility;

    /**
     * 发票抬头
     */
    private String billLookUp;

    /**
     * 平台开票方式
     */
    private Long billMethod;

    /**
     * 费率（1.9废弃）
     */
    private Long billRate;

    /**
     * 市
     */
    private Integer cityId;

    private String cityName;

    /**
     * 企业注册住所
     */
    private String companyAddress;

    /**
     * 市
     */
    private Integer companyCityId;

    private String companyCityName;

    /**
     * 县/区
     */
    private Integer companyDistrictId;

    private String companyDistrictName;

    /**
     * 省
     */
    private Integer companyProvinceId;

    private String companyProvinceName;

    /**
     * 收件人
     */
    private String consignee;

    /**
     * 收件人联系电话
     */
    private String consigneePhone;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    /**
     * 县/区
     */
    private Integer districtId;

    private String districtName;

    /**
     * 联系电话
     */
    private String linkPhone;

    /**
     * 是否支持费非外调车开平台票据（0、不支持；1、支持）
     */
    private Integer notOtherCarGetPlatformBill;

    /**
     * 委托协议
     */
    private String paymentAgreement;

    /**
     * 省
     */
    private Integer provinceId;
    /**
     * 省份
     */
    private String provinceName;

    /**
     * 费率设置
     */
    private Long rateId;

    /**
     * 服务费公式（1.9废弃）
     */
    private String serviceFeeFormula;

    /**
     * 税号
     */
    private String taxpayerNumber;

    /**
     * 车队id
     */
    private Long tenantId;

    /**
     * 营业执照
     */
    private String businessLicense;

    /**
     * 联系人姓名
     */
    private String LinkMan;

    /**
     * 运营证
     */
    private String OperationCertificate;

    /**
     * 运输协议
     */
    @TableField(exist = false)
    private BillAgreement billAgreement;


}
