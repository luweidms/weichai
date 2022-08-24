package com.youming.youche.order.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author xxx
 * @since 2022-03-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderCostOtherType extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 显示顺序号
     */
    private Long sortNum;
    /**
     * 租户ID,该值为-1则所有租户都包含此类型
     */
    private Long tenantId;
    /**
     * 消费类型名称
     */
    private String typeName;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
    public OrderCostOtherType(String typeName, Long tenantId, Long sortNum) {
        super();
        this.typeName = typeName;
        this.tenantId = tenantId;
        this.sortNum = sortNum;
    }

}
