package com.youming.youche.finance.provider.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.finance.api.IAdvanceExpireInfoService;
import com.youming.youche.finance.api.ac.ICmSalaryInfoNewService;
import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.finance.domain.AdvanceExpireInfo;
import com.youming.youche.finance.domain.ac.CmSalaryInfoNew;
import com.youming.youche.finance.dto.AdvanceExpireOutDto;
import com.youming.youche.finance.provider.mapper.AdvanceExpireInfoMapper;
import com.youming.youche.market.api.user.IUserRepairInfoService;
import com.youming.youche.market.domain.user.UserRepairInfo;
import com.youming.youche.order.api.order.IAccountBankRelService;
import com.youming.youche.order.api.order.IAccountDetailsService;
import com.youming.youche.order.api.order.IBusiSubjectsRelService;
import com.youming.youche.order.api.order.IConsumeOilFlowService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderOilSourceService;
import com.youming.youche.order.api.order.IOrderSchedulerHService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IPayFeeLimitService;
import com.youming.youche.order.api.order.IUserRepairMarginService;
import com.youming.youche.order.api.order.other.IBaseBusiToOrderService;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.order.AccountBankRel;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.ConsumeOilFlow;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.domain.order.UserRepairMargin;
import com.youming.youche.order.dto.ParametersNewDto;
import com.youming.youche.order.dto.SysTenantDefDto;
import com.youming.youche.order.dto.order.PayoutIntfDto;
import com.youming.youche.order.vo.AdvanceExpireOutVo;
import com.youming.youche.record.common.EnumConsts;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.util.CommonUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.security.Provider;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
* <p>
    * 手动到期沉淀表 服务实现类
    * </p>
* @author luona
* @since 2022-04-13
*/
@DubboService(version = "1.0.0")
public class AdvanceExpireInfoServiceImpl extends BaseServiceImpl<AdvanceExpireInfoMapper, AdvanceExpireInfo> implements IAdvanceExpireInfoService {
    @Resource
    AdvanceExpireInfoMapper advanceExpireInfoMapper;
    @DubboReference(version = "1.0.0")
    IOrderLimitService orderLimitService;
    @DubboReference(version = "1.0.0")
    IConsumeOilFlowService consumeOilFlowService;
    @DubboReference(version = "1.0.0")
    IUserRepairMarginService userRepairMarginService;
    @Resource
    LoginUtils loginUtils;
    @Resource
    RedisUtil redisUtil;
    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;
    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;
    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;
    @DubboReference(version = "1.0.0")
    ISysUserService sysUserService;
    @DubboReference(version = "1.0.0")
    IOrderAccountService orderAccountService;
    @DubboReference(version = "1.0.0")
    IBusiSubjectsRelService iBusiSubjectsRelService;
    @DubboReference(version = "1.0.0")
    IAccountDetailsService accountDetailsService;
    @DubboReference(version = "1.0.0")
    IAccountBankRelService iAccountBankRelService;
    @DubboReference(version = "1.0.0")
    IOrderSchedulerService orderSchedulerService;
    @DubboReference(version = "1.0.0")
    IOrderSchedulerHService orderSchedulerHService;
    @DubboReference(version = "1.0.0")
    IPayFeeLimitService payFeeLimitService;
    @DubboReference(version = "1.0.0")
    IBaseBusiToOrderService iBaseBusiToOrderService;
    @DubboReference(version = "1.0.0")
    IOrderOilSourceService orderOilSourceService;
    @DubboReference(version = "1.0.0")
    IUserRepairInfoService userRepairInfoService;



