package com.youming.youche.capital.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author Terry
* @since 2022-03-02
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(
        excludeProperty = {"createTime", "updateTime"}
)
public class PayFeeLimit extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 类别
     */
    private Integer type;

    /**
     * 科目
     */
    private Integer subType;

    /**
     * 科目名称
     */
    private String subTypeName;

    /**
     * 上限值
     */
    private Long value;

    /**
     * 创建时间
     */
    private LocalDateTime createDate;

    /**
     * 操作人Id
     */
    private Long opId;

    /**
     * 操作人
     */
    private String opName;

    /**
     * 操作日期
     */
    private LocalDateTime opDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 单位名称
     */
//    public String unitName;

}
