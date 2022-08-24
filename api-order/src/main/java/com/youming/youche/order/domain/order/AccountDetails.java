package com.youming.youche.order.domain.order;

    import com.baomidou.mybatisplus.annotation.TableField;
    import com.youming.youche.commons.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

    import java.io.ByteArrayInputStream;
    import java.io.ByteArrayOutputStream;
    import java.io.ObjectInputStream;
    import java.io.ObjectOutputStream;
    import java.time.LocalDateTime;

/**
* <p>
    * 
    * </p>
* @author CaoYaJie
* @since 2022-03-22
*/
    @Data
        @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public class AccountDetails extends BaseDomain {

    private static final long serialVersionUID = 1L;

    /**
     * 账户ID
     */
    private Long accountId;

    /**
     * 服务费金额
     */
    private Long amount;

    /**
     * 账户类型
     */
    private Long bookType;

    /**
     * 账户类型名称
     */
    @TableField(exist = false)
    private String bookTypeName;

    /**
     * 流水名称编号
     */
    private Long businessNumber;

    /**
     * 业务类型
     */
    private Integer businessTypes;

    /**
     *消费类型
     */
    private Integer costType;

    /**
     * 消费类型名称
     */
    @TableField(exist = false)
    private String costTypeName;

    /**
     * 可用金额
     */
    private Long currentAmount;

    /**
     * 当前欠款金额
     */
    private Long currentDebtAmount;

    /**
     * 欠款金额
     */
    private Long debtAmount;

    /**
     * ETC卡号
     */
    private String etcNumber;

    /**
     * 发生时间
     */
    private LocalDateTime happenDate;

    /**
     * 是否油老板提现0非油老板1是油老板
     */
    private Integer isOilPay;

    /**
     * 0未被报表收集1已经收集
     */
    private Integer isReported;

    /**
     * 处理备注
     */
    private String note;

    /**
     * 操作人
     */
    private Long opId;

    /**
     * 订单号
     */
    private String orderId;

    /**
     * 对方名称
     */
    private String otherName;

    /**
     * 对方用户编号
     */
    private Long otherUserId;

    /**
     * 支付方式
     */
    private String payWay;

    /**
     * 车牌号码
     */
    private String plateNumber;

    /**
     * 报表核销金额
     */
    private Long reportAmount;

    /**
     * 报表收集日期
     */
    private LocalDateTime reportDate;

    /**
     * 批次号
     */
    private Long soNbr;

    /**
     * 科目ID
     */
    private Long subjectsId;

    /**
     * 科目名称
     */
    private String subjectsName;

    /**
     * 车队id
     */
    private Long tenantId;

    /**
     * 资金转移
     */
    private Long transferAmount;

    /**
     * 修改人
     */
    private Long updateOpId;

    /**
     * 用戶id
     */
    private Long userId;

    /**
     * 用戶类型
     */
    private Integer userType;

    /**
     * 资金渠道类型
     */
    private String vehicleAffiliation;

    //深度拷贝
    public AccountDetails deepClone() throws Exception{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        return (AccountDetails) ois.readObject();
    }

}