    @Override
    public Page<AdvanceExpireOutDto> queryUndueExpires(AdvanceExpireOutVo advanceExpireOutVo,Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (advanceExpireOutVo.getState()==null){
            advanceExpireOutVo.setState(-1);
        }
        Long tenantId = loginInfo.getTenantId();
        advanceExpireOutVo.setTenantId(tenantId);
        advanceExpireOutVo.setIncludeManual(true);
        advanceExpireOutVo.setUserType(-1);
        advanceExpireOutVo.setPayUserType(-1);
        Page<AdvanceExpireOutDto> page = new Page<>();
//        if(StringUtils.isEmpty(advanceExpireOutVo.getSignType())) {//全部
//            Page<AdvanceExpireOutDto> page1 = queryUndueExpiresOne(advanceExpireOutVo, pageNum, pageSize);
//            Page<AdvanceExpireOutDto> page2 = queryUndueExpiresTwo(advanceExpireOutVo, pageNum, pageSize);
//            Page<AdvanceExpireOutDto> page3 = queryUndueExpiresThree(advanceExpireOutVo, pageNum, pageSize);
//
//            Page<AdvanceExpireOutDto> page11 = queryUndueExpiresOne(advanceExpireOutVo, pageNum, (int)page1.getTotal());
//            Page<AdvanceExpireOutDto> page22 = queryUndueExpiresTwo(advanceExpireOutVo, pageNum, (int)page2.getTotal());
//            Page<AdvanceExpireOutDto> page33 = queryUndueExpiresThree(advanceExpireOutVo, pageNum, (int)page3.getTotal());
////            page.setTotal(page1.getTotal()+page2.getTotal()+ page3.getTotal());
////            page.setPages(page1.getPages()+page2.getPages()+page3.getPages());
////            page.setSize(page1.getSize());
////            page.setCurrent(page1.getCurrent());
//            List<AdvanceExpireOutDto> result = new ArrayList<>();
//            for (AdvanceExpireOutDto record : page11.getRecords()) {
//                result.add(record);
//            }
//            for (AdvanceExpireOutDto record : page22.getRecords()) {
//                result.add(record);
//            }
//            for (AdvanceExpireOutDto record : page33.getRecords()) {
//                result.add(record);
//            }
//            Page pages = this.getPages((int) page1.getCurrent(), (int) page1.getSize(), result);
//            page.setRecords(pages.getRecords());
//            page.setCurrent(pages.getCurrent());
//            page.setTotal(pages.getTotal());
//            page.setPages(pages.getPages());
//            return page;
//
//        }
        if(OrderAccountConst.ORDER_LIMIT_STS.DRIVER_SIGN1.equals(advanceExpireOutVo.getSignType())) {//订单尾款
            if(StrUtil.isNotEmpty(advanceExpireOutVo.getFlowId())){
                String reg = "^[0-9]+(.[0-9]+)?$";
                if(!advanceExpireOutVo.getFlowId().matches(reg)){
                    throw new BusinessException("请输入正确的流水号格式");
                }
                advanceExpireOutVo.setOrderId(Long.parseLong(advanceExpireOutVo.getFlowId()));
            }
            Page<AdvanceExpireOutDto> page1 = queryUndueExpiresOne(advanceExpireOutVo, pageNum, pageSize,accessToken);
            page.setTotal(page1.getTotal());
            page.setPages(page1.getPages());
            page.setSize(page1.getSize());
            page.setRecords(page1.getRecords());
            page.setCurrent(page1.getCurrent());
            return page;
        }
        if(OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN2.equals(advanceExpireOutVo.getSignType())) {//油
            if(StrUtil.isNotEmpty(advanceExpireOutVo.getFlowId())){
                advanceExpireOutVo.setBusiCode(advanceExpireOutVo.getFlowId());
            }
            Page<AdvanceExpireOutDto> page2 = queryUndueExpiresTwo(advanceExpireOutVo, pageNum, pageSize,accessToken);
            page.setTotal(page2.getTotal());
            page.setPages(page2.getPages());
            page.setSize(page2.getSize());
            page.setRecords(page2.getRecords());
            page.setCurrent(page2.getCurrent());
            return page;
        }
        if(OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN3.equals(advanceExpireOutVo.getSignType())) {//维修
            if(StrUtil.isNotEmpty(advanceExpireOutVo.getFlowId())){
                advanceExpireOutVo.setBusiCode(advanceExpireOutVo.getFlowId());
            }
            Page<AdvanceExpireOutDto> page3 = queryUndueExpiresThree(advanceExpireOutVo, pageNum, pageSize,accessToken);
            page.setTotal(page3.getTotal());
            page.setPages(page3.getPages());
            page.setSize(page3.getSize());
            page.setRecords(page3.getRecords());
            page.setCurrent(page3.getCurrent());
            return page;
        }
        return null;
    }

