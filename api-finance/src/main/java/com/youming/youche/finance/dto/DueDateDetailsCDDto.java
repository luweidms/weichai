package com.youming.youche.finance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zengwen
 * @date 2022/4/19 19:58
 */
@Data
public class DueDateDetailsCDDto implements Serializable {

    /**
     * 提现时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    /**
     * 交易金额，单位分
     */
    private Long txnAmt;

    /**
     * 订单号
     */
    private Long orderId;

    /**
     * 业务ID
     */
    private Long busiId;

    /**
     * 业务名称
     */
    private String busiIdName;

    /**
     * 状态
     */
    private String respCode;

    /**
     * 状态名称
     */
    private String respCodeName;

    /**
     * 支付车队名称
     */
    private String name;

    /**
     * 状态
     */
    private Integer payConfirm;

    /**
     * 是否线上退款
     */
    private Integer isAutoMatic;

    /**
     * 流水号
     */
    private Long flowId;

    /**
     * 1待核销；2已核销；3已撤销
     */
    private Integer verificationState;

    /**
     * 科目ID subjects_info表
     */
    private Long subjectsId;

    /**
     * 付款人用户类型
     */
    private Integer userType;

    /**
     * 状态名称
     */
    private String stateName;

    /**
     * 业务单号
     */
    private String busiCode;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 文件URL
     */
    private String fileUrl;

}
