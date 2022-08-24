package com.youming.youche.capital.vo.audit;

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
     * 业务编号
     */
    private String busiCode;
    /**
     * 业务Id
     */
    private Long busiId;
    /**
     * 描述
     */
    private String desc;
    /**
     * 审核结果
     */
    private Integer chooseResult;
    /**
     * 审核配置id
     */
    private Long instId;

}
