package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: AuditApplyVo
 * @Package: com.youming.youche.record.vo.violation
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/25 16:40
 * @company:
 */
@Data
public class AuditApplyVo implements Serializable {

    String busiCode;

    Long busiId;

    String desc;

    Integer chooseResult;

    String oldUserId;

    String driverUserId;

    String plateNumbers;

}
