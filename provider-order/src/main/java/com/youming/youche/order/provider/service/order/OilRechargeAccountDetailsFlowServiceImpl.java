package com.youming.youche.order.provider.service.order;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.capital.api.IOilAccountService;
import com.youming.youche.capital.domain.UserDataInfo;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.domain.payable.SysOperator;
import com.youming.youche.order.api.order.*;
import com.youming.youche.order.api.service.IServiceInfoService;
import com.youming.youche.order.domain.order.*;
import com.youming.youche.order.dto.order.OilRechargeAccountDetailsFlowOutDto;
import com.youming.youche.order.provider.mapper.order.*;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.order.util.BeanUtils;
import com.youming.youche.order.vo.OilRechargeAccountDetailsFlowVo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.SysTenantDef;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author liangyan
 * @since 2022-03-23
 */
@DubboService(version = "1.0.0")
@Service
public class OilRechargeAccountDetailsFlowServiceImpl extends BaseServiceImpl<OilRechargeAccountDetailsFlowMapper, OilRechargeAccountDetailsFlow> implements IOilRechargeAccountDetailsFlowService {

    @Resource
    IOilRechargeAccountService iOilRechargeAccountService;

    @Resource
    IOilRechargeAccountDetailsService iOilRechargeAccountDetailsService;
    @Resource
    private IOrderAccountService orderAccountService;
    @Resource
    private IAccountBankRelService accountBankRelService1;
    @Autowired
    private IOilRechargeAccountDetailsService oilRechargeAccountDetailsService;
    @Autowired
    private IOilRechargeAccountDetailsFlowService oilRechargeAccountDetailsFlowService;
    @Autowired
    private IBillInfoReceiveRelService billInfoReceiveRelService;
    @DubboReference(version = "1.0.0")
    private ISysTenantDefService sysTenantDefService;
    @Resource
    private ReadisUtil readisUtil;
    @DubboReference(version = "1.0.0")
    private ISysOperLogService sysOperLogService;
    @Autowired
    private ISubjectsInfoService subjectsInfoService;
    @Autowired
    private IOilRechargeAccountFlowService oilRechargeAccountFlowService;
    @Autowired
    private IOilRechargeAccountDetailsFlowExtService oilRechargeAccountDetailsFlowExtService;
    @Autowired
    private IOilSourceRecordService oilSourceRecordService;
    @Autowired
    private IOilRechargeAccountService oilRechargeAccountService;
    @Autowired
    private IOrderFeeExtService orderFeeExtService;
    @Autowired
    private IOrderFeeExtHService orderFeeExtHService;
    @Autowired
    private IBillPlatformService billPlatformService;
    @Autowired
    private IOilRecord56kService oilRecord56kService;
    @DubboReference(version = "1.0.0")
    private IUserDataInfoService userDataInfoService;
    @Resource
    LoginUtils loginUtils;
    @DubboReference(version = "1.0.0")
    IOilAccountService iOilAccountService;
    @Resource
    IPinganLockInfoService iPinganLockInfoService;
    @Resource
    IOrderAccountService iOrderAccountService;
    @Resource
    IAccountBankRelService iAccountBankRelService;
    @Resource
    PayoutIntfMapper payoutIntfMapper;
    @Resource
    IServiceInfoService iServiceInfoService;
    @DubboReference(version = "1.0.0")
    IUserDataInfoService iUserDataInfoService;
    @Resource
    RedisUtil redisUtil;
    @Resource
    IBillSettingService iBillSettingService;
    @Resource
    IBillPlatformService iBillPlatformService;
    @Resource
    IOilRechargeAccountDetailsService iOilRechargeAccountDetailsServicel;
    @Lazy
    @Resource
    IBusiSubjectsRelService iBusiSubjectsRelService;
    @Resource
    IAccountDetailsService iAccountDetailsService;

