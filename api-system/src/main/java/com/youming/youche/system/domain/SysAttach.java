package com.youming.youche.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.youming.youche.commons.base.BaseDomain;
import com.youming.youche.components.fdfs.FastDFSHelper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.time.LocalDateTime;

/**
 * <p>
 * 图片资源表
 * </p>
 *
 * @author Terry
 * @since 2022-01-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SysAttach extends BaseDomain {

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

    @TableField(exist = false)
    private String fullPathUrl;

    private static final Log log = LogFactory.getLog(SysAttach.class);

    public String getFullPathUrl() {
        try {
            FastDFSHelper client = FastDFSHelper.getInstance();
            this.setFullPathUrl(client.getHttpURL(this.storePath, this.fileName));
        } catch (Exception var2) {
            log.error(var2.getMessage());
        }

        return this.fullPathUrl;
    }

    public void setFullPathUrl(String fullPathUrl) {
        this.fullPathUrl = fullPathUrl;
    }
}
