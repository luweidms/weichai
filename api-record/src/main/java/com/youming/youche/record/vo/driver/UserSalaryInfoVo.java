package com.youming.youche.record.vo.driver;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: userSalaryInfo
 * @Package: com.youming.youche.vo.driver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/1/18 17:24
 * @company:
 */
@Data
public class UserSalaryInfoVo implements Serializable {

    private Long id;

    private String startNum;

    private String endNum;

    private String price;
}
