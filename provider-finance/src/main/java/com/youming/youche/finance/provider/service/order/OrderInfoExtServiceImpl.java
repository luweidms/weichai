package com.youming.youche.finance.provider.service.order;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.IOaLoanThreeService;
import com.youming.youche.finance.api.IOilTurnEntityService;
import com.youming.youche.finance.api.order.IOrderInfoExtService;
import com.youming.youche.finance.constant.OrderConsts;
import com.youming.youche.finance.domain.OaLoan;
import com.youming.youche.finance.domain.OilTurnEntity;
import com.youming.youche.finance.domain.order.OrderInfoExt;
import com.youming.youche.finance.dto.OilCardRechargeDto;
import com.youming.youche.finance.dto.PayoutInfoOutDto;
import com.youming.youche.finance.dto.order.TurnCashDto;
import com.youming.youche.finance.provider.mapper.order.OrderInfoExtMapper;
import com.youming.youche.finance.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.finance.vo.OilCardRechargeVo;
import com.youming.youche.finance.vo.QueryPayOutVo;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.order.IBusiSubjectsRelService;
import com.youming.youche.order.api.order.IOrderFeeHService;
import com.youming.youche.order.api.order.IOrderInfoHService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderOilSourceService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.api.order.other.IBaseBusiToOrderService;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OrderFeeH;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoH;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderOilSource;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.dto.OilExcDto;
import com.youming.youche.record.api.account.IAccountBankRelService;
import com.youming.youche.record.domain.account.AccountBankRel;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysOrganizeService;
import com.youming.youche.system.api.ISysRoleService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.utils.excel.ExportExcel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>
 * 订单扩展表 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-15
 */
@DubboService(version = "1.0.0")
public class OrderInfoExtServiceImpl extends BaseServiceImpl<OrderInfoExtMapper, OrderInfoExt> implements IOrderInfoExtService {
    private static final Logger log = LoggerFactory.getLogger(OrderInfoExtServiceImpl.class);
    @Resource
    private OrderInfoExtMapper orderInfoExtMapper;


    @Resource
    LoginUtils loginUtils;
    @Resource
    IOaLoanThreeService iOaLoanService;
    @DubboReference(version = "1.0.0")
    IOrderOilSourceService iOrderOilSourceService;
    @DubboReference(version = "1.0.0")
    IOrderLimitService iOrderLimitService;
    @DubboReference(version = "1.0.0")
    IOrderSchedulerService iOrderSchedulerService;
    @DubboReference(version = "1.0.0")
    ISysRoleService sysRoleService;
    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;
    @DubboReference(version = "1.0.0")
    ISysOrganizeService sysOrganizeService;
    @DubboReference(version = "1.0.0")
    IPayoutIntfService payoutIntfService;
    @Resource
    RedisUtil redisUtil;
    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;
    @Resource
    IOilTurnEntityService oilTurnEntityService;
    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;
    @DubboReference(version = "1.0.0")
    IOrderSchedulerService orderSchedulerService;
    @DubboReference(version = "1.0.0")
    IOrderSchedulerHService orderSchedulerHService;
    @DubboReference(version = "1.0.0")
    IOrderFeeService orderFeeService;
    @DubboReference(version = "1.0.0")
    IOrderFeeHService orderFeeHService;
    @DubboReference(version = "1.0.0")
    IOrderInfoService orderInfoService;
    @DubboReference(version = "1.0.0")
    IOrderInfoHService orderInfoHService;
    @DubboReference(version = "1.0.0")
    IBusiSubjectsRelService busiSubjectsRelService;
    @DubboReference(version = "1.0.0")
    IBaseBusiToOrderService busiToOrderService;
    @DubboReference(version = "1.0.0")
    IAccountBankRelService accountBankRelService;
    @Override
    public OrderInfoExt getOrderInfoExtByOrderId(Long orderId) {

        QueryWrapper<OrderInfoExt> orderInfoExtQueryWrapper = new QueryWrapper<>();
        orderInfoExtQueryWrapper.eq("order_id", orderId);
        List<OrderInfoExt> orderInfoExts = orderInfoExtMapper.selectList(orderInfoExtQueryWrapper);
        if (orderInfoExts != null && orderInfoExts.size() > 0) {
            return orderInfoExts.get(0);
        }

        return null;
    }

    @Override
    public TurnCashDto queryOilAndEtcBalance(Long userId, String month, String turnType, String turnOilType,
                                             Integer userType, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);

        OilExcDto oilExcDto = new OilExcDto();

