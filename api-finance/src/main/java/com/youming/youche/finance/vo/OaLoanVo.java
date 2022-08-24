package com.youming.youche.finance.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 2022-2-1 14:00
 */
@Data
public class OaLoanVo implements Serializable {

    private static final long serialVersionUID = -2091976793303817142L;

    private Long id;// 借支id
    private Long loanId;//借支号

    private String oaLoanId; //借支序列id

    private Integer loanSubject;//借支科目类型

    private String amount; //借支金额

    private String orderId; //订单编号

    private String plateNumber;//车牌号

    private String carOwnerName;//司机姓名

    private String carPhone;//司机手机号

    private Integer amountReceipt;//借支票据 ()

    private String remark;//借款说明

    private String borrowName;//收款人

    private String borrowPhone;//收款手机

    private String bankName;//银行名称

    private  String accNo ; // 账户
    private String accName;// 账户名

    private String bankAccount;//银行账户
    /**
     * 开户行
     */

    private String isNeedBill; //是否需要开票

    private String accountName;//开户银行名称

    private String appReason; // 借款原因

    /**
     * 银行支行
     */
    private String bankBranch;
    /**
     * 收款账户类型：0私人账户；1对公账户
     */
    private Integer bankType;
    /**
     * 对私收款虚拟账户
     */
    private String collectAcctId;

    private String restRemark;//其他说明

    private List<String> fileUrl;//文件路径

    private List<String> fileName;//文件名称

    private String weight;//过磅重量

    private Integer launch;//借支发起（1车管中心，2司机)

    private String AccidentDate;//事故时间

    private String InsuranceDate;//出险时间

    private Integer AccidentType;//事故类型 ()

    private Integer AccidentReason;// 事故原因()

    private Integer DutyDivide;//责任划分()

    private Integer AccidentDivide;//事故司机()

    private String InsuranceFirm;//保险公司

    private String claimAmount;//理赔金额

    private String ReportNo;//报案号

    private Integer LoanTransReason; //是否因公 1  因私 2 违章

    private  Integer Classify; //借支分类：1原有的借支申请(内部员工),2车管中心借支

//    private Integer business;

    private  String accUserId; // 借支人id
    /**
     * 事故时间
     */
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date accidentDate;

//    /**
//     * 出险时间
//     */
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date insuranceDate;

    /**
     * 理赔金额
     */
    private Long insuranceMoney;

    /**
     * 报案号
     */
    private String reportNumber;

    //  上传文件集合
    private List<OaFilesVo> filesVos;

    private  String fileId;

    /**
     * 核销备注
     */
    private String verifyRemark;

    /**
     * 借支订单入口表示
     */
    private  Integer identification;

    private Long borrowUserId;

    /**
     * 订单录入，司机借支专用，如果是1则表示是司机
     */
    private Integer type;
}
