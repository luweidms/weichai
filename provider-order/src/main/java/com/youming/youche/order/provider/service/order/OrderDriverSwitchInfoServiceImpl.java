package com.youming.youche.order.provider.service.order;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.cloud.domin.sms.SysSmsSend;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.order.api.order.IOrderDriverSwitchInfoService;
import com.youming.youche.order.api.order.IOrderFeeExtService;
import com.youming.youche.order.api.order.IOrderInfoExtService;
import com.youming.youche.order.api.order.IOrderInfoService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderOilSourceService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IOrderStatementService;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.order.OrderDriverSwitchInfo;
import com.youming.youche.order.domain.order.OrderInfo;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.dto.AcOrderSubsidyInDto;
import com.youming.youche.order.dto.OrderDriverSwitchInfoOutDto;
import com.youming.youche.order.dto.UserDataInfoDto;
import com.youming.youche.order.dto.order.OrderDriverSwitchInfoDto;
import com.youming.youche.order.dto.order.QueryOrderDriverSwitchDto;
import com.youming.youche.order.dto.order.QueryOrderTitleBasicsInfoDto;
import com.youming.youche.order.provider.mapper.order.OrderDriverSwitchInfoMapper;
import com.youming.youche.order.provider.utils.CommonUtil;
import com.youming.youche.order.provider.utils.ReadisUtil;
import com.youming.youche.order.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.order.vo.ScanOrderDriverSwitchVo;
import com.youming.youche.system.api.ISysTenantDefService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.domain.SysTenantDef;
import com.youming.youche.system.domain.UserDataInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * ???????????????
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-24
 */