    @Resource
    IPayoutIntfService iPayoutIntfService;
    @Resource
    AccountBankRelMapper accountBankRelMapper;
    @Resource
    OrderSchedulerMapper orderSchedulerMapper;
    @Resource
    OrderSchedulerHMapper orderSchedulerHMapper;
    @Override
    public List<OilRechargeAccountDetailsFlow> getOrderDetailsFlows(Long userId, String orderNum, Integer busiType, Integer orderBy) {
        LambdaQueryWrapper<OilRechargeAccountDetailsFlow> lambda= Wrappers.lambdaQuery();
        lambda.eq(OilRechargeAccountDetailsFlow::getUserId,userId)
              .eq(OilRechargeAccountDetailsFlow::getOrderNum,orderNum);
        if(busiType != null && busiType>-1){
            lambda.eq(OilRechargeAccountDetailsFlow::getBusiType,busiType);
        }
        if(orderBy != null && orderBy==1){
            lambda.orderByDesc(OilRechargeAccountDetailsFlow::getCreateTime);
        }else{
            lambda.orderByDesc(OilRechargeAccountDetailsFlow::getCreateTime);
        }
        return this.list(lambda);
    }

    @Override
    public List<OilRechargeAccountDetailsFlow> getUnMatchedFlowsASC(Long userId, Integer sourceType) {
        LambdaQueryWrapper<OilRechargeAccountDetailsFlow> lambda=Wrappers.lambdaQuery();
        lambda.eq(OilRechargeAccountDetailsFlow::getUserId,userId)
              .eq(OilRechargeAccountDetailsFlow::getSourceType,sourceType)
              .eq(OilRechargeAccountDetailsFlow::getUnMatchAmount,0L)
              .eq(OilRechargeAccountDetailsFlow::getVerifyState,1)
              .orderByAsc(OilRechargeAccountDetailsFlow::getCreateTime);

        return this.list(lambda);
    }

    @Override
    public OilRechargeAccountDetailsFlow getRechargeDetailsFlows(String busiCode, int sourceType) {
        LambdaQueryWrapper<OilRechargeAccountDetailsFlow> queryWrapper= Wrappers.lambdaQuery();
        queryWrapper.eq(OilRechargeAccountDetailsFlow::getBusiCode,busiCode)
                .eq(OilRechargeAccountDetailsFlow::getSourceType,sourceType);
        return this.getOne(queryWrapper);
    }

    @Override
    public void payRechargeSucess(String busiCode, int isAutomatic) {
        List<OilRechargeAccountDetailsFlow> detailsFlows = this.getDetailsFlows(busiCode);
        for (OilRechargeAccountDetailsFlow detailsFlow : detailsFlows) {
            if (detailsFlow.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE3) {
                continue;
            }

            long relId = detailsFlow.getRelId();// 明细账户Id
            long amount = detailsFlow.getAmount();
            OilRechargeAccount oilRechargeAccount = iOilRechargeAccountService.getOilRechargeAccount(detailsFlow.getUserId());
            OilRechargeAccountDetails oilRechargeAccountDetails = iOilRechargeAccountDetailsService
                    .getById(relId);
            oilRechargeAccountDetails.setAccountBalance(oilRechargeAccountDetails.getAccountBalance() + amount);
            iOilRechargeAccountDetailsService.saveOrUpdate(oilRechargeAccountDetails);
            if (detailsFlow.getSourceType() == SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5) {
                oilRechargeAccount.setInvoiceOilBalance(oilRechargeAccount.getInvoiceOilBalance() == null ? 0L : oilRechargeAccount.getInvoiceOilBalance()
                        + amount);
            } else {
                oilRechargeAccount.setCashRechargeBalance(oilRechargeAccount.getCashRechargeBalance() + amount);
            }
            oilRechargeAccount.setAccountBalance(oilRechargeAccount.getAccountBalance() + amount);
            iOilRechargeAccountService.saveOrUpdate(oilRechargeAccount);
            detailsFlow.setVerifyState(SysStaticDataEnum.OIL_RECHARGE_VERIFY_STATE.STATE1);
            this.saveOrUpdate(detailsFlow);
        }
    }

