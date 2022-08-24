package com.youming.youche.finance.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hzx
 * @date 2022/4/13 14:31
 */
@Data
public class OrderBillCheckInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long checkId; // id
    private Long checkFee; // 核销金额
    private String checkDesc; // 核销说明
    private String fileIds; // 附件ID 集合 以英文逗号隔开
    private String fileUrls; // 附件半路径 集合 以英文逗号隔开
    private Integer checkType; // 核销类型（对应静态数据 的 code_type = CHECK_TYPE
    private Double checkFeeDouble; // 核销金额
    private String checkTypeName; // 核销类型名称
    private String flowNames; // 流水名称
    private String fullUrls; //  附近地址
    private String fileMinfullUrls; // 附近地址

}
