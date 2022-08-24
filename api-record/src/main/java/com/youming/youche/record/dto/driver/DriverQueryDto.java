package com.youming.youche.record.dto.driver;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: DriverQueryDto
 * @Package: com.youming.youche.dto
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/1/7 17:06
 * @company:
 */
@Data
public class DriverQueryDto implements Serializable {
    private static final long serialVersionUID = 5935173189265310203L;
    private int carUserType;//司机类型
    private String loginAcct;//手机号
    private String attachTenantName;//归属车队
    private String linkman;//姓名
    private String attachTennantLinkman;//车队联系人
    private String attachTennantLinkPhone;//车队联系人电话

}
