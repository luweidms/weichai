package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName CpnInfoReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 10:40
 */
@Data
public class CpnInfoReqDto implements Serializable {
    /** 经营范围 */
    private String busiScope;

    /** 注册资本(万) */
    private String regFund;

    /** 法人证件类型：
     P01：个体身份证 */
    private String chgrNoType;

    /** 法人证件编号 */
    private String chgrNo;

    /** 法人姓名 */
    private String chgrName;

    /** 法人手机号 */
    private String chgrMobile;

    /** 法人证件开始日期，格式：YYYYMMDD */
    private String chgrStartDate;

    /** 法人证件结束日期，如果永久有效，填写9999123 */
    private String chgrEndDate;

    /** 法人证件地址 */
    private String chgrAddr;

    /** 法人证件照正面照片，base64编码 */
    private String chgrFrontPhoto;

    /** 法人证件照反面照片，base64编码 */
    private String chgrBackPhoto;
}
