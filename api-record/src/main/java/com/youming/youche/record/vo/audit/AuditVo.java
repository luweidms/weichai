package com.youming.youche.record.vo.audit;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: AuditVo
 * @Package: com.youming.youche.system.vo
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/2/19 3:39
 * @company:
 */
@Data
public class AuditVo implements Serializable {

    private String busiCode; // 业务编号

    private Long busiId; // 业务主键id

    private String desc; // 描述

    private Integer chooseResult; // 审核结果 1 审核通过 2 审核不通过

    private Long instId; // 通过这个判断当前待审核的数据是否跟数据库一致

}
