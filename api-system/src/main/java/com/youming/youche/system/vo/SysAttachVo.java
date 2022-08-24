package com.youming.youche.system.vo;

import lombok.Data;

@Data
public class SysAttachVo {

    private static final long serialVersionUID = 1L;


    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 存储路径
     */
    private String storePath;

    /**
     * 操作Id
     */
    private Long opId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 租户Id
     */
    private Long tenantId;

    /**
     * 完整地址
     */
    private String fullPath;


}
