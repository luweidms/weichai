package com.youming.youche.system.vo;

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

    /**
     * 业务编码
     */
    private String busiCode;

    /**
     * 业务id
     */
    private Long busiId;

    /**
     * 备注
     */
    private String desc;

    /**
     * 审核成功标识（1审核通过 其他审核不通）
     */
    private Integer chooseResult;

    /**
     * 审核节点id
     */
    private Long instId;

}
