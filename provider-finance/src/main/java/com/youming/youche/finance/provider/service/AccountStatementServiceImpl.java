package com.youming.youche.finance.provider.service;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.IAccountStatementDetailsService;
import com.youming.youche.finance.api.IAccountStatementOrderInfoService;
import com.youming.youche.finance.api.IAccountStatementService;
import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.finance.constant.OrderConsts;
import com.youming.youche.finance.domain.AccountStatement;
import com.youming.youche.finance.domain.AccountStatementDetails;
import com.youming.youche.finance.domain.AccountStatementOrderInfo;
import com.youming.youche.finance.dto.AccountStatementAppDto;
import com.youming.youche.finance.dto.AccountStatementUserDto;
import com.youming.youche.finance.dto.DoQueryBillReceiverPageListDto;
import com.youming.youche.finance.dto.OrderFeeDto;
import com.youming.youche.finance.dto.QueryAccountStatementOrdersDto;
import com.youming.youche.finance.dto.order.AccountStatementMarginDto;
import com.youming.youche.finance.dto.order.OrderStatementListOutDto;
import com.youming.youche.finance.dto.order.OrderStatementListToDoubleOutDto;
import com.youming.youche.finance.provider.mapper.AccountStatementDetailsMapper;
import com.youming.youche.finance.provider.mapper.AccountStatementMapper;
import com.youming.youche.finance.provider.mapper.order.OrderInfoMapper;
import com.youming.youche.finance.provider.utils.AcUtil;
import com.youming.youche.finance.provider.utils.MatchAmountUtil;
import com.youming.youche.finance.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.finance.vo.AccountStatementOutVO;
import com.youming.youche.finance.vo.CreateAccountStatementVo;
import com.youming.youche.finance.vo.DoQueryBillReceiverPageListVo;
import com.youming.youche.finance.vo.QueryAccountStatementOrdersVo;
import com.youming.youche.finance.vo.QueryAccountStatementUserVo;
import com.youming.youche.finance.vo.order.AccountStatementCreateVo;
import com.youming.youche.finance.vo.order.AccountStatementInVo;
import com.youming.youche.finance.vo.order.OrderStatementListInMonthDate;
import com.youming.youche.finance.vo.order.OrderStatementListInVo;
import com.youming.youche.finance.vo.order.ReceiverUserInDto;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.order.api.IOilCardManagementService;
import com.youming.youche.order.api.order.IAccountDetailsService;
import com.youming.youche.order.api.order.IBillAgreementService;
import com.youming.youche.order.api.order.IBillPlatformService;
import com.youming.youche.order.api.order.IBusiSubjectsRelService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderOilSourceService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IPayFeeLimitService;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.api.order.IPayoutOrderService;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.dto.OrderInfoDto;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.dto.order.OilCardPledgeOrderListDto;
import com.youming.youche.order.vo.OilCardPledgeOrderListVo;
import com.youming.youche.record.api.tenant.ITenantUserRelService;
import com.youming.youche.record.api.tenant.IUserReceiverInfoService;
import com.youming.youche.record.api.vehicle.ITenantVehicleRelVerService;
import com.youming.youche.record.dto.tenant.TenantVehicleRelQueryDto;
import com.youming.youche.record.vo.tenant.TenantVehicleRelQueryVo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.ITenantStaffRelService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.util.DateUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


/**
* <p>
    *  服务实现类
    * </p>
* @author luona
* @since 2022-04-11
*/
@DubboService(version = "1.0.0")
public class AccountStatementServiceImpl extends BaseServiceImpl<AccountStatementMapper, AccountStatement> implements IAccountStatementService {
    @Resource
    AccountStatementMapper accountStatementMapper;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;

    @Resource
    LoginUtils loginUtils;

    @Resource
    private RedisUtil redisUtil;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Resource
    OrderInfoMapper orderInfoMapper;

    @Resource
    AccountStatementDetailsMapper accountStatementDetailsMapper;

    @DubboReference(version = "1.0.0")
    ITenantVehicleRelVerService tenantVehicleRelVerService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    @DubboReference(version = "1.0.0")
    ITenantStaffRelService tenantStaffRelService;

    @DubboReference(version = "1.0.0")
    ITenantUserRelService tenantUserRelService;

    @DubboReference(version = "1.0.0",check = false)
    IServiceInfoService serviceInfoService;

    @DubboReference(version = "1.0.0")
    IUserReceiverInfoService userReceiverInfoService;

    @DubboReference(version = "1.0.0")
    IOrderLimitService orderLimitService;

    @Resource
    IAccountStatementDetailsService accountStatementDetailsService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @DubboReference(version = "1.0.0")
    IOrderSchedulerService iOrderSchedulerService;

    @DubboReference(version = "1.0.0")
    IOilCardManagementService oilCardManagementService;

    @DubboReference(version = "1.0.0")
    ISysUserService sysUserService;

    @DubboReference(version = "1.0.0")
    IBusiSubjectsRelService iBusiSubjectsRelService;

    @DubboReference(version = "1.0.0")
    IOrderAccountService orderAccountService;

    @DubboReference(version = "1.0.0")
    IAccountDetailsService accountDetailsService;

    @DubboReference(version = "1.0.0")
    IOrderOilSourceService orderOilSourceService;

    @DubboReference(version = "1.0.0")
    IPayFeeLimitService payFeeLimitService;

    @DubboReference(version = "1.0.0")
    IPayoutIntfService payoutIntfService;

    @DubboReference(version = "1.0.0")
    IBillPlatformService billPlatformService;

    @DubboReference(version = "1.0.0")
    IBillAgreementService billAgreementService;

    @DubboReference(version = "1.0.0")
    IOrderSchedulerService orderSchedulerService;

    @DubboReference(version = "1.0.0")
    IOrderSchedulerHService orderSchedulerHService;

    @DubboReference(version = "1.0.0")
    IPayoutOrderService payoutOrderService;

    @Resource
    IAccountStatementOrderInfoService accountStatementOrderInfoService;

    @Override
    public Page<AccountStatementOutVO> getAccountStatements(AccountStatement accountStatement, Integer opType, Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        if(opType!=null && opType==1){
            accountStatement.setTenantId(tenantId);
        }else{
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantId);
            accountStatement.setTenantId(null);
            accountStatement.setReceiverUserId(sysTenantDef.getAdminUser());//获取当前租户超管userId
        }
        Page<AccountStatement> accountVoPage = accountStatementMapper.getAccountStatements(new Page<>(pageNum, pageSize),accountStatement,opType);
        List<AccountStatement> accountStatementList = accountVoPage.getRecords();
        List<AccountStatementOutVO> results = new ArrayList<>();
        if(accountStatementList!=null) {
            for (AccountStatement entity : accountStatementList) {
                AccountStatementOutVO out = new AccountStatementOutVO();
                if (opType != null) {
                    out.setOpType(opType);
                }
                BeanUtils.copyProperties(entity, out);
                out.setDeductionTypeName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "ACCOUNT_STATEMENT_DEDUCTION_TYPE", String.valueOf(entity.getDeductionType())).getCodeName());

