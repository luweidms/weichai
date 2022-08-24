package com.youming.youche.system.vo.mycenter;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName CreatePublicAccountVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/23 17:29
 */
@Data
public class CreatePublicAccountVo implements Serializable {

    /** 用户Id */
    @NotNull(message = "用户Id不能为空")
    private Long userId;
    /** 车队Id */
    @NotNull(message = "车队Id不能为空")
    private Long tenantId;
    /** 账户级别：0：个人；1：车队 */
    @NotBlank(message = "账户级别不能为空")
    private String accLevel;
    /** 证照类型 */
    @NotBlank(message = "证照类型不能为空")
    private String certType;
    /** 证件名称 */
    @NotBlank(message = "证件名称不能为空")
    private String certName;
    /** 证照号码 */
    @NotBlank(message = "证照号码不能为空")
    private String certNo;
    /** 个体工商户标志,Y/N */
    @NotBlank(message = "个体工商户标志不能为空")
    private String indiFlag;
    /** 证照照片Id */
    @NotBlank(message = "证照照片不能为空")
    private String certFrontPhoto;
    /** 证照照片Url */
    @NotBlank(message = "证照照片不能为空")
    private String certFrontPhotoUrl;
    /** 证照开始日期，格式：YYYYMMDD */
    @NotBlank(message = "证照开始日期不能为空")
    private String certStartDate;
    /** 证照结束日期，如果永久有效，填写99991231 */
    @NotBlank(message = "证照结束日期不能为空")
    private String certEndDate;
    /** 法人姓名 */
    @NotBlank(message = "法人姓名不能为空")
    private String chgrName;
    /** 法人手机号 */
    @NotBlank(message = "法人手机号不能为空")
    private String chgrMobile;
    /** 法人证件照正面照片Id */
    @NotBlank(message = "法人证件照不能为空")
    private String chgrFrontPhoto;
    /** 法人证件照反面照片Id */
    @NotBlank(message = "法人证件照不能为空")
    private String chgrBackPhoto;
    /** 法人证件照正面照片Url */
    @NotBlank(message = "法人证件照不能为空")
    private String chgrFrontPhotoUrl;
    /** 法人证件照反面照片Url */
    @NotBlank(message = "法人证件照不能为空")
    private String chgrBackPhotoUrl;
    /** 法人证件编号 */
    @NotBlank(message = "法人证件编号不能为空")
    private String chgrNo;
    /** 法人证件开始日期，格式：YYYYMMDD */
    private String chgrStartDate;
    @NotBlank(message = "证照类型不能为空")
    /** 法人证件结束日期，如果永久有效，填写99991231 */
    private String chgrEndDate;
    /** 法人证件地址 */
    @NotBlank(message = "法人证件地址不能为空")
    private String chgrAddr;
    /** 管理员手机号 */
    @NotBlank(message = "管理员手机号不能为空")
    private String mgrMobile;
    /** 管理员身份：C法人 */
    @NotBlank(message = "管理员身份不能为空")
    private String mgrIdentity;
}
