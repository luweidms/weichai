package com.youming.youche.system.vo.mycenter;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @ClassName AccountTransactionRecordVo
 * @Description 添加描述
 * @Author zag
 * @Date 2022/2/20 16:53
 */
@Data
public class AccountFlowListVo implements Serializable {

    private Long id;
    private String tranTime;
    private String respNo;
    private String tranType;
    private String payMbrName;
    private String payMbrNo;
    private String recvMbrName;
    private String recvMbrNo;
    private String tranAmt;
    private String tranStatus;
    private Long payoutId;
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private LocalDateTime createTime;

    private String strCreateTime;
    public String getStrCreateTime(){
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.setStrCreateTime(df.format(this.createTime));
        return this.strCreateTime;
    }
    private String tranTypeName;
    public String getTranTypeName(){
        switch (this.tranType){
            case "AC":
                this.setTranTypeName("充值");
                break;
            case "BP":
                this.setTranTypeName("转帐");
                break;
            case "WD":
                this.setTranTypeName("提现");
                break;
        }
        return this.tranTypeName;
    }
    private String tranStatusName;
    public String getTranStatusName(){
        switch (this.tranStatus){
            case "Y":
                this.setTranStatusName("交易成功");
                break;
            case "F":
                this.setTranStatusName("交易失败");
                break;
            case "R":
                this.setTranStatusName("提现已受理");
                break;
            case "O":
                this.setTranStatusName("交易响应超时");
                break;
        }
        return this.tranStatusName;
    }
    private String businessType;
    private String businessTypeName;
}