    @Override
    public List<OilRechargeAccountDetailsFlow> getDetailsFlows(String busiCode) {
        LambdaQueryWrapper<OilRechargeAccountDetailsFlow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OilRechargeAccountDetailsFlow::getBusiCode, busiCode);
        return this.list(queryWrapper);
    }

    /**
     * niejiwei
     * 司机小程序
     * 充值流水列表-小程序接口
     * 50057
     * @return
     */
    @Override
    public Page<OilRechargeAccountDetailsFlowOutDto> getOilRechargeAccountDetailsFlow(OilRechargeAccountDetailsFlowVo vo,
                                                                                      Integer pageNum, Integer pageSize,
                                                                                      String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (vo.getType()==null){
            vo.setType(1);//1充值账户 2已开票账户
        }
        SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
        Integer[] sourTypes = null;
        if(vo.getType()==1){
            sourTypes=new Integer[]{SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2,
                    SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE3,
                    SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5};
            if (vo.getOilRechargeBillType()!=null){
                if(vo.getOilRechargeBillType()==1){//油票
                    sourTypes=new Integer[]{SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2,
                            SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE3};
                }else if(vo.getOilRechargeBillType()==2){//抵扣运输专票
                    sourTypes=new Integer[]{
                            SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5};
                }
            }

        }else{
            sourTypes=new Integer[]{SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE1,
                    SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE4,
                    SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE6};
            if(vo.getOilRechargeBillType()>-1){//油票
                sourTypes=new Integer[]{-1};
            }
        }
        vo.setSourTypes(sourTypes);
        Map banks = new HashMap();
        List<AccountBankRel> bankList = accountBankRelMapper.selectAcctUserTypeRelList(vo.getUserId(), vo.getAcctNameOrNo());
       List<String> accIds = new ArrayList<>();
        if(bankList!=null && bankList.size()>0){
            for( AccountBankRel bankRel : bankList){
                accIds.add(bankRel.getPinganPayAcctId());
                banks.put(bankRel.getPinganPayAcctId(),bankRel);
            }
        }
        List<Long> userIds = vo.getUserIds();
        if (StringUtils.isNoneBlank(vo.getRelationName())||StringUtils.isNoneBlank(vo.getRelationPhone())){
            //查询用户和车队信息
            List<com.youming.youche.capital.domain.SysTenantDef> tenants = baseMapper.selectAllByBatchId(vo.getRelationName(), vo.getRelationPhone());
            List<UserDataInfo> users = baseMapper.searchAllByBatchIdUserDataInfos(vo.getRelationName(), vo.getRelationPhone());
            if(tenants!=null&&tenants.size()>0){
                for(com.youming.youche.capital.domain.SysTenantDef t:tenants){
                    userIds.add(t.getAdminUser());
                }
            }
            if(users!=null&&users.size()>0){
                for(UserDataInfo u:users){
                    userIds.add(u.getId());
                }
            }
        }
        List<String> busiCodes =vo.getBusiCodes();

        List<OilRechargeAccountDetailsFlow> relationFlow = baseMapper.searchAllByBatchIdDetailsFlows(userIds);
        for(OilRechargeAccountDetailsFlow flow:relationFlow){
            busiCodes.add(flow.getBusiCode());
        }
        List<Long> orders = vo.getOrders();
            //查询用户和车队信息
            if(StringUtils.isNoneBlank(vo.getPlateNumber())){
                LambdaQueryWrapper<OrderScheduler> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(OrderScheduler::getPlateNumber,vo.getPlateNumber());
                List<OrderScheduler> orderScheduler = orderSchedulerMapper.selectList(wrapper);
                LambdaQueryWrapper<OrderSchedulerH> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(OrderSchedulerH::getPlateNumber,vo.getPlateNumber());
                List<OrderSchedulerH> orderSchedulerH = orderSchedulerHMapper.selectList(queryWrapper);
                if(orderScheduler!=null&&orderScheduler.size()>0){
                    for(OrderScheduler s:orderScheduler){
                        orders.add(s.getOrderId());
                    }
                }
                if(orderSchedulerH!=null&&orderSchedulerH.size()>0){
                    for(OrderSchedulerH s:orderSchedulerH){
                        orders.add(s.getOrderId());
                    }
                }
            }
        if(vo.getStartTime() != null && org.apache.commons.lang.StringUtils.isNotEmpty(vo.getStartTime())){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String applyTimeStr = vo.getStartTime()+" 00:00:00";
            LocalDateTime beginApplyTime1 = LocalDateTime.parse(applyTimeStr, dateTimeFormatter);
            vo.setStartTime1(beginApplyTime1);
        }
        if(vo.getEndTime() != null && org.apache.commons.lang.StringUtils.isNotEmpty(vo.getEndTime())){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String endApplyTimeStr = vo.getEndTime()+" 23:59:59";
            LocalDateTime endApplyTime1 = LocalDateTime.parse(endApplyTimeStr, dateTimeFormatter);
            vo.setEndTime1(endApplyTime1);
        }

        List<String> busiTypeStr = null;
        if (vo.getBusiType() == null || org.apache.commons.lang.StringUtils.isBlank(vo.getBusiType())) {
            busiTypeStr = null;
        } else {
            busiTypeStr = Arrays.asList(vo.getBusiType().split(","));
        }
        List<OilRechargeAccountDetailsFlow> list = baseMapper.searchAllByAccountTypeFlows(vo,sysTenantDef.getAdminUser(),busiTypeStr);
        Set<Long> opIdSet = new HashSet<Long>();
        Set<String> busiCodeSet = new HashSet<String>();
        Set<Long> orderSet = new HashSet<Long>();
            for (OilRechargeAccountDetailsFlow flow:list) {
            if (flow.getOpId()!=null){
                opIdSet.add(flow.getOpId());
            }
                if(StringUtils.isNotBlank(flow.getBusiCode())){
                    busiCodeSet.add(flow.getBusiCode());
                }
                if(StringUtils.isNotBlank(flow.getOrderNum())&&StringUtils.isNumeric(flow.getOrderNum())){
                    orderSet.add(Long.valueOf(flow.getOrderNum()));
                }
        }
        /**查询操作员列表 start*/
        Map operMap = new HashMap();
        List<SysOperator> operatorList = null;
           if (opIdSet!=null && opIdSet.size()>0){
               operMap.put("opIdSet",opIdSet);
               operatorList= baseMapper.selectAllByBatchIdSysOperators(opIdSet);
           }
        Map operators = new HashMap();
        if(operatorList!=null && operatorList.size()>0){
            for( SysOperator operator : operatorList){
                operators.put(operator.getOperatorId(),operator);
            }
        }
        /**查询操作员列表end*/

        /**查询关联的流水 start*/
        List<OilRechargeAccountDetailsFlow> relationFlowList = null;
        if(busiCodeSet!=null&&busiCodeSet.size()>0){
            relationFlowList = baseMapper.selectAllByOtherBatchIdFlows(busiCodeSet,sysTenantDef.getAdminUser());
        }
        Map<String,OilRechargeAccountDetailsFlow> relationFlows = new HashMap<String,OilRechargeAccountDetailsFlow>();
        Set<Long> relationUserIds = new HashSet<Long>();
        if(relationFlowList!=null && relationFlowList.size()>0){
            for( OilRechargeAccountDetailsFlow flow : relationFlowList){
                relationFlows.put(flow.getBusiCode(),flow);
                relationUserIds.add(flow.getUserId());
            }
        }
        /**查询关联的流水 end*/


        /**查询订单车牌号 start*/
        List<Map> orderInfoList = new ArrayList<>();
        if(orderSet!=null&&orderSet.size()>0){
            LambdaQueryWrapper<OrderScheduler> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(OrderScheduler::getOrderId,orderSet);
            List orderLists = orderSchedulerMapper.selectList(wrapper);
            if(orderLists!=null&&orderLists.size()>0){
                orderInfoList.addAll(orderLists);
            }
        }

        if(orderSet!=null&&orderSet.size()>0){
          LambdaQueryWrapper<OrderSchedulerH> wrapper = new LambdaQueryWrapper<>();
          wrapper.in(OrderSchedulerH::getOrderId,orderSet);
            List orderList = orderSchedulerHMapper.selectList(wrapper);
            if(orderList!=null&&orderList.size()>0){
                orderInfoList.addAll(orderList);
            }
        }

        Map<String,String> plateNumbers = new HashMap<String,String>();
        Map<String,Long> orderTenantIds = new HashMap<String,Long>();
        if(orderInfoList!=null && orderInfoList.size()>0){
            for( Map m : orderInfoList){
                plateNumbers.put(m.get("orderId")+"",m.get("palteNumber")+"");
                orderTenantIds.put(m.get("orderId")+"",Long.valueOf(m.get("tenantId")+""));
            }
        }

        /**查询关联的流水 end*/
        /*查询关联流水的用户信息，如果是车队获取车队名字和车队账户，否者获取司机和司机手机*/
        List<UserDataInfo> relationUserList = null;
        List<com.youming.youche.capital.domain.SysTenantDef> relationTenantList = null;
        if(relationUserIds!=null&&relationUserIds.size()>0){
            relationUserList =baseMapper.relationUserList(relationUserIds);
            relationTenantList = baseMapper.relationTenantList(relationUserIds);
        }
        Map<Long,Map> userInfoMap = new HashMap<Long,Map>();
        if(relationUserList!=null && relationUserList.size()>0){
            for( UserDataInfo u : relationUserList){
                Map userInfo = new HashMap();
                userInfo.put("userId", u.getId());
                userInfo.put("relationName", u.getLinkman());
                userInfo.put("relationPhone", u.getMobilePhone());
                userInfoMap.put(u.getId(),userInfo);
            }
        }
        //如果是车队存在，更新Map
        if(relationTenantList!=null && relationTenantList.size()>0){
            for( com.youming.youche.capital.domain.SysTenantDef s : relationTenantList){
                Map userInfo = new HashMap();
                userInfo.put("userId", s.getAdminUser());
                userInfo.put("relationName", s.getName());
                userInfo.put("relationPhone", s.getLinkPhone());
                userInfoMap.put(s.getAdminUser(),userInfo);
            }
        }
        List<OilRechargeAccountDetailsFlowOutDto> listOut = new ArrayList<>();
        for (OilRechargeAccountDetailsFlow entity : list) {
            OilRechargeAccountDetailsFlowOutDto out = new OilRechargeAccountDetailsFlowOutDto();
            BeanUtil.copyProperties( entity,out);

            AccountBankRel bankRel = (AccountBankRel)banks.get(out.getPinganAccId());
            if(bankRel != null){
                out.setAccName(bankRel.getAcctName()+"("+bankRel.getAcctNo()+")");
            }
            SysOperator sysOperator = (SysOperator)operators.get(out.getOpId());
            if(sysOperator == null) {
                out.setOperName("平台管理员");
            }else{
                out.setOperName(sysOperator.getOperatorName() + "(" + sysOperator.getLoginAcct() + ")");
            }
            if(StringUtils.isBlank(out.getAccName())){
                if( SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE1 == out.getSourceType()){
                    out.setAccName("加油返利");
                    out.setOperName("平台管理员");
                    if(StringUtils.isBlank(out.getRelationName())){//如果没有，获取自己
                        out.setRelationName(sysTenantDef.getName());
                    }
                    if(StringUtils.isBlank(out.getRelationPhone())){//如果没有，获取自己
                        out.setRelationPhone(sysTenantDef.getLinkPhone());
                    }
                }else if( SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE6 == out.getSourceType()
                        ||out.getBusiType()== SysStaticDataEnum.OIL_RECHARGE_BUSI_TYPE.BUSI_TYPE4){
                    out.setAccName("油费撤回");
                }else if( SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE4 == out.getSourceType()){
                    out.setAccName("客户油费");
                }
            }

            if( SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE5 == out.getSourceType()){
                out.setBillTypeName("抵扣运输专票");
            }else if(SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE2 == out.getSourceType()
                    || SysStaticDataEnum.OIL_RECHARGE_SOURCE_TYPE.SOURCE_TYPE3 == out.getSourceType()){
                out.setBillTypeName("自己收油票");
            }
            //拼装关联的流水的用户信息
            //1 获取关联的流水
            OilRechargeAccountDetailsFlow relationFlow1 = relationFlows.get(out.getBusiCode());
            //2获取用户信息
            if(relationFlow1!=null){
                long relationUserId = relationFlow1.getUserId();
                Map userInfo = userInfoMap.get(relationUserId);
                out.setRelationName((userInfo==null||userInfo.get("relationName")==null)?"":userInfo.get("relationName")+"");
                out.setRelationPhone((userInfo==null||userInfo.get("relationPhone")==null)?"":userInfo.get("relationPhone")+"");
            }
            if(orderTenantIds.get(out.getOrderNum())!=null
                    &&orderTenantIds.get(out.getOrderNum())!=loginInfo.getTenantId()){
                out.setIsOrderUnSkip(true);
            }

            String orderNum=out.getOrderNum();
            if(StringUtils.isNoneBlank(orderNum)){
                if(!StringUtils.isNumeric(orderNum)){
                    out.setOrderNum(null);
                }else{
                    out.setPlateNumber(plateNumbers.get(out.getOrderNum()));
                }
            }
            listOut.add(out);
        }
        Page<OilRechargeAccountDetailsFlowOutDto> page  = new Page<>(pageNum,pageSize);
        page.setRecords(listOut);
        page.setTotal(listOut.size());
        page.setSize(pageSize);
        page.setCurrent(pageNum);
        return page;
    }

}
