package com.youming.youche.cloud.dto.cmb;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName MgrInfoChgReqDto
 * @Description 添加描述
 * @Author zag
 * @Date 2022/1/13 11:09
 */
@Data
public class MgrInfoChgReqDto implements Serializable {
    /** 平台编号 */
    private String platformNo;

    /** 接口调用模式，
     A：商户变更管理员（修改手机号），除邮箱外信息必填
     B：管理员信息修改（不修改手机号），其他信息选填 */
    private String mode;

    /** 商户编号，mode=A时必填，指定变更手机号的商户 */
    private String merchNo;

    /** 当前手机号 */
    private String currMobile;

    /** 变更管理员证件编号，mode=A时必填 */
    private String chgCertNo;

    /** 管理员证件类型：P01，mode=A时必填 */
    private String chgCertType;

    /** 变更管理员姓名，mode=A时必填 */
    private String chgName;

    /** 管理员手机号，mode=A时必填 */
    private String chgMobile;

    /** 管理员邮箱 */
    private String chgEmail;
}
