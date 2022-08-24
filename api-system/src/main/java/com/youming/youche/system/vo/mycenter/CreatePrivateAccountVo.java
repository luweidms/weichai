package com.youming.youche.system.vo.mycenter;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName CreatePrivateAccountVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/23 17:29
 */
@Data
public class CreatePrivateAccountVo implements Serializable {

    @NotNull(message = "用户Id不能为空")
    private Long userId;
    @NotNull(message = "车队Id不能为空")
    private Long tenantId;
    @NotBlank(message = "账户级别不能为空")
    private String accLevel;
    @NotBlank(message = "证照类型不能为空")
    private String certType;
    @NotBlank(message = "身份证号码不能为空")
    private String certNo;
    @NotBlank(message = "证件名称不能为空")
    private String certName;
    @NotBlank(message = "管理员手机号不能为空")
    private String mgrMobile;
    @NotBlank(message = "管理员身份不能为空")
    private String mgrIdentity;
    @NotBlank(message = "身份证照不能为空")
    private String certFrontPhoto;
    @NotBlank(message = "身份证照不能为空")
    private String certBackPhoto;
    @NotBlank(message = "身份证照不能为空")
    private String certFrontPhotoUrl;
    @NotBlank(message = "身份证照不能为空")
    private String certBackPhotoUrl;
    @NotBlank(message = "身份证开始日期不能为空")
    private String certStartDate;
    @NotBlank(message = "身份证结束日期不能为空")
    private String certEndDate;

}
