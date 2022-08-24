package com.youming.youche.system.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.order.api.order.IAccountBankRelService;
import com.youming.youche.order.api.order.IPayFeeLimitService;
import com.youming.youche.order.api.order.other.IBaseBusiToOrderService;
import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.order.dto.order.PayoutIntfDto;
import com.youming.youche.record.common.EnumConsts;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.domain.ServiceProviderBill;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.dto.BillRecordsDto;
import com.youming.youche.system.dto.ServiceBillDto;
import com.youming.youche.system.provider.mapper.ServiceProviderBillMapper;
import com.youming.youche.system.api.IServiceProviderBillService;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.system.provider.utis.ReadisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static com.youming.youche.conts.EnumConsts.PayInter.SERVICE_PROVIDER_ORDER;
import static com.youming.youche.conts.EnumConsts.SysStaticData.SERVICE_BUSI_TYPE;


/**
* <p>
    * 服务商账单表 服务实现类
    * </p>
* @author liangyan
* @since 2022-03-17
*/
@DubboService(version = "1.0.0")
public class ServiceProviderBillServiceImpl extends BaseServiceImpl<ServiceProviderBillMapper, ServiceProviderBill> implements IServiceProviderBillService {

    @Resource
    LoginUtils loginUtils;

    @Resource
    private ReadisUtil readisUtil;

    @Resource
    private ServiceProviderBillMapper serviceProviderBillMapper;

    @Resource
    ISysTenantDefService sysTenantDefService;

    @DubboReference(version = "1.0.0")
    IAccountBankRelService iAccountBankRelService;

    @DubboReference(version = "1.0.0")
    IPayFeeLimitService payFeeLimitService;

    @DubboReference(version = "1.0.0")
    IBaseBusiToOrderService iBaseBusiToOrderService;

    @Resource
    ISysOperLogService sysOperLogService;

    @Override
    public  PageInfo<ServiceProviderBill>  getServiceProviderBillList(Integer pageNum,Integer pageSize,
                                                                      String serviceProviderName, String billRecordsNo,
                                                                      Integer serviceProviderType, Integer paymentStatus,
                                                                      String billNo,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        if(tenantId == null){
            return null;
        }
        PageHelper.startPage(pageNum, pageSize);
        QueryWrapper<ServiceProviderBill> serviceProviderBillQueryWrapper = new QueryWrapper<>();
        serviceProviderBillQueryWrapper.eq("tenant_id",tenantId);
        if(StringUtils.isNotBlank(billNo)){
            serviceProviderBillQueryWrapper.like("bill_no",billNo);
        }
        if(StringUtils.isNoneBlank(serviceProviderName)){
            serviceProviderBillQueryWrapper.like("service_provider_name",serviceProviderName);
        }
        if(StringUtils.isNoneBlank(billRecordsNo)){
            serviceProviderBillQueryWrapper.like("bill_records_no",billRecordsNo);
        }
        if(serviceProviderType != null){
            serviceProviderBillQueryWrapper.eq("service_provider_type",serviceProviderType);
        }
        if(paymentStatus != null){
            serviceProviderBillQueryWrapper.eq("payment_status",paymentStatus);
        }
        serviceProviderBillQueryWrapper.orderByDesc("create_time");

        List<ServiceProviderBill>  serviceProviderBills = serviceProviderBillMapper.selectList(serviceProviderBillQueryWrapper);
        if(serviceProviderBills != null && serviceProviderBills.size() > 0){
            for (ServiceProviderBill serviceProviderBill : serviceProviderBills) {
                if (serviceProviderBill.getServiceProviderType() != null) {
                    String serviceTypeName = readisUtil.getSysStaticData(SERVICE_BUSI_TYPE, serviceProviderBill.getServiceProviderType() .toString()).getCodeName();
                    serviceProviderBill.setServiceProviderTypeName(serviceTypeName);
                }
                if(serviceProviderBill.getPaymentStatus() != null){
                    if(serviceProviderBill.getPaymentStatus() ==1){
                        serviceProviderBill.setPaymentStatusName("待结算");
                    }
                    if(serviceProviderBill.getPaymentStatus() ==2 ){
                        serviceProviderBill.setPaymentStatusName("已结算");
                    }
                    if(serviceProviderBill.getPaymentStatus() == 3){
                        serviceProviderBill.setPaymentStatusName("已打款");
                    }
                }
            }
        }
        PageInfo<ServiceProviderBill> pageInfo=new PageInfo<>(serviceProviderBills);
        return pageInfo;
    }

