package com.youming.youche.record.domain.sys;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 图片资源表
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysAttach extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

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
     * 创建时间
     */
    private String createDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 租户Id
     */
    private Long tenantId;

}
