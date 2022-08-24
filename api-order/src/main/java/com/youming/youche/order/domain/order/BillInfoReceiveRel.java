package com.youming.youche.order.domain.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-19
 */
@Data
@Accessors(chain = true)
public class BillInfoReceiveRel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(
            value = "rel_id",
            type = IdType.AUTO
    )
    private Long relId;

    /**
     * 收票人Id
     */
    private Long billInfoId;

    /**
     * 油票抵扣运输专票(0、否；1、是)
     */
    private Boolean deduction;

    /**
     * 收件ID
     */
    private Long receiveId;

    /**
     * 车队id
     */
    private Long tenantId;


    /**
     * 开票信息
     */
    @TableField(exist = false)
    private BillInfo billInfo;
    /**
     * 收件人信息
     */
    @TableField(exist = false)
    private BillReceive billReceive;


}