    @Override
    public ServiceProviderBill queryServiceProviderBill(Long serviceUserId, LocalDateTime time,Long tenantId) {
        LambdaQueryWrapper<ServiceProviderBill> lambda= Wrappers.lambdaQuery();
        lambda.eq(ServiceProviderBill::getServiceUserId,serviceUserId);
        if(time != null){
            lambda.eq(ServiceProviderBill::getCreateTime,time);
        }
        lambda.eq(ServiceProviderBill::getTenantId,tenantId);
        lambda.orderByDesc(ServiceProviderBill::getCreateTime).last("limit 0,1");
        return this.getOne(lambda);
    }

    @Override
    public Page<BillRecordsDto> getServiceProviderBillRecords(Integer serviceProviderType,String billNo, Long tenantId, Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        tenantId = loginInfo.getTenantId();
        Page<BillRecordsDto> page = new Page<>();;
        if (serviceProviderType==1) {
            page = serviceProviderBillMapper.getServiceProviderBillRecords(new Page<>(pageNum, pageSize), serviceProviderType, billNo, tenantId);
        }else if(serviceProviderType==2){
            page = serviceProviderBillMapper.getServiceProviderBillLog(new Page<>(pageNum, pageSize), serviceProviderType, billNo, tenantId);
        }
        return page;
    }

    @Override
    public ServiceBillDto getServiceProviderBillInfo(Long billNo) {
        ServiceBillDto serviceBillInfo = serviceProviderBillMapper.getServiceBillInfo(billNo);


//        if (serviceProviderType==1) {
//            billRecordsDto = serviceProviderBillMapper.getServiceProviderBillInfo(serviceProviderType, billNo, tenantId);
//        } else if(serviceProviderType==2) {
//            billRecordsDto = serviceProviderBillMapper.getServiceProviderBillInfoLog(serviceProviderType, billNo, tenantId);
//        }
        return serviceBillInfo;
    }

    @Override
    public int ServiceProviderBillBalance(Double realityBillAmout, String billNo, Long tenantId,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        tenantId = loginInfo.getTenantId();

        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantId);

