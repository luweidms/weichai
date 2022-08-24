package com.youming.youche.finance.domain;

    import java.time.LocalDateTime;
    import com.baomidou.mybatisplus.annotation.TableField;
    import com.youming.youche.commons.base.BaseDomain;
    import lombok.Data;
    import lombok.EqualsAndHashCode;
    import lombok.experimental.Accessors;

/**
* <p>
    * 借支报销文件表
    * </p>
* @author luona
* @since 2022-04-14
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class OaFiles extends BaseDomain {

    private static final long serialVersionUID = 1L;


            /**
            * 关联数据类型:1借支 2借支核销 3报销
            */
        @TableField("REL_TYPE")
    private Integer relType;

            /**
            * 关联ID（关联表主键ID）
            */
        @TableField("REL_ID")
    private Long relId;

            /**
            * 关联文件ID
            */
        @TableField("FILE_ID")
    private Long fileId;

            /**
            * 关联文件名
            */
        @TableField("FILE_NAME")
    private String fileName;

            /**
            * 文件url
            */
        @TableField("FILE_URL")
    private String fileUrl;

            /**
            * 操作员ID
            */
        @TableField("OP_ID")
    private Long opId;

            /**
            * 操作日志
            */
        @TableField("OP_DATE")
    private LocalDateTime opDate;

            /**
            * 备注
            */
        @TableField("REMARK")
    private String remark;

        @TableField("TENANT_ID")
    private Long tenantId;


}
