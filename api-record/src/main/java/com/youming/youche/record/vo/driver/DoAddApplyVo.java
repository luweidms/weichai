package com.youming.youche.record.vo.driver;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: DoAddApplyVo
 * @Package: com.youming.youche.record.vo.driver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/1/27 14:56
 * @company:
 */
@Data
public class  DoAddApplyVo implements Serializable {
    private Integer carUserType;
    private Long busiId;
    private String applyRemark;
    private Long applyFileId;
    private Long tenantId;
    private Integer type;
    private String plateNumbers;
    private Integer update;
}