        if (userId == null || userId <= 0L) {
            throw new BusinessException("未找到转移用户id");
        }
        if (user.getTenantId() == null || user.getTenantId() <= 0) {
            throw new BusinessException("请输入租户id");
        }
        if (month == null || month.isEmpty()) {
            throw new BusinessException("未找到转移用户转移月份");
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date turnMonth = sdf.parse(month);
                //获取指定日期所在月第一天
                Calendar ca2 = Calendar.getInstance();
                ca2.setTime(turnMonth);
                ca2.add(Calendar.MONTH, 0);
                ca2.set(Calendar.DAY_OF_MONTH, 1);
                String firstDay = dateFormat.format(ca2.getTime());
                //获取指定日期所在月最后一天
                Calendar ca3 = Calendar.getInstance();
                ca3.setTime(turnMonth);
                ca3.set(Calendar.DAY_OF_MONTH, ca3.getActualMaximum(Calendar.DAY_OF_MONTH));
                String lastDay = dateFormat.format(ca3.getTime());
                oilExcDto.setCreateTime(firstDay + " 00:00:00");
                oilExcDto.setUpdateTime(lastDay + " 23:59:59");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (turnType == null || turnType.isEmpty()) {//1油卡转现  ,2ETC转现
            throw new BusinessException("未找到转现类型");
        } else {
            if (EnumConsts.TURN_CASH.TURN_TYPE1.equals(turnType)) {
                if (turnOilType == null || turnOilType.isEmpty()) {//1油卡转现  ,2ETC转现
                    throw new BusinessException("请选择油转移到现金账户还是转成实体油卡!");
                }
            }
        }
        //查找司机当前月借支油总和
        Long oaLoanOilAmout = 0L;
        OilExcDto oilExcDto1 = new OilExcDto();
        if (EnumConsts.TURN_CASH.TURN_TYPE1.equals(turnType)) {
//            OilExcDto oilExcDto1 = new OilExcDto();
            oilExcDto1.setCreateTime(oilExcDto.getCreateTime());
            oilExcDto1.setUpdateTime(oilExcDto.getUpdateTime());
            oilExcDto1.setUserId(userId);
            oilExcDto1.setTenantId(user.getTenantId());
            List<OaLoan> oaLoanList = iOaLoanService.queryCarDriverOaLoan(oilExcDto1);
            if (oaLoanList != null && oaLoanList.size() > 0) {
                for (OaLoan oa : oaLoanList) {
                    oaLoanOilAmout += (oa.getAmount() == null ? 0 : oa.getAmount());
                }
            }
        }
        //查找司机未付现金
        // TODO 2022-6-27 修改
        //oilExcDto.setCreateTime(oilExcDto1.getCreateTime());
        //oilExcDto.setUpdateTime(oilExcDto1.getUpdateTime());
        oilExcDto.setTenantId(user.getTenantId());
        oilExcDto.setUserType(userType);
        oilExcDto.setUserId(userId);
        Long tempCanTurnMoney = 0L;//可转移金额
        Long tempOrderMoney = 0L;//订单油卡或ETC总金额
        Long tempConsumeMoney = 0L;//消费总金额
        Long tempTurnDiscount = EnumConsts.TURN_CASH.TURN_DISCOUNT2;//转移折扣
        if (EnumConsts.TURN_CASH.TURN_TYPE1.equals(turnType)) {//油卡转现
            //查询司机订单油来源
            List<OrderOilSource> sourceList = iOrderOilSourceService.getOrderOilSource(oilExcDto);
            //代收油不可以转现
            for (OrderOilSource ol : sourceList) {
                boolean isCollection = false;
                OrderSchedulerH orderSchedulerH = iOrderSchedulerService.getOrderSchedulerH(ol.getOrderId());
                if (orderSchedulerH != null) {
                    //代收单
                    if (orderSchedulerH.getIsCollection() != null && orderSchedulerH.getIsCollection() == OrderConsts.IS_COLLECTION.YES) {
                        isCollection = true;
                    }
                } else {
                    OrderScheduler orderScheduler = iOrderSchedulerService.getOrderScheduler(ol.getOrderId());
                    if (orderScheduler != null) {
                        //代收单
                        if (orderScheduler.getIsCollection() != null && orderScheduler.getIsCollection() == OrderConsts.IS_COLLECTION.YES) {
                            isCollection = true;
                        }
                    }
                }
                if (!isCollection) {
                    tempCanTurnMoney += (ol.getNoPayOil() + ol.getNoCreditOil() + ol.getNoRebateOil());//未付油
                    tempOrderMoney += (ol.getSourceAmount() + ol.getCreditOil() + ol.getRebateOil());//订单油
                    tempConsumeMoney += (ol.getPaidOil() + ol.getPaidCreditOil() + ol.getPaidRebateOil());//已付油
                }
            }
        } else if (EnumConsts.TURN_CASH.TURN_TYPE2.equals(turnType)) {//ETC转现
            List<OrderLimit> list = iOrderLimitService.queryOilAndEtcBalance(oilExcDto);
            if (list != null && list.size() > 0) {
                for (OrderLimit ol : list) {
                    tempCanTurnMoney += ol.getNoPayEtc();//未付ETC
                    tempOrderMoney += ol.getOrderEtc();//订单ETC
                    tempConsumeMoney += ol.getPaidEtc();//已付ETC
                }
            }
        }

        tempTurnDiscount = EnumConsts.TURN_CASH.TURN_DISCOUNT2;
        //查找未到期金额
        TurnCashDto tco = new TurnCashDto();
        tco.setUserId(userId);
        tco.setCanTurnMoney(tempCanTurnMoney);
        tco.setCanTurnMoneyDouble((double) tempCanTurnMoney / 100);
        tco.setOrderMoney(tempOrderMoney);
        tco.setOrderMoneyDouble((double) tempOrderMoney / 100);
        tco.setConsumeMoney(tempConsumeMoney);
        tco.setConsumeMoneyDouble((double) tempConsumeMoney / 100);
        tco.setTurnDiscount(tempTurnDiscount);
        tco.setTurnDiscountDouble((double) tempTurnDiscount / 100);
        tco.setOaLoanOilAmount(oaLoanOilAmout);
        tco.setOaLoanOilAmountDouble((double) oaLoanOilAmout / 100);
        return tco;
    }

