package com.youming.youche.finance.api.payable;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.finance.dto.DoQueryDto;
import com.youming.youche.finance.dto.PayManagerDto;
import com.youming.youche.finance.dto.PayManagerWXDto;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.dto.QueryPayManagerDto;
import com.youming.youche.order.vo.QueryPayManagerVo;
import com.youming.youche.system.dto.ac.OrderAccountOutDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @InterfaceName IPayByCashService
 * @Description 现金付款
 * @Author luwei
 * @Date 2022/4/7 15:33
 */
public interface IPayByCashService {


    /**
     * 付款
     *
     * @param accId       收款虚拟卡号
     * @param payAccId    付款虚拟卡号
     * @param isAutomatic 0 线下打款 1线上打款
     * @param flowId      付款记录Id
     * @param userType    收款方用户类型
     */
    void confirmation(String accId, String payAccId, Integer isAutomatic, Long flowId, Integer userType,String fileId,String accessToken);

    /**
     * 批量审核
     *
     * @param busiCode     审核Id
     * @param {flowId}       业务主键 多个,分割
     * @param {desc}         备注
     * @param {chooseResult} 1 审核通过 2审核不通过
     * @param {accId }       收款虚拟卡号
     * @param {payAccId}     付款虚拟卡号
     * @param {isAutomatic}  0 线下打款 1线上打款
     * @param {userType}     收款方用户类型
     *   @param {fileId}     图片Id
     */
    void sure(String busiCode, String flowId, String desc, Integer chooseResult, String accId, String payAccId, Integer isAutomatic, Integer userType,String fileId,String expireTime,String serviceFee,String accessToken);

    /***
     * @Description: 验证开票信息
     * @Author: luwei
     * @Date: 2022/4/19 5:09 下午
     * @Param payName:
     * @return: boolean
     * @Version: 1.0
     **/
    boolean payBill(String payName);

    String vehicleOrDriverBill(Long orderId);

    /**
     * 是否有支行,true 有  false 无
     * @param accNo  虚拟卡号 收付虚拟卡号都可以
     * @throws Exception
     */
    public boolean JudgeAmount(String accNo);

    /**
     * 订单尾款回退
     * @param pay
     * @param expireDate
     * @throws Exception
     */
    void dealPayoutIntf(PayoutIntf  pay, LocalDateTime expireDate, String accessToken);

    /**
     * 是否已经输入错误3次
     */
    String doQueryPassType(String accessToken);

    /**
     * 输入支付密码
     *
     * @param accessToken
     * @param payPasswd 密码
     * @param status 1:今天不在输入2:今天还需要输入 SysStaticDataEnum.PWD_STATUS
     * @return
     */
    String doWithdrawal(String accessToken, String payPasswd, Integer status);

    /**
     * 付款撤销
     * @param accessToken
     * @param flowId
     */
    void withdraw(String accessToken, Long flowId);

    /**
     *  判断短信验证码是否正确
     */
    boolean checkSmsCode(String billId, String captcha, String accessToken);

    /**
     * 获取验证码
     */
    boolean doQueryCode(String accessToken);

    /**
     * 发送短信
     *
     * @param templateId
     * @param payoutIntf
     */
    void confirmationSMS(Long templateId, com.youming.youche.finance.domain.munual.PayoutIntf payoutIntf);

    String judgebillLookUp(Long orderId);
    /**
     * 微信付款申请接口编码21180
     */
    Page<QueryPayManagerDto> doQueryAllPayManagerWX(QueryPayManagerVo queryPayManagerVo, Long orgId, Long payAmt, Integer isNeedBiil, Integer payId, String accessToken, Integer pageSize, Integer pageNum);
    /**
     * 微信付款申请付款类型数据获取接口编码21181
     */
    List<DoQueryDto> doQueryPayType(String accessToken);

    /**
     * 微信付款申请审核接口编码
     * @param payId
     * @param state
     * @param payAmt
     * @param isNeedBiil
     * @param accessToken
     * @return
     */
    PayManagerDto getPayManager(Long payId, Integer state,Long payAmt,Integer isNeedBiil,String accessToken);

    /**
     * 借支-车险报告一级审核在22003
     * @param busiId
     * @param desc
     * @param chooseResult
     * @param loanSubject
     * @param remark
     * @param loanTransReason
     * @param accidentDate
     * @param insuranceDate
     * @param accidentType
     * @param accidentReason
     * @param dutyDivide
     * @param accidentDivide
     * @param insuranceFirm
     * @param insuranceMoney
     * @param reportNumber
     * @param accidentExplain
     * @param accessToken
     * @return
     */
    String oaLoanDriverAudit(Long busiId, String desc, String chooseResult,
                             String loanSubject, String remark, Integer loanTransReason,
                             String accidentDate, String insuranceDate, String accidentType,
                             String accidentReason, String dutyDivide, String accidentDivide,
                             String insuranceFirm, String insuranceMoney, String reportNumber, String accidentExplain, String accessToken);


    /**
     * 校验支付密码（对于支付密码处理（正确、错误））
     * @param account 账户信息
     * @param accPwd  未解密的密码
     * @param session
     *
     */
    void DealPassError(Long userId,String accPwd);
}