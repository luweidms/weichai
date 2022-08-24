package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName MerchRegReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 11:07
 */
@Data
public class MerchRegReqDto implements Serializable {
    /** 请求流水，全局唯一 */
    private String reqNo;

    /** 平台编号，平台进件后由银行分配 */
    private String platformNo;

    /** 证照类型，通过证照类型能间接推算出企业还是自然人
     C35：统一社会信用代码(个体工商户，企业)
     C01：经营执照代码（个体工商户，企业）
     C08：事业组织代码(事业单位)
     P01：个体身份证 */
    private String certType;

    /** 证照号码 */
    private String certNo;

    /** 个体工商户标志,Y/N,在统一社会信用代码或经营执照代码的情况下必填，其他情况不参考 */
    private String indiFlag;

    /** 证件上的名称 */
    private String certName;

    /** 证照开始日期，格式：YYYYMMDD */
    private String certStartDate;

    /** 证照结束日期，如果永久有效，填写99991231 */
    private String certEndDate;

    /** 证照照片（传身份证时，为人像面），base64编码，前端传入url,后端编码转换 */
    private String certFrontPhoto;

    /** 传身份证时，为国徽面，公司证件不要求 */
    private String certBackPhoto;

    /** 管理员手机号，用于短信验证 */
    private String mgrMobile;

    /** 管理员身份：C法人；A授权代理经办人 */
    private String mgrIdentity;

    /** 管理员邮箱 */
    private String mgrEmail;

    /** 管理员证件编号,mgrIdentity=A时必填 */
    private String mgrCertNo;

    /** 管理员证件类型,mgrIdentity=A时必填 */
    private String mgrCertType;

    /** 管理员姓名,mgrIdentity=A时必填 */
    private String mgrName;

    /** 管理员授权函，BASE64格式,mgrIdentity=A时必填 */
    private String mgrAuthLetter;

    /** 类型为C01,C35,C08时候填充，必填性具体看下面每个域的说明 */
    private CpnInfoReqDto cpnInfo;

    /** F:根据fromMbr*信息开子账户,建议
     U:不开通子账户,不建议 */
    private String opnMode;

    /** openMode为F时必填，业务系统编号，例如userId、userKey,自动根据from*两个要素创建一个子账户 */
    private String fromMbrNo;

    /** openMode为F时必填，业务系统名称，例如userName */
    private String fromMbrName;
}