@DubboService(version = "1.0.0")
@Service
        public class OrderDriverSwitchInfoServiceImpl extends BaseServiceImpl<OrderDriverSwitchInfoMapper, OrderDriverSwitchInfo> implements IOrderDriverSwitchInfoService {

    private static transient Log log = LogFactory.getLog(OrderDriverSwitchInfoServiceImpl.class);

    private static final int IMA_WIDTH = 500;
    private static final int IMA_HEIGHT = 500;


    @Resource
    SysStaticDataRedisUtils sysStaticDataRedisUtils;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService userDataInfoService;

    @Resource
    IOrderSchedulerService orderSchedulerService;

    @DubboReference(version = "1.0.0")
    ISysTenantDefService sysTenantDefService;

    @DubboReference(version = "1.0.0")
    ISysSmsSendService sysSmsSendService;

    @Resource
    IOrderOilSourceService orderOilSourceService;

    @Resource
    IOrderLimitService orderLimitService;

    @Resource
    IOrderFeeExtService orderFeeExtService;

    @Resource
    IOrderInfoService orderInfoService;

    @Resource
    IOrderInfoExtService orderInfoExtService;

    @Resource
    private LoginUtils loginUtils;

    @Resource
    private ReadisUtil readisUtil;

    @Resource
    IOrderStatementService orderStatementService;

    @Resource
    IOrderDriverSwitchInfoService orderDriverSwitchInfoService;



    @Override
    public List<OrderDriverSwitchInfo> getSwitchInfosByOrder(Long orderId, Integer state) {
        LambdaQueryWrapper<OrderDriverSwitchInfo> lambda = Wrappers.lambdaQuery();
        lambda.eq(OrderDriverSwitchInfo::getOrderId, orderId);
        if (state > -1) {
            lambda.eq(OrderDriverSwitchInfo::getState, state);
        }
        lambda.groupBy(OrderDriverSwitchInfo::getReceiveUserId);
        return this.list(lambda);
    }

    @Override
    public IPage<OrderDriverSwitchInfoDto> getOrderDriverSwitchInfos(Long orderId, String formerUserName, String receiveUserName, String originUserName, int state, int pageNum, int pageSize) {
        IPage<OrderDriverSwitchInfo> orderDriverSwitchInfoIPage = new Page<>(pageNum, pageSize);
        IPage<OrderDriverSwitchInfoDto> orderDriverSwitchInfoDtoIPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OrderDriverSwitchInfo> lambda = Wrappers.lambdaQuery();
        lambda.eq(OrderDriverSwitchInfo::getOrderId, orderId).orderByDesc(OrderDriverSwitchInfo::getId);
        if (StringUtils.isNotBlank(formerUserName)) {
            lambda.like(OrderDriverSwitchInfo::getFormerUserName, "%" + formerUserName + "%");
        }
        if (StringUtils.isNotBlank(receiveUserName)) {
            lambda.like(OrderDriverSwitchInfo::getReceiveUserName, "%" + receiveUserName + "%");
        }
        if (StringUtils.isNotBlank(originUserName)) {
            lambda.like(OrderDriverSwitchInfo::getOriginUserName, "%" + originUserName + "%");
        }
        if (state > -1) {
            lambda.eq(OrderDriverSwitchInfo::getState, state);
        }
        super.page(orderDriverSwitchInfoIPage, lambda);
        if (orderDriverSwitchInfoIPage != null) {
            List<OrderDriverSwitchInfo> list = orderDriverSwitchInfoIPage.getRecords();
            List<OrderDriverSwitchInfoDto> retList = new ArrayList<>();
            for (OrderDriverSwitchInfo o : list) {
                OrderDriverSwitchInfoDto out = new OrderDriverSwitchInfoDto();
                BeanUtil.copyProperties(o, out);
                if (o.getState() != null) {
                    SysStaticData sysStaticData = sysStaticDataRedisUtils.getSysStaticDataByCodeValue("DRIVER_SWIICH_STATE", o.getState() + "");
                    if (sysStaticData.getCodeName() != null) {
                        out.setStateName(sysStaticData.getCodeName());
                    }
                }
                retList.add(out);
            }
            orderDriverSwitchInfoDtoIPage.setRecords(retList);
            orderDriverSwitchInfoDtoIPage.setTotal(orderDriverSwitchInfoIPage.getTotal());
        }

        return orderDriverSwitchInfoDtoIPage;
    }

    @Override
    public OrderDriverSwitchInfoDto queryDetails(Long switchId) {
        OrderDriverSwitchInfo orderDriverSwitchInfo = super.getById(switchId);
        OrderDriverSwitchInfoDto orderDriverSwitchInfoDto = new OrderDriverSwitchInfoDto();
        if (orderDriverSwitchInfo != null) {
            BeanUtil.copyProperties(orderDriverSwitchInfo, orderDriverSwitchInfoDto);
            try {
               // FastDFSHelper client = FastDFSHelper.getInstance();
                orderDriverSwitchInfoDto.setReceiveMileageUrl(orderDriverSwitchInfoDto.getReceiveMileageUrl());
                orderDriverSwitchInfoDto.setFormerMileageFileUrl(orderDriverSwitchInfoDto.getFormerMileageFileUrl());
                if (orderDriverSwitchInfoDto.getFormerMileage() != null && orderDriverSwitchInfoDto.getFormerMileage() > 0) {
                    orderDriverSwitchInfoDto.setFormerMileageStr(CommonUtil.divide(orderDriverSwitchInfoDto.getFormerMileage(), 1000L));//???????????????
                    orderDriverSwitchInfoDto.setFormerMileage(orderDriverSwitchInfoDto.getFormerMileage());
                }

                if (orderDriverSwitchInfoDto.getReceiveMileage() != null && orderDriverSwitchInfoDto.getReceiveMileage() > 0) {
                    orderDriverSwitchInfoDto.setReceiveMileageStr(CommonUtil.divide(orderDriverSwitchInfoDto.getReceiveMileage(), 1000L));//???????????????
                    orderDriverSwitchInfoDto.setFormerMileageStr(CommonUtil.divide(orderDriverSwitchInfoDto.getFormerMileage(), 1000L));
                }
                if (orderDriverSwitchInfoDto.getState()==1){
                    //PC????????????????????????????????????????????????????????????????????????
                    if (orderDriverSwitchInfo.getIsWxChange()==0){
                        orderDriverSwitchInfoDto.setFormerMileage(orderDriverSwitchInfoDto.getReceiveMileage());
                        orderDriverSwitchInfoDto.setFormerMileageStr(orderDriverSwitchInfoDto.getReceiveMileageStr());
                        orderDriverSwitchInfoDto.setFormerMileageFileId(orderDriverSwitchInfoDto.getReceiveMileageId());
                        orderDriverSwitchInfoDto.setFormerMileageFileUrl(orderDriverSwitchInfoDto.getReceiveMileageUrl());
                    }
                }
                if(orderDriverSwitchInfoDto.getState() != null){
                    String codeName = readisUtil.getSysStaticData("DRIVER_SWIICH_STATE", orderDriverSwitchInfoDto.getState().toString()).getCodeName();
                    orderDriverSwitchInfoDto.setStateName(codeName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new BusinessException("??????????????????????????????");
        }
        return orderDriverSwitchInfoDto;
    }

    @Override
    public UserDataInfoDto getDriverByPhone(String billId, String accessToken) {
        UserDataInfo userDataInfo = userDataInfoService.getPhone(billId);
        UserDataInfoDto userDataInfoDto = new UserDataInfoDto();
        LoginInfo baseUser = loginUtils.get(accessToken);
        if (userDataInfo == null) {
            throw new BusinessException("????????????????????????");
        }
        if (userDataInfo.getTenantId() == null || userDataInfo.getTenantId() <= 0 || !String.valueOf(userDataInfo.getTenantId()).equals(baseUser.getTenantId() + "")) {
            throw new BusinessException("??????????????????????????????");
        } else {
            userDataInfoDto.setLinkman(userDataInfo.getLinkman());
            userDataInfoDto.setId(userDataInfo.getId());
        }
        return userDataInfoDto;
    }

    @Override
    public void doDriverSwitch(long orderId, long userId, String recevieMileage, String mileageFileId, String mileageFileUrl, String receiveRemark, String accessToken) {
        OrderDriverSwitchInfo latestEffectiveInfo = getLatestEffective(orderId);
        LoginInfo loginInfo = loginUtils.get(accessToken);
        long recevieMileageLong = Double.valueOf(CommonUtil.multiply(recevieMileage,"1000")).longValue();
        UserDataInfo userDataInfo = userDataInfoService.getById(userId);
        if (userDataInfo == null) {
            throw new BusinessException("????????????????????????????????????");
        }
        if (orderId > 0) {
            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
            if (orderScheduler == null) {
                throw new BusinessException("??????[" + orderId + "]????????????????????????????????????");
            }
            Long receiveCopilot = orderScheduler.getCopilotUserId();
            OrderDriverSwitchInfo newData = new OrderDriverSwitchInfo();
            if (latestEffectiveInfo != null) {//???????????????????????????
                if (latestEffectiveInfo.getState() == OrderConsts.DRIVER_SWIICH_STATE.STATE0) {//????????????????????????
                    throw new BusinessException("??????[" + orderId + "]????????????????????????????????????????????????");
                }
                newData.setFormerMileage(latestEffectiveInfo.getReceiveMileage());
                newData.setFormerMileageFileId(latestEffectiveInfo.getReceiveMileageId());
                newData.setFormerMileageFileUrl(latestEffectiveInfo.getReceiveMileageUrl());
                receiveCopilot = newData.getReceiveCopilot();
                if (latestEffectiveInfo.getReceiveCopilot() != null
                        && latestEffectiveInfo.getReceiveCopilot() == userDataInfo.getId().longValue()) {
                    receiveCopilot = latestEffectiveInfo.getReceiveUserId();
                }
            } else {
                newData.setFormerMileage(recevieMileageLong);
                newData.setFormerMileageFileId(mileageFileId);
                newData.setFormerMileageFileUrl(mileageFileUrl);
            }
            newData.setFormerUserId(orderScheduler.getOnDutyDriverId());
            newData.setFormerUserName(orderScheduler.getOnDutyDriverName());
            newData.setFormerUserPhone(orderScheduler.getOnDutyDriverPhone());  //????????????????????????
            newData.setReceiveUserId(userDataInfo.getId());
            newData.setReceiveUserName(userDataInfo.getLinkman());
            newData.setReceiveUserPhone(userDataInfo.getMobilePhone());
            newData.setReceiveMileage(recevieMileageLong);
            newData.setReceiveMileageId(mileageFileId);
            newData.setReceiveMileageUrl(mileageFileUrl);
            newData.setOrderId(orderId);
            newData.setCreateTime(LocalDateTime.now());
            newData.setAffirmDate(LocalDateTime.now());//web??????????????????????????????
            newData.setReceiveRemark(receiveRemark);
            newData.setFormerRemark(receiveRemark);//web????????????????????????????????????
            newData.setOriginUserId(loginInfo.getUserInfoId());
            newData.setOriginUserName(loginInfo.getName());
            newData.setOriginUserPhone(loginInfo.getTelPhone());
            newData.setState(OrderConsts.DRIVER_SWIICH_STATE.STATE1);//web??????????????????????????????
            newData.setTenantId(orderScheduler.getTenantId());//????????????
            if (String.valueOf(newData.getFormerUserId()).equals(newData.getReceiveUserId() + "")) {
                throw new BusinessException("??????????????????????????????????????????");
            }
            newData.setReceiveCopilot(receiveCopilot);
            super.saveOrUpdate(newData);
            OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderScheduler.getOrderId());
            OrderInfo orderInfo = orderInfoService.getOrder(orderScheduler.getOrderId());
            this.updateDriverInfo(loginInfo, newData.getReceiveUserId(), orderScheduler, "0", orderInfoExt.getOilAffiliation(), orderInfo.getIsNeedBill());
            //???????????????????????????????????????????????????????????????????????????
            if (latestEffectiveInfo != null) {
                latestEffectiveInfo.setEndDate(LocalDateTime.now());
                super.saveOrUpdate(latestEffectiveInfo);
            }
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());

            Map<String, String> paraMap = new HashMap<String, String>();
            paraMap.put("tenantName", sysTenantDef.getShortName());
            paraMap.put("orderId", orderId+"");
            SysSmsSend sysSmsSend = new SysSmsSend();
            sysSmsSend.setBillId(userDataInfo.getMobilePhone());
            sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT);
            sysSmsSend.setTemplateId(EnumConsts.SmsTemplate.TUBE_SWITCH_TEMP);
            sysSmsSend.setObjId(String.valueOf(newData.getId()));
            sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.ORDER_DRIVER_SWITCH));
            sysSmsSend.setParamMap(paraMap);
            sysSmsSendService.sendSms(sysSmsSend);
        } else {
            throw new BusinessException("??????[" + orderId + "]?????????????????????");
        }
    }

    @Override
    public OrderDriverSwitchInfo getLatestEffective(long orderId) {
        OrderDriverSwitchInfo retData = null;
        LambdaQueryWrapper<OrderDriverSwitchInfo> lambda = Wrappers.lambdaQuery();
        lambda.eq(OrderDriverSwitchInfo::getOrderId, orderId);
        lambda.in(OrderDriverSwitchInfo::getState, new Integer[]{OrderConsts.DRIVER_SWIICH_STATE.STATE0, OrderConsts.DRIVER_SWIICH_STATE.STATE1});
        lambda.orderByDesc(OrderDriverSwitchInfo::getId);
        List<OrderDriverSwitchInfo> orderDriverSwitchInfoList = super.list(lambda);
        if (orderDriverSwitchInfoList != null && orderDriverSwitchInfoList.size() > 0)
            return orderDriverSwitchInfoList.get(0);
        return retData;
    }

    @Override
    public List<OrderDriverSwitchInfo> getSwitchInfosByOrderId(long orderId, int state) {
        LambdaQueryWrapper<OrderDriverSwitchInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderDriverSwitchInfo::getOrderId, orderId);
        if (state > -1) {
            queryWrapper.eq(OrderDriverSwitchInfo::getState, state);
        }

        return baseMapper.selectList(queryWrapper);
    }


    public void updateDriverInfo(LoginInfo loginInfo, long userId, OrderScheduler orderScheduler, String vehicleAffiliation, String oilAffiliation, int isNeedBill) {
        UserDataInfo userDataInfo = new UserDataInfo();
        if (userDataInfoService.getById(userId) != null && userDataInfoService.getById(userId).getTenantId().intValue() == loginInfo.getTenantId().intValue()) {
            userDataInfo = userDataInfoService.getById(userId);
        }
        //???????????????????????????
        orderScheduler.setOnDutyDriverId(userDataInfo.getId());
        orderScheduler.setOnDutyDriverName(userDataInfo.getLinkman());
        orderScheduler.setOnDutyDriverPhone(userDataInfo.getMobilePhone());
        orderSchedulerService.saveOrUpdate(orderScheduler);

        //??????????????????
        //  IOrderFeeTF orderFeeTF=(IOrderFeeTF)SysContexts.getBean("orderFeeTF");
        orderFeeExtService.setDriverSubsidy(orderScheduler.getOrderId(), userDataInfo.getId(), false, null, loginInfo);
        //??????????????????-??????????????????????????????orderlimit?????????????????????????????????
        //  IPaymentTF paymentTF = (IPaymentTF)SysContexts.getBean("paymentTF");
        AcOrderSubsidyInDto acOrderSubsidyIn = new AcOrderSubsidyInDto();
        acOrderSubsidyIn.setOrderId(orderScheduler.getOrderId());
        acOrderSubsidyIn.setDriverUserId(userDataInfo.getId());
        acOrderSubsidyIn.setDriverUserName(userDataInfo.getLinkman());
        OrderLimit ol = orderLimitService.createOrderLimit(acOrderSubsidyIn);

        //??????????????????-????????????????????????????????????????????????????????????????????????
        // IOperationOilTF operationOilTF = (IOperationOilTF) SysContexts.getBean("operationOilTF");
        orderOilSourceService.saveOrderOilSource(userDataInfo.getId(), orderScheduler.getOrderId(), orderScheduler.getOrderId(), 0L,
                0L, 0L, orderScheduler.getTenantId(), LocalDateTime.now(), loginInfo.getId(), isNeedBill, vehicleAffiliation, orderScheduler.getDependTime(), oilAffiliation, ol.getOilConsumer(),
                0L, 0L, 0L, 0L, 0L, 0L, SysStaticDataEnum.USER_TYPE.DRIVER_USER, ol.getOilAccountType(), ol.getOilBillType(), loginInfo);
    }

    @Override
    public QueryOrderDriverSwitchDto queryOrderDriverSwitch(Long orderId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userInfoId = loginInfo.getUserInfoId();

        QueryOrderDriverSwitchDto queryOrderDriverSwitchDto = new QueryOrderDriverSwitchDto();

        // ??????????????????????????????
        List<OrderDriverSwitchInfo> list = this.getSwitchInfosByOrderId(orderId, -1);
        List<OrderDriverSwitchInfoDto> listOut= new ArrayList<OrderDriverSwitchInfoDto>();
        for(OrderDriverSwitchInfo os:list){
            OrderDriverSwitchInfoDto osOut= new OrderDriverSwitchInfoDto();
            BeanUtil.copyProperties(os, osOut);
            if (os.getState() != null) {
                SysStaticData sysStaticData = sysStaticDataRedisUtils.getSysStaticDataByCodeValue("DRIVER_SWIICH_STATE", os.getState() + "");
                if (sysStaticData.getCodeName() != null) {
                    osOut.setStateName(sysStaticData.getCodeName());
                }
            }
            try {
                FastDFSHelper client = FastDFSHelper.getInstance();
                osOut.setReceiveMileageUrl(client.getHttpURL(osOut.getReceiveMileageUrl()).split("\\?")[0]);
                osOut.setFormerMileageFileUrl(client.getHttpURL(osOut.getFormerMileageFileUrl()).split("\\?")[0]);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BusinessException("????????????");
            }
            listOut.add(osOut);
        }

        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        QueryOrderTitleBasicsInfoDto dto = orderStatementService.queryOrderTitleBasicsInfo(orderId);
        BeanUtil.copyProperties(dto, queryOrderDriverSwitchDto);
        if(orderInfo != null
                && orderInfo.getOrderState() >= OrderConsts.ORDER_STATE.TO_BE_LOAD
                && orderInfo.getOrderState() < OrderConsts.ORDER_STATE.FINISH){

            // ??????????????????????????????????????????
            List<OrderDriverSwitchInfo> switchList = this.getSwitchInfosByOrderId(orderId, OrderConsts.DRIVER_SWIICH_STATE.STATE0);
            if(switchList != null && switchList.size() > 0) {
                queryOrderDriverSwitchDto.setSwitchType(2); // ?????????????????????
            } else {
                OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);

                // ?????????????????????????????????????????????
                if(orderScheduler != null && orderScheduler.getOnDutyDriverId() != null && userInfoId == orderScheduler.getOnDutyDriverId().longValue()){
                    queryOrderDriverSwitchDto.setSwitchType(1);
                }else{
                    queryOrderDriverSwitchDto.setSwitchType(0);
                }
                queryOrderDriverSwitchDto.setOnDutyDriverId(orderScheduler.getOnDutyDriverId() == null ? userInfoId :  orderScheduler.getOnDutyDriverId());
            }
        }

        queryOrderDriverSwitchDto.setListOut(listOut);
        return queryOrderDriverSwitchDto;
    }

    @Override
    @Transactional
    public void formerSwithcInfo(Long orderId, Long formerUserId, Long formerMileage, String formerMileageFileId, String formerMileageFileUrl, String formerRemark, Long receiveUserId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userInfoId = loginInfo.getUserInfoId();
        String name = loginInfo.getName();
        String telphone = loginInfo.getTelPhone();

        OrderDriverSwitchInfo latestEffectiveInfo = this.getLatestEffective(orderId);//??????????????????????????????????????????

        if(orderId != null && orderId > 0){
            OrderScheduler orderScheduler = orderSchedulerService.getOrderScheduler(orderId);
            OrderInfo orderInfo = orderInfoService.getOrder(orderId);
            if(orderScheduler == null){
                throw new BusinessException("??????["+orderId+"]????????????????????????????????????");
            }
            UserDataInfo userDataInfo = userDataInfoService.getOwnDriver(receiveUserId, orderInfo.getTenantId());
            if(userDataInfo == null){
                throw new BusinessException("??????????????????????????????????????????");
            }
            OrderDriverSwitchInfo newData = new OrderDriverSwitchInfo();
            if(latestEffectiveInfo != null){//???????????????????????????
                if(latestEffectiveInfo.getState() == OrderConsts.DRIVER_SWIICH_STATE.STATE0){//????????????????????????
                    throw new BusinessException("??????["+orderId+"]????????????????????????????????????????????????");
                }
            }
            newData.setFormerMileage(formerMileage);
            newData.setFormerMileageFileId(formerMileageFileId);
            newData.setFormerMileageFileUrl(formerMileageFileUrl);
            newData.setFormerUserId(orderScheduler.getOnDutyDriverId());
            newData.setFormerUserName(orderScheduler.getOnDutyDriverName());
            newData.setFormerUserPhone(orderScheduler.getOnDutyDriverPhone());  //????????????????????????
            newData.setOrderId(orderId);
            newData.setCreateTime(LocalDateTime.now());
            newData.setFormerRemark(formerRemark);
            newData.setOriginUserId(userInfoId);
            newData.setOriginUserName(name);
            newData.setOriginUserPhone(telphone);
            newData.setReceiveUserId(receiveUserId);//??????????????????
            newData.setReceiveUserName(userDataInfo.getLinkman());
            newData.setReceiveUserPhone(userDataInfo.getMobilePhone());
            newData.setTenantId(orderScheduler.getTenantId());//????????????
            newData.setState(OrderConsts.DRIVER_SWIICH_STATE.STATE0);//?????????
            if(String.valueOf(newData.getFormerUserId()).equals(newData.getReceiveUserId()+"")){
                throw new BusinessException("??????????????????????????????????????????");
            }
            this.saveOrUpdate(newData);
            //??????????????????

            if (StringUtils.isEmpty(telphone)) {
                UserDataInfo userDataInfoMobilePhone = userDataInfoService.getUserDataInfo(loginInfo.getUserInfoId());
                telphone = userDataInfoMobilePhone.getMobilePhone();
            }

            Map paraMap = new HashMap();
            paraMap.put("carDriverName", name);
            paraMap.put("carDriverPhone", telphone);
            paraMap.put("orderId", String.valueOf(orderId));

            SysSmsSend sysSmsSend = new SysSmsSend();
            sysSmsSend.setBillId(userDataInfo.getMobilePhone());
            sysSmsSend.setSmsType(SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT);
            sysSmsSend.setTemplateId(EnumConsts.SmsTemplate.DRIVER_SWITCH_TEMP);
            sysSmsSend.setObjType(String.valueOf(SysStaticDataEnum.OBJ_TYPE.ORDER_DRIVER_SWITCH));
            sysSmsSend.setObjId(String.valueOf(newData.getId()));
            sysSmsSend.setParamMap(paraMap);
            sysSmsSendService.sendSms(sysSmsSend);
        } else {
            throw new BusinessException("??????["+orderId+"]?????????????????????");
        }
    }

    @Override
    public OrderDriverSwitchInfoOutDto queryDataById(Long switchId) {
        OrderDriverSwitchInfo info = this.getById(switchId);
        OrderDriverSwitchInfoDto out = new OrderDriverSwitchInfoDto();
        OrderDriverSwitchInfoOutDto dto=new OrderDriverSwitchInfoOutDto();
        if(info!=null){
            BeanUtils.copyProperties(info,out);
            FastDFSHelper client = null;
            try {
                client = FastDFSHelper.getInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                out.setReceiveMileageUrl(client.getHttpURL(out.getReceiveMileageUrl()).split("\\?")[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                out.setFormerMileageFileUrl(client.getHttpURL(out.getFormerMileageFileUrl()).split("\\?")[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        QueryOrderTitleBasicsInfoDto retMap = orderStatementService.queryOrderTitleBasicsInfo(out.getOrderId());
        dto.setDto(out);
        dto.setInfoDto(retMap);
        if(retMap!=null
                &&Long.valueOf(retMap.getOrderState()+"")>=OrderConsts.ORDER_STATE.TO_BE_LOAD
                &&Long.valueOf(retMap.getOrderState()+"")<OrderConsts.ORDER_STATE.FINISH
                &&out.getState()==OrderConsts.DRIVER_SWIICH_STATE.STATE0){
            dto.setSwitchType(1);
        }
        return dto;
    }

    @Override
    public boolean confirmOrderDriverSwitch(long switchId, long receiveMileage, String receiveMileageId, String receiveMileageUrl, String receiveRemark, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        OrderDriverSwitchInfo info = orderDriverSwitchInfoService.getById(switchId);
        long orderId = info.getOrderId();
        OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderId);
        OrderInfo orderInfo = orderInfoService.getOrder(orderId);
        if(!(orderInfo!=null
                &&orderInfo.getOrderState()>=OrderConsts.ORDER_STATE.TO_BE_LOAD
                &&orderInfo.getOrderState()<OrderConsts.ORDER_STATE.FINISH)){
            String orderStateName=(readisUtil.getSysStaticData("ORDER_STATE", orderInfo.getOrderState() + "")).getCodeName();
            throw new BusinessException("?????????"+orderId+"????????????"+orderStateName+"????????????????????????????????????????????????");
        }
        if(info.getState()!=OrderConsts.DRIVER_SWIICH_STATE.STATE0){//?????????????????????????????????????????????
            throw new BusinessException("???????????????????????????????????????????????????????????????");
        }
        OrderScheduler orderScheduler =orderSchedulerService.getOrderScheduler(orderId);
        if(orderScheduler==null){
            throw new BusinessException("??????["+orderId+"]????????????????????????????????????");
        }
        Long receiveCopilot=orderScheduler.getCopilotUserId();
        if(!String.valueOf(info.getReceiveUserId()).equals(loginInfo.getUserInfoId()+"")){
            throw new BusinessException("??????["+orderId+"]????????????????????????????????????????????????");
        }
        OrderDriverSwitchInfo latestEffectiveInfo = orderDriverSwitchInfoService.getLatestEffective(orderId);//??????????????????????????????????????????
        if(latestEffectiveInfo!=null){
            if(latestEffectiveInfo.getReceiveCopilot()!=null
                    &&latestEffectiveInfo.getReceiveCopilot().longValue()==loginInfo.getUserInfoId().longValue()){
                receiveCopilot=latestEffectiveInfo.getReceiveUserId();
            }
        }
        //?????????????????????????????????????????????????????????
        info.setReceiveUserId(loginInfo.getUserInfoId());
        info.setReceiveUserName(loginInfo.getName());
        info.setReceiveUserPhone(loginInfo.getTelPhone());
        info.setAffirmDate(LocalDateTime.now());
        info.setState(OrderConsts.DRIVER_SWIICH_STATE.STATE1);
        info.setReceiveMileage(receiveMileage);
        info.setReceiveMileageId(receiveMileageId);
        info.setReceiveMileageUrl(receiveMileageUrl);
        info.setReceiveRemark(receiveRemark);
        info.setReceiveCopilot(receiveCopilot);
        info.setIsWxChange(1);
        orderDriverSwitchInfoService.saveOrUpdate(info);
        this.updateDriverInfo(loginInfo,info.getReceiveUserId(),orderScheduler, "0", orderInfoExt.getOilAffiliation(), orderInfo.getIsNeedBill());

        //???????????????????????????????????????????????????????????????????????????
        if(latestEffectiveInfo!=null){
            if (latestEffectiveInfo.getState()==1) {
                latestEffectiveInfo.setEndDate(LocalDateTime.now());
                orderDriverSwitchInfoService.saveOrUpdate(latestEffectiveInfo);
            }
        }
        return true;
    }

    @Override
    public boolean refuseOrderDriverSwitch(Long switchId, String state) {
        OrderDriverSwitchInfo info =orderDriverSwitchInfoService.getById(switchId);
        if(info.getState()!=OrderConsts.DRIVER_SWIICH_STATE.STATE0){//?????????????????????????????????????????????
            throw new BusinessException("?????????????????????????????????????????????????????????");
        }
        if(StringUtils.isBlank(state)){//???????????????????????????????????????
            throw new BusinessException("???????????????/???????????????");
        }
        //????????????????????????????????????????????????
        info.setFefuseDate(LocalDateTime.now());
        info.setState(Integer.valueOf(state));//??????????????????????????????????????????
        orderDriverSwitchInfoService.saveOrUpdate(info);
        return true;
    }

    @Override
    public boolean scanOrderDriverSwitch(ScanOrderDriverSwitchVo scanOrderDriverSwitchVo,String accessToken) {
        OrderDriverSwitchInfo info = new OrderDriverSwitchInfo();
        info.setOrderId(scanOrderDriverSwitchVo.getOrderId());
        info.setFormerUserId(scanOrderDriverSwitchVo.getFormerUserId());
        info.setFormerMileage(Long.valueOf(scanOrderDriverSwitchVo.getFormerMileage()));
        info.setFormerMileageFileId(scanOrderDriverSwitchVo.getFormerMileageFileId());
        info.setFormerMileageFileUrl(scanOrderDriverSwitchVo.getFormerMileageFileUrl());
        info.setFormerRemark(scanOrderDriverSwitchVo.getFormerRemark());
        info.setReceiveUserId(scanOrderDriverSwitchVo.getReceiveUserId());
        info.setReceiveMileage(Long.valueOf(scanOrderDriverSwitchVo.getReceiveMileage()));
        info.setReceiveMileageId(scanOrderDriverSwitchVo.getReceiveMileageId());
        info.setReceiveMileageUrl(scanOrderDriverSwitchVo.getReceiveMileageUrl());
        info.setReceiveRemark(scanOrderDriverSwitchVo.getReceiveRemark());
        orderDriverSwitchInfoService.doDriverSwitch(info,accessToken);
        return true;
    }

    @Override
    public void doDriverSwitch(OrderDriverSwitchInfo info,String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        OrderDriverSwitchInfo latestEffectiveInfo = orderDriverSwitchInfoService.getLatestEffective(info.getOrderId());//??????????????????????????????????????????
        if (loginInfo.getTenantId() == null || loginInfo.getTenantId() <= 0) {
            throw new BusinessException("??????????????????????????????????????????");
        }
        UserDataInfo userDataInfo = userDataInfoService.getOwnDriver(info.getReceiveUserId(), loginInfo.getTenantId());
        if(userDataInfo==null){
            throw new BusinessException("??????????????????????????????????????????");
        }
        if(info.getOrderId()>0){
            OrderScheduler orderScheduler =orderSchedulerService.getOrderScheduler(info.getOrderId());
            if(orderScheduler==null){
                throw new BusinessException("??????["+info.getOrderId()+"]????????????????????????????????????");
            }
            Long receiveCopilot = orderScheduler.getCopilotUserId();//???????????????
            OrderDriverSwitchInfo newData= new OrderDriverSwitchInfo();
            if(latestEffectiveInfo!=null){//???????????????????????????
                if(latestEffectiveInfo.getState()==OrderConsts.DRIVER_SWIICH_STATE.STATE0){//????????????????????????
                    throw new BusinessException("??????["+info.getOrderId()+"]????????????????????????????????????????????????");
                }
                //??????????????????????????????????????????
                if(receiveCopilot!=null&&receiveCopilot.longValue()==latestEffectiveInfo.getReceiveCopilot().longValue()){
                    receiveCopilot=latestEffectiveInfo.getReceiveUserId();
                }
            }

            newData.setFormerUserId(orderScheduler.getOnDutyDriverId());
            newData.setFormerUserName(orderScheduler.getOnDutyDriverName());
            newData.setFormerUserPhone(orderScheduler.getOnDutyDriverPhone());  //????????????????????????
            newData.setFormerMileage(info.getFormerMileage());
            newData.setFormerMileageFileId(info.getFormerMileageFileId());
            newData.setFormerMileageFileUrl(info.getFormerMileageFileUrl());
            newData.setReceiveUserId(info.getReceiveUserId());
            newData.setReceiveUserName(userDataInfo.getLinkman());
            newData.setReceiveUserPhone(userDataInfo.getMobilePhone());
            newData.setReceiveMileage(info.getReceiveMileage());
            newData.setReceiveMileageId(info.getReceiveMileageId());
            newData.setReceiveMileageUrl(info.getReceiveMileageUrl());
            newData.setOrderId(info.getOrderId());
            newData.setCreateTime(LocalDateTime.now());
            newData.setAffirmDate(LocalDateTime.now());//web??????????????????????????????
            newData.setReceiveRemark(info.getReceiveRemark());
            newData.setFormerRemark(info.getFormerRemark());//web????????????????????????????????????
            newData.setOriginUserId(orderScheduler.getOnDutyDriverId());
            newData.setOriginUserName(orderScheduler.getOnDutyDriverName());
            newData.setOriginUserPhone(orderScheduler.getOnDutyDriverPhone());
            newData.setState(OrderConsts.DRIVER_SWIICH_STATE.STATE1);//web??????????????????????????????
            newData.setTenantId(orderScheduler.getTenantId());//????????????
            newData.setReceiveCopilot(receiveCopilot);
            if(String.valueOf(newData.getFormerUserId()).equals(newData.getReceiveUserId()+"")){
                throw new BusinessException("??????????????????????????????????????????");
            }

            orderDriverSwitchInfoService.saveOrUpdate(newData);
            OrderInfoExt orderInfoExt = orderInfoExtService.getOrderInfoExt(orderScheduler.getOrderId());
            OrderInfo orderInfo = orderInfoService.getOrder(orderScheduler.getOrderId());
            this.updateDriverInfo(loginInfo,newData.getReceiveUserId(),orderScheduler, "0", orderInfoExt.getOilAffiliation(), orderInfo.getIsNeedBill());
            SysTenantDef sysTenantDef = sysTenantDefService.getSysTenantDef(loginInfo.getTenantId());
            //???????????????????????????????????????????????????????????????????????????
            if(latestEffectiveInfo!=null){
                latestEffectiveInfo.setEndDate(LocalDateTime.now());
                orderDriverSwitchInfoService.saveOrUpdate(latestEffectiveInfo);
            }
            //??????????????????
            Map paraMap = new HashMap();
            paraMap.put("tenantName", sysTenantDef.getShortName());
            paraMap.put("orderId", newData.getOrderId());
            //??????????????????
            sysSmsSendService.sendSms(userDataInfo.getMobilePhone(), EnumConsts.SmsTemplate.TUBE_SWITCH_TEMP, SysStaticDataEnum.SMS_TYPE.ORDER_ASSISTANT,
                    SysStaticDataEnum.OBJ_TYPE.ORDER_DRIVER_SWITCH, String.valueOf(newData.getId()), paraMap,accessToken);
        }else{
            throw new BusinessException("??????["+info.getOrderId()+"]?????????????????????");
        }
    }
}
