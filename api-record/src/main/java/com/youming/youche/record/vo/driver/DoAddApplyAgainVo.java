package com.youming.youche.record.vo.driver;

import lombok.Data;

import java.io.Serializable;

/**
 * @version:
 * @Title: DoAddApplyAgainVo
 * @Package: com.youming.youche.record.vo.driver
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2022/1/27 11:57
 * @company:
 */
@Data
public class DoAddApplyAgainVo implements Serializable {

    /**
     * 司机ID
     */
    private Long applyId;

    /**
     * 邀请备注
     */
    private  String applyRemark;

    /**
     * 邀请附件
     */
    private Long applyFileId;

    /**
     * 类型
     */
    private Integer type;

}
