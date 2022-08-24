package com.youming.youche.market.dto.facilitator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceSaveInDto  implements Serializable {
    private static final long serialVersionUID = -253340185505743284L;
    /**
     * 供应商账号
     */
    private String loginAcct;
    /**
     * 服务商
     */
    private String serviceName;
    /**
     * 服务商类型（1.油站、2.维修、3.etc供应商）
     */
    private Integer serviceType;
    /**
     * 公司地址
     */
    private String companyAddress;
    /**
     * 联系人
     */
    private String linkman;
    /**
     * 身份证号码
     */
    private String identification;
    /**
     * 身份证图片正面
     */
    private Long idenPictureFront;
    /**
     * 身份证正面路径
     */
    private String idenPictureFrontUrl;
    /**
     * 身份证背面
     */
    private Long  idenPictureBack;
    /**
     * 身份证路径背面
     */
    private String idenPictureBackUrl;
    /**
     * 是否开票（1.是、2.否）
     */
    private Integer isBill;
    /**
     * 是否有开票能力（1.有，2.无）
     */
    private Integer isBillAbility;
    /**
     * 账期
     */
    private Integer paymentDays;
    /**
     * 用户编码
     */
    private Long serviceUserId;
    /**
     * 申请说明
     */
    private String applyReason;
    /**
     * 关联文件ID
     */
    private String fileId;
    /** 文件链接，可直接下载，有效期1小时 */
    private String fileUrl;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 回单图片ID
     */
    private String receiptCode;
    /**
     * 回单图片ID 路徑
     */
    private String receiptCodeUrl;
    /**
     * 收据登录账户
     */
    private String receiptLoginAcct;



    private String puAcctName ;
    private String puAcctNo ;
    private String puBankId ;
    private String puBankName ;
    private String puBranchName ;
    private Long puProvinceId ;
    private Long puCityId ;
    private Long puDistrictId;
    private String pvAcctName ;
    private String pvAcctNo ;
    private String pvBankId ;
    private String pvBankName ;
    private String pvBranchName ;
    private Long pvProvinceId ;
    private Long pvCityId ;
    private Long pvDistrictId;
    private List<Long> applyList;
    private int isRegister;
    /**
     * 用户头像id
     */
    private Long userPrice;
    /**
     * '用户头像路径',
     */
    private String userPriceUrl;
    /**
     * '结算方式，1账期，2月结',
     */
    private Integer balanceType;
    /**
     * '账期结算月份'
     */
    private Integer paymentMonth;
    /**
     * '授信金额'
     */
    private Double quotaAmt;
    /**
     * '平台代收',
     */
    private Integer agentCollection;
}
