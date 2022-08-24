package com.youming.youche.record.vo.driver;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: DriverAuditVo
 * @Package: com.youming.youche.system.vo
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/19 18:02
 * @company:
 */
@Data
public class DriverAuditVo implements Serializable {

    private Long userId; // 用户id

    private String desc; // 审核意见

    private Integer chooseResult; // 审核结果
}
