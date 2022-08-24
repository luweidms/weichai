package com.youming.youche.market.domain.facilitator;

    import com.baomidou.mybatisplus.annotation.TableField;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 招行帐户信息表
    * </p>
* @author CaoYaJie
* @since 2022-02-09
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
    private Long userid;

            /**
            * 关联车队Id
            */
        @TableField("tenantId")
    private Long tenantid;

            /**
            * 帐户级别：0：个人；1：车队
            */
        @TableField("accLevel")
    private String acclevel;

            /**
            * 请求流水号
            */
//        @TableField("reqNo")
        @TableField(exist=false)
    private String reqno;

            /**
            * 平台编号
            */
        @TableField("platformNo")
    private String platformno;

            /**
            * 证照类型：C35：统一社会信用代码(个体工商户，企业)	C01：经营执照代码（个体工商户，企业）P01：个体身份证
            */
        @TableField("certType")
    private String certtype;

            /**
            * 证照号码
            */
        @TableField("certNo")
    private String certno;

            /**
            * 个体工商户标志,Y/N
            */
        @TableField("indiFlag")
    private String indiflag;

            /**
            * 证件上的名称
            */
        @TableField("certName")
    private String certname;

            /**
            * 证照开始日期，格式：YYYYMMDD
            */
        @TableField("certStartDate")
    private String certstartdate;

            /**
            * 证照结束日期，如果永久有效，填写99991231
            */
        @TableField("certEndDate")
    private String certenddate;

            /**
            * 证照照片（传身份证时，为人像面）
            */
        @TableField("certFrontPhoto")
    private String certfrontphoto;

            /**
            * 传身份证时，为国徽面，公司证件不要求
            */
        @TableField("certBackPhoto")
    private String certbackphoto;

            /**
            * 证照正面照片路径
            */
        @TableField("certFrontPhotoUrl")
    private String certfrontphotourl;

            /**
            * 证照反面照片路径
            */
        @TableField("certBackPhotoUrl")
    private String certbackphotourl;

            /**
            * 管理员手机号，用于短信验证
            */
        @TableField("mgrMobile")
    private String mgrmobile;

            /**
            * 管理员身份：C法人；A授权代理经办人
            */
        @TableField("mgrIdentity")
    private String mgridentity;

            /**
            * 管理员邮箱
            */
        @TableField("mgrEmail")
    private String mgremail;

            /**
            * 管理员证件编号
            */
        @TableField("mgrCertNo")
    private String mgrcertno;

            /**
            * 管理员证件类型
            */
        @TableField("mgrCertType")
    private String mgrcerttype;

            /**
            * 管理员姓名
            */
        @TableField("mgrName")
    private String mgrname;

            /**
            * 管理员授权函
            */
        @TableField("mgrAuthLetter")
    private String mgrauthletter;

            /**
            * 经营范围
            */
        @TableField("busiScope")
    private String busiscope;

            /**
            * 注册资本(万)
            */
        @TableField("regFund")
    private String regfund;

            /**
            * 法人证件类型：	P01：个体身份证
            */
        @TableField("chgrNoType")
    private String chgrnotype;

            /**
            * 法人证件编号
            */
        @TableField("chgrNo")
    private String chgrno;

            /**
            * 法人姓名
            */
        @TableField("chgrName")
    private String chgrname;

            /**
            * 法人手机号
            */
        @TableField("chgrMobile")
    private String chgrmobile;

            /**
            * 法人证件开始日期，格式：YYYYMMDD
            */
        @TableField("chgrStartDate")
    private String chgrstartdate;

            /**
            * 法人证件结束日期，如果永久有效，填写99991231
            */
        @TableField("chgrEndDate")
    private String chgrenddate;

            /**
            * 法人证件地址
            */
        @TableField("chgrAddr")
    private String chgraddr;

            /**
            * 法人证件照正面照片
            */
        @TableField("chgrFrontPhoto")
    private String chgrfrontphoto;

            /**
            * 法人证件照反面照片
            */
        @TableField("chgrBackPhoto")
    private String chgrbackphoto;

            /**
            * 法人证件照正面照片路径
            */
        @TableField("chgrFrontPhotoUrl")
    private String chgrfrontphotourl;

            /**
            * 法人证件照反面照片路径
            */
        @TableField("chgrBackPhotoUrl")
    private String chgrbackphotourl;

            /**
            * F:根据fromMbr*信息开子账户,建议	U:不开通子账户,不建议
            */
        @TableField("opnMode")
    private String opnmode;

            /**
            * openMode为F时必填，业务系统编号，例如userId、userKey
            */
        @TableField("fromMbrNo")
    private String frommbrno;

            /**
            * openMode为F时必填，业务系统名称，例如userName
            */
        @TableField("fromMbrName")
    private String frommbrname;

            /**
            * 银行处理流水号
            */
//        @TableField("respNo")
        @TableField(exist=false)
    private String respno;

            /**
            * 注册状态	I:受理中	S：成功	F：失败
            */
    private String status;

            /**
            * status为F时，返回失败原因
            */
    private String result;

            /**
            * 商户编号，审批通过后分配
            */
        @TableField("merchNo")
    private String merchno;

            /**
            * openMode=’F’时必填，子商户编号，审批后生成
            */
        @TableField("mbrNo")
    private String mbrno;

            /**
            * 可用余额
            */
        @TableField("avaBal")
    private String avabal;


}