                if (opType == 2) {
                    out.setStateName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "ACCOUNT_STATEMENT_STATE_SEND", String.valueOf(entity.getState())).getCodeName());
                } else {
                    out.setStateName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "ACCOUNT_STATEMENT_STATE", String.valueOf(entity.getState())).getCodeName());
                }

                // 新逻辑：对账单状态已结算  未付=总运费-已付
                if (out.getState() == OrderAccountConst.ACCOUNT_STATEMENT.STATE4) {
                    out.setNoPayFee(out.getOrderTotalFee() - out.getPaidFee());
                }
                results.add(out);
            }
        }

        Page<AccountStatementOutVO> outPage = new Page<>();
        outPage.setRecords(results);
        outPage.setCurrent(accountVoPage.getCurrent());
        outPage.setSize(accountVoPage.getSize());
        outPage.setPages(accountVoPage.getPages());
        outPage.setTotal(accountVoPage.getTotal());
        return outPage;
    }

    @Async
    @Override
    public void downloadExcelFile(AccountStatement accountStatement, Integer opType, String accessToken, ImportOrExportRecords importOrExportRecords) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        if(opType!=null && opType==1){
            accountStatement.setTenantId(tenantId);
        }else{
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantId);
            accountStatement.setTenantId(null);
            accountStatement.setReceiverUserId(sysTenantDef.getAdminUser());//获取当前租户超管userId
        }

        List<AccountStatement> accountStatementList = baseMapper.getAccountStatementList(accountStatement, opType);
        List<AccountStatementOutVO> list = new ArrayList<>();
        if(accountStatementList!=null) {
            for (AccountStatement entity : accountStatementList) {
                AccountStatementOutVO out = new AccountStatementOutVO();
                if (opType != null) {
                    out.setOpType(opType);
                }
                BeanUtils.copyProperties(entity, out);
                out.setDeductionTypeName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "ACCOUNT_STATEMENT_DEDUCTION_TYPE", String.valueOf(entity.getDeductionType())).getCodeName());

                if (opType == 2) {
                    out.setStateName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "ACCOUNT_STATEMENT_STATE_SEND", String.valueOf(entity.getState())).getCodeName());
                } else {
                    out.setStateName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "ACCOUNT_STATEMENT_STATE", String.valueOf(entity.getState())).getCodeName());
                }
                list.add(out);
            }
        }

        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{
                    "账单月份","账单接收人","接收人手机",
                    "订单数","总运费","异常补偿","异常扣减",
                    "时效罚款","已付金额",
                    "未付金额"
                   };
            resourceFild = new String[]{
                    "getBillMonth","getReceiverName","getReceiverPhone",
                    "getOrderNum","getOrderTotalFeeDouble","getExceptionInDouble","getExceptionOutDouble",
                    "getTimePenaltyDouble","getPaidFeeDouble",
                    "getNoPayFeeDouble"};

            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(list, showName, resourceFild, AccountStatementOutVO.class,
                    null);

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "对账单列表.xlsx", inputStream.available());
            os.close();
            inputStream.close();
            importOrExportRecords.setMediaUrl(path);
            importOrExportRecords.setState(2);
            importOrExportRecordsService.update(importOrExportRecords);
        } catch (Exception e) {
            importOrExportRecords.setState(3);
            importOrExportRecordsService.update(importOrExportRecords);
            e.printStackTrace();
        }
    }

    @Override
    public void payForCarFeeWriteBack(String busiCode) {
        if (StringUtils.isBlank(busiCode)) {
            throw new BusinessException("对账单业务编号有误");
        }
        AccountStatement as = this.getAccountStatementByBillNumber(busiCode);
        if (as == null) {
            throw new BusinessException("根据对账单id：" + busiCode + " 未找到对账单信息");
        }
        as.setUpdateDate(LocalDateTime.now());
        as.setVerificationState(OrderAccountConst.ACCOUNT_STATEMENT.VERIFICATION_STATE2);
        this.saveOrUpdate(as);
    }

    @Override
    public AccountStatement getAccountStatementByBillNumber(String billNumber) {
        LambdaQueryWrapper<AccountStatement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountStatement::getBillNumber, billNumber);
        List<AccountStatement> list = this.list(queryWrapper);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return new AccountStatement();
    }


    @Override
    @Transactional
    public void createAccountStatement(AccountStatementCreateVo accountStatementCreateVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        if (accountStatementCreateVo.getCreateType() == null) {
            throw new BusinessException("请输入账单创建方式");
        }
        if (accountStatementCreateVo.getBillMonths() == null || accountStatementCreateVo.getBillMonths().size() <= 0) {
            throw new BusinessException("请输入账单月份");
        }
        if (accountStatementCreateVo.getReceiverUserInDtos() == null || accountStatementCreateVo.getReceiverUserInDtos().size() <= 0) {
            throw new BusinessException("请输入账单信息");
        }
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException("请输入账单创建租户id");
        }

        //获取接收人的手机号码
        Set<String> receiverPhoneSet = new HashSet<String>();
        String receiver = "";
        for (ReceiverUserInDto as : accountStatementCreateVo.getReceiverUserInDtos()) {
            if (StringUtils.isNotBlank(as.getBillReceiverMobile())) {
                receiverPhoneSet.add(as.getBillReceiverMobile());
            }
        }
        if (accountStatementCreateVo.getCreateType() == OrderAccountConst.ACCOUNT_STATEMENT.CREATE_TYPE1) {//接收人批量创建

        } else if (accountStatementCreateVo.getCreateType() == OrderAccountConst.ACCOUNT_STATEMENT.CREATE_TYPE2) {//个性化创建
            if (receiverPhoneSet.size() != 1) {
                throw new BusinessException("个性化创建账单，账单接收人只能选一个");
            }
            receiver = accountStatementCreateVo.getReceiverUserInDtos().get(0).getBillReceiverMobile();
        } else {
            throw new BusinessException("输入创建账单方式不正确");
        }
        Map<String, List<AccountStatementInVo>> plateNumberMap = new HashMap<String, List<AccountStatementInVo>>();
        Map<String, List<String>> plateMap = new HashMap<String, List<String>>();

        if (accountStatementCreateVo.getCreateType() == OrderAccountConst.ACCOUNT_STATEMENT.CREATE_TYPE1) {//接收人批量创建
            Iterator<String> it = receiverPhoneSet.iterator();
            TenantVehicleRelQueryVo tenantVehicleRelQueryVo = new TenantVehicleRelQueryVo();
            tenantVehicleRelQueryVo.setTenantId(tenantId);

            //查询已经创建月份账单车牌号
            for (String month : accountStatementCreateVo.getBillMonths()) {
                //获取指定月份 - 接收人 的对账单
                List<AccountStatement> asList = baseMapper.queryAccountStatement(month, receiverPhoneSet, tenantId);
                if (asList != null && asList.size() > 0) {
                    for (AccountStatement as : asList) {
                        // 指定对账单车牌
                        List<String> plateNumbers = new ArrayList<String>();
                        // 获取指定对账单详情
                        List<AccountStatementDetails> details = accountStatementDetailsMapper.getAccountStatementDetails(as.getId(), SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);

                        for (AccountStatementDetails detail : details) {
                            plateNumbers.add(detail.getPlateNumber());
                        }

                        //存在  更新车牌号
                        if (plateMap.get(as.getReceiverPhone()) != null) {
                            List<String> tempPlateList = plateMap.get(as.getReceiverPhone());
                            tempPlateList.addAll(plateNumbers);
                        } else {
                            //不存在添加车牌号
                            if (plateNumbers.size() > 0) {
                                plateMap.put(as.getReceiverPhone(), plateNumbers);
                            }
                        }
                    }
                }
            }

            while (it.hasNext()) {
                String billReceiverMobile = it.next();
                tenantVehicleRelQueryVo.setBillReceiverMobile(billReceiverMobile);

                // 该接受人已存在的车牌
                if (plateMap.get(billReceiverMobile) != null) {
                    tenantVehicleRelQueryVo.setNotInPlateNumber(plateMap.getOrDefault(billReceiverMobile, new ArrayList<>()));
                    tenantVehicleRelQueryVo.setMonList(accountStatementCreateVo.getBillMonths());
                }
                // 查询车辆关系表中的数据
                List<TenantVehicleRelQueryDto> tenantVehicleRelQueryDtos = tenantVehicleRelVerService.doQueryVehicleSimpleInfoNoPage(tenantVehicleRelQueryVo);

                List<AccountStatementInVo> plateNumbers = new ArrayList<AccountStatementInVo>();
                if (tenantVehicleRelQueryDtos != null && tenantVehicleRelQueryDtos.size() > 0) {
                    for (TenantVehicleRelQueryDto tenantVehicleRelQueryDto : tenantVehicleRelQueryDtos) {
                        AccountStatementInVo temp = new AccountStatementInVo();
                        temp.setVehicleCode(tenantVehicleRelQueryDto.getVehicleCode());
                        temp.setPlateNumber(tenantVehicleRelQueryDto.getPlateNumber());
                        temp.setLinkman(tenantVehicleRelQueryDto.getLinkman());
                        temp.setMobilePhone(tenantVehicleRelQueryDto.getMobilePhone());
                        temp.setDriverUserId(tenantVehicleRelQueryDto.getDriverUserId());
                        temp.setBillReceiverUserId(tenantVehicleRelQueryDto.getBillReceiverUserId());
                        temp.setBillReceiverMobile(tenantVehicleRelQueryDto.getBillReceiverMobile());
                        temp.setBillReceiverName(tenantVehicleRelQueryDto.getBillReceiverName());
                        temp.setUserType(0);
                        plateNumbers.add(temp);
                    }
                } else {
                    AccountStatementInVo temp = new AccountStatementInVo();
                    temp.setBillReceiverMobile(billReceiverMobile);
                    for(ReceiverUserInDto in : accountStatementCreateVo.getReceiverUserInDtos()){
                        if(StringUtils.equals(billReceiverMobile,in.getBillReceiverMobile())){
                            temp.setBillReceiverName((in.getBillReceiverName()));
                            temp.setBillReceiverUserId(in.getBillReceiverUserId());
                        }
                    }
                    temp.setUserType(0);
                    plateNumbers.add(temp);
                }
                plateNumberMap.put(billReceiverMobile, plateNumbers);
            }
        } else if (accountStatementCreateVo.getCreateType() == OrderAccountConst.ACCOUNT_STATEMENT.CREATE_TYPE2) {//个性化创建
            plateNumberMap.put(receiver, accountStatementCreateVo.getAccountStatementInVos());
        }

        // 创建对账单
        List<AccountStatement> asList = this.creteAccountStatement(accountStatementCreateVo.getBillMonths(), plateNumberMap, tenantId, loginInfo);

        //保存对账单创建日志
        for (AccountStatement as : asList) {
            SysOperLogConst.BusiCode accountStatementCode = SysOperLogConst.BusiCode.accountStatement;//招商车挂靠车对账单
            sysOperLogService.saveSysOperLog(loginInfo, accountStatementCode,as.getId(), SysOperLogConst.OperType.Add, "创建账单");
        }

    }

    public List<AccountStatement> creteAccountStatement(List<String> billMonths, Map<String, List<AccountStatementInVo>> map,Long tenantId, LoginInfo loginInfo) {
        if (billMonths == null || billMonths.size() <= 0) {
            throw new BusinessException("请输入账单创建月份");
        }
        if (map == null) {
            throw new BusinessException("请输入账单接收人信息");
        }
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException("请输入账单创建租户id");
        }
        Map<String, List<OrderStatementListOutDto>> orderMap = new HashMap<String, List<OrderStatementListOutDto>>();
        OrderStatementListInVo in = new OrderStatementListInVo();
        in.setMonthStr(StringUtils.join(billMonths, ","));
        in.setTenantId(tenantId);
        Iterator<Map.Entry<String, List<AccountStatementInVo>>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, List<AccountStatementInVo>> entry = it.next();
            String receiverPhone = entry.getKey();
            List<AccountStatementInVo> list = entry.getValue();
            List<String> plateNumbers = new ArrayList<String>();
            for (AccountStatementInVo out : list) {
                if (StringUtils.isNotBlank(out.getPlateNumber())) {
                    plateNumbers.add(out.getPlateNumber());
                } else {
                    plateNumbers.add(" ");
                }
            }
            in.setPlateNumbers(plateNumbers);
            List<OrderStatementListOutDto> orders = this.doQueryAccountStatementOrders(in);
            orderMap.put(receiverPhone, orders);
        }

        // 创建对账单
        List<AccountStatement> list = this.createAccountStatement(StringUtils.join(billMonths, ","), orderMap, map, tenantId, loginInfo);
        for (AccountStatement as : list) {
            String billNumber = AcUtil.createAccountStatementNum(redisUtil);
            as.setBillNumber(billNumber);

            AccountStatement accountStatement = baseMapper.selectById(as.getId());
            if (null == accountStatement) {
                baseMapper.insert(as);
            } else {
                baseMapper.updateById(as);
            }
            //保存车辆
            List<AccountStatementInVo> receivers = map.get(as.getReceiverPhone());
            if (as.getCarNum() != null && as.getCarNum() > 0) {
                accountStatementDetailsService.batchSaveDetails(as, receivers, loginInfo);
            }
        }

        return list;
    }

    public List<OrderStatementListOutDto> doQueryAccountStatementOrders(OrderStatementListInVo in) {
        // 处理参数
        this.doOrderStatementListInVo(in);

        List<OrderStatementListOutDto> orders = orderInfoMapper.queryOrderStatementList(in);

        for (OrderStatementListOutDto out : orders) {
            //是否支付预付款   待支付
            if (out.getPreAmountFlag() == null || out.getPreAmountFlag() == OrderConsts.AMOUNT_FLAG.WILL_PAY) {
                out.setPreCashFee(0L);
                out.setPreOilFee(0L);
                out.setPreEtcFee(0L);
                out.setPreOilVirtualFee(0L);
                out.setTotalOilFee(0L);
            }
            //到付状态
            if (out.getArrivePaymentState() == null || out.getArrivePaymentState() == OrderConsts.AMOUNT_FLAG.WILL_PAY) {
                out.setArrivePaymentFee(0L);
            }
            //限制表尾款到期状态
            if (out.getFianlSts() == null) {
                out.setFinalFee(0L);
                out.setExceptionOut(0L);
                out.setFinePrice(0L);
                out.setInsuranceFee(0L);
            }
            //总运费 = 中标价 + 异常补偿
            out.setTotalAmount(out.getTotalFee() + out.getExceptionIn());
            //定金已付金额 = 预付现金 + 预付实体油卡 + 预付etc + 预付虚拟油 + 限制表到付款(抵扣掉异常，时效后的) + 异常补偿 + 限制表已付尾款 = 已付尾款=已到期+预支的尾款+已抵扣尾款  + 异常扣减 + 时效罚款 + 保费
            out.setPaidAmount(out.getPreCashFee() + out.getPreOilFee() + out.getPreEtcFee() + out.getPreOilVirtualFee() + out.getArriveFee() + out.getExceptionIn() + out.getPaidFinal() + Math.abs(out.getExceptionOut() + Math.abs(out.getFinePrice())) + out.getInsuranceFee());
            // 订单未付金额 = 总运费 - 定金已付金额
            out.setNoPayAmount(out.getTotalAmount() - out.getPaidAmount());
        }
        return orders;
    }

    private void doOrderStatementListInVo(OrderStatementListInVo in) {
        // 处理参数
        if (StringUtils.isNotEmpty(in.getMonthStr())) {
            List<OrderStatementListInMonthDate> dateList = new ArrayList<>();
            for (String month : in.getMonthStr().split(",")) {
                OrderStatementListInMonthDate date = new OrderStatementListInMonthDate();
                date.setDateStart(month + "-01 00:00:00");
                try {
                    Date end = DateUtil.formatStringToDate(month, DateUtil.YEAR_MONTH_FORMAT);
                    date.setDateEnd(CommonUtil.getLastMonthDate(end) +" 23:59:59");
                } catch (Exception e) {
                    throw new BusinessException("转换异常");
                }
                dateList.add(date);
            }
            in.setMonthArr(dateList);
        }

        if (StringUtils.isNotEmpty(in.getStartTime())) {
            in.setStartTime(in.getStartTime() + " 00:00:00");
        }

        if (StringUtils.isNotEmpty(in.getEndTime())) {
            in.setEndTime(in.getEndTime() + " 23:59:59");
        }

        if (StringUtils.isNotEmpty(in.getOrderState())) {
            in.setOrderStates(Arrays.stream(in.getOrderState().replaceAll("，",",").split(",")).collect(Collectors.toList()));
        }

        if (StringUtils.isNotEmpty(in.getFinalState())) {
            in.setFinalStates(Arrays.stream(in.getFinalState().replaceAll("，",",").split(",")).collect(Collectors.toList()));
            boolean state = false;
            for (String finalState : in.getFinalStates()) {
                if (finalState.equals("2")) {
                    state = true;
                    break;
                }
            }

            in.setState(state);
        }

        if (StringUtils.isNotEmpty(in.getIsCollection())) {
            String [] str = StringUtils.split(in.getIsCollection().replaceAll("，",","),",");
            //0非代收，1代收
            boolean isCollection0 = false;
            boolean isCollection1 = false;
            for (String state : str) {
                if (state.equals("0")) {
                    isCollection0 = true;
                } else {
                    isCollection1 = true;
                }
            }

            in.setIsCollection0(isCollection0);
            in.setIsCollection1(isCollection1);
        }
    }

    public List<AccountStatement> createAccountStatement(String billMonth,Map<String, List<OrderStatementListOutDto>> orderMap, Map<String, List<AccountStatementInVo>> receiverMap,Long tenantId, LoginInfo loginInfo) {
        if (StringUtils.isBlank(billMonth)) {
            throw new BusinessException("请输入账单创建月份");
        }
        if (orderMap == null) {
            throw new BusinessException("请输入订单信息集合");
        }
        if (receiverMap == null) {
            throw new BusinessException("请输入账单接收人信息");
        }
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException("请输入账单创建租户id");
        }
        SysTenantDef tenant = sysTenantDefService.getSysTenantDef(tenantId);
        if (tenant == null) {
            throw new BusinessException("根据租户id：" + tenantId + "没有找到租户信息");
        }
        List<AccountStatement> result = new ArrayList<AccountStatement>();
        Iterator<Map.Entry<String, List<AccountStatementInVo>>> it = receiverMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, List<AccountStatementInVo>> entry = it.next();
            String receiverPhone = entry.getKey();
            String receiverName = null;
            Long receiverUserId = null;

            UserDataInfo userDataInfo = userDataInfoService.getPhone(receiverPhone);

            // 获取接收人信息
            if(userDataInfo != null){
                receiverName = userDataInfo.getLinkman();
                receiverUserId = userDataInfo.getId();
            }else {
                TenantVehicleRelQueryVo tenantVehicleRelQueryVo = new TenantVehicleRelQueryVo();
                tenantVehicleRelQueryVo.setTenantId(tenantId);
                tenantVehicleRelQueryVo.setBillReceiverMobile(receiverPhone);
                tenantVehicleRelQueryVo.setMonList(Arrays.asList(billMonth.split(",")));
                List<TenantVehicleRelQueryDto> tempResult = tenantVehicleRelVerService.doQueryBillReceiverNoPage(tenantVehicleRelQueryVo);
                if (tempResult != null && tempResult.size() > 0) {
                    receiverName = tempResult.get(0).getBillReceiverName();
                    if (tempResult.get(0).getBillReceiverUserId() != null && StringUtils.isNotBlank(String.valueOf(tempResult.get(0).getBillReceiverUserId()))) {
                        receiverUserId = Long.valueOf(String.valueOf(tempResult.get(0).getBillReceiverUserId()));
                    }
                }
            }

            Map<String, Object> inParam = new HashMap<String, Object>();
            inParam.put("tenantId", tenantId);
            inParam.put("billReceiverMobile", receiverPhone);

            List<AccountStatementInVo> list = entry.getValue();
            if (list == null || list.size() <= 0) {
                throw new BusinessException("账单接收人信息为空");
            }

            int carTemp = 0;
            Set<String> userTypes = new HashSet();
            for(AccountStatementInVo statementIn : list) {
                if (statementIn.getUserType() > 0) {
                    userTypes.add(statementIn.getUserType() + "");
                }
                if (StringUtils.isNotBlank(statementIn.getPlateNumber())) {
                    carTemp++;
                }
            }

            int carNum = carTemp;
            List<OrderStatementListOutDto> orderAlls = orderMap.get(receiverPhone);

            int orderNum = orderAlls !=  null ? orderAlls.size() : 0;
            Map<String,List<OrderStatementListOutDto>> utOrderMap = new HashMap<>();
            if (orderAlls != null && orderAlls.size()>0) {
                orderAlls.forEach(o->{
                    if(StringUtils.isNotBlank(o.getUserType())){
                        userTypes.add(o.getUserType());
                    }
                });
                userTypes.forEach(ut->{
                    List<OrderStatementListOutDto> outs = new ArrayList<>();
                    orderAlls.forEach(o->{
                        if(StringUtils.isNotBlank(o.getUserType()) && StringUtils.equals(ut,o.getUserType())){
                            outs.add(o);
                        }
                    });
                    utOrderMap.put(ut,outs);
                });
            } else if(!userTypes.isEmpty()){
                userTypes.forEach(ut-> utOrderMap.put(ut,null));
            } else if(userTypes.isEmpty()){
                //如果该用户没有订单，也没有收款人角色，则创建收款人角色，并且对账单的角色为收款人
                boolean exist = false;
                if(receiverUserId != null){
                    List<Integer> userTypeList = getUserType(receiverUserId, tenantId);
                     if(userTypeList != null && userTypeList.size()>0){
                        for(Integer ut : userTypeList) {
                            if (ut == SysStaticDataEnum.USER_TYPE.RECEIVER_USER) {
                                exist = true;
                            }
                        }
                    }
                }
                if(!exist){
                    userReceiverInfoService.initUserReceiverInfo(receiverPhone, receiverName, loginInfo);
                }
                utOrderMap.put(SysStaticDataEnum.USER_TYPE.RECEIVER_USER+"",null);
            }

            if(utOrderMap != null&&utOrderMap.size()>0){
                String finalReceiverName = receiverName;
                Long finalReceiverUserId = receiverUserId;
                utOrderMap.forEach((ut, orders)->{
                    int usertype = Integer.parseInt(ut);
                    Long orderTotalFee = 0L;
                    Long exceptionIn = 0L;
                    Long exceptionOut = 0L;
                    Long timePenalty = 0L;
                    Long oilTurnCash = 0L;
                    Long etcTurnCash = 0L;
                    Long paidFee = 0L;
                    Long noPayFee = 0L;
                    Long oilCardDeposit = 0L;
                    Long marginBalance = 0L;
                    if (orders != null) {
                        for (OrderStatementListOutDto out : orders) {
                            if(finalReceiverUserId != null) {
                                if (out.getUserId().toString().equals(finalReceiverUserId.toString())) {
                                    orderTotalFee += out.getTotalAmount();//中标价+异常补偿
                                    exceptionIn += out.getExceptionIn();
                                    exceptionOut += Math.abs(out.getExceptionOut());
                                    timePenalty += Math.abs(out.getFinePrice());
                                    oilTurnCash += out.getOilTurnCash();
                                    etcTurnCash += out.getEtcTurnCash();
                                    paidFee += out.getPaidAmount();
                                    noPayFee += out.getNoPayAmount();
                                    oilCardDeposit += out.getPledgeOilcardFee();
                                }
                            }
                        }
                    }
                    if (finalReceiverUserId != null && finalReceiverUserId > 0) {//账单接收人所有未到期金额，不是账单月份的订单
                        List<OrderLimit> limits = null;
                        try {
                            limits = orderLimitService.getOrderLimit(finalReceiverUserId, "NoPayFinal",tenantId,usertype);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (limits != null && limits.size() > 0) {
                            for (OrderLimit limit : limits) {
                                marginBalance += limit.getNoPayFinal() == null ? 0 : limit.getNoPayFinal();
                            }
                        }
                    }
                    AccountStatement as = null;
                    try {
                        // 说明  原来已付是paidFee算的   新逻辑  已付默认是0   只有点结算的时候的金额就是已付
                        as = this.createAccountStatement(billMonth, finalReceiverName, receiverPhone, finalReceiverUserId, carNum,
                                0L, OrderAccountConst.ACCOUNT_STATEMENT.DEDUCTION_TYPE1, orderNum, orderTotalFee, exceptionIn, exceptionOut,
                                timePenalty, oilTurnCash, etcTurnCash, paidFee, noPayFee, oilCardDeposit, marginBalance,
                                OrderAccountConst.ACCOUNT_STATEMENT.STATE1, OrderAccountConst.ACCOUNT_STATEMENT.VERIFICATION_STATE0, tenantId, tenant.getName(), tenant.getLinkPhone(),usertype, loginInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    result.add(as);

                });
            }

        }
        return result;
    }

    public AccountStatement createAccountStatement(String billMonth, String receiverName, String receiverPhone,
                                                   Long receiverUserId, int carNum, Long carTotalFee, Integer deductionType, int orderNum,
                                                   Long orderTotalFee, Long exceptionIn, Long exceptionOut, Long timePenalty, Long oilTurnCash,
                                                   Long etcTurnCash, Long paidFee, Long noPayFee, Long oilCardDeposit, Long marginBalance, Integer state,
                                                   Integer verificationState, Long tenantId, String tenantName, String tenantBill,int userType, LoginInfo loginInfo) {

        AccountStatement as = new AccountStatement();
        as.setBillMonth(billMonth);
        as.setReceiverName(receiverName);
        as.setReceiverPhone(receiverPhone);
        as.setReceiverUserId(receiverUserId);
        as.setCarNum(carNum);
        as.setCarTotalFee(carTotalFee);
        as.setDeductionType(deductionType);
        as.setOrderNum(orderNum);
        as.setOrderTotalFee(orderTotalFee);
        as.setExceptionIn(exceptionIn);
        as.setExceptionOut(exceptionOut);
        as.setTimePenalty(timePenalty);
        as.setOilTurnCash(oilTurnCash);
        as.setEtcTurnCash(etcTurnCash);
        // 已付  设置了一个默认值  只有创建的时候会赋值 (结算的时候  手动赋值)  其他的时候  不能操作
//        as.setPaidFee(paidFee);
        as.setPaidFee(0L);
        as.setNoPayFee(noPayFee);
        as.setOilCardDeposit(oilCardDeposit);
        as.setMarginBalance(marginBalance);
        as.setState(state);
        as.setVerificationState(verificationState);
        as.setTenantId(tenantId);
        as.setTenantBill(tenantBill);
        as.setTenantName(tenantName);
        as.setPayUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        as.setReceUserType(userType);
        as.setCreateDate(LocalDateTime.now());
        as.setUpdateDate(LocalDateTime.now());
        if (loginInfo != null) {
            as.setOpId(loginInfo.getId());
            as.setUpdateOpId(loginInfo.getId());
        }
        return as;
    }

    private List<Integer> getUserType(long userId, Long tenantId) {
        List<Integer> userType = new ArrayList<>();

        // 超级管理员
        if (sysTenantDefService.isAdminUser(userId, tenantId)) {
            userType.add(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        }

        // 员工
        if (tenantStaffRelService.isStaff(userId, tenantId)) {
            userType.add(SysStaticDataEnum.USER_TYPE.CUSTOMER_USER);
        }

        // 司机
        if (tenantUserRelService.isDriver(userId, tenantId)) {
            userType.add(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        }

       //服务商
        if (serviceInfoService.isService(userId, tenantId)) {
            userType.add(SysStaticDataEnum.USER_TYPE.SERVICE_USER);
        }

        //收款人
        if (userReceiverInfoService.isReceiver(userId, tenantId)) {
            userType.add(SysStaticDataEnum.USER_TYPE.RECEIVER_USER);
        }
        return userType;
    }

    @Override
    public void saveOrUpdateAccountStatementDetail(AccountStatement as, List<AccountStatementDetails> details, String operType, boolean isWriteLog, LoginInfo loginInfo) {
        if (as == null) {
            throw new BusinessException("请输入对账单信息");
        }
        if (details == null ) {
            throw new BusinessException("请输入对账单车辆信息");
        }
        if (StringUtils.isBlank(operType)) {
            throw new BusinessException("请输入操作类型");
        }
        if (as.getState() != null && (as.getState() == OrderAccountConst.ACCOUNT_STATEMENT.STATE4)) {
            throw new BusinessException("对账单状态为已结算时，不可修改车辆信息");
        }
        long totalCarFee = as.getCarTotalFee() == null ? 0L : as.getCarTotalFee();
        long marginBalance = as.getMarginBalance() == null ? 0L : as.getMarginBalance();

        Long totalFee = 0L;
        for (AccountStatementDetails detail : details) {
            totalFee += detail.getTotalFee() == null ? 0L : detail.getTotalFee();
        }
        if (OrderAccountConst.ACCOUNT_STATEMENT.UPDATE.equals(operType)) {
            if (totalFee != totalCarFee) {
                as.setCarTotalFee(totalFee);
                as.setState(OrderAccountConst.ACCOUNT_STATEMENT.STATE1);
                as.setSettlementAmount(null);
            }
        }  else {
            AccountStatement accountStatementNew = this.calculationAccountStatement(as, details, operType, loginInfo);
            if (accountStatementNew == null) {
                throw new BusinessException("计算对账单相关费用出错");
            }
            as.setCarNum(accountStatementNew.getCarNum());
            as.setCarTotalFee(accountStatementNew.getCarTotalFee());
            as.setOrderNum(accountStatementNew.getOrderNum());
            as.setOrderTotalFee(accountStatementNew.getOrderTotalFee());
            as.setExceptionIn(accountStatementNew.getExceptionIn());
            as.setExceptionOut(accountStatementNew.getExceptionOut());
            as.setTimePenalty(accountStatementNew.getTimePenalty());
            as.setOilTurnCash(accountStatementNew.getOilTurnCash());
            as.setEtcTurnCash(accountStatementNew.getEtcTurnCash());
            as.setPaidFee(accountStatementNew.getPaidFee());
            as.setNoPayFee(accountStatementNew.getNoPayFee());
            as.setOilCardDeposit(accountStatementNew.getOilCardDeposit());
            as.setMarginBalance(marginBalance);
            if (totalCarFee != accountStatementNew.getCarTotalFee().longValue() || marginBalance != accountStatementNew.getMarginBalance().longValue()) {
                if (OrderAccountConst.ACCOUNT_STATEMENT.STATE1 != as.getState() && OrderAccountConst.ACCOUNT_STATEMENT.STATE5 != as.getState()) {
                    as.setState(OrderAccountConst.ACCOUNT_STATEMENT.STATE1);
                }
                as.setSettlementAmount(null);
            }
        }
        as.setUpdateDate(LocalDateTime.now());
        as.setUpdateOpId(loginInfo.getId());
        if (as.getId() != null || baseMapper.selectById(as.getId()) != null) {
            baseMapper.updateById(as);
        } else {
            baseMapper.insert(as);
        }
        if(isWriteLog){
            SysOperLogConst.BusiCode accountStatementCode = SysOperLogConst.BusiCode.accountStatement;//招商车挂靠车对账单
            sysOperLogService.saveSysOperLog(loginInfo, accountStatementCode,as.getId(), SysOperLogConst.OperType.Add, "修改车辆费用信息");
        }

    }

    public AccountStatement calculationAccountStatement(AccountStatement as, List<AccountStatementDetails> details, String operType, LoginInfo loginInfo) {
        if (as == null) {
            throw new BusinessException("请输入对账单信息");
        }
		/*if (details == null || details.size() <= 0) {
			throw new BusinessException("请输入对账单车辆信息");
		}*/
        if (as.getState() != null && (as.getState() == OrderAccountConst.ACCOUNT_STATEMENT.STATE4)) {
            throw new BusinessException("对账单状态为已结算时，不可修改对账单信息");
        }

        String billMonths = as.getBillMonth();
        Long tenantId = as.getTenantId();
        Long totalFee = 0L;
        List<String> plateNumbers = new ArrayList<String>();
        if (details != null && details.size() >0) {
            for (AccountStatementDetails detail : details) {
                totalFee += detail.getTotalFee() == null ? 0L : detail.getTotalFee();
                if(StringUtils.isNotBlank(detail.getPlateNumber())){
                    plateNumbers.add(detail.getPlateNumber());
                }else {
                    plateNumbers.add(" ");
                }
            }
        } else {
            plateNumbers.add(" ");
        }

        OrderStatementListInVo in = new OrderStatementListInVo();
        in.setMonthStr(billMonths);
        in.setTenantId(tenantId);
        in.setPlateNumbers(plateNumbers);
        List<OrderStatementListOutDto> orders = this.doQueryAccountStatementOrders(in);
        Map<String, List<OrderStatementListOutDto>> orderMap = new HashMap<String, List<OrderStatementListOutDto>>();
        orderMap.put(as.getReceiverPhone(), orders);
        Map<String, List<AccountStatementInVo>> receiverMap = new HashMap<String, List<AccountStatementInVo>>();
        List<AccountStatementInVo> asiList = new ArrayList<AccountStatementInVo>();
        AccountStatementInVo asi = new AccountStatementInVo();
        asi.setBillReceiverUserId(as.getReceiverUserId());
        asi.setBillReceiverMobile(as.getReceiverPhone());
        asi.setBillReceiverName(as.getReceiverName());
        asi.setUserType(as.getReceUserType());
        asiList.add(asi);
        receiverMap.put(as.getReceiverPhone(), asiList);
        //新的AccountStatement
        AccountStatement accountStatementNew = null;
        List<AccountStatement> list = this.createAccountStatement(billMonths, orderMap, receiverMap, tenantId, loginInfo);
        if(list != null && list.size()>0){
            for(AccountStatement statement : list){
                if(StringUtils.equals(statement.getReceUserType()+"" , as.getReceUserType()+"")){
                    accountStatementNew = statement;
                }
            }
        }
        if (accountStatementNew == null) {
            throw new BusinessException("计算对账单相关费用出错");
        }
        accountStatementNew.setCarNum(details.size());
        accountStatementNew.setCarTotalFee(totalFee);
        return accountStatementNew;
    }

    @Override
    @Transactional
    public void sendAccountStatement(Long flowId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (flowId == null || flowId <= 0) {
            throw new BusinessException("请输入对账单id");
        }
        AccountStatement as = baseMapper.selectById(flowId);
        if (as == null) {
            throw new BusinessException("根据对账单id：" + flowId + " 未找到对账单信息");
        }
        if (as.getState() != null && (as.getState() != OrderAccountConst.ACCOUNT_STATEMENT.STATE1 && as.getState() != OrderAccountConst.ACCOUNT_STATEMENT.STATE5)) {
            throw new BusinessException("对账单状态不是待发送或被驳回，不可发送账单");
        }
//        if (as.getSettlementAmount() == null) {
//            throw new BusinessException("对账单结算金额还未设置，不可发送账单");
//        }

        // 重新计算对账单费用
        accountStatementDetailsService.refreshEtcFeeForBill(flowId, loginInfo);
        as.setUpdateDate(LocalDateTime.now());
        as.setUpdateOpId(loginInfo.getUserInfoId());
        as.setSendTime(LocalDateTime.now());
        as.setState(OrderAccountConst.ACCOUNT_STATEMENT.STATE2);

        baseMapper.updateById(as);
        SysOperLogConst.BusiCode accountStatementCode = SysOperLogConst.BusiCode.accountStatement;//招商车挂靠车对账单
        sysOperLogService.saveSysOperLog(loginInfo, accountStatementCode,as.getId(), SysOperLogConst.OperType.Add, "发送账单");
    }

    @Override
    @Transactional
    public void deleteAccountStatement(Long flowId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        if (flowId == null || flowId <= 0) {
            throw new BusinessException("请输入对账单id");
        }

        //TODO:上锁操作
//        boolean isLock = SysContexts.getLock(this.getClass().getName() + "settlementAccountStatement" + flowId, 3, 5);
//        if (!isLock) {
//            throw new BusinessException("请求过于频繁，请稍后再试!");
//        }
        AccountStatement as = baseMapper.selectById(flowId);
        if (as == null) {
            throw new BusinessException("根据对账单id：" + flowId + " 未找到对账单信息");
        }
        if (!(as.getState() == OrderAccountConst.ACCOUNT_STATEMENT.STATE1 || as.getState() == OrderAccountConst.ACCOUNT_STATEMENT.STATE5)) {
            throw new BusinessException("仅未发送和被驳回的对账单可以删除！");
        }

        as.setUpdateDate(LocalDateTime.now());
        as.setUpdateOpId(loginInfo.getUserInfoId());
        as.setState(OrderAccountConst.ACCOUNT_STATEMENT.STATE0);

        baseMapper.updateById(as);

        LambdaQueryWrapper<AccountStatementDetails> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountStatementDetails::getAccountStatementId, flowId);

        List<AccountStatementDetails> accountStatementDetails = accountStatementDetailsMapper.selectList(queryWrapper);
        if(accountStatementDetails != null && accountStatementDetails.size()>0){
            accountStatementDetails.forEach(ad->{
                // 移除指定对账单详情信息
                accountStatementDetailsService.removeDetail(ad.getId(), loginInfo);
            });
        }

        SysOperLogConst.BusiCode accountStatementCode = SysOperLogConst.BusiCode.accountStatement;//招商车挂靠车对账单
        sysOperLogService.saveSysOperLog(loginInfo, accountStatementCode,as.getId(), SysOperLogConst.OperType.Del, "结算账单");

        // 刷新对账单的费用
        accountStatementDetailsService.refreshEtcFeeForBill(flowId, loginInfo);
    }

    @Override
    public AccountStatementMarginDto queryAccountStatementMargin(Long flowId) {
        if (flowId == null || flowId <= 0) {
            throw new BusinessException("请输入对账单id");
        }
        AccountStatement as = baseMapper.selectById(flowId);
        if (as == null) {
            throw new BusinessException("根据对账单id：" + flowId + " 未找到对账单信息");
        }

        AccountStatementMarginDto result = new AccountStatementMarginDto();
        String billMonth = as.getBillMonth();
        Long tenantId = as.getTenantId();
        List<String> plateNumbers = new ArrayList<String>();

        // 查询对账单明细
        List<AccountStatementDetails> details = accountStatementDetailsMapper.getAccountStatementDetails(as.getId(), SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        if (details != null && details.size() >0) {
            for (AccountStatementDetails detail : details) {
                plateNumbers.add(detail.getPlateNumber());
            }
        } else {
            plateNumbers.add(" ");
        }
        OrderStatementListInVo in = new OrderStatementListInVo();
        in.setMonthStr(billMonth);
        in.setTenantId(tenantId);
        in.setPlateNumbers(plateNumbers);
        // 查询对账单订单信息
        List<OrderStatementListOutDto> list = this.doQueryAccountStatementOrders(in);
        String billMonths = as.getBillMonth();
        String[] arr = billMonths.split(",");
        List<Map<String, Object>> rtnList = new ArrayList();
        for (String month : arr) {
            Map<String, Object> balanceList = new HashMap<String, Object>();
            Long marginBalance = 0L;
            for (OrderStatementListOutDto out : list) {
                String dependMonth = DateUtil.formatDateByFormat(out.getDependTime(), DateUtil.YEAR_MONTH_FORMAT);
                if (as.getReceiverPhone().equals(out.getUserPhone()) && month.equals(dependMonth)) {
                    marginBalance += out.getNoPayFinal();
                }
            }
            balanceList.put("month", month); // 月份
            balanceList.put("value", marginBalance); // 金额
            rtnList.add(balanceList);
        }
        result.setFlowId(flowId);
        result.setBillMonth(billMonths);
        result.setSettlementAmount(as.getSettlementAmount() == null ? 0L : as.getSettlementAmount());
        result.setSettlementRemark(as.getSettlementRemark());
        result.setBalanceList(rtnList);
        return result;
    }

    @Override
    @Transactional
    public void setUpSettlementAmount(Long flowId, Long settlementAmount, String settlementRemark, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        if (flowId == null || flowId <= 0) {
            throw new BusinessException("请输入对账单id");
        }
        if (settlementAmount == null || settlementAmount < 0) {
            throw new BusinessException("结算金额不能为空或小于0");
        }
        AccountStatement as = baseMapper.selectById(flowId);
        if (as == null) {
            throw new BusinessException("根据对账单id：" + flowId + " 未找到对账单信息");
        }
        if (as.getState() != null && (as.getState() == OrderAccountConst.ACCOUNT_STATEMENT.STATE4)) {
            throw new BusinessException("对账单状态为已结算时，不可设置结算金额");
        }
        Long marginBalance = as.getMarginBalance() == null ? 0L : as.getMarginBalance();
        Long carTotalFee = as.getCarTotalFee() == null ? 0L : as.getCarTotalFee();
        long settlementAmountMore = 0L;//最大可结算金额
        if (as.getDeductionType() == OrderAccountConst.ACCOUNT_STATEMENT.DEDUCTION_TYPE1) {//尾款抵扣
            if (marginBalance >= carTotalFee) {
                settlementAmountMore = (marginBalance - carTotalFee);
            }
        } else if (as.getDeductionType() == OrderAccountConst.ACCOUNT_STATEMENT.DEDUCTION_TYPE2) {//现金收取
            settlementAmountMore = marginBalance;
        } else {
            throw new BusinessException("对账单扣费方式不正确");
        }
        if (settlementAmount > settlementAmountMore) {
            throw new BusinessException("对账单结算金额不可以大于最大可结算金额");
        }

        as.setUpdateDate(LocalDateTime.now());
        as.setUpdateOpId(loginInfo.getUserInfoId());
        as.setSettlementAmount(settlementAmount);
        as.setSettlementRemark(settlementRemark);
        if (as.getState() != null && (as.getState() == OrderAccountConst.ACCOUNT_STATEMENT.STATE3)) {
            as.setState(OrderAccountConst.ACCOUNT_STATEMENT.STATE2);
        }
        baseMapper.updateById(as);
        SysOperLogConst.BusiCode accountStatementCode = SysOperLogConst.BusiCode.accountStatement;//招商车挂靠车对账单
        sysOperLogService.saveSysOperLog(loginInfo, accountStatementCode,as.getId(), SysOperLogConst.OperType.Add, "结算金额设置：" + (double)settlementAmount / 100);
    }

    @Override
    @Transactional
    public void settlementAmount(Long flowId, Long settlementAmount, String settlementRemark, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        if (flowId == null || flowId <= 0) {
            throw new BusinessException("请输入对账单id");
        }
        if (settlementAmount == null || settlementAmount < 0) {
            throw new BusinessException("结算金额不能为空或小于0");
        }
        AccountStatement as = baseMapper.selectById(flowId);
        if (as == null) {
            throw new BusinessException("根据对账单id：" + flowId + " 未找到对账单信息");
        }
        if (as.getState() != null && (as.getState() == OrderAccountConst.ACCOUNT_STATEMENT.STATE4)) {
            throw new BusinessException("对账单状态为已结算时，不可设置结算金额");
        }

        Long marginBalance = as.getMarginBalance() == null ? 0L : as.getMarginBalance();
        Long carTotalFee = as.getCarTotalFee() == null ? 0L : as.getCarTotalFee();

        long settlementAmountMore = 0L;//最大可结算金额
        //TODO 暂时先注释掉  这块有点模糊
//        if (as.getDeductionType() == OrderAccountConst.ACCOUNT_STATEMENT.DEDUCTION_TYPE1) {//尾款抵扣
//            if (marginBalance >= carTotalFee) {
//                settlementAmountMore = (marginBalance - carTotalFee);
//            }
//        } else if (as.getDeductionType() == OrderAccountConst.ACCOUNT_STATEMENT.DEDUCTION_TYPE2) {//现金收取
//            settlementAmountMore = marginBalance;
//        } else {
//            throw new BusinessException("对账单扣费方式不正确");
//        }
//        if (settlementAmount > settlementAmountMore) {
//            throw new BusinessException("对账单结算金额不可以大于最大可结算金额");
//        }

        Date date = new Date();
        as.setUpdateDate(LocalDateTime.now());
        as.setUpdateOpId(loginInfo.getUserInfoId());
        as.setSettlementAmount(settlementAmount);
        as.setSettlementRemark(settlementRemark);
        as.setPaidFee(settlementAmount);
        as.setState(OrderAccountConst.ACCOUNT_STATEMENT.STATE4);
        baseMapper.updateById(as);

        // 新系统的应该只有现金收取
        if (as.getDeductionType() != null && as.getDeductionType() == OrderAccountConst.ACCOUNT_STATEMENT.DEDUCTION_TYPE1) {//1尾款扣除
            this.marginSettlementAmount(as, accessToken);
        } else if (as.getDeductionType() != null && as.getDeductionType() == OrderAccountConst.ACCOUNT_STATEMENT.DEDUCTION_TYPE2) {//2现金收取
            this.cashSettlementAmount(as, accessToken);
        } else {
            throw new BusinessException("对账单结算方式不正确");
        }

        //刷新对账单的ETC费用，在发送、确认、结算对账单时调用
        accountStatementDetailsService.refreshEtcFeeForBill(flowId, loginInfo);

        // 防止已付金额冲掉  在做一次
        as.setPaidFee(settlementAmount);
        baseMapper.updateById(as);

        SysOperLogConst.BusiCode accountStatementCode = SysOperLogConst.BusiCode.accountStatement;//招商车挂靠车对账单
        sysOperLogService.saveSysOperLog(loginInfo, accountStatementCode,as.getId(), SysOperLogConst.OperType.Add, "结算账单 结算金额：" + (double)settlementAmount / 100);

        //修改该对账单下所有车辆匹配的ETC消费记录，将状态改为已结算，同时反写ETC消费记录对应的对账单编号
        accountStatementDetailsService.updateEtcToBill(as, loginInfo);
    }

    @Override
    public Page<OrderStatementListToDoubleOutDto> getAccountStatementOrders(Long flowId, OrderStatementListInVo in, Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tId = loginInfo.getTenantId();

        in.setTenantId(tId);

        if (flowId == null) {
            throw new BusinessException("请输入对账单id");
        }
        if (in == null) {
            throw new BusinessException("请输入查询条件参数对象");
        }
        AccountStatement as = baseMapper.selectById(flowId);
        if (as == null) {
            throw new BusinessException("根据对账单id：" + flowId + " 未找到对账单信息");
        }
        in.setUserId(as.getReceiverUserId());
        in.setUserPhone(as.getReceiverPhone());
        String billMonth = as.getBillMonth();
        Long tenantId = as.getTenantId();
        List<String> plateNumbers = new ArrayList<String>();

        // 对账单详情
        List<AccountStatementDetails> details = accountStatementDetailsMapper.getAccountStatementDetails(flowId, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        if (details != null && details.size() > 0) {
            for (AccountStatementDetails detail : details) {
                plateNumbers.add(detail.getPlateNumber());
            }
        } else {
            plateNumbers.add(" ");
        }

        in.setMonthStr(billMonth);
        in.setTenantId(tenantId);
        in.setPlateNumbers(plateNumbers);
        in.setUserType(as.getReceUserType()+"");

        Page<OrderStatementListOutDto> orderInfoVoPage = new Page<>(pageNum, pageSize);
        // 处理参数
        this.doOrderStatementListInVo(in);

        // 对账单订单信息
        Page<OrderStatementListOutDto> page = orderInfoMapper.queryOrderStatementPage(orderInfoVoPage, in);
        Page<OrderStatementListToDoubleOutDto> dtoPage = new Page<>();
        dtoPage.setPages(page.getPages());
        dtoPage.setSize(page.getSize());
        dtoPage.setCurrent(page.getCurrent());
        dtoPage.setTotal(page.getTotal());

        List<OrderStatementListOutDto> list = page.getRecords();
        List<OrderStatementListToDoubleOutDto> orders = new ArrayList<OrderStatementListToDoubleOutDto>();
        for (OrderStatementListOutDto map : list) {
            OrderStatementListToDoubleOutDto temp = new OrderStatementListToDoubleOutDto();
            BeanUtils.copyProperties(map, temp);
            //20190514 订单收款人和账单的接收人不一致就是代收
            if (temp.getUserId().equals(as.getReceiverUserId()) && temp.getUserType().equals(as.getReceUserType()+"")) {
                temp.setIsCollection(0);
            } else {
                temp.setIsCollection(1);
            }

            orders.add(temp);
        }

        for (OrderStatementListToDoubleOutDto out : orders) {
            if (out.getOrderState() != null) {
                out.setOrderStateName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "ORDER_STATE", String.valueOf(out.getOrderState())).getCodeName());
            }

            if (out.getPreAmountFlag() == null || out.getPreAmountFlag() == OrderConsts.AMOUNT_FLAG.WILL_PAY) {
                out.setPreCashFee(0L);
                out.setPreOilFee(0L);
                out.setPreEtcFee(0L);
                out.setPreOilVirtualFee(0L);
                out.setTotalOilFee(0L);
            }
            if (out.getArrivePaymentState() == null || out.getArrivePaymentState() == OrderConsts.AMOUNT_FLAG.WILL_PAY) {
                out.setArrivePaymentFee(0L);
            }
            if (out.getFianlSts() == null) {
                out.setFinalFee(0L);
                out.setExceptionOut(0L);
                out.setFinePrice(0L);
                out.setInsuranceFee(0L);
            }
            out.setTotalAmount(out.getTotalFee() + out.getExceptionIn());
            //已付金额 =预付款+到付款+异常补偿+已付尾款（到期、预支、尾款结算对帐单、招商车辆费用抵扣）+|异常扣减|+|时效罚款|+保险  +油卡押金释放(未到期转到期)
            out.setPaidAmount(out.getPreCashFee() + out.getPreOilFee() + out.getPreEtcFee() + out.getPreOilVirtualFee() + out.getArriveFee() + out.getExceptionIn() + out.getPaidFinal() + Math.abs(out.getExceptionOut() + Math.abs(out.getFinePrice())) + out.getInsuranceFee());
            out.setNoPayAmount(out.getTotalAmount() - out.getPaidAmount());
            OrderInfoDto orderInfoDto = iOrderSchedulerService.queryOrderLineString(out.getOrderId());
            if (orderInfoDto != null) {
                out.setOrderLine(orderInfoDto.getOrderLine());
                out.setIsTransitLine(orderInfoDto.getIsTransitLine());
            }
        }

        dtoPage.setRecords(orders);
        return dtoPage;
    }

    @Override
    public Integer getAccountStatementCount(Long userId) {
        LambdaQueryWrapper<AccountStatement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountStatement::getReceiverUserId, userId);
        //queryWrapper.eq(AccountStatement::getState, 2);
        //queryWrapper.isNotNull(AccountStatement::getState);
        return this.count(queryWrapper);
    }

    @Override
    public Page<AccountStatementAppDto> getAccountStatementFromApp(Long receiverUserId, String billMonth, String state, String tenantName, String accessToken, Integer pageNum, Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        Page<AccountStatement> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AccountStatement> queryWrapper = new LambdaQueryWrapper<>();
        //queryWrapper.gt(AccountStatement::getState, 1);
        if (receiverUserId != null && receiverUserId > 0) {
            queryWrapper.eq(AccountStatement::getReceiverUserId, receiverUserId);
        }
        if (StringUtils.isNotBlank(billMonth)) {
            queryWrapper.like(AccountStatement::getBillMonth, billMonth);
        }
        if (StringUtils.isNotBlank(tenantName)) {
            queryWrapper.like(AccountStatement::getTenantName, tenantName);
        }
        if (StringUtils.isNotBlank(state)) {
            queryWrapper.in(AccountStatement::getState, Arrays.stream(state.replaceAll("，", ",").split(",")).collect(Collectors.toList()));
        }
        queryWrapper.orderByDesc(AccountStatement::getSendTime);

        Page<AccountStatement> accountStatementPage = baseMapper.selectPage(page, queryWrapper);
        Page<AccountStatementAppDto> dtoPage = new Page<>();
        dtoPage.setSize(accountStatementPage.getSize());
        dtoPage.setTotal(accountStatementPage.getTotal());
        dtoPage.setPages(accountStatementPage.getPages());
        dtoPage.setCurrent(accountStatementPage.getCurrent());

        List<AccountStatementAppDto> list = new ArrayList<>();
        for (AccountStatement accountStatement : accountStatementPage.getRecords()) {
            AccountStatementAppDto appDto = new AccountStatementAppDto();
            BeanUtil.copyProperties(accountStatement, appDto);

            if (accountStatement.getState() != null) {
                appDto.setStateName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "ACCOUNT_STATEMENT_STATE_SEND", String.valueOf(accountStatement.getState())).getCodeName());
            }

            if (accountStatement.getDeductionType() != null) {
                appDto.setDeductionTypeName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId,"ACCOUNT_STATEMENT_DEDUCTION_TYPE", String.valueOf(accountStatement.getDeductionType())).getCodeName());
            }

            list.add(appDto);
        }
        dtoPage.setRecords(list);
        return dtoPage;
    }

    @Transactional
    @Override
    public void confirmAccountStatement(Long flowId, String isPass, String remark, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        if (flowId == null || flowId <= 0) {
            throw new BusinessException("请输入对账单id");
        }
        if (StringUtils.isBlank(isPass)) {
            throw new BusinessException("确认账单时请选择通过或驳回");
        }
        if (OrderAccountConst.ACCOUNT_STATEMENT.IS_PASS_N.equals(isPass)) {
            if (StringUtils.isBlank(remark)) {
                throw new BusinessException("驳回账单时，必须输入驳回理由");
            }
        }
        AccountStatement as = baseMapper.selectById(flowId);
        if (as == null) {
            throw new BusinessException("根据对账单id：" + flowId + " 未找到对账单信息");
        }
        if (as.getState() != null && as.getState() != OrderAccountConst.ACCOUNT_STATEMENT.STATE2 ) {
            throw new BusinessException("对账单状态不是待确认，不可确认账单");
        }

        // 刷新对账单的ETC费用，在发送、确认、结算对账单时调用
        accountStatementDetailsService.refreshEtcFeeForBill(flowId, loginInfo);
        as.setUpdateDate(LocalDateTime.now());
        as.setUpdateOpId(loginInfo.getUserInfoId());
        String pass = "";
        if (OrderAccountConst.ACCOUNT_STATEMENT.IS_PASS_Y.equals(isPass)) {
            as.setState(OrderAccountConst.ACCOUNT_STATEMENT.STATE3);
            pass = "通过";
        } else {
            as.setState(OrderAccountConst.ACCOUNT_STATEMENT.STATE5);
            pass = "驳回";
        }
        as.setRemark(remark);
        baseMapper.updateById(as);
        SysOperLogConst.BusiCode accountStatementCode = SysOperLogConst.BusiCode.accountStatement;//招商车挂靠车对账单
        sysOperLogService.saveSysOperLog(loginInfo, accountStatementCode,as.getId(), SysOperLogConst.OperType.Add, pass + "账单，理由：" + (StringUtils.isNotEmpty(remark) ? remark : "") );
    }

    @Override
    public AccountStatementAppDto getAccountStatementDetailFromApp(Long flowId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        if (flowId == null || flowId <= 0) {
            throw new BusinessException("请输入对账单id");
        }
        AccountStatement as = baseMapper.selectById(flowId);
        if (as == null) {
            throw new BusinessException("根据对账单id：" + flowId + " 未找到对账单信息");
        }
        AccountStatementAppDto dto = new AccountStatementAppDto();
        BeanUtil.copyProperties(as, dto);

        if (as.getState() != null) {
            dto.setStateName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "ACCOUNT_STATEMENT_STATE_SEND", String.valueOf(as.getState())).getCodeName());
        }

        if (as.getDeductionType() != null) {
            dto.setDeductionTypeName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId,"ACCOUNT_STATEMENT_DEDUCTION_TYPE", String.valueOf(as.getDeductionType())).getCodeName());
        }

        return dto;
    }

    @Override
    public Page<OilCardPledgeOrderListDto> getOilCardDetailFromApp(Long flowId, Integer pageNum, Integer pageSize) {
        OilCardPledgeOrderListVo in = new OilCardPledgeOrderListVo();
        if (flowId == null || flowId <= 0) {
            throw new BusinessException("请输入对账单id");
        }

        AccountStatement as = baseMapper.selectById(flowId);
        if (as == null) {
            throw new BusinessException("根据对账单id：" + flowId + " 未找到对账单信息");
        }
        String billMonth = as.getBillMonth();
        Long tenantId = as.getTenantId();
        List<String> plateNumbers = new ArrayList<String>();

        // 对账单详情信息
        List<AccountStatementDetails> details = accountStatementDetailsMapper.getAccountStatementDetails(as.getId(), SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        for (AccountStatementDetails detail : details) {
            plateNumbers.add(detail.getPlateNumber());
        }

        if(plateNumbers != null && plateNumbers.size() > 0){
            OrderStatementListInVo vo = new OrderStatementListInVo();
            vo.setMonthStr(billMonth);
            vo.setTenantId(tenantId);
            vo.setPlateNumbers(plateNumbers);

            // 处理参数
            this.doOrderStatementListInVo(vo);

            // 对账单订单信息
            List<OrderStatementListOutDto> orderList = orderInfoMapper.queryOrderStatementList(vo);
            List<Long> orderIds = new ArrayList<Long>();
            Map<String, Object> map = new HashMap<String, Object>();
            if (orderList != null && orderList.size() > 0) {
                for (OrderStatementListOutDto out : orderList) {
                    if (out.getPledgeOilcardFee() != null && out.getPledgeOilcardFee() > 0) {
                        orderIds.add(out.getOrderId());
                        map.put(String.valueOf(out.getOrderId()), out.getPledgeOilcardFee());
                    }
                }
                in.setOrderIds(orderIds);
            }

            if (orderIds != null && orderIds.size() > 0){
                in.setPageNum(pageNum);
                in.setPageSize(pageSize);
                Page<OilCardPledgeOrderListDto> pledgeOrderListDtoPage = oilCardManagementService.queryOilCardPledgeOrderInfo(in);
                for (OilCardPledgeOrderListDto record : pledgeOrderListDtoPage.getRecords()) {
                    Long amount = (Long) map.get(String.valueOf(record.getOrderId()));
                    record.setAmount(amount);
                    record.setAmountDouble(CommonUtil.getDoubleFormatLongMoney(amount, 2));
                }

                return pledgeOrderListDtoPage;
            }
        }
        return new Page<>(pageNum, pageSize);
    }

    @Override
    public void marginSettlementAmount(AccountStatement as, String accessToken) {
        if (as == null) {
            throw new BusinessException("对账单信息不正确");
        }
        if (as.getDeductionType() != null && as.getDeductionType() == OrderAccountConst.ACCOUNT_STATEMENT.DEDUCTION_TYPE1) {//1尾款扣除
        } else {
            throw new BusinessException("对账单结算方式不是尾款扣除方式");
        }
        //判断账单接收人是否存在系统中
        boolean receiverExist = true;

        SysUser sysUser = null;
        if (as.getReceiverUserId() != null && as.getReceiverUserId() > 0) {
            sysUser = sysUserService.getSysOperatorByUserId(as.getReceiverUserId());
            if (sysUser == null) {
                throw new BusinessException("根据账单接收人id" + as.getReceiverUserId() + "未找到账单接收人信息");
            }
            receiverExist = true;
        } else {
            receiverExist = false;
        }
        if (!receiverExist) {
            return;
        }

        Long tenantId = as.getTenantId();
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantId);
        if (sysTenantDef == null) {
            throw new BusinessException("根据租户id：" + tenantId + " 没有找到租户信息");
        }

        long marginBalance = 0l;
        List<OrderLimit> limits = new ArrayList<OrderLimit>();
        if (as.getReceiverUserId() != null && as.getReceiverUserId() > 0) {//账单接收人所有未到期金额，不是账单月份的订单
            limits = orderLimitService.getOrderLimit(as.getReceiverUserId(), "NoPayFinal",tenantId,as.getReceUserType());
            if (limits != null && limits.size() > 0) {
                for (OrderLimit limit : limits) {
                    marginBalance += limit.getNoPayFinal() == null ? 0 : limit.getNoPayFinal();
                }
            }
        }

        if (marginBalance != as.getMarginBalance()) {
            throw new BusinessException("账单接收人订单未到期余额不等于对账单上的账户余额");
        }
        if ((as.getMarginBalance().longValue() - as.getCarTotalFee().longValue()) >= 0 && as.getSettlementAmount().longValue() > (as.getMarginBalance().longValue() - as.getCarTotalFee().longValue())) {
            throw new BusinessException("账单结算金额不能大于账户余额抵扣车辆费用后的余额");
        }
        if ((as.getMarginBalance() - as.getCarTotalFee()) < 0 && as.getSettlementAmount() > 0) {
            throw new BusinessException("账单结算金额不能大于账户余额抵扣车辆费用后的余额");
        }

        /**
         * 说明：
         *     1、车辆总费用>0  账户未到期金额>0  ==>  插入payout_intf表
         *     2、车辆总费用>0  账单接收人存在  ==> payout_intf表
         *     3、结算金额>0    订单付款回显orderLimit有数据  ==> payout_intf表
         */

        if (as.getCarTotalFee() > 0) {//有车辆费用
            System.out.println("marginBalance: " + as.getMarginBalance() + "\tcarToTalFee:" + as.getCarTotalFee());
            if (as.getMarginBalance() >= as.getCarTotalFee()) {
                this.deductionCarFee(limits, as.getCarTotalFee(), as.getReceiverUserId(), tenantId, as, accessToken);
                as.setVerificationState(OrderAccountConst.ACCOUNT_STATEMENT.VERIFICATION_STATE1);
            } else if (as.getMarginBalance() > 0) {
                this.deductionCarFee(limits, as.getMarginBalance(), as.getReceiverUserId(), tenantId, as, accessToken);
                long amount = as.getCarTotalFee() - as.getMarginBalance();
                this.payForCarFee(amount, as.getReceiverUserId(), tenantId, as.getBillNumber(), as, accessToken);
            } else {//没有未到期金额
                if (receiverExist) { //账单接收人必须存在系统才生出应收应付，否则不生成流水
                    this.payForCarFee(as.getCarTotalFee(), as.getReceiverUserId(), tenantId, as.getBillNumber(),as, accessToken);
                }
            }
        }

        if (as.getSettlementAmount() > 0) {//有结算金额
            for (OrderLimit ol : limits) {
                ol.setMatchAmount(null);
                ol.setMatchIncome(null);
                ol.setMatchBackIncome(null);
            }
            this.marginPaySettlementAmount(limits,as.getSettlementAmount(),as.getReceiverUserId(), tenantId,as, accessToken);
        }
    }

    /**
     * 未到期抵扣车辆费用
     * @param limits
     * @param amount
     * @param userId
     * @param tenantId
     * @param as
     */
    public void deductionCarFee(List<OrderLimit> limits,Long amount,Long userId,Long tenantId,AccountStatement as, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);


        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantId);
        if (sysTenantDef == null) {
            throw new BusinessException("根据租户id：" + tenantId + " 没有找到租户信息");
        }

        SysUser tenantSysOperator = sysUserService.getSysOperatorByUserId(sysTenantDef.getAdminUser());
        SysUser sysOperator = sysUserService.getSysOperatorByUserId(userId);
        if (sysOperator == null) {
            throw new BusinessException("根据账单接收人id" + userId + "未找到账单接收人信息");
        }
        MatchAmountUtil.matchAmount(amount, 0, 0, "noPayFinal", limits);
        long totalMatchAmount = 0;
        long soNbr = CommonUtil.createSoNbr();
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();

        //未到期抵扣车辆费用
        BusiSubjectsRel amountFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ACCOUNT_STATEMENT_DEDUCTION, 0L);
        busiList.add(amountFeeSubjectsRel);

        //计算费用合集
        List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.ACCOUNT_STATEMENT, busiList);
        for (OrderLimit limit : limits) {

            // 匹配金额存在
            if (limit.getMatchAmount() != null && limit.getMatchAmount() > 0) {
                Long orderId = limit.getOrderId(); // 订单号
                String vehicleAffiliation = limit.getVehicleAffiliation();// 车辆归属
                String oilAffiliation = limit.getOilAffiliation();//资金(油)渠道类型
                for (BusiSubjectsRel bsr : busiSubjectsRelList) {
                    if (bsr.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_STATEMENT_DEDUCTION) {
                        bsr.setAmountFee(limit.getMatchAmount());
                    }
                }

                OrderAccount account = orderAccountService.queryOrderAccount(userId, vehicleAffiliation, 0L, tenantId,oilAffiliation,as.getReceUserType());

                // 写入账户明细表并修改账户金额费用
                accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.ACCOUNT_STATEMENT,
                        tenantSysOperator.getUserInfoId(), tenantSysOperator.getOpName(), account, busiSubjectsRelList, soNbr, orderId,
                        sysOperator.getOpName(), null, tenantId, null, "", null, vehicleAffiliation, loginInfo);

                // 写入订单限制表和订单资金流向表
                ParametersNewDto inParam = orderOilSourceService.setParametersNew(sysOperator.getUserInfoId(), sysOperator.getBillId(),
                        EnumConsts.PayInter.ACCOUNT_STATEMENT, orderId, amount, vehicleAffiliation, "");

                inParam.setTenantId(tenantId);
                inParam.setBatchId(String.valueOf(soNbr));
                inParam.setOrderLimitBase(limit);

                orderOilSourceService.busiToOrderNew(inParam, busiSubjectsRelList, loginInfo);
                totalMatchAmount += limit.getMatchAmount();
                limit.setMatchAmount(null);
                limit.setMatchIncome(null);
                limit.setMatchBackIncome(null);
            }
        }

        if (totalMatchAmount != amount) {
            throw new BusinessException("订单分摊抵扣金额与车辆费用总金额一致");
        }
    }

    /**
     * 账单接收人付款给账单创建车队
     * @param amount 需要支付车辆费用金额
     * @param userId 账单接收人
     * @param tenantId 账单创建人租户id
     * @throws Exception
     */
    public void payForCarFee(Long amount, Long userId, Long tenantId, String busiCode, AccountStatement as, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        if (amount == null || amount <= 0) {
            throw new BusinessException("请输入正确的金额");
        }
        SysTenantDef tenantSysTenantDef = sysTenantDefService.getSysTenantDef(tenantId);
        if (tenantSysTenantDef == null) {
            throw new BusinessException("根据租户id：" + tenantId + " 没有找到租户信息");
        }
        SysUser tenantSysOperator = sysUserService.getSysOperatorByUserId(tenantSysTenantDef.getAdminUser());
        SysUser sysOperator = sysUserService.getSysOperatorByUserId(userId);
        if (sysOperator == null) {
            throw new BusinessException("根据账单接收人id" + userId + "未找到账单接收人信息");
        }

        //查询用户是否车队
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
        boolean isTenant = false;
        if (sysTenantDef != null) {
            if (sysTenantDef.getVirtualState() != null && sysTenantDef.getVirtualState() != 1) { //排除掉代收
                isTenant = true;
            }
        }

        String vehicleAffiliation = OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0;
        String oilAffiliation = OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0;

        OrderAccount account = orderAccountService.queryOrderAccount(userId, vehicleAffiliation, 0L, tenantId,oilAffiliation,as.getReceUserType());
        long soNbr = CommonUtil.createSoNbr();
        //账单接收人应付逾期
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel amountFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ACCOUNT_STATEMENT_CAR_PAYABLE, amount);
        busiList.add(amountFeeSubjectsRel);
        // 计算费用集合
        List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.ACCOUNT_STATEMENT, busiList);

        // 写入账户明细表并修改账户金额费用
        accountDetailsService.insetAccDet(EnumConsts.BusiType.EXCEPTION_CODE, EnumConsts.PayInter.ACCOUNT_STATEMENT,
                tenantSysOperator.getUserInfoId(), tenantSysOperator.getOpName(), account, busiSubjectsRelList, soNbr,0L, sysOperator.getOpName(), null, tenantId, null, "", null, vehicleAffiliation, loginInfo);

        //账单创建人应收逾期
        OrderAccount fleetAccount = orderAccountService.queryOrderAccount(tenantSysTenantDef.getAdminUser(), vehicleAffiliation, 0L, tenantId,oilAffiliation,as.getPayUserType());
        List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel receivableOverdueRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ACCOUNT_STATEMENT_CAR_RECEIVABLE, amount);
        fleetBusiList.add(receivableOverdueRel);

        // 计算费用集合
        List<BusiSubjectsRel> fleetSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.ACCOUNT_STATEMENT, fleetBusiList);
        accountDetailsService.insetAccDet(EnumConsts.BusiType.EXCEPTION_CODE, EnumConsts.PayInter.ACCOUNT_STATEMENT,
                sysOperator.getUserInfoId(), sysOperator.getOpName(), fleetAccount, fleetSubjectsRelList, soNbr, 0L,
                tenantSysOperator.getOpName(), null, tenantId, null, "", null, vehicleAffiliation, loginInfo);
        busiSubjectsRelList.addAll(fleetSubjectsRelList);

        //是否自动打款
        boolean isAutoTransfer = false;
        if (isTenant) {
            isAutoTransfer = payFeeLimitService.isMemberAutoTransfer(sysTenantDef.getId());
        }
        Integer isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
        if (isAutoTransfer) {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
        } else {
            isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
        }

        PayoutIntf payoutIntf = payoutIntfService.createPayoutIntf(tenantSysTenantDef.getAdminUser(), OrderAccountConst.PAY_TYPE.TENANT, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, amount, tenantId, vehicleAffiliation, 0L,
                -1L, isAutomatic, isAutomatic, userId, OrderAccountConst.PAY_TYPE.USER, EnumConsts.PayInter.ACCOUNT_STATEMENT, EnumConsts.SubjectIds.ACCOUNT_STATEMENT_CAR_PAYABLE,oilAffiliation,as.getPayUserType(),as.getReceUserType(),0L, accessToken);

        payoutIntf.setObjId(Long.valueOf(tenantSysOperator.getBillId()));
        payoutIntf.setRemark("招商车挂靠车车辆费用");

        if (isTenant) {
            payoutIntf.setPayType(OrderAccountConst.PAY_TYPE.TENANT);
            payoutIntf.setPayTenantId(sysTenantDef.getId());
        }
        payoutIntf.setBusiCode(busiCode);
        if (OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation)) {
            payoutIntf.setAccountType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT);//付款
            payoutIntf.setBankType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_RECEIVABLE_ACCOUNT);//收款
        } else if (OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
            payoutIntf.setAccountType(EnumConsts.BALANCE_BANK_TYPE.BUSINESS_PAYABLE_ACCOUNT);//付款
            payoutIntf.setBankType(EnumConsts.BALANCE_BANK_TYPE.BUSINESS_RECEIVABLE_ACCOUNT);//收款
        } else {
            throw new BusinessException("不可选择平台票");
        }

        payoutIntfService.doSavePayOutIntfForOA(payoutIntf, accessToken);

        // 写入订单限制表和订单资金流向表
        ParametersNewDto inParam = orderOilSourceService.setParametersNew(sysOperator.getUserInfoId(), sysOperator.getBillId(),
                EnumConsts.PayInter.ACCOUNT_STATEMENT, 0L, amount, vehicleAffiliation, "");
        inParam.setTenantUserId(tenantSysTenantDef.getAdminUser());
        inParam.setTenantBillId(tenantSysOperator.getBillId());
        inParam.setTenantUserName(tenantSysTenantDef.getLinkMan());

        orderOilSourceService.busiToOrder(inParam, busiSubjectsRelList, loginInfo);
    }

    /**
     * 未到期支付账单结算金额
     * @param limits
     * @param amount
     * @param userId
     * @param tenantId
     * @param as
     * @param accessToken
     */
    public void marginPaySettlementAmount(List<OrderLimit> limits, Long amount, Long userId, Long tenantId, AccountStatement as, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        SysTenantDef tenantSysTenantDef = sysTenantDefService.getSysTenantDef(tenantId);
        if (tenantSysTenantDef == null) {
            throw new BusinessException("根据租户id：" + tenantId + " 没有找到租户信息");
        }
        SysUser tenantSysOperator = sysUserService.getSysOperatorByUserId(tenantSysTenantDef.getAdminUser());
        SysUser sysOperator = sysUserService.getSysOperatorByUserId(userId);
        if (sysOperator == null) {
            throw new BusinessException("根据账单接收人id" + userId + "未找到账单接收人信息");
        }
        //查询用户是否车队
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDefByAdminUserId(userId);
        boolean isTenant = false;
        if (sysTenantDef != null) {
            isTenant = true;
        }
        MatchAmountUtil.matchAmount(amount, 0, 0, "noPayFinal", limits);

        long totalMatchAmount = 0;
        long soNbr = CommonUtil.createSoNbr();
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();

        //结算金额应收逾期
        BusiSubjectsRel receivableRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ACCOUNT_STATEMENT_MARGIN_RECEIVABLE, 0L);
        busiList.add(receivableRel);
        BusiSubjectsRel serviceFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.RECEIVABLE_IN_SERVICE_FEE_1698, 0L);
        busiList.add(serviceFeeSubjectsRel);
        BusiSubjectsRel marginRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ACCOUNT_STATEMENT_MARGIN, 0L);
        busiList.add(marginRel);

        //结算金额应付逾期
        BusiSubjectsRel payableRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.ACCOUNT_STATEMENT_MARGIN_PAYABLE, 0L);
        fleetBusiList.add(payableRel);
        BusiSubjectsRel payableServiceFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.PAYABLE_IN_SERVICE_FEE_4010, 0L);
        fleetBusiList.add(payableServiceFeeSubjectsRel);
        // 计算费用集合
        List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.ACCOUNT_STATEMENT, busiList);
        List<BusiSubjectsRel> fleetBusiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.ACCOUNT_STATEMENT, fleetBusiList);

        for (OrderLimit limit : limits) {
            if (limit.getMatchAmount() != null && limit.getMatchAmount() > 0) {
                Long orderId = limit.getOrderId();
                String vehicleAffiliation = limit.getVehicleAffiliation();
                String oilAffiliation = limit.getOilAffiliation();
                //路歌开票 服务费 20190717  司机应收账户不记服务费
                long serviceFee = 0;
                //应收
                for (BusiSubjectsRel bsr : busiSubjectsRelList) {
                    if (bsr.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_STATEMENT_MARGIN_RECEIVABLE || bsr.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_STATEMENT_MARGIN) {
                        bsr.setAmountFee(limit.getMatchAmount());
                    }
                }

                OrderAccount account = orderAccountService.queryOrderAccount(userId, vehicleAffiliation, 0L, tenantId,oilAffiliation,as.getReceUserType());

                // 写入账户明细表并修改账户金额费用
                accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.ACCOUNT_STATEMENT,
                        tenantSysOperator.getUserInfoId(), tenantSysOperator.getOpName(), account, busiSubjectsRelList, soNbr, orderId,
                        sysOperator.getOpName(), null, tenantId, null, "", null, vehicleAffiliation, loginInfo);

                //应付
                for (BusiSubjectsRel bsr : fleetBusiSubjectsRelList) {
                    if (bsr.getSubjectsId() == EnumConsts.SubjectIds.ACCOUNT_STATEMENT_MARGIN_PAYABLE) {
                        bsr.setAmountFee(limit.getMatchAmount());
                    }
                    if (bsr.getSubjectsId() == EnumConsts.SubjectIds.PAYABLE_IN_SERVICE_FEE_4010) {
                        boolean isLuge = billPlatformService.judgeBillPlatform(Long.parseLong(vehicleAffiliation), String.valueOf(SysStaticDataEnum.BILL_FORM_STATIC.BILL_LUGE));
                        if (isLuge) {
                            Map<String, Object> result = billAgreementService.calculationServiceFee(Long.parseLong(vehicleAffiliation), limit.getMatchAmount(), 0L, 0L, limit.getMatchAmount(), tenantId,null);
                            serviceFee = (Long) result.get("lugeBillServiceFee");
                            if (serviceFee > 0) {
                                bsr.setAmountFee(serviceFee);
                            }
                        }
                    }
                }

                OrderAccount fleetAccount = orderAccountService.queryOrderAccount(tenantSysTenantDef.getAdminUser(), vehicleAffiliation, 0L, tenantId,oilAffiliation,as.getPayUserType());

                // 写入账户明细表并修改账户金额费用
                accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.ACCOUNT_STATEMENT,
                        sysOperator.getUserInfoId(), sysOperator.getOpName(), fleetAccount, fleetBusiSubjectsRelList, soNbr, orderId,
                        tenantSysOperator.getOpName(), null, tenantId, null, "", null, vehicleAffiliation, loginInfo);

                //生成待打款支付记录
                //是否自动打款
                boolean isAutoTransfer = payFeeLimitService.isMemberAutoTransfer(account.getSourceTenantId());
                Integer isAutomatic = null;
                if (isAutoTransfer) {
                    isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1;
                } else {
                    isAutomatic = OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0;
                }

                PayoutIntf payoutIntf = payoutIntfService.createPayoutIntf(userId, OrderAccountConst.PAY_TYPE.USER, OrderAccountConst.TXN_TYPE.XX_TXN_TYPE, limit.getMatchAmount(), -1L, vehicleAffiliation, orderId,
                        tenantId, isAutomatic, isAutomatic, tenantSysTenantDef.getAdminUser(), OrderAccountConst.PAY_TYPE.TENANT, EnumConsts.PayInter.ACCOUNT_STATEMENT, EnumConsts.SubjectIds.ACCOUNT_STATEMENT_MARGIN_RECEIVABLE,oilAffiliation,as.getPayUserType(),as.getReceUserType(),serviceFee, accessToken);

                // 说明：原来的话   生成打款记录的时候  金额是计算出来的   现在用结算时候的金额
                payoutIntf.setTxnAmt(as.getSettlementAmount());

                payoutIntf.setObjId(Long.valueOf(sysOperator.getBillId()));
                payoutIntf.setRemark("未到期结算账单金额");

                if (isTenant) {
                    payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.TENANT);
                    payoutIntf.setTenantId(sysTenantDef.getId());
                }
                if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
                        !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                    payoutIntf.setIsDriver(OrderAccountConst.PAY_TYPE.HAVIR);
                }

                OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
                payoutIntf.setBusiCode(String.valueOf(orderId));
                if (orderScheduler != null) {
                    payoutIntf.setPlateNumber(orderScheduler.getPlateNumber());
                } else {
                    OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
                    if (orderSchedulerH != null) {
                        payoutIntf.setPlateNumber(orderSchedulerH.getPlateNumber());
                    }
                }

                payoutIntfService.doSavePayOutIntfVirToVir(payoutIntf, accessToken);
                if (!OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0.equals(vehicleAffiliation) &&
                        !OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION1.equals(vehicleAffiliation)) {
                    //写入payout_order
                    payoutOrderService.createPayoutOrder(userId, limit.getMatchAmount(), OrderAccountConst.FEE_TYPE.CASH_TYPE, payoutIntf.getId(), orderId, tenantId, String.valueOf(vehicleAffiliation));
                }

                // 写入订单限制表和订单资金流向表
                ParametersNewDto inParam = orderOilSourceService.setParametersNew(sysOperator.getUserInfoId(), sysOperator.getBillId(),
                        EnumConsts.PayInter.ACCOUNT_STATEMENT, orderId, amount, vehicleAffiliation, "");
                inParam.setTenantId(tenantId);
                inParam.setBatchId(String.valueOf(soNbr));
                inParam.setTenantUserId(tenantSysTenantDef.getAdminUser());
                inParam.setTenantBillId(tenantSysOperator.getBillId());
                inParam.setTenantUserName(tenantSysTenantDef.getLinkMan());
                inParam.setOrderLimitBase(limit);

                orderOilSourceService.busiToOrderNew(inParam, busiSubjectsRelList, loginInfo);
                totalMatchAmount += limit.getMatchAmount();
                limit.setMatchAmount(null);
                limit.setMatchIncome(null);
                limit.setMatchBackIncome(null);

            }
        }

        if (totalMatchAmount != amount) {
            throw new BusinessException("订单分摊抵扣金额与结算总金额不一致");
        }
    }

    @Override
    public void cashSettlementAmount(AccountStatement as, String accessToken) {
        if (as == null) {
            throw new BusinessException("对账单信息不正确");
        }
        if (as.getDeductionType() != null && as.getDeductionType() == OrderAccountConst.ACCOUNT_STATEMENT.DEDUCTION_TYPE1) {//1尾款扣除
            throw new BusinessException("对账单结算方式不是现金收取扣除方式");
        } else {

        }
        //判断账单接收人是否存在系统中
        boolean receiverExist = true;
        SysUser sysOperator = null;
        if (as.getReceiverUserId() != null && as.getReceiverUserId() > 0) {
            sysOperator = sysUserService.getSysOperatorByUserId(as.getReceiverUserId());
            if (sysOperator == null) {
                throw new BusinessException("根据账单接收人id" + as.getReceiverUserId() + "未找到账单接收人信息");
            }
            receiverExist = true;
        } else {
            receiverExist = false;
        }

        if (!receiverExist) {
            return;
        }
        Long tenantId = as.getTenantId();

        long marginBalance = 0l;
        List<OrderLimit> limits = new ArrayList<OrderLimit>();
        if (as.getReceiverUserId() != null && as.getReceiverUserId() > 0) {//账单接收人所有未到期金额，不是账单月份的订单
            limits = orderLimitService.getOrderLimit(as.getReceiverUserId(), "NoPayFinal",tenantId,as.getReceUserType());
            if (limits != null && limits.size() > 0) {
                for (OrderLimit limit : limits) {
                    marginBalance += limit.getNoPayFinal() == null ? 0 : limit.getNoPayFinal();
                }
            }
        }

        if (marginBalance != as.getMarginBalance()) {
            throw new BusinessException("账单接收人订单未到期余额不等于对账单上的账户余额");
        }
        if (as.getSettlementAmount().longValue() > as.getMarginBalance().longValue()) {
            throw new BusinessException("账单结算金额不能大于账户余额");
        }
        //账单接收人存在系统，生成打款记录
        if (receiverExist) {
            if (as.getCarTotalFee() > 0) {//有车辆费用
                this.payForCarFee(as.getCarTotalFee(), as.getReceiverUserId(), tenantId, as.getBillNumber(),as, accessToken);
            }
            if (as.getSettlementAmount() > 0) {//结算金额
                this.marginPaySettlementAmount(limits,as.getSettlementAmount(),as.getReceiverUserId(), tenantId,as, accessToken);
            }
        }
    }

    @Override
    public Page<DoQueryBillReceiverPageListDto> doQueryBillReceiverPageList(DoQueryBillReceiverPageListVo vo, Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        if (vo.getTenantId() == null) {
            vo.setTenantId(tenantId);
        }

        if (vo.getVehicleClass() != null && vo.getVehicleClass() > 0) {
            if (vo.getVehicleClass() != SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 && vo.getVehicleClass() != SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4) {
                throw new BusinessException("车辆类型参数错误");
            }
        }

        Page<DoQueryBillReceiverPageListDto> page = new Page<>(pageNum, pageSize);
        Page<DoQueryBillReceiverPageListDto> ipage = baseMapper.doQueryBillReceiverPageList(vo, page);
        return ipage;
    }

    @Override
    public Page<AccountStatementUserDto> queryAccountStatementUser(QueryAccountStatementUserVo vo, Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        if (StringUtils.isEmpty(vo.getMonList())) {
            throw new BusinessException("情选择月份");
        }

        List<String> monList = Arrays.stream(vo.getMonList().split(",")).collect(Collectors.toList());
        Page<AccountStatementUserDto> page = new Page<>(pageNum, pageSize);
        Page<AccountStatementUserDto> ipage = baseMapper.selectAccountStatementUserPage(vo.getBillReceiverName(), vo.getBillReceiverMobile(), tenantId, monList, page);
        return ipage;
    }

    @Override
    @Transactional
    public void createAccountStatementNew(CreateAccountStatementVo vo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());

        if (vo.getBillMonths() == null || vo.getBillMonths().size() <= 0) {
            throw new BusinessException("请输入账单月份");
        }
        if (vo.getReceiverUserInDtos() == null || vo.getReceiverUserInDtos().size() <= 0) {
            throw new BusinessException("请输入账单信息");
        }

        String monListStr = StringUtils.join(vo.getBillMonths(), ",");
        for (ReceiverUserInDto receiverUserInDto : vo.getReceiverUserInDtos()) {
            AccountStatement accountStatement = baseMapper.selectAccountStatementByMonAndUserId(receiverUserInDto.getBillReceiverUserId(), monListStr);
            List<Long> orderList = this.getAccountStatementOrderList(receiverUserInDto.getBillReceiverUserId(), vo.getBillMonths(), tenantId);
            Integer orderNum = orderList.size();
            Long orderTotalFee = baseMapper.getAccountStatementOrderListTotalFee(orderList); // 总运费
            Long exceptionIn = baseMapper.getAccountStatementOrderListExceptionIn(orderList); // 异常补偿
            Long exceptionOut = baseMapper.getAccountStatementOrderListExceptionOut(orderList); // 异常扣减
            Long timePenalty = baseMapper.getAccountStatementOrderListAgingFee(orderList); // 时效补贴
            Long paidFee = baseMapper.getAccountStatementOrderListPaidFee(orderList, tenantId, sysTenantDef.getAdminUser());
            Long noPaidFee = baseMapper.getAccountStatementOrderListNoPaidFee(orderList, tenantId, sysTenantDef.getAdminUser());


            // 对账单不存在 创建 已存在  更新
            if (accountStatement == null) {
                accountStatement = new AccountStatement();
                accountStatement.setBillMonth(monListStr);
                accountStatement.setReceiverName(receiverUserInDto.getBillReceiverName());
                accountStatement.setReceiverPhone(receiverUserInDto.getBillReceiverMobile());
                accountStatement.setReceiverUserId(receiverUserInDto.getBillReceiverUserId());
                accountStatement.setCarNum(0);// 字段现在不显示
                accountStatement.setCarTotalFee(0L);// 现在也不显示
                accountStatement.setDeductionType(OrderAccountConst.ACCOUNT_STATEMENT.DEDUCTION_TYPE1);
                accountStatement.setOrderNum(orderNum);//新逻辑
                accountStatement.setOrderTotalFee(orderTotalFee);//新逻辑
                accountStatement.setExceptionIn(exceptionIn);//新逻辑
                accountStatement.setExceptionOut(exceptionOut);//新逻辑
                accountStatement.setTimePenalty(timePenalty);
                accountStatement.setOilTurnCash(0L);
                accountStatement.setEtcTurnCash(0L);
                accountStatement.setPaidFee(paidFee);
                accountStatement.setNoPayFee(noPaidFee);
                accountStatement.setOilCardDeposit(0L);
                accountStatement.setMarginBalance(0L);
                accountStatement.setState(OrderAccountConst.ACCOUNT_STATEMENT.STATE1);
                accountStatement.setVerificationState(OrderAccountConst.ACCOUNT_STATEMENT.VERIFICATION_STATE0);
                accountStatement.setTenantId(tenantId);
                accountStatement.setTenantBill(sysTenantDef.getLinkPhone());
                accountStatement.setTenantName(sysTenantDef.getName());
                accountStatement.setPayUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
                accountStatement.setReceUserType(SysStaticDataEnum.USER_TYPE.RECEIVER_USER);
                accountStatement.setCreateDate(LocalDateTime.now());
                accountStatement.setUpdateDate(LocalDateTime.now());

                baseMapper.insert(accountStatement);
            } else {
                accountStatement.setOrderNum(orderNum);//新逻辑
                accountStatement.setOrderTotalFee(orderTotalFee);//新逻辑
                accountStatement.setExceptionIn(exceptionIn);//新逻辑
                accountStatement.setExceptionOut(exceptionOut);//新逻辑
                accountStatement.setTimePenalty(timePenalty);
                accountStatement.setPaidFee(paidFee);
                accountStatement.setNoPayFee(noPaidFee);

                baseMapper.updateById(accountStatement);
            }

            accountStatementOrderInfoService.deleteAccountStatementOrder(accountStatement.getId());
            if (CollectionUtils.isNotEmpty(orderList)) {
                List<AccountStatementOrderInfo> accountStatementOrderInfos = new ArrayList<AccountStatementOrderInfo>();

                for (Long orderId : orderList) {
                    AccountStatementOrderInfo accountStatementOrderInfo = new AccountStatementOrderInfo();
                    accountStatementOrderInfo.setOrderId(orderId);
                    accountStatementOrderInfo.setAccountStatementId(accountStatement.getId());
                    accountStatementOrderInfos.add(accountStatementOrderInfo);


                }

                accountStatementOrderInfoService.saveBatch(accountStatementOrderInfos);

                QueryAccountStatementOrdersVo queryVo = new QueryAccountStatementOrdersVo();
                queryVo.setFlowId(accountStatement.getId());
                List<QueryAccountStatementOrdersDto> queryAccountStatementOrdersDtos = baseMapper.queryAccountStatementOrderList(queryVo);

                Map<Long, Long> exceptionInMap = listToMap(baseMapper.getAccountStatementOrderListExceptionInGroup(orderList)); // 异常补偿
                Map<Long, Long> exceptionOutMap = listToMap(baseMapper.getAccountStatementOrderListExceptionOutGroup(orderList)); // 异常扣减
                Map<Long, Long> timePenaltyMap = listToMap(baseMapper.getAccountStatementOrderListAgingFeeGroup(orderList)); // 时效补贴
                Map<Long, Long> paidFeeMap = listToMap(baseMapper.getAccountStatementOrderListPaidFeeGroup(orderList, tenantId, sysTenantDef.getAdminUser()));
                Map<Long, Long> noPaidFeeMap = listToMap(baseMapper.getAccountStatementOrderListNoPaidFeeGroup(orderList, tenantId, sysTenantDef.getAdminUser()));

                for (AccountStatementOrderInfo accountStatementOrderInfo : accountStatementOrderInfos) {
                    Optional<QueryAccountStatementOrdersDto> optional = queryAccountStatementOrdersDtos.stream().filter(item -> item.getOrderId().equals(accountStatementOrderInfo.getOrderId())).findFirst();
                    if (optional.isPresent()) {
                        QueryAccountStatementOrdersDto dto = optional.get();
                        accountStatementOrderInfo.setDependTime(dto.getDependTime());
                        accountStatementOrderInfo.setOrderState(dto.getOrderState());
                        accountStatementOrderInfo.setCompanyName(dto.getCompanyName());
                        accountStatementOrderInfo.setSourceName(dto.getSourceName());
                        accountStatementOrderInfo.setCarDriverId(dto.getCarDriverId());
                        accountStatementOrderInfo.setCarDriverMan(dto.getCarDriverMan());
                        accountStatementOrderInfo.setCarDriverPhone(dto.getCarDriverPhone());
                        accountStatementOrderInfo.setPayeeUserId(dto.getPayeeUserId());
                        accountStatementOrderInfo.setPayee(dto.getPayee());
                        accountStatementOrderInfo.setPayeePhone(dto.getPayeePhone());
                        accountStatementOrderInfo.setIsCollection(dto.getIsCollection());
                        accountStatementOrderInfo.setTotalFee(dto.getTotalFee());
                        accountStatementOrderInfo.setPreCashFee(dto.getPreCashFee());
                        accountStatementOrderInfo.setTotalOilFee(dto.getTotalOilFee());
                        accountStatementOrderInfo.setPreOilFee(dto.getPreOilFee());
                        accountStatementOrderInfo.setPreOilVirtualFee(dto.getPreOilVirtualFee());
                        accountStatementOrderInfo.setPreEtcFee(dto.getPreEtcFee());
                        accountStatementOrderInfo.setArrivePaymentFee(dto.getArrivePaymentFee());
                        accountStatementOrderInfo.setFinalFee(dto.getFinalFee());
                        accountStatementOrderInfo.setFinalSts(dto.getFianlSts());
                        accountStatementOrderInfo.setPaidFinal(dto.getPaidFinal());


                        Long exceptionInOrder = getMapValue(exceptionInMap, accountStatementOrderInfo.getOrderId()); // 异常补偿
                        Long exceptionOutOrder = getMapValue(exceptionOutMap, accountStatementOrderInfo.getOrderId()); // 异常扣减
                        Long timePenaltyOrder = getMapValue(timePenaltyMap, accountStatementOrderInfo.getOrderId()); // 时效补贴
                        Long paidFeeOrder = getMapValue(paidFeeMap, accountStatementOrderInfo.getOrderId());
                        Long noPaidFeeOrder = getMapValue(noPaidFeeMap, accountStatementOrderInfo.getOrderId());

                        accountStatementOrderInfo.setExceptionIn(exceptionInOrder);
                        accountStatementOrderInfo.setExceptionOut(exceptionOutOrder);
                        accountStatementOrderInfo.setFinePrice(timePenaltyOrder);
                        accountStatementOrderInfo.setPaidAmount(paidFeeOrder);
                        accountStatementOrderInfo.setNoPaidAmount(noPaidFeeOrder);

                        accountStatementOrderInfo.setTotalAmount(dto.getTotalFee() + exceptionInOrder);

                        accountStatementOrderInfoService.updateById(accountStatementOrderInfo);
                    }
                }
            }
        }
    }

    public List<Long> getAccountStatementOrderList(Long collectionUserId, List<String> monList, Long tenantId) {
        return baseMapper.getAccountStatementOrderList(collectionUserId, monList, tenantId);
    }

    @Override
    public Page<QueryAccountStatementOrdersDto> queryAccountStatementOrders(QueryAccountStatementOrdersVo vo, String accessToken, Integer pageNum, Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();

        if (vo.getFlowId() == null) {
            throw new BusinessException("请输入对账单id");
        }

        AccountStatement as = baseMapper.selectById(vo.getFlowId());
        if (as == null) {
            throw new BusinessException("根据对账单id：" + vo.getFlowId() + " 未找到对账单信息");
        }

        if (as.getOrderNum() == null || as.getOrderNum() <= 0) {
            return new Page<>(pageNum, pageSize);
        } else {

            if (StringUtils.isNotEmpty(vo.getDependStartTime())) {
                vo.setDependStartTime(vo.getDependStartTime() + " 00:00:00");
            }

            if (StringUtils.isNotEmpty(vo.getDependEndTime())) {
                vo.setDependEndTime(vo.getDependEndTime() + " 23:59:59");
            }

            if (StringUtils.isNotBlank(vo.getOrderStates())) {
                List<Integer> orderStateList = Arrays.stream(vo.getOrderStates().replaceAll("，", ",").split(",")).map(Integer::parseInt).collect(Collectors.toList());
                vo.setOrderStateList(orderStateList);
            }

            Page<AccountStatementOrderInfo> ipage = accountStatementOrderInfoService.getAccountStatementOrderPage(as.getId(), vo, pageNum, pageSize);
            Page<QueryAccountStatementOrdersDto> dtoPage = new Page<>();

            dtoPage.setPages(ipage.getPages());
            dtoPage.setSize(ipage.getSize());
            dtoPage.setCurrent(ipage.getCurrent());
            dtoPage.setTotal(ipage.getTotal());

            List<QueryAccountStatementOrdersDto> dtoList = new ArrayList<>();
            for (AccountStatementOrderInfo record : ipage.getRecords()) {

                QueryAccountStatementOrdersDto dto = new QueryAccountStatementOrdersDto();
                BeanUtil.copyProperties(record, dto);
                dto.setNoPayAmount(record.getNoPaidAmount());


                if (dto.getOrderState() != null) {
                    dto.setOrderStateName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "ORDER_STATE", record.getOrderState()+"").getCodeName());
                }

                if (dto.getFianlSts() != null) {
                    if (dto.getFianlSts() == 0) {
                        dto.setFianlStsName("未到期");
                    } else if (dto.getFianlSts() == 1) {
                        dto.setFianlStsName("已到期");
                    } else if (dto.getFianlSts() == 2) {
                        dto.setFianlStsName("未到期转到期失败");
                    }
                } else {
                    dto.setFianlStsName("未支付");
                }

                dtoList.add(dto);
            }
            dtoPage.setRecords(dtoList);
            return dtoPage;
        }
    }

    private Map<Long, Long> listToMap(List<OrderFeeDto> list) {
        Map<Long, Long> map = new HashMap<>();
        if (CollectionUtils.isNotEmpty(list)) {
            for (OrderFeeDto orderFeeDto : list) {
                map.put(orderFeeDto.getOrderId(), orderFeeDto.getFee());
            }
        }

        return map;
    }

    private Long getMapValue(Map<Long, Long> map, Long orderId) {
        return map.getOrDefault(orderId, 0L) != null ? map.getOrDefault(orderId, 0L) : 0L;
    }


}
