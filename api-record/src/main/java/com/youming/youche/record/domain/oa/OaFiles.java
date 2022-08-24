package com.youming.youche.record.domain.oa;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OaFiles extends BaseDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 关联数据类型:1借支 2借支核销 3报销
     */
    private Integer relType;

    /**
     * 关联ID（关联表主键ID）
     */
    private Long relId;

    /**
     * 关联文件ID
     */
    private Long fileId;

    /**
     * 关联文件名
     */
    private String fileName;

    /**
     * 文件url
     */
    private String fileUrl;

    /**
     * 操作员ID
     */
    private Long opId;

    /**
     * 操作日志
     */
    private String opDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 车队id
     */
    private Long tenantId;

    public String getFileUrl() {
//        if (StringUtils.isNotBlank(fileUrl)) {
//            if (StringUtils.containsIgnoreCase(fileUrl, "_big")) {
//                if (StringUtils.containsIgnoreCase(fileUrl, ".png")) {
//                    fileUrl = fileUrl.replace("_big.png", ".png");
//                }
//                if (StringUtils.containsIgnoreCase(fileUrl, ".jpg")) {
//                    fileUrl = fileUrl.replace("_big.jpg", ".jpg");
//                }
//                if (StringUtils.containsIgnoreCase(fileUrl, ".jpeg")) {
//                    fileUrl = fileUrl.replace("_big.jpeg", ".jpeg");
//                }
//                if (StringUtils.containsIgnoreCase(fileUrl, ".gif")) {
//                    fileUrl = fileUrl.replace("_big.gif", ".gif");
//                }
//            }
//        }
        return fileUrl;
    }

}