    @Override
    public Page<AdvanceExpireOutDto> queryUndueExpiresOne(AdvanceExpireOutVo advanceExpireOutVo, Integer pageNum, Integer pageSize, String accessToken) {
        Page<AdvanceExpireOutDto> page = new Page<>();
        Page<OrderLimit> orderLimitPage = orderLimitService.queryOrderLimits(advanceExpireOutVo, pageNum, pageSize);
        page.setTotal(orderLimitPage.getTotal());
        page.setPages(orderLimitPage.getPages());
        page.setSize(orderLimitPage.getSize());
        page.setCurrent(orderLimitPage.getCurrent());
        List<AdvanceExpireOutDto> retList = new ArrayList<>();
        if (orderLimitPage.getTotal()>0) {
            List<OrderLimit> list = orderLimitPage.getRecords();
            for (OrderLimit object : list) {
                AdvanceExpireOutDto advanceExpireOut = new AdvanceExpireOutDto();
                advanceExpireOut.setFlowId(object.getOrderId());
                advanceExpireOut.setBusiCode(object.getOrderId()+"");
                advanceExpireOut.setUserName(object.getUserName());
                advanceExpireOut.setUserId(object.getUserId());
                advanceExpireOut.setUserPhone(object.getUserPhone());

                if ((object.getVehicleClass()==2 || object.getVehicleClass()==3) && StringUtils.isNotBlank(object.getCollectionUserName())){
                    advanceExpireOut.setDriverName(object.getCarDriverMan());
                }else {
                    advanceExpireOut.setDriverName(object.getUserName());
                }
                advanceExpireOut.setMarginBalance(object.getNoPayFinal());
//                BeanUtils.copyProperties(object,advanceExpireOut);
                if (object.getExpireType() != null && object.getExpireType() == 1) {//手动到期
                    QueryWrapper<AdvanceExpireInfo> queryWrapper=new QueryWrapper<>();
                    queryWrapper.eq("from_flow_id",object.getOrderId());
                    queryWrapper.eq("sign_type",OrderAccountConst.MARGIN_TYPE.FIANL_MARGIN + "");
                    AdvanceExpireInfo advanceExpireInfo = this.baseMapper.selectOne(queryWrapper);
                    if (advanceExpireInfo != null && advanceExpireInfo.getMarginBalance() != null) {
                        advanceExpireOut.setMarginBalance(advanceExpireInfo.getMarginBalance());
                    }
                }
                //预计到期时间,自动到期
//                LocalDateTime planExpireDate = object.getFinalPlanDate();
//                if ((planExpireDate.isEqual(LocalDateTime.now())||planExpireDate.isBefore(LocalDateTime.now())) && object.getFianlSts()==0){
//                    LambdaQueryWrapper<AdvanceExpireInfo> queryWrapper=new LambdaQueryWrapper<>();
//                    queryWrapper.eq(AdvanceExpireInfo::getFromFlowId,object.getOrderId());
//                    //尾款自动到期,自动删除该条数据
//                    this.remove(queryWrapper);
////                    this.doExpire(advanceExpireOut.getFlowId(),advanceExpireOut.getUserId(),advanceExpireOut.getUserType(),advanceExpireOutVo.getSignType(),accessToken);
//                    //流转到平台支付、应付逾期
//                    if(advanceExpireOut.getMarginBalance()!=0 && advanceExpireOut.getMarginBalance()!=null) {
//                        this.settlement(1,"1",advanceExpireOut.getFlowId(), advanceExpireOut.getUserId(), object.getUserType(),advanceExpireOut.getMarginBalance(), accessToken);
//                    }
//                }
                advanceExpireOut.setType(OrderAccountConst.MARGIN_TYPE.FIANL_MARGIN);
                advanceExpireOut.setTypeName(getSysStaticData("MARGIN_TYPE", advanceExpireOut.getType() + "").getCodeName());
                advanceExpireOut.setPlanExpireDate(object.getFinalPlanDate());
                advanceExpireOut.setState(object.getFianlSts());
                advanceExpireOut.setStateName(getSysStaticData("GET_TYPE", advanceExpireOut.getState() + "").getCodeName());
                advanceExpireOut.setExpireType(object.getExpireType());
                advanceExpireOut.setReason(object.getStsNote());
                advanceExpireOut.setUserType(object.getUserType());
                retList.add(advanceExpireOut);
            }
            page.setRecords(retList);
        }
        return page;
    }