        LambdaQueryWrapper<ServiceProviderBill> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceProviderBill::getBillNo, billNo);
        queryWrapper.eq(ServiceProviderBill::getTenantId, tenantId);
        ServiceProviderBill serviceProviderBill = baseMapper.selectOne(queryWrapper);

        // 保存支出接口表
        PayoutIntfDto payoutIntfDto = new PayoutIntfDto();
        payoutIntfDto.setObjType(SysStaticDataEnum.OBJ_TYPE.WITHDRAWAL + "");// 15表示提现
        if (StringUtils.isNotEmpty(loginInfo.getTelPhone())) {
            payoutIntfDto.setObjId(Long.parseLong(loginInfo.getTelPhone()));
        }
        payoutIntfDto.setUserId(serviceProviderBill.getServiceUserId());
        //会员体系改造开始
        payoutIntfDto.setUserType(SysStaticDataEnum.USER_TYPE.SERVICE_USER);
        payoutIntfDto.setPayUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        //会员体系改造结束
        payoutIntfDto.setTxnAmt(new Double(realityBillAmout * 100).longValue());
        payoutIntfDto.setCreateDate(new Date());
        payoutIntfDto.setTenantId(-1L);
        payoutIntfDto.setVehicleAffiliation(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0);
        payoutIntfDto.setWithdrawalsChannel("4");
        payoutIntfDto.setIsDriver(OrderAccountConst.IS_DRIVER.DRIVER);
        payoutIntfDto.setSourceRemark(SysStaticDataEnum.SOURCE_REMARK.SOURCE_REMARK_4);//工资打款
        //打款租户
        payoutIntfDto.setPayTenantId(serviceProviderBill.getTenantId());

        AccountBankRel defalutAccountBankRel = iAccountBankRelService.getDefaultAccountBankRel(serviceProviderBill.getServiceUserId(), EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0, SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        if (defalutAccountBankRel != null) {
            payoutIntfDto.setBankCode(defalutAccountBankRel.getBankName());
            payoutIntfDto.setProvince(defalutAccountBankRel.getProvinceName());
            payoutIntfDto.setCity(defalutAccountBankRel.getCityName());
            payoutIntfDto.setAccNo(defalutAccountBankRel.getPinganCollectAcctId());
            payoutIntfDto.setAccName(defalutAccountBankRel.getAcctName());
        }
        //获取订单数据
        payoutIntfDto.setOrderId(serviceProviderBill.getBillNo());
        /**
         * 初始状态 ： 即使系统自动打款,由于不开票，也要改为手动核销
         * 版本更替 >> 2018-07-04 liujl 接入平安银行改造，改为不需要手动核销司机工资
         */
        payoutIntfDto.setTxnType(OrderAccountConst.TXN_TYPE.XS_TXN_TYPE);//直接打款到银行卡
        payoutIntfDto.setRemark("直接打款到银行卡");

        payoutIntfDto.setPayObjId(sysTenantDef.getAdminUser());
        payoutIntfDto.setPayType(OrderAccountConst.PAY_TYPE.TENANT);//打款类型
        payoutIntfDto.setBusiId(SERVICE_PROVIDER_ORDER);
        payoutIntfDto.setBusiCode(billNo);
        payoutIntfDto.setSubjectsId(EnumConsts.SubjectIds.SALARY_RECEIVABLE);
        payoutIntfDto.setIsDriver(OrderAccountConst.IS_DRIVER.DRIVER);
        payoutIntfDto.setPriorityLevel(OrderAccountConst.PRIORITY_LEVEL.PRIORITY_LEVEL5);//付款优先级别
        payoutIntfDto.setBankType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_RECEIVABLE_ACCOUNT);

        /**
         * 版本更替 >> 2018-07-04 liujl
         * 判断车队是否勾选了自动打款
         * （1）、自动打款
         *       A、对公收款账户的余额转到对公付款账户。
         *       B、判断对公付款账户的余额是否大于预付金额，
         *         大于：转到司机的对私收款账户，在打款记录中增加一条已打款记录（payout_intf表）并已核销，同时触发提现接口。（若提现失败,记录提现失败记录到payout_intf表）
         *         小于：在打款记录中增加一条待打款记录（payout_intf表）并未核销，工资金额增加到车队的应付逾期账户。
         * （2）、不是自动打款
         *       在打款记录中增加一条待打款记录（payout_intf表）并未核销，工资金额增加到车队的应付逾期账户。
         */
        boolean isAutoTransfer = payFeeLimitService.isMemberAutoTransfer(tenantId);
        if (isAutoTransfer) {
            payoutIntfDto.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1);//系统自动打款
        } else {
            payoutIntfDto.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0);//手动打款
        }

        Long type = payFeeLimitService.getAmountLimitCfgVal(tenantId, SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.DRIVER_SARALY_403);

        if (type == 0) {
            payoutIntfDto.setAccountType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT);//私人付款账户余额
        } else if (type == 1) {
            payoutIntfDto.setAccountType(EnumConsts.BALANCE_BANK_TYPE.BUSINESS_PAYABLE_ACCOUNT);//对公付款账户余额
        }
        iBaseBusiToOrderService.doSavePayOutIntfForOA(payoutIntfDto, accessToken);

        int i = 0;
        if (realityBillAmout!=null && realityBillAmout!=0){
            i = serviceProviderBillMapper.ServiceProviderBillBalance(realityBillAmout, billNo, tenantId);
        }

        sysOperLogService.saveSysOperLogSys(SysOperLogConst.BusiCode.ServiceInfo, serviceProviderBill.getId(), SysOperLogConst.OperType.Add, "服务商账单结算", loginInfo.getTenantId());
        return i;
    }

    @Override
    public Long saveServiceProviderBillReturnId(ServiceProviderBill serviceProviderBill) {
        this.save(serviceProviderBill);
        return serviceProviderBill.getId();
    }

    @Override
    public Long getReceivableOverdueBalance(String accessToken) {

        LoginInfo loginInfo = loginUtils.get(accessToken);

        LambdaQueryWrapper<ServiceProviderBill> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceProviderBill::getTenantId, loginInfo.getTenantId());
        queryWrapper.in(ServiceProviderBill::getPaymentStatus, 1, 2);
        List<ServiceProviderBill> list = this.list(queryWrapper);

        double sum = list.stream().mapToDouble(ServiceProviderBill::getBillAmount).sum();

        return new Double(sum).longValue();
    }

}
