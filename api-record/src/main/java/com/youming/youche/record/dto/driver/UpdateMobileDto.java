package com.youming.youche.record.dto.driver;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: UpdateMobileDto
 * @Package: com.youming.youche.record.dto.driver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/1/26 18:36
 * @company:
 */
@Data
public class UpdateMobileDto implements Serializable {

    private String mobilePhone; // 手机号码

    private String oldMobilePhone; // 旧手机号码

    private Long userId; // 用户编号

    private Long relId; // 租户会员id
}
