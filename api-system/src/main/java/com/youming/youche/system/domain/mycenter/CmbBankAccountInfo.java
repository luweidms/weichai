package com.youming.youche.system.domain.mycenter;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @ClassName CmbBankAccountInfo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/25 17:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class CmbBankAccountInfo extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 关联用户Id
     */
    @TableField("userId")
    private Long userId;

    /**
     * 关联车队Id
     */
    @TableField("tenantId")
    private Long tenantId;

    /**
     * 帐户级别：0：个人；1：车队
     */
    @TableField("accLevel")
    private String accLevel;

    /**
     * 请求流水号
     */
    //private String reqno;

    /**
     * 平台编号
     */
    @TableField("platformNo")
    private String platformNo;

    /**
     * 证照类型：C35：统一社会信用代码(个体工商户，企业)	C01：经营执照代码（个体工商户，企业）P01：个体身份证
     */
    @TableField("certType")
    private String certType;

    /**
     * 证照号码
     */
    @TableField("certNo")
    private String certNo;

    /**
     * 个体工商户标志,Y/N
     */
    @TableField("indiFlag")
    private String indiFlag;

    /**
     * 证件上的名称
     */
    @TableField("certName")
    private String certName;

    /**
     * 证照开始日期，格式：YYYYMMDD
     */
    @TableField("certStartDate")
    private String certStartDate;

    /**
     * 证照结束日期，如果永久有效，填写99991231
     */
    @TableField("certEndDate")
    private String certEndDate;

    /**
     * 证照照片（传身份证时，为人像面）
     */
    @TableField("certFrontPhoto")
    private String certFrontPhoto;

    /**
     * 传身份证时，为国徽面，公司证件不要求
     */
    @TableField("certBackPhoto")
    private String certBackPhoto;

    /**
     * 证照正面照片路径
     */
    @TableField("certFrontPhotoUrl")
    private String certFrontPhotoUrl;

    /**
     * 证照反面照片路径
     */
    @TableField("certBackPhotoUrl")
    private String certBackPhotoUrl;

    /**
     * 管理员手机号，用于短信验证
     */
    @TableField("mgrMobile")
    private String mgrMobile;

    /**
     * 管理员身份：C法人；A授权代理经办人
     */
    @TableField("mgrIdentity")
    private String mgrIdentity;

    /**
     * 管理员邮箱
     */
    @TableField("mgrEmail")
    private String mgrEmail;

    /**
     * 管理员证件编号
     */
    @TableField("mgrCertNo")
    private String mgrCertNo;

    /**
     * 管理员证件类型
     */
    @TableField("mgrCertType")
    private String mgrCertType;

    /**
     * 管理员姓名
     */
    @TableField("mgrName")
    private String mgrName;

    /**
     * 管理员授权函
     */
    @TableField("mgrAuthLetter")
    private String mgrAuthLetter;

    /**
     * 经营范围
     */
    @TableField("busiScope")
    private String busiScope;

    /**
     * 注册资本(万)
     */
    @TableField("regFund")
    private String regFund;

    /**
     * 法人证件类型：	P01：个体身份证
     */
    @TableField("chgrNoType")
    private String chgrNoType;

    /**
     * 法人证件编号
     */
    @TableField("chgrNo")
    private String chgrNo;

    /**
     * 法人姓名
     */
    @TableField("chgrName")
    private String chgrName;

    /**
     * 法人手机号
     */
    @TableField("chgrMobile")
    private String chgrMobile;

    /**
     * 法人证件开始日期，格式：YYYYMMDD
     */
    @TableField("chgrStartDate")
    private String chgrStartDate;

    /**
     * 法人证件结束日期，如果永久有效，填写99991231
     */
    @TableField("chgrEndDate")
    private String chgrEndDate;

    /**
     * 法人证件地址
     */
    @TableField("chgrAddr")
    private String chgrAddr;

    /**
     * 法人证件照正面照片
     */
    @TableField("chgrFrontPhoto")
    private String chgrFrontPhoto;

    /**
     * 法人证件照反面照片
     */
    @TableField("chgrBackPhoto")
    private String chgrBackPhoto;

    /**
     * 法人证件照正面照片路径
     */
    @TableField("chgrFrontPhotoUrl")
    private String chgrFrontPhotoUrl;

    /**
     * 法人证件照反面照片路径
     */
    @TableField("chgrBackPhotoUrl")
    private String chgrBackPhotoUrl;

    /**
     * F:根据fromMbr*信息开子账户,建议	U:不开通子账户,不建议
     */
    @TableField("opnMode")
    private String opnMode;

    /**
     * openMode为F时必填，业务系统编号，例如userId、userKey
     */
    @TableField("fromMbrNo")
    private String fromMbrNo;

    /**
     * openMode为F时必填，业务系统名称，例如userName
     */
    @TableField("fromMbrName")
    private String fromMbrName;

    /**
     * 银行处理流水号
     */
    //private String respno;

    /**
     * 注册状态	I:受理中	S：成功	F：失败
     */
    @TableField("status")
    private String status;

    /**
     * status为F时，返回失败原因
     */
    @TableField("result")
    private String result;

    /**
     * 商户编号，审批通过后分配
     */
    @TableField("merchNo")
    private String merchNo;

    /**
     * openMode=’F’时必填，子商户编号，审批后生成
     */
    @TableField("mbrNo")
    private String mbrNo;

    /**
     * 可用余额
     */
    @TableField("avaBal")
    private String avaBal;

}