    @Override
    public Page<AdvanceExpireOutDto> queryUndueExpiresTwo(AdvanceExpireOutVo advanceExpireOutVo, Integer pageNum, Integer pageSize, String accessToken) {
        Page<AdvanceExpireOutDto> page = new Page<>();
        advanceExpireOutVo.setIncludeManual(true);
        Page<ConsumeOilFlow> consumeOilFlowPage = consumeOilFlowService.queryConsumeOilFlowsNew(advanceExpireOutVo, pageNum, pageSize);
        page.setTotal(consumeOilFlowPage.getTotal());
        page.setPages(consumeOilFlowPage.getPages());
        page.setSize(consumeOilFlowPage.getSize());
        page.setCurrent(consumeOilFlowPage.getCurrent());
        List<AdvanceExpireOutDto> retList = new ArrayList<>();
        if (consumeOilFlowPage.getTotal()>0){
            List<ConsumeOilFlow> list = consumeOilFlowPage.getRecords();
            for (ConsumeOilFlow cons : list){
                AdvanceExpireOutDto advanceExpireOut=new AdvanceExpireOutDto();
                advanceExpireOut.setUserName(cons.getUserName());
                advanceExpireOut.setUserPhone(cons.getUserBill());
                advanceExpireOut.setDriverName(cons.getOtherName());

                if (cons.getUndueAmount()!=null){
                    advanceExpireOut.setMarginBalance(cons.getUndueAmount());
                }
                if (cons.getSoNbr()!=null){
                    advanceExpireOut.setSoNbr(cons.getSoNbr());
                }
                if (cons.getFlowId()!=null){
                    advanceExpireOut.setFlowId(cons.getFlowId());
//                    advanceExpireOut.setBusiCode(String.valueOf(cons.getFlowId()));
                    // 油支付生成的单号
                    advanceExpireOut.setBusiCode(cons.getOrderId());
                }
                if (cons.getOilAffiliation()!=null){
                    advanceExpireOut.setOilAffiliation(cons.getOilAffiliation());
                }
                if (cons.getVehicleAffiliation()!=null){
                    advanceExpireOut.setVehicleAffiliation(cons.getVehicleAffiliation());
                }
                advanceExpireOut.setUserType(cons.getUserType());
                advanceExpireOut.setPayUserType(cons.getPayUserType());
                advanceExpireOut.setUserId(cons.getUserId());
                advanceExpireOut.setFlowIds(String.valueOf(cons.getFlowIds()));

                Integer expireType = cons.getExpireType();
                if(expireType!=null && expireType==1){//手动到期
                    Long marginBalance = advanceExpireInfoMapper.getAdvanceExpireInfoByIds(advanceExpireOut.getFlowIds(),OrderAccountConst.MARGIN_TYPE.OIL_MARGIN+"");
                    advanceExpireOut.setMarginBalance(marginBalance);
                }
                //预计到期时间,自动到期
//                LocalDateTime planExpireDate = cons.getGetDate();
//                if ((planExpireDate.isEqual(LocalDateTime.now())||planExpireDate.isBefore(LocalDateTime.now())) && cons.getState()==0){
//                    LambdaQueryWrapper<AdvanceExpireInfo> queryWrapper=new LambdaQueryWrapper<>();
//                    queryWrapper.eq(AdvanceExpireInfo::getFromFlowId,cons.getFlowId());
                    //尾款自动到期,自动删除该条数据
//                    this.remove(queryWrapper);
//                    this.doExpire(advanceExpireOut.getFlowId(),advanceExpireOut.getUserId(),advanceExpireOut.getUserType(),advanceExpireOutVo.getSignType(),accessToken);
                    //流转到平台支付和应付逾期
//                }
                advanceExpireOut.setType(OrderAccountConst.MARGIN_TYPE.OIL_MARGIN);
                advanceExpireOut.setTypeName(getSysStaticData("MARGIN_TYPE", advanceExpireOut.getType()+ "").getCodeName());
                advanceExpireOut.setPlanExpireDate(cons.getGetDate());
                advanceExpireOut.setState(cons.getState());
                advanceExpireOut.setStateName(getSysStaticData("GET_TYPE", advanceExpireOut.getState()+ "").getCodeName());
                advanceExpireOut.setExpireType(expireType);
                advanceExpireOut.setReason(cons.getGetResult());
                advanceExpireOut.setMarginBalanceDouble(advanceExpireOut.getMarginBalance()==null?null:advanceExpireOut.getMarginBalance()/100.00);
                retList.add(advanceExpireOut);
            }
            page.setRecords(retList);
        }
        return page;
    }

