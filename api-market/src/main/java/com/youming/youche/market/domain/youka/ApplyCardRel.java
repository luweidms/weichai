package com.youming.youche.market.domain.youka;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 油卡申请关系表
 * </p>
 *
 * @author Terry
 * @since 2022-02-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ApplyCardRel extends BaseDomain {

    private static final long serialVersionUID = 1L;


    /**
     * 主键
     */
    @TableId(value = "rel_id", type = IdType.AUTO)
    private Long relId;

    /**
     * 申请记录id
     */
    private Long applyId;

    /**
     * 油卡id
     */
    private Long cardId;

    /**
     * 卡号
     */
    private String oilCardNum;

    /**
     * 4待交付 5已交付 6已完成
     */
    private Integer state;

    /**
     * 站点
     */
    private Long productId;

    /**
     * 是否开票
     */
    private Integer isNeedBill;

    /**
     * 车队
     */
    private Long tenantId;

    /**
     * 卡类型(中石油 中石化)
     */
    private Integer oilCardType;

    /**
     * 处理时间
     */
    private LocalDateTime dealTime;

    /**
     * 服务商id
     */
    private Long serviceUserId;

    /**
     * 是否共享 1是 2否
     */
    private Integer isShare;

    /**
     * 是否被回收完成 0否 1是
     */
    private Integer isRecoveryed;

    /**
     * 交付id
     */
    private Long deliverId;

    /**
     * 操作人
     */
    private Long opId;

    /**
     * 操作人
     */
    private String opName;

    /**
     * 下发批次id
     */
    private Long batchId;

    /**
     * 卡种类 1油卡 2etc
     */
    private Integer cardType;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 下发时间
     */
    private LocalDateTime startTime;

    /**
     * 回收时间
     */
    private LocalDateTime endTime;


}
