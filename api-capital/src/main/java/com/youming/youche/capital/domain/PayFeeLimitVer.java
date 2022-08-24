package com.youming.youche.capital.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author Terry
 * @since 2022-03-03
 */
@Data
@Accessors(chain = true)
@TableName(
        excludeProperty = {"createTime", "updateTime"}
)
public class PayFeeLimitVer extends BaseDomain {

    private static final long serialVersionUID = 1L;


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 原记录表ID
     */
    private Long hisId;

    /**
     * 批次编号
     */
    private String batchNo;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;
    /**
     * 审核标志 0-未审核，1-审核成功，2-审核失败，3-取消
     */
    private Integer flag;
    /**
     * 操作时间
     */
    private LocalDateTime opDate;
    /**
     * 操作人Id
     */
    private Long opId;
    /**
     * 操作人
     */
    private String opName;
    /**
     * 备注
     */
    private String remark;
    /**
     * 科目
     */
    private Integer subType;
    /**
     * 科目名称
     */
    private String subTypeName;
    /**
     * 车队id
     */
    private Long tenantId;
    /**
     * 类别
     */
    private Integer type;
    /**
     * 上限值
     */
    private Long value;


}