    @Override
    public Page<AdvanceExpireOutDto> queryUndueExpiresThree(AdvanceExpireOutVo advanceExpireOutVo, Integer pageNum, Integer pageSize, String accessToken) {
        Page<AdvanceExpireOutDto> page = new Page<>();
        Page<UserRepairMargin> userRepairMarginPage = userRepairMarginService.queryUserRepairMargins(advanceExpireOutVo, pageNum, pageSize);
        page.setTotal(userRepairMarginPage.getTotal());
        page.setPages(userRepairMarginPage.getPages());
        page.setSize(userRepairMarginPage.getSize());
        page.setCurrent(userRepairMarginPage.getCurrent());
        List<AdvanceExpireOutDto> retList = new ArrayList<>();
        if(userRepairMarginPage.getCurrent()>0){
            List<UserRepairMargin> list =userRepairMarginPage.getRecords();
            for (UserRepairMargin ol : list) {
                AdvanceExpireOutDto advanceExpireOut = new AdvanceExpireOutDto();
                advanceExpireOut.setFlowId(ol.getId());
                advanceExpireOut.setBusiCode(ol.getOrderId());
                advanceExpireOut.setUserName(ol.getUserName());
                advanceExpireOut.setUserPhone(ol.getUserBill());
                advanceExpireOut.setDriverName(ol.getOtherName());
                advanceExpireOut.setUserId(ol.getUserId());
                UserDataInfo userDataInfo = userDataInfoService.selectUserType(ol.getUserId());
                if (advanceExpireOut.getUserType()==null){
                    advanceExpireOut.setUserType(userDataInfo.getUserType());
                }
//                if ((ol.getVehicleClass()==2 || ol.getVehicleClass()==3) && StringUtils.isNotBlank(ol.getCollectionUserName())){
//                    advanceExpireOut.setDriverName(ol.getCarDriverMan());
//                }else {
//                    advanceExpireOut.setDriverName(ol.getOtherName());
//                }
                advanceExpireOut.setMarginBalance(ol.getUndueAmount());
                if (ol.getExpireType() != null && ol.getExpireType() == 1) {//手动到期
                    QueryWrapper<AdvanceExpireInfo> queryWrapper=new QueryWrapper<>();
                    queryWrapper.eq("from_flow_id",ol.getId());
                    queryWrapper.eq("sign_type",OrderAccountConst.MARGIN_TYPE.REPAIR_MARGIN + "");
                    AdvanceExpireInfo advanceExpireInfo = this.baseMapper.selectOne(queryWrapper);
                    if (advanceExpireInfo != null && advanceExpireInfo.getMarginBalance() != null) {
                        advanceExpireOut.setMarginBalance(advanceExpireInfo.getMarginBalance());
                    }
                }
                //预计到期时间,自动到期
//                LocalDateTime planExpireDate = ol.getGetDate();
//                if ((planExpireDate.isEqual(LocalDateTime.now())||planExpireDate.isBefore(LocalDateTime.now())) && ol.getState()==0){
//                    LambdaQueryWrapper<AdvanceExpireInfo> queryWrapper=new LambdaQueryWrapper<>();
//                    queryWrapper.eq(AdvanceExpireInfo::getFromFlowId,ol.getId());
                    //尾款自动到期,自动删除该条数据
//                    this.remove(queryWrapper);
//                    this.doExpire(advanceExpireOut.getFlowId(),advanceExpireOut.getUserId(),advanceExpireOut.getUserType(),advanceExpireOutVo.getSignType(),accessToken);
                    //流转到平台支付和应付逾期

//                }
                advanceExpireOut.setType(OrderAccountConst.MARGIN_TYPE.REPAIR_MARGIN);
                advanceExpireOut.setTypeName(getSysStaticData("MARGIN_TYPE", advanceExpireOut.getType() + "").getCodeName());
                advanceExpireOut.setPlanExpireDate(ol.getGetDate());
                advanceExpireOut.setState(ol.getState());
                advanceExpireOut.setStateName(getSysStaticData("GET_TYPE", advanceExpireOut.getState() + "").getCodeName());
                advanceExpireOut.setExpireType(ol.getExpireType());
                advanceExpireOut.setReason(ol.getGetResult());
                retList.add(advanceExpireOut);
            }
            page.setRecords(retList);
        }
        return page;
    }

