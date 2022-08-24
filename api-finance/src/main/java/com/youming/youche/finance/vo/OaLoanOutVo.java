package com.youming.youche.finance.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OaLoanOutVo implements Serializable {

    /**
     * 借支id
     */
    private String id;
    /**
     * 借支号
     */
    private String loanId;
    /**
     *现金核销
     */
    private String cashAmountStr;
    /**
     *票据核销
     */
    private String billAmountStr;
    /**
     *附件路径
     */
    private List<String> fileUrl;
    /**
     *核销备注
     */
    private  String verifyRemark;

    private  String  fileId; // 图片id

   private String strfileId;

   private Integer fromWx;
}
