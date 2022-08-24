package com.youming.youche.finance.domain.order;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 账单核销费用明细
 *
 * @author hzx
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderBillCheckInfo extends BaseDomain {

    /**
     * 主键
     */
    private Long id;

    /**
     * 账单号
     */
    private String billNumber;

    /**
     * 核销金额
     */
    private Long checkFee;

    /**
     * 核销说明
     */
    private String checkDesc;

    /**
     * 核销类型（对应静态数据 的 code_type = CHECK_TYPE
     */
    private Integer checkType;

    /**
     * 附件ID 集合 以英文逗号隔开
     */
    private String fileIds;

    /**
     * 附件半路径 集合 以英文逗号隔开
     */
    private String fileUrls;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 创建时间
     */
//    private String createDate;

    /**
     * 操作人
     */
    private Long operId;

    /**
     * 操作时间
     */
    private String operDate;

    private Long tenantId;

}