    @Override
    public AdvanceExpireInfo getAdvanceExpireInfoByFlowId(long flowId, String signType) {
        LambdaQueryWrapper<AdvanceExpireInfo> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(AdvanceExpireInfo::getFromFlowId,flowId);
        lambdaQueryWrapper.eq(AdvanceExpireInfo::getSignType,signType).orderByDesc(AdvanceExpireInfo::getCreateTime);
        List<AdvanceExpireInfo> list = super.list(lambdaQueryWrapper);
        if(list != null && list.size()>0){
            return list.get(0);
        }
        return new AdvanceExpireInfo();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean doExpire(Long flowId, Long userId, Integer userType, String signType,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if(OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN2.equals(signType)) {//油
            ConsumeOilFlow consumeOilFlow = consumeOilFlowService.getConsumeOilFlow(flowId);
            AdvanceExpireInfo advanceExpireInfo = new AdvanceExpireInfo();
//            LocalDateTime now = LocalDateTime.now();
//            consumeOilFlow.setGetDate(now);
//            consumeOilFlow.setExpireType(1);//手动到期
//            consumeOilFlow.setState(1);
//            consumeOilFlowService.save(consumeOilFlow);
            advanceExpireInfoMapper.updateConsumeOilFlow(flowId,LocalDateTime.now());
            advanceExpireInfo.setFromFlowId(consumeOilFlow.getId());
            advanceExpireInfo.setDriverName(consumeOilFlow.getUserName());
            advanceExpireInfo.setSignType(OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN2);
            advanceExpireInfo.setMarginBalance(consumeOilFlow.getUndueAmount());
            advanceExpireInfo.setPlanExpireDate(LocalDateTime.now());
            if(userId==0){
                userId=consumeOilFlow.getUserId();
            }
            UserDataInfo userDataInfo = userDataInfoService.getUserDataInfo(userId);
            advanceExpireInfo.setUserName(userDataInfo.getLinkman());
            advanceExpireInfo.setBillId(userDataInfo.getMobilePhone());
            AdvanceExpireInfo advance= this.getAdvanceExpireInfoByFlowId(advanceExpireInfo.getFromFlowId(), signType);
            if(advance.getFromFlowId()!=null){
                throw new BusinessException("该记录已经到期，请刷新页面重试！");
            }
//            advanceExpireInfo.setState(1);
//            QueryWrapper queryWrapper=new QueryWrapper();
//            queryWrapper.eq("from_flow_id",advanceExpireInfo.getFromFlowId());
            this.save(advanceExpireInfo);
            // 保存操作日志
            sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.AdvanceExpire, consumeOilFlow.getId(), SysOperLogConst.OperType.Update, "操作提前到期");
            // 流转到平台支付
//            if(consumeOilFlow.getUndueAmount()!=0 && consumeOilFlow.getUndueAmount()!=null) {
//                this.settlement(0,signType, flowId, userId, userType, consumeOilFlow.getUndueAmount(), accessToken);
//            }
        }else {
            AdvanceExpireInfo advanceExpireInfo = new AdvanceExpireInfo();
            if (OrderAccountConst.ORDER_LIMIT_STS.DRIVER_SIGN1.equals(signType)) {//订单尾款
                if (userType==null || userType<=0){
                    userType=0;
                }
                OrderLimit orderLimit = orderLimitService.getOrderLimitByUserIdAndOrderId(userId, flowId, userType);//订单编号和用户Id查询订单限制表
//                OrderLimit limit=new OrderLimit();
//                limit.setFinalPlanDate(LocalDateTime.now());
//                limit.setExpireType(1);//手动到期
//                limit.setFianlSts(1);
//                QueryWrapper queryWrapper =new QueryWrapper();
//                queryWrapper.eq("order_id",flowId);
//                orderLimitService.update(limit,queryWrapper);
                advanceExpireInfoMapper.updateOrderLimit(1,LocalDateTime.now(),flowId);
                advanceExpireInfo.setFromFlowId(orderLimit.getOrderId());
                advanceExpireInfo.setDriverName(orderLimit.getUserName());
                advanceExpireInfo.setSignType(OrderAccountConst.ORDER_LIMIT_STS.DRIVER_SIGN1);
                advanceExpireInfo.setMarginBalance(orderLimit.getNoPayFinal());
                advanceExpireInfo.setPlanExpireDate(LocalDateTime.now());
                if (userId == 0) {
                    userId = orderLimit.getUserId();
                }
            }
            if(OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN3.equals(signType)){//维修
                UserRepairMargin userRepairMargin =userRepairMarginService.getUserRepairMargin(flowId);
//                userRepairMargin.setGetDate(LocalDateTime.now());
//                userRepairMargin.setExpireType(1);//手动到期
//                userRepairMarginService.save(userRepairMargin);
                advanceExpireInfoMapper.updateUserRepairMargin(flowId,LocalDateTime.now());
                advanceExpireInfo.setFromFlowId(userRepairMargin.getId());
                advanceExpireInfo.setDriverName(userRepairMargin.getUserName());
                advanceExpireInfo.setSignType(OrderAccountConst.ORDER_LIMIT_STS.SERVICE_SIGN3);
                advanceExpireInfo.setMarginBalance(userRepairMargin.getUndueAmount());
                if(userId==0){
                    userId=userRepairMargin.getUserId();
                }
            }
            UserDataInfo userDataInfo = userDataInfoService.getUserDataInfo(userId);
            advanceExpireInfo.setUserName(userDataInfo.getLinkman());
            advanceExpireInfo.setBillId(userDataInfo.getMobilePhone());
            AdvanceExpireInfo advance= this.getAdvanceExpireInfoByFlowId(advanceExpireInfo.getFromFlowId(), signType);
            if(advance.equals(null)){
                throw new BusinessException("该记录已经到期，请刷新页面重试！");
            }
            this.save(advanceExpireInfo);
            // 保存操作日志
            sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.AdvanceExpire, flowId, SysOperLogConst.OperType.Update, "操作提前到期");
        }
        return true;
    }

    /**
     * 提前到期数据流转平台支付
     * @param orderId
     * @param userId
     * @param sumFee
     * @param accessToken
     */
    private void settlement(int autoExpire,String signType,Long orderId, Long userId, Integer userType, Long sumFee, String accessToken){
        UserDataInfo userDataInfo = userDataInfoService.selectUserType(userId);

        LoginInfo user = loginUtils.get(accessToken);
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(user.getTenantId());
        if (userDataInfo == null) {
            throw new BusinessException("未找到提前到期信息!");
        }
        Long tenantUserId = sysTenantDefService.getTenantAdminUser(user.getTenantId());
        if (tenantUserId == null || tenantUserId <= 0L) {
            throw new BusinessException("没有找到租户的用户id!");
        }
        UserDataInfo tenantUser = userDataInfoService.getUserDataInfo(tenantUserId);
        if (tenantUser == null) {
            throw new BusinessException("没有找到租户信息");
        }
        SysUser tenantSysOperator = sysUserService.getSysOperatorByUserIdOrPhone(tenantUserId, null, 0L);
        if (tenantSysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        // 通过用户id获取用户信息
        SysUser sysOperator = sysUserService.getSysOperatorByUserIdOrPhone(user.getUserInfoId(), null, 0L);
        if (sysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }

        //实发工资
        String vehicleAffiliation = "0";//工资单是不需要开票的
        String oilAffiliation = "0";//工资单是不需要开票的
        Long soNbr = CommonUtil.createSoNbr();
        // 根据用户ID和资金渠道类型获取账户信息(司机)
        OrderAccount driverAccount = orderAccountService.queryOrderAccount(userDataInfo.getId(), vehicleAffiliation,
                0L, sysTenantDef.getId(), oilAffiliation, userType);
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        // 应收逾期(车管报销)
        if (autoExpire==1) {
            BusiSubjectsRel amountFeeSubjectsRel1 = new BusiSubjectsRel();
            amountFeeSubjectsRel1.setSubjectsId(EnumConsts.SubjectIds.SALARY_RECEIVABLE);

            amountFeeSubjectsRel1.setAmountFee(sumFee);
            busiList.add(amountFeeSubjectsRel1);
        }
        // 计算费用集合
        List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.CAR_DRIVER_SALARY, busiList);
        // 写入账户明细表并修改账户金额费用
        accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.CAR_DRIVER_SALARY, tenantSysOperator.getUserInfoId(),
                tenantSysOperator.getName(), driverAccount, busiSubjectsRelList, soNbr, 0L, sysOperator.getName(),
                null, sysTenantDef.getId(), null, "", null, vehicleAffiliation, user);

        // 根据用户ID和资金渠道类型获取账户信息(车队)
        OrderAccount tenantAccount = orderAccountService.queryOrderAccount(sysTenantDef.getAdminUser(), vehicleAffiliation, 0L,
                user.getTenantId(), oilAffiliation, SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        List<BusiSubjectsRel> tenantBusiList = new ArrayList<BusiSubjectsRel>();
        // 应付逾期(车管报销)
        if (autoExpire==1) {
            BusiSubjectsRel amountFeeSubjectsRel = new BusiSubjectsRel();
            amountFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.SALARY_HANDLE);
            amountFeeSubjectsRel.setAmountFee(sumFee);
            tenantBusiList.add(amountFeeSubjectsRel);
        }
        // 计算费用集合
        List<BusiSubjectsRel> tenantSubjectsRels = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.CAR_DRIVER_SALARY, tenantBusiList);
        // 写入账户明细表并修改账户金额费用
        accountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.CAR_DRIVER_SALARY,
                tenantSysOperator.getUserInfoId(), tenantSysOperator.getName(), tenantAccount, tenantSubjectsRels, soNbr, 0L,
                sysOperator.getName(), null, sysTenantDef.getId(), null, "", null, vehicleAffiliation, user);

        // 保存支出接口表
        PayoutIntfDto payoutIntfDto = new PayoutIntfDto();
        payoutIntfDto.setObjType(SysStaticDataEnum.OBJ_TYPE.WITHDRAWAL + "");// 15表示提现
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(user.getTelPhone())) {
            payoutIntfDto.setObjId(Long.parseLong(user.getTelPhone()));
        }
        payoutIntfDto.setUserId(userDataInfo.getId());
        //会员体系改造开始
        payoutIntfDto.setUserType(SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        payoutIntfDto.setPayUserType(SysStaticDataEnum.USER_TYPE.ADMIN_USER);
        //会员体系改造结束
        payoutIntfDto.setTxnAmt(sumFee);
        payoutIntfDto.setCreateDate(new Date());
        payoutIntfDto.setTenantId(-1L);
        payoutIntfDto.setVehicleAffiliation(OrderAccountConst.VEHICLE_AFFILIATION.VEHICLE_AFFILIATION0);
        payoutIntfDto.setWithdrawalsChannel("4");
        payoutIntfDto.setIsDriver(OrderAccountConst.IS_DRIVER.DRIVER);
        payoutIntfDto.setSourceRemark(SysStaticDataEnum.SOURCE_REMARK.SOURCE_REMARK_4);//工资打款
        //打款租户
        payoutIntfDto.setPayTenantId(sysTenantDef.getId());

        AccountBankRel defalutAccountBankRel = iAccountBankRelService.getDefaultAccountBankRel(userDataInfo.getId(), EnumConsts.ACCOUNT_BANK_REL.BANK_TYPE_0, SysStaticDataEnum.USER_TYPE.DRIVER_USER);
        if (defalutAccountBankRel != null) {
            payoutIntfDto.setBankCode(defalutAccountBankRel.getBankName());
            payoutIntfDto.setProvince(defalutAccountBankRel.getProvinceName());
            payoutIntfDto.setCity(defalutAccountBankRel.getCityName());
            payoutIntfDto.setAccNo(defalutAccountBankRel.getPinganCollectAcctId());
            payoutIntfDto.setAccName(defalutAccountBankRel.getAcctName());
        }
        if (signType.equals("1")) {
            //获取订单数据
            if (orderId != null && orderId > -1) {
                com.youming.youche.order.domain.order.OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
                String busiCode = null;
                String plateNumber = null;
                if (orderScheduler != null && orderScheduler.getOrderId() != null) {
                    busiCode = String.valueOf(orderId);
                    if (StrUtil.isNotEmpty(orderScheduler.getPlateNumber())) {
                        plateNumber = orderScheduler.getPlateNumber();
                    }
                } else {
                    OrderSchedulerH orderSchedulerH = orderSchedulerHService.getOrderSchedulerH(orderId);
                    if (orderSchedulerH != null && orderSchedulerH.getOrderId() != null) {
                        busiCode = String.valueOf(orderId);
                        if (StrUtil.isNotEmpty(orderSchedulerH.getPlateNumber())) {
                            plateNumber = orderSchedulerH.getPlateNumber();
                        }
                    }
                }
                payoutIntfDto.setBusiCode(busiCode);
                payoutIntfDto.setPlateNumber(plateNumber);
                payoutIntfDto.setOrderId(orderId);
            }
        }else if(signType.equals("2")){
            LambdaQueryWrapper<UserRepairInfo> queryWrapper=new LambdaQueryWrapper<UserRepairInfo>();
            queryWrapper.eq(UserRepairInfo::getId,orderId);
            queryWrapper.orderByDesc(UserRepairInfo::getCreateTime);
            List<UserRepairInfo> list = userRepairInfoService.list(queryWrapper);
            payoutIntfDto.setPlateNumber(list.get(1).getPlateNumber());


        }
        /**
         * 初始状态 ： 即使系统自动打款,由于不开票，也要改为手动核销
         * 版本更替 >> 2018-07-04 liujl 接入平安银行改造，改为不需要手动核销司机工资
         */
        payoutIntfDto.setTxnType(OrderAccountConst.TXN_TYPE.XS_TXN_TYPE);//直接打款到银行卡
        payoutIntfDto.setRemark("即将到期转可提现");

        payoutIntfDto.setPayObjId(sysTenantDef.getAdminUser());
        payoutIntfDto.setPayType(OrderAccountConst.PAY_TYPE.TENANT);//打款类型
        //业务类型
        payoutIntfDto.setBusiId(Long.valueOf(EnumConsts.ZhPayInter.PAYFOR_CODE));//尾款到期

//        if (signType.equals("1")){
//            payoutIntfDto.setBusiId(Long.valueOf(EnumConsts.ZhPayInter.PAYFOR_CODE));//尾款到期
//        }else if (signType.equals("2")){
//            payoutIntfDto.setBusiId(Long.valueOf(OrderAccountConst.MARGIN_TYPE.OIL_MARGIN));//油费到期
//        }else if (signType.equals("3")){
//            payoutIntfDto.setBusiId(Long.valueOf(OrderAccountConst.MARGIN_TYPE.REPAIR_MARGIN));//维修保养到期
//        }

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
        boolean isAutoTransfer = payFeeLimitService.isMemberAutoTransfer(user.getTenantId());
        if (isAutoTransfer) {
            payoutIntfDto.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1);//系统自动打款
        } else {
            payoutIntfDto.setIsAutomatic(OrderAccountConst.IS_AUTOMATIC.AUTOMATIC0);//手动打款
        }

        Long type = payFeeLimitService.getAmountLimitCfgVal(user.getTenantId(), SysStaticDataEnum.PAY_FEE_LIMIT_SUB_TYPE.DRIVER_SARALY_403);

        if (type == 0) {
            payoutIntfDto.setAccountType(EnumConsts.BALANCE_BANK_TYPE.PRIVATE_PAYABLE_ACCOUNT);//私人付款账户余额
        } else if (type == 1) {
            payoutIntfDto.setAccountType(EnumConsts.BALANCE_BANK_TYPE.BUSINESS_PAYABLE_ACCOUNT);//对公付款账户余额
        }
        iBaseBusiToOrderService.doSavePayOutIntfForOA(payoutIntfDto, accessToken);
        SysTenantDefDto sysTenantDefDto = new SysTenantDefDto();
        ParametersNewDto parametersNewDto = orderOilSourceService.setParametersNew(userDataInfo.getId(), userDataInfo.getMobilePhone(), EnumConsts.PayInter.CAR_DRIVER_SALARY, 0L, sumFee, vehicleAffiliation, "");
        parametersNewDto.setAccountDatailsId(payoutIntfDto.getId());
        parametersNewDto.setTenantId(sysTenantDef.getId());
//        parametersNewDto.setSalaryId(userDataInfo.getId());
        org.springframework.beans.BeanUtils.copyProperties(sysTenantDef, sysTenantDefDto);
        parametersNewDto.setSysTenantDef(sysTenantDefDto);
        parametersNewDto.setBatchId(String.valueOf(soNbr));
        busiSubjectsRelList.addAll(tenantSubjectsRels);
        orderOilSourceService.busiToOrderNew(parametersNewDto, busiSubjectsRelList, user);

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

}
