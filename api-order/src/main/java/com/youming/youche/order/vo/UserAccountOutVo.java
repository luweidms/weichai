package com.youming.youche.order.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/4/7
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class UserAccountOutVo implements Serializable {
    private String billId ;
    private String accState;

    private String strbalanceBegin;
    private String strbalanceEnd;

    private String strmarginBegin;
    private String strmarginEnd;

    private String stroilBegin;
    private String stroilEnd ;

    private String stretcBegin;
    private String stretcEnd ;

    private Long balanceBegin;
    private Long balanceEnd ;

    private Long marginBegin;
    private Long marginEnd;

    private Long oilBegin;
    private Long oilEnd ;

    private Long etcBegin;
    private  Long etcEnd ;

    private Long tenantId;
    private Long receivableOverdueBalanceBegin;
    private  Long receivableOverdueBalanceEnd ;
    private Long payableOverdueBalanceBegin;
    private  Long payableOverdueBalanceEnd ;
    private String receivableOverdueBalanceBeginStr;
    private  String receivableOverdueBalanceEndStr ;
    private String payableOverdueBalanceBeginStr;
    private  String payableOverdueBalanceEndStr;
    /**
     * 账户类型 1母卡 2子卡
     */
    private Integer accountType;
    private String strRepairFundBegin;
    private String strRepairFundEnd;
    /**
     * 用户id
     */
    private Long userId;
    private String linkman;

    private Double stroilBeginDouble;
    private Double stroilEndDouble;

    private Double strbalanceBeginDouble;
    private Double strbalanceEndDouble;

    private Double strmarginBeginDouble;
    private Double strmarginEndDouble;


    private Double strRepairFundBeginDouble;
    private Double strRepairFundEndDouble;

    private Double stretcBeginDouble;
    private Double stretcEndDouble;

    private Double balanceBeginDouble;
    private Double balanceEndDouble;

    private Double marginBeginDouble;
    private Double marginEndDouble;

    private Double oilBeginDouble;
    private Double oilEndDouble;

    private Double etcBeginDouble;
    private Double etcEndDouble;

    private Double receivableOverdueBalanceBeginStrDouble;
    private Double receivableOverdueBalanceEndStrDouble;
    private Double payableOverdueBalanceBeginStrDouble;
    private Double payableOverdueBalanceEndStrDouble;
}