    @Override
    public Page<PayoutInfoOutDto> queryPayOut(QueryPayOutVo queryPayOutVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        List<Long> orgIds = loginInfo.getOrgIds();
        List<Long> orgList = new ArrayList<>();
        if((orgIds == null || orgIds.size() < 1 ) && (queryPayOutVo.getServiceUserId() == null || queryPayOutVo.getServiceUserId() < 0)){
            throw new BusinessException("账户信息失效，请重现登陆");
        }
        boolean hasAllDataPermission = sysRoleService.hasAllData(loginInfo);
        if(orgIds != null && orgIds.size()>0) {
            orgList = sysOrganizeService.getSubOrgList(tenantId, orgIds);
            orgList.removeAll(Collections.singleton(null));
        }
        if (StringUtils.isNotEmpty(queryPayOutVo.getBeginDate()) && StringUtils.isNotEmpty(queryPayOutVo.getBeginDate().trim())) {
            queryPayOutVo.setBeginDate(queryPayOutVo.getBeginDate().trim() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(queryPayOutVo.getEndDate()) && StringUtils.isNotEmpty(queryPayOutVo.getEndDate().trim())) {
            queryPayOutVo.setEndDate(queryPayOutVo.getEndDate().trim() + " 23:59:59");
        }
        if (StringUtils.isNotEmpty(queryPayOutVo.getBeginPayTime()) && StringUtils.isNotEmpty(queryPayOutVo.getBeginPayTime().trim())) {
            queryPayOutVo.setBeginPayTime(queryPayOutVo.getBeginPayTime().trim() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(queryPayOutVo.getEndPayTime()) && StringUtils.isNotEmpty(queryPayOutVo.getEndPayTime().trim())) {
            queryPayOutVo.setEndPayTime(queryPayOutVo.getEndPayTime().trim() + " 23:59:59");
        }
        if (StringUtils.isNotEmpty(queryPayOutVo.getFinishTime()) && StringUtils.isNotEmpty(queryPayOutVo.getFinishTime().trim())) {
            queryPayOutVo.setFinishTime(queryPayOutVo.getFinishTime().replaceAll("-", ""));
        }
        if (StringUtils.isNotEmpty(queryPayOutVo.getEndTime()) && StringUtils.isNotEmpty(queryPayOutVo.getEndTime().trim())) {
            queryPayOutVo.setEndTime(queryPayOutVo.getEndTime().replaceAll("-", ""));
        }
        if (StringUtils.isNotEmpty(queryPayOutVo.getDependTimeStart()) && StringUtils.isNotEmpty(queryPayOutVo.getDependTimeStart().trim())) {
            queryPayOutVo.setDependTimeStart(queryPayOutVo.getDependTimeStart().trim() + " 00:00:00");
        }
        if ( StringUtils.isNotEmpty(queryPayOutVo.getDependTimeEnd()) && StringUtils.isNotEmpty(queryPayOutVo.getDependTimeEnd().trim())) {
            queryPayOutVo.setDependTimeEnd(queryPayOutVo.getDependTimeEnd().trim() + " 23:59:59");
        }
        queryPayOutVo.setTenantId(tenantId);
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantId);
        Long payObjId = sysTenantDef.getAdminUser();
        Integer payUserType = SysStaticDataEnum.USER_TYPE.ADMIN_USER;
        if(queryPayOutVo.getServiceUserId() != null && queryPayOutVo.getServiceUserId() > 0){
            hasAllDataPermission = false;
        }
        Page<PayoutInfoOutDto> payoutInfoOutDtoPage = baseMapper.queryPayOut(new Page<>(queryPayOutVo.getPageNum(), queryPayOutVo.getPageSize()), queryPayOutVo, payObjId, payUserType, hasAllDataPermission, orgList);
        List<PayoutInfoOutDto> list = payoutInfoOutDtoPage.getRecords();
        List<PayoutInfoOutDto> outList = new ArrayList<>();
        List<Long> flowIds = list.stream().map(x -> {
            return x.getFlowId();
        }).collect(Collectors.toList());
        List<PayoutIntf> payoutIntfList=null;
        if(flowIds.size()!=0){
            payoutIntfList = payoutIntfService.getPayoutIntf(flowIds);
        }
        if (list != null) {
            for (PayoutInfoOutDto payoutInfoOutDto : list) {
                PayoutInfoOutDto p = new PayoutInfoOutDto();
                BeanUtils.copyProperties(payoutInfoOutDto, p);
                if (payoutInfoOutDto.getOrgId() != null && payoutInfoOutDto.getOrgId() > 0) {
                    payoutInfoOutDto.setOrgName(sysOrganizeService.getCurrentTenantOrgNameById(tenantId, payoutInfoOutDto.getOrgId()));
                } else if (payoutInfoOutDto.getVehicleOrgId() != null && payoutInfoOutDto.getVehicleOrgId() > 0) {
                    payoutInfoOutDto.setOrgName(sysOrganizeService.getCurrentTenantOrgNameById(tenantId, payoutInfoOutDto.getVehicleOrgId()));
                }
                if (payoutInfoOutDto.getCompleteTime() != null) {
                    Date date = null;
                    try {
                        date = DateUtils.parseDate(payoutInfoOutDto.getCompleteTime(), new String[]{"yyyyMMddHHmmss"});
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    payoutInfoOutDto.setCompleteTime(DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss"));
                }
                if (payoutInfoOutDto.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                    payoutInfoOutDto.setUserName(payoutInfoOutDto.getReceivablesBankAccName());
                    payoutInfoOutDto.setAccNameAndAccNo("账户名：" + payoutInfoOutDto.getReceivablesBankAccName() + "  账号：" + payoutInfoOutDto.getReceivablesBankAccNo());
                    if (payoutInfoOutDto.getTxnAmt() == 0L && payoutInfoOutDto.getAppendFreight() > 0L) {
                        payoutInfoOutDto.setAccNameAndAccNo("请在“附加运费”菜单中查看处理结果");
                    }
                    if (payoutIntfList!=null){
                        for (PayoutIntf payoutIntf : payoutIntfList) {
                            if (payoutInfoOutDto.getFlowId().equals(payoutIntf.getXid())) {
                                payoutInfoOutDto.setPayTime100(payoutIntf.getPayTime());
                                payoutInfoOutDto.setRespCodeName100(getSysStaticData("RESP_CODE_TYPE", payoutIntf.getRespCode() + "").getCodeName());
                            }
                        }
                    }
                } else {
                    payoutInfoOutDto.setAccName(payoutInfoOutDto.getReceivablesBankAccName());
                    payoutInfoOutDto.setAccNo(payoutInfoOutDto.getReceivablesBankAccNo());
                }
                if (payoutInfoOutDto.getSubjectsId() != null && payoutInfoOutDto.getSubjectsId().longValue() == EnumConsts.SubjectIds.Oil_PAYFOR_RECEIVABLE_IN) {
                    payoutInfoOutDto.setIsNeedBill(6L);
                }
                if (payoutInfoOutDto.getBusiId()!=null){
                    payoutInfoOutDto.setBusiIdName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, loginInfo.getTenantId(), "BUSINESS_NUMBER_TYPE", String.valueOf(payoutInfoOutDto.getBusiId())));

                }
                if (payoutInfoOutDto.getRespCode()!=null) {
                    payoutInfoOutDto.setRespCodeName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, loginInfo.getTenantId(), "RESP_CODE_TYPE", payoutInfoOutDto.getRespCode() + ""));
                }

                if (StringUtils.isNotEmpty(payoutInfoOutDto.getPayBankAccNo())) {
                    AccountBankRel payBankAccNo = accountBankRelService.getAccountBankRelByAcctNo(payoutInfoOutDto.getPayBankAccNo());
                    if (payBankAccNo != null) {
                        payoutInfoOutDto.setPayBankAccNo(payBankAccNo.getPinganPayAcctId());
                    } else {
                        payoutInfoOutDto.setPayBankAccNo("");
                    }
                }

                if (StringUtils.isNotEmpty(payoutInfoOutDto.getAcctNo()) || StringUtils.isNotEmpty(payoutInfoOutDto.getReceivablesBankAccNo())) {
                    AccountBankRel acctNo = accountBankRelService.getAccountBankRelByAcctNo(StringUtils.isNotEmpty(payoutInfoOutDto.getAcctNo()) ? payoutInfoOutDto.getAcctNo() : payoutInfoOutDto.getReceivablesBankAccNo());
                    if (acctNo != null) {
                        payoutInfoOutDto.setAccNo(acctNo.getPinganPayAcctId());
                    } else {
                        payoutInfoOutDto.setAccNo("");
                    }
                }
                payoutInfoOutDto.setTenantId(tenantId);
                if(payoutInfoOutDto.getUserType()!=null && payoutInfoOutDto.getSubjectsId()!=null) {
                    if (payoutInfoOutDto.getUserType().equals(SysStaticDataEnum.USER_TYPE.RECEIVER_USER)) {
                        payoutInfoOutDto.setVirtualState(SysStaticDataEnum.VIRTUAL_TENANT_STATE.IS_VIRTUAL);
                    }
                }
                outList.add(payoutInfoOutDto);

            }
        }
        payoutInfoOutDtoPage.setRecords(outList);
        return payoutInfoOutDtoPage;
    }

    public SysStaticData getSysStaticData(String codeType, String codeValue) {
        List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(com.youming.youche.conts.SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
        if (list != null && list.size() > 0) {
            for (SysStaticData sysStaticData : list) {
                if (sysStaticData.getCodeValue().equals(codeValue)) {
                    return sysStaticData;
                }
            }
        }

        return new SysStaticData();
    }


    @Override
    @Async
    public void queryPayOutListExport(QueryPayOutVo queryPayOutVo, String accessToken, ImportOrExportRecords importOrExportRecords) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        List<Long> orgIds = loginInfo.getOrgIds();
        List<Long> orgList = new ArrayList<>();
        if((orgIds == null || orgIds.size() < 1 ) && (queryPayOutVo.getServiceUserId() == null || queryPayOutVo.getServiceUserId() < 0)){
            throw new BusinessException("账户信息失效，请重现登陆");
        }
        boolean hasAllDataPermission = sysRoleService.hasAllData(loginInfo);
        if(orgIds != null && orgIds.size()>0) {
            orgList = sysOrganizeService.getSubOrgList(tenantId, orgIds);
            orgList.removeAll(Collections.singleton(null));
        }
        if (StringUtils.isNotEmpty(queryPayOutVo.getBeginDate()) && StringUtils.isNotEmpty(queryPayOutVo.getBeginDate().trim())) {
            queryPayOutVo.setBeginDate(queryPayOutVo.getBeginDate().trim() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(queryPayOutVo.getEndDate()) && StringUtils.isNotEmpty(queryPayOutVo.getEndDate().trim())) {
            queryPayOutVo.setEndDate(queryPayOutVo.getEndDate().trim() + " 23:59:59");
        }
        if (StringUtils.isNotEmpty(queryPayOutVo.getBeginPayTime()) && StringUtils.isNotEmpty(queryPayOutVo.getBeginPayTime().trim())) {
            queryPayOutVo.setBeginPayTime(queryPayOutVo.getBeginPayTime().trim() + " 00:00:00");
        }
        if (StringUtils.isNotEmpty(queryPayOutVo.getEndPayTime()) && StringUtils.isNotEmpty(queryPayOutVo.getEndPayTime().trim())) {
            queryPayOutVo.setEndPayTime(queryPayOutVo.getEndPayTime().trim() + " 23:59:59");
        }
        if (StringUtils.isNotEmpty(queryPayOutVo.getFinishTime()) && StringUtils.isNotEmpty(queryPayOutVo.getFinishTime().trim())) {
            queryPayOutVo.setFinishTime(queryPayOutVo.getFinishTime().replaceAll("-", ""));
        }
        if (StringUtils.isNotEmpty(queryPayOutVo.getEndTime()) && StringUtils.isNotEmpty(queryPayOutVo.getEndTime().trim())) {
            queryPayOutVo.setEndTime(queryPayOutVo.getEndTime().replaceAll("-", ""));
        }
        if (StringUtils.isNotEmpty(queryPayOutVo.getDependTimeStart()) && StringUtils.isNotEmpty(queryPayOutVo.getDependTimeStart().trim())) {
            queryPayOutVo.setDependTimeStart(queryPayOutVo.getDependTimeStart().trim() + " 00:00:00");
        }
        if ( StringUtils.isNotEmpty(queryPayOutVo.getDependTimeEnd()) && StringUtils.isNotEmpty(queryPayOutVo.getDependTimeEnd().trim())) {
            queryPayOutVo.setDependTimeEnd(queryPayOutVo.getDependTimeEnd().trim() + " 23:59:59");
        }
        queryPayOutVo.setTenantId(tenantId);
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(tenantId);
        Long payObjId = sysTenantDef.getAdminUser();
        Integer payUserType = SysStaticDataEnum.USER_TYPE.ADMIN_USER;
        if(queryPayOutVo.getServiceUserId() != null && queryPayOutVo.getServiceUserId() > 0){
            hasAllDataPermission = false;
        }
        List<PayoutInfoOutDto> payoutInfoOutDtoList = baseMapper.queryPayOutListExport(queryPayOutVo, payObjId, payUserType, hasAllDataPermission, orgList);
        List<Long> flowIds = payoutInfoOutDtoList.stream().map(x -> {
            return x.getFlowId();
        }).collect(Collectors.toList());
        List<PayoutIntf> payoutIntfList=null;
        if(flowIds.size()!=0){
            payoutIntfList = payoutIntfService.getPayoutIntf(flowIds);
        }

        if (payoutInfoOutDtoList != null) {
            for (PayoutInfoOutDto payoutInfoOutDto : payoutInfoOutDtoList) {
                PayoutInfoOutDto p = new PayoutInfoOutDto();
                BeanUtils.copyProperties(payoutInfoOutDto, p);
                if (payoutInfoOutDto.getOrgId() != null && payoutInfoOutDto.getOrgId() > 0) {
                    payoutInfoOutDto.setOrgName(sysOrganizeService.getCurrentTenantOrgNameById(tenantId, payoutInfoOutDto.getOrgId()));
                } else if (payoutInfoOutDto.getVehicleOrgId() != null && payoutInfoOutDto.getVehicleOrgId() > 0) {
                    payoutInfoOutDto.setOrgName(sysOrganizeService.getCurrentTenantOrgNameById(tenantId, payoutInfoOutDto.getVehicleOrgId()));
                }
                if (payoutInfoOutDto.getCompleteTime() != null) {
                    Date date = null;
                    try {
                        date = DateUtils.parseDate(payoutInfoOutDto.getCompleteTime(), new String[]{"yyyyMMddHHmmss"});
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    payoutInfoOutDto.setCompleteTime(DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss"));
                }
                if (payoutInfoOutDto.getIsNeedBill() == OrderConsts.IS_NEED_BILL.TERRACE_BILL) {
                    payoutInfoOutDto.setUserName(payoutInfoOutDto.getReceivablesBankAccName());
                    payoutInfoOutDto.setAccNameAndAccNo("账户名：" + payoutInfoOutDto.getReceivablesBankAccName() + "  账号：" + payoutInfoOutDto.getReceivablesBankAccNo());
                    if (payoutInfoOutDto.getTxnAmt() == 0L && payoutInfoOutDto.getAppendFreight() > 0L) {
                        payoutInfoOutDto.setAccNameAndAccNo("请在“附加运费”菜单中查看处理结果");
                    }
                    if (payoutIntfList!=null){
                        for (PayoutIntf payoutIntf : payoutIntfList) {
                            if (payoutInfoOutDto.getFlowId().equals(payoutIntf.getXid())) {
                                payoutInfoOutDto.setPayTime100(payoutIntf.getPayTime());
                                payoutInfoOutDto.setRespCodeName100(getSysStaticData("RESP_CODE_TYPE", payoutIntf.getRespCode() + "").getCodeName());
                            }
                        }
                    }
                } else {
                    payoutInfoOutDto.setAccName(payoutInfoOutDto.getReceivablesBankAccName());
                    payoutInfoOutDto.setAccNo(payoutInfoOutDto.getReceivablesBankAccNo());
                }
                if (payoutInfoOutDto.getSubjectsId() != null && payoutInfoOutDto.getSubjectsId().longValue() == EnumConsts.SubjectIds.Oil_PAYFOR_RECEIVABLE_IN) {
                    payoutInfoOutDto.setIsNeedBill(6L);
                }
                if (payoutInfoOutDto.getBusiId()!=null){
                    payoutInfoOutDto.setBusiIdName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, loginInfo.getTenantId(), "BUSINESS_NUMBER_TYPE", String.valueOf(payoutInfoOutDto.getBusiId())));

                }
                if (payoutInfoOutDto.getRespCode()!=null) {
                    payoutInfoOutDto.setRespCodeName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil, loginInfo.getTenantId(), "RESP_CODE_TYPE", payoutInfoOutDto.getRespCode() + ""));
                }

                if (StringUtils.isNotEmpty(payoutInfoOutDto.getPayBankAccNo())) {
                    AccountBankRel payBankAccNo = accountBankRelService.getAccountBankRelByAcctNo(payoutInfoOutDto.getPayBankAccNo());
                    if (payBankAccNo != null) {
                        payoutInfoOutDto.setPayBankAccNo(payBankAccNo.getPinganPayAcctId());
                    }
                }

                if (StringUtils.isNotEmpty(payoutInfoOutDto.getAcctNo())) {
                    AccountBankRel acctNo = accountBankRelService.getAccountBankRelByAcctNo(payoutInfoOutDto.getAcctNo());
                    if (acctNo != null) {
                        payoutInfoOutDto.setAccNo(acctNo.getPinganPayAcctId());
                    } else {
                        payoutInfoOutDto.setAccNo("");
                    }
                }
                payoutInfoOutDto.setTenantId(tenantId);
                if(payoutInfoOutDto.getUserType()!=null && payoutInfoOutDto.getSubjectsId()!=null) {
                    if (payoutInfoOutDto.getUserType().equals(SysStaticDataEnum.USER_TYPE.RECEIVER_USER)) {
                        payoutInfoOutDto.setVirtualState(SysStaticDataEnum.VIRTUAL_TENANT_STATE.IS_VIRTUAL);
                    }
                }
                payoutInfoOutDto.setTxnAmt(payoutInfoOutDto.getTxnAmt()==null?0:payoutInfoOutDto.getTxnAmt()/100);
                payoutInfoOutDto.setBillServiceFee(payoutInfoOutDto.getBillServiceFee()==null?0:payoutInfoOutDto.getBillServiceFee()/100);
                payoutInfoOutDto.setAppendFreight(payoutInfoOutDto.getAppendFreight()==null?0:payoutInfoOutDto.getAppendFreight()/100);
            }
        }


        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{
                    "支付流水号", "支付账户名", "付款账号", "收款账户名", "收款账号", "业务方", "所属部门", "车牌号", "业务单号", "要求靠台时间",
                    "客户名称", "线路名称", "代收名称", "订单备注", "业务类型", "票据类型", "业务金额", "票据服务费", "附加运费", "支付总金额",
                    "发起支付时间"};
            resourceFild = new String[]{
                    "getFlowId", "getPayBankAccName", "getPayBankAccNo", "getAccName", "getAccNo", "getUserName", "getOrgId", "getPlateNumber", "getBusiId", "getDependTime",
                    "getCustomName", "getSourceName", "getCollectionUserName", "getOrderRemark", "getBusiCode", "getIsNeedBill", "getTxnAmt", "getBillServiceFee", "getAppendFreight", "getTxnAmt",
                    "getPayTime"};

            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(payoutInfoOutDtoList, showName, resourceFild, PayoutInfoOutDto.class,
                    null);

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "payout.xlsx", inputStream.available());
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
    public Page<OilCardRechargeDto> oilCardRecharge(OilCardRechargeVo oilCardRechargeVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        oilCardRechargeVo.setTenantId(tenantId);
        if (StringUtils.isNotBlank(oilCardRechargeVo.getStartTime())) {
            oilCardRechargeVo.setStartTime(oilCardRechargeVo.getStartTime() + " 00:00:00");
        }
        if (StringUtils.isNotBlank(oilCardRechargeVo.getEndTime())) {
            oilCardRechargeVo.setEndTime(oilCardRechargeVo.getEndTime() + " 23:59:59");
        }
        Page<OilCardRechargeDto> oilCardRechargeDtoPage = baseMapper.oilCardRecharge(new Page<>(oilCardRechargeVo.getPageNum(), oilCardRechargeVo.getPageSize()), oilCardRechargeVo);
        return oilCardRechargeDtoPage;
    }

    @Override
    @Async
    public void oilCardRechargeListExport(OilCardRechargeVo oilCardRechargeVo, String accessToken, ImportOrExportRecords importOrExportRecords) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long tenantId = loginInfo.getTenantId();
        oilCardRechargeVo.setTenantId(tenantId);
        if (StringUtils.isNotBlank(oilCardRechargeVo.getStartTime())) {
            oilCardRechargeVo.setStartTime(oilCardRechargeVo.getStartTime() + " 00:00:00");
        }
        if (StringUtils.isNotBlank(oilCardRechargeVo.getEndTime())) {
            oilCardRechargeVo.setEndTime(oilCardRechargeVo.getEndTime() + " 23:59:59");
        }
        List<OilCardRechargeDto> list = baseMapper.oilCardRechargeExport(oilCardRechargeVo);
        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{
                    "订单号", "司机手机", "司机姓名", "司机类型", "发起时间", "服务商名称", "油卡卡号", "油卡金额", "待核销金额", "状态",
                    "核销时间"};
            resourceFild = new String[]{
                    "getId", "getMobilePhone", "getLinkman", "getVehicleStatus", "getCreateDate", "getCompanyName", "getOilCardNum", "getAssignTotal", "getNoVerificationAmount", "getState",
                    "getVerificationDate"};

            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(list, showName, resourceFild, OilCardRechargeDto.class,
                    null);

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "oilCardRecharge.xlsx", inputStream.available());
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

    /**
     * @param userId             用户id
     * @param billId             用户手机号
     * @param businessId         业务id
     * @param orderId            订单id
     * @param amount             费用
     * @param vehicleAffiliation 资金渠道
     * @param finalPlanDate      尾款到账日期
     */
    public Map<String, String> setParameters(long userId, String billId, long businessId, long orderId, long amount, String vehicleAffiliation, String finalPlanDate) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", String.valueOf(userId));
        map.put("billId", String.valueOf(billId));
        map.put("businessId", String.valueOf(businessId));
        map.put("orderId", String.valueOf(orderId));
        map.put("amount", String.valueOf(amount));
        map.put("vehicleAffiliation", String.valueOf(vehicleAffiliation));
        map.put("finalPlanDate", String.valueOf(finalPlanDate));
        return map;
    }

    /**
     * 实体油卡核销
     */
    public void oilEntityVerification(long userId, String billId, String vehicleAffiliation, long orderId, long amount, long tenantId) throws Exception {
        log.info("实体油卡费用操作接口:userId=" + userId + "amount=" + amount + "orderId=" + orderId + "vehicleAffiliation=" + vehicleAffiliation);
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel amountFeeSubjectsRel = new BusiSubjectsRel();
        amountFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.OIL_ENTITY_VERIFICATION);
        amountFeeSubjectsRel.setAmountFee(amount);
        busiList.add(amountFeeSubjectsRel);
        //计算费用集合
        List<BusiSubjectsRel> busiSubjectsRelList = busiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OIL_ENTITY_VERIFICATION, busiList);
        Map<String, String> inParam = this.setParameters(userId, billId, EnumConsts.PayInter.OIL_ENTITY_VERIFICATION, orderId, amount, vehicleAffiliation, "");
        busiToOrderService.busiToOrder(inParam, busiSubjectsRelList);
    }

    @Override
    public void verificatOilTurnEntity(String ids, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (ids == null || ids.trim().length() < 0) {
            throw new BusinessException("参数Ids不能为空");
        }
        for (String id : ids.split(",")) {
            if (id != null && id.trim().length() > 0) {
                String idtrim = id.trim();
                OilTurnEntity ote = oilTurnEntityService.getOilTurnEntity(Long.valueOf(idtrim));
                if (ote == null) {
                    throw new BusinessException("没有找到id为:" + id.trim() + "油转实体油卡记录");
                }
                Long verificatedEntityFee = ote.getNoVerificationAmount();
                sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.OilTurnOilEntity, Long.valueOf(id.trim()), SysOperLogConst.OperType.Update, "[" + loginInfo.getName() + "]进行手动核销操作");
                oilTurnEntityService.updateById(Long.valueOf(id.trim()), 2, new Date(), 0L);
                //实卡油卡核销 进入订单限制表
                OrderScheduler details = orderSchedulerService.getOrderScheduler(ote.getOrderId());
                OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(ote.getOrderId());
                OrderFee orderFee = orderFeeService.getOrderFee(ote.getOrderId());
                OrderFeeH feeH = orderFeeHService.getOrderFeeH(ote.getOrderId());
                OrderInfo info = orderInfoService.getOrder(ote.getOrderId());
                OrderInfoH infoH = orderInfoHService.getOrderH(ote.getOrderId());
                if (orderFee == null && feeH == null) {
                    throw new BusinessException("订单信息不存在!");
                }
                if (details == null && orderSchedulerH == null) {
                    throw new BusinessException("订单信息不存在!");
                }
                //实体油卡核销(不区分自有车、招商车、社会车)
                if (details != null && orderFee != null) {
                    if (info.getToTenantId() != null && info.getToTenantId() > 0) {
                        SysTenantDef def = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
                        OrderLimit limit = iOrderLimitService.getOrderLimit(def.getAdminUser(), orderFee.getOrderId(), orderFee.getTenantId(), SysStaticDataEnum.USER_TYPE.ADMIN_USER, loginInfo);
                        if (limit != null) {
                            try {
                                this.oilEntityVerification(limit.getUserId(), details.getCarDriverPhone(), orderFee.getVehicleAffiliation(), details.getOrderId(), verificatedEntityFee, details.getTenantId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                this.oilEntityVerification(details.getCarDriverId(), details.getCarDriverPhone(), orderFee.getVehicleAffiliation(), details.getOrderId(), verificatedEntityFee, details.getTenantId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            this.oilEntityVerification(details.getCarDriverId(), details.getCarDriverPhone(), orderFee.getVehicleAffiliation(), details.getOrderId(), verificatedEntityFee, details.getTenantId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    if (infoH.getToTenantId() != null && infoH.getToTenantId() > 0) {
                        SysTenantDef def = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
                        OrderLimit limit = iOrderLimitService.getOrderLimit(def.getAdminUser(), feeH.getOrderId(), feeH.getTenantId(), SysStaticDataEnum.USER_TYPE.ADMIN_USER, loginInfo);
                        if (limit != null) {
                            try {
                                this.oilEntityVerification(limit.getUserId(), orderSchedulerH.getCarDriverPhone(), feeH.getVehicleAffiliation(), orderSchedulerH.getOrderId(), verificatedEntityFee, orderSchedulerH.getTenantId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                this.oilEntityVerification(orderSchedulerH.getCarDriverId(), orderSchedulerH.getCarDriverPhone(), feeH.getVehicleAffiliation(), orderSchedulerH.getOrderId(), verificatedEntityFee, orderSchedulerH.getTenantId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            this.oilEntityVerification(orderSchedulerH.getCarDriverId(), orderSchedulerH.getCarDriverPhone(), feeH.getVehicleAffiliation(), orderSchedulerH.getOrderId(), verificatedEntityFee, orderSchedulerH.getTenantId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
