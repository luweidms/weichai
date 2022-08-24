package com.youming.youche.finance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.youming.youche.finance.domain.OaLoan;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class OaLoanOutDto extends OaLoan {

    private String orgName;//项目部
    private String fundAllocation;//资金归属
    private String loanTypeName;//类别 名称
    private String loanSubjectName;//科目  名称
    private String stsName;//状态名称
    private Long loanId;//借支号
    private String nextPotIdName;//下一节点处理人或组织
    private Long currentUserId;//操作人id
    private Long currentOrgId;//操作人组织id
    private List<String> fileUrl;//文件url
    private List<String> fileName;//文件名称
    private List<String> fileUrlV;//核销文件url
    private List<String> fileNameV;//核销文件名字
    private float cashAmount;//现金核销金额
    private float billAmount;//票据核销金额
    private String imgIds;//图片
    private String imgUrls;//图片
    private List<Long> fileId;//文件id
    private List<Long> fileIdV;//文件id
    private String nextNodeName;//下一节点名称
    private Double amountDouble;//金额
    private Double payedAmountDouble;//核销金额
    private String mobilePhone;//手机号
    private String accidentTypeName;//事故类型名称
    private String accidentReasonName;//事故原因名称
    private String dutyDivideName;//责任划分名称
    private String accidentDivideName;//事故司机名称
    private Long accUserId;//申请人id
    private String nodeName;//下一节点名称
    private boolean haveOrgEntity;//
    private String stateName;//状态名称
    private boolean business;//是否因公 ture  因私 false
    private float allCashAmount;//现金核销金额
    private float allBillAmount;//票据核销金额
    private double nopayedAmount;//未核销金额
    private String isNeedBillName;// 是否需要开票
    private  String userMobilePhone ; // 申请人手机
    private  boolean IsHasPermission; // 审核判断
    private  String classifyName; // 借支入口
    private  String payIdStr;//支付ID
    private  String launchName;//申请发起名称
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime carDependDate;//车辆靠台时间
    private  String borrowerTypeName;//借支类型名称
    private  Long orderIds;//订单号
    private  float million;//百万
    private  float lac;//
    private  float myriad;//
    private  float thousand;//
    private  float hundred;//
    private  float ten;//
    private  float unit;//
    private  float corner;//
    private  float corners;//
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private  LocalDateTime lastVerifDate;//最后校验时间
    private  float lastVerifAmount;//最后校验金额
    private  float lastVerifAmountStr;//最后校验金额
    private  Double lastVerifAmountDouble;//最后校验金额
    private  String lastVerifName; // 最近核销人
    private  String auditkman; // 审核人信息
    private  double noPayedAmountStr;//未支付金额
    private  double salaryDeductionStr;//补贴基恩
    private  double payedAmountStr;//已支付金额
    private  List<Map> auditList; //审核信息
    private  Long totalAmount; // 累计核销金额
    private  double totalAmountStr;//累计核销金额

     //  核销统计返回的参数
    private  Long sumOaLoanAmount;// 总贷款数
    private  Long  sumPayedAmount;// 总已核销金额
    private  Long sumNoPayedAmount;// 总未核销金额
    private  Long sumSalaryDeduction;// 总补贴金额
    private  Long sumLastVerifAmount;//总最后校验金额
    private  double sumOaLoanAmountDouble;// 总贷款数
    private  double sumPayedAmountDouble;//总已核销金额
    private  double sumNoPayedAmountDouble;//总未核销金额
    private  double sumSalaryDeductionDouble;//总补贴金额
    private  double sumLastVerifAmountDouble;//总最后校验金额
    private Date appDates;//申请时间
    /**
     * 车辆id
     */
    private Long vehicleCode;

    /**
     * 车辆种类
     */
    private Integer vehicleClass;
    private  Integer userType;// 用户类型

    private  String linkman; // 一级审核人
    private String linkman1 ; //二级审核人
    private String linkman2 ;//三级审核人
    private String linkman3 ;//四级审核人
    private String linkman4 ;//五级审核人
}
