package com.youming.youche.finance.vo.order;

import lombok.Data;

import java.io.Serializable;

/**
 * 核销信息
 *
 * @author hzx
 * @date 2022/2/16 10:50
 */
@Data
public class CheckDatas implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 说明
     */
    private String checkDesc;

    /**
     * true表示新增
     */
    private String newAdd;

    /**
     * 金额
     */
    private String checkFeeDouble;

    /**
     * 类型
     */
    private Integer checkType;

    /**
     * 类型名称
     */
    private String checkTypeName;

    /**
     * 附件
     */
    private String fileMinfullUrls;

    /**
     * 附件ID
     */
    private String fileIds;

    /**
     * 附件完整地址
     */
    private String fullUrls;

    /**
     * 文件地址
     */
    private String fileUrls;

}
