package com.youming.youche.system.vo.mycenter;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName CreateBankAccountVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/15 17:23
 */
@Data
public class CreateBankAccountVo implements Serializable {

    /** 用户Id */
    private Long userId;
    /** 车队Id */
    private Long tenantId;
    /** 账户级别 */
    private String accLevel;
    /** 证照类型 */
    private String certType;
    /** 证照编号 */
    private String certNo;
    /** 是否个体户标志 */
    private String indiFlag;
    /** 证照名称 */
    private String certName;
    /** 证照开始日期 */
    private String certStartDate;
    /** 证照结束日期 */
    private String certEndDate;
    /** 证照正面照 */
    private String certFrontPhoto;
    /** 证照背面照 */
    private String certBackPhoto;
    /** 证照正面照url */
    private String certFrontPhotoUrl;
    /** 证照背面照url */
    private String certBackPhotoUrl;
    /** 管理员手机号 */
    private String mgrMobile;
    /** 管理员身份证号 */
    private String mgrIdentity;

    /** 法人证件类型 */
    private String chgrNoType;
    /** 法人证件号 */
    private String chgrNo;
    /** 法人姓名 */
    private String chgrName;
    /** 法人手机号 */
    private String chgrMobile;
    /** 法人证件开始日期 */
    private String chgrStartDate;
    /** 法人证件结束日期 */
    private String chgrEndDate;
    /** 法人身份证地址 */
    private String chgrAddr;
    /** 法人证件正面照片 */
    private String chgrFrontPhoto;
    /** 法人证件背面照片 */
    private String chgrBackPhoto;
    /** 法人证件正面照片Url */
    private String chgrFrontPhotoUrl;
    /** 管理员身份证号Url */
    private String chgrBackPhotoUrl;
    /** 证件编号 */
    private String fromMbrNo;
    /** 账户名称 */
    private String fromMbrName;

}
