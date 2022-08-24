package com.youming.youche.finance.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.cloud.api.sms.ISysSmsSendService;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.IOilEntityService;
import com.youming.youche.finance.api.IRechargeInfoRecordService;
import com.youming.youche.finance.api.voucher.IVoucherInfoService;
import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.finance.commons.util.HttpsMain;
import com.youming.youche.finance.constant.OrderConsts;
import com.youming.youche.finance.domain.OilEntity;
import com.youming.youche.finance.domain.RechargeInfoRecord;
import com.youming.youche.finance.domain.voucher.VoucherInfo;
import com.youming.youche.finance.provider.mapper.OilEntityMapper;
import com.youming.youche.finance.provider.mapper.RechargeInfoRecordMapper;
import com.youming.youche.finance.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.finance.vo.OilEntityInfoDto;
import com.youming.youche.finance.vo.OilEntityVo;
import com.youming.youche.market.domain.youka.VoucherInfoRecord;
import com.youming.youche.order.api.IOilCardManagementService;
import com.youming.youche.order.api.IOrderFeeService;
import com.youming.youche.order.api.order.IAccountDetailsService;
import com.youming.youche.order.api.order.IBusiSubjectsRelService;
import com.youming.youche.order.api.order.IOilCardLogService;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.api.order.IOrderFeeHService;
import com.youming.youche.order.api.order.IOrderFeeStatementService;
import com.youming.youche.order.api.order.IOrderInfoExtHService;
import com.youming.youche.order.api.order.IOrderInfoExtService;
import com.youming.youche.order.api.order.IOrderLimitService;
import com.youming.youche.order.api.order.IOrderSchedulerService;
import com.youming.youche.order.api.order.IPayoutIntfService;
import com.youming.youche.order.api.order.other.IBaseBusiToOrderService;
import com.youming.youche.order.api.order.other.IOpAccountService;
import com.youming.youche.order.domain.OilCardManagement;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.OrderFee;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.order.domain.order.OilCardLog;
import com.youming.youche.order.domain.order.OrderFeeH;
import com.youming.youche.order.domain.order.OrderInfoExt;
import com.youming.youche.order.domain.order.OrderInfoExtH;
import com.youming.youche.order.domain.order.OrderLimit;
import com.youming.youche.order.domain.order.OrderScheduler;
import com.youming.youche.order.domain.order.OrderSchedulerH;
import com.youming.youche.order.domain.order.PayoutIntf;
import com.youming.youche.order.domain.service.ServiceInfo;
import com.youming.youche.order.dto.OrderResponseDto;
import com.youming.youche.order.vo.OrderAccountVO;
import com.youming.youche.record.api.tenant.ITenantStaffRelService;
import com.youming.youche.record.dto.StaffDataInfoDto;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.audit.IAuditOutService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExportExcel;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.youming.youche.finance.constant.OrderConsts.IS_TURN_AUTOMATIC.IS_TURN_AUTOMATIC_0;


/**
* <p>
    * 油充值核销表 服务实现类
    * </p>
* @author WuHao
* @since 2022-04-14
*/
@DubboService(version = "1.0.0")
    public class OilEntityServiceImpl extends BaseServiceImpl<OilEntityMapper, OilEntity> implements IOilEntityService {

    @Resource
    OilEntityMapper oilEntityMapper;

    @Resource
    RedisUtil redisUtil;

    @DubboReference(version = "1.0.0")
    IOrderLimitService iOrderLimitService;
    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;
    @DubboReference(version = "1.0.0")
    IOrderInfoExtService iOrderInfoExtService;
    @DubboReference(version = "1.0.0")
    IOrderInfoExtHService iOrderInfoExtHService;
    @DubboReference(version = "1.0.0")
    IOrderFeeStatementService orderFeeStatementService;
    @DubboReference(version = "1.0.0")
    IOilCardManagementService iOilCardManagementService;
    @DubboReference(version = "1.0.0")
    IOrderFeeHService iOrderFeeHService;
    @DubboReference(version = "1.0.0")
    IOrderSchedulerService iOrderSchedulerService;
    @DubboReference(version = "1.0.0")
    IOrderFeeService iOrderFeeService;
    @DubboReference(version = "1.0.0")
    IBusiSubjectsRelService iBusiSubjectsRelService;
    @Resource
    IRechargeInfoRecordService iRechargeInfoRecordService;
    @DubboReference(version = "1.0.0")
    IOilCardLogService iOilCardLogService;
    @DubboReference(version = "1.0.0")
    IBaseBusiToOrderService iBaseBusiToOrderService;
    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;
    @DubboReference(version = "1.0.0")
    IOrderAccountService iOrderAccountService;
    @DubboReference(version = "1.0.0")
    ISysOperLogService iSysOperLogService;
    @DubboReference(version = "1.0.0")
    ISysSmsSendService iSysSmsSendService;
    @DubboReference(version = "1.0.0")
    ITenantStaffRelService iTenantStaffRelService;
    @Resource
    IVoucherInfoService iVoucherInfoService;
    @DubboReference(version = "1.0.0")
    IPayoutIntfService iPayoutIntfService;
    @DubboReference(version = "1.0.0")
    IAccountDetailsService iAccountDetailsService;
    @DubboReference(version = "1.0.0")
    ISysUserService sysUserService;
    @DubboReference(version = "1.0.0")
    IAuditOutService iAuditOutService;

    @Resource
    RechargeInfoRecordMapper rechargeInfoRecordMapper;
    @Resource
    LoginUtils loginUtils;

    @Override
    public IPage<OilEntityInfoDto> getOilEntitys(OilEntityVo oilEntityVo, Integer pageNum, Integer pageSize, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if (user == null || user.getTenantId() == null) {
            throw new BusinessException("用户未登录！");
        }
        //IPage<OilEntityInfoDto> page = oilEntityMapper.selectOr(oilEntityVo,user.getTenantId(),new Page<>(pageNum,pageSize));
        QueryWrapper<OilEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_id", user.getTenantId());

//        if (oilEntityVo.getOilCarNum()!=null ) {
//            if (oilEntityVo.getOilCarNum() > 0) {
//                queryWrapper.eq("oil_car_num", oilEntityVo.getOilCarNum());
//            }
//        }
        if (StringUtils.isNotEmpty(oilEntityVo.getOilCarNum())) {
            queryWrapper.eq("oil_car_num", oilEntityVo.getOilCarNum());
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getStartTime())) {
            queryWrapper.gt("verification_date", oilEntityVo.getStartTime());
            queryWrapper.ne("verification_state", 1);
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getEndTime())) {
            oilEntityVo.setEndTime(oilEntityVo.getEndTime()+" 23:59:59");
            queryWrapper.lt("verification_date", oilEntityVo.getEndTime());
            queryWrapper.ne("verification_state", 1);
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getPlateNumber())){
            queryWrapper.like("plate_number", oilEntityVo.getPlateNumber());
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getServiceName())){
            queryWrapper.like("service_name", oilEntityVo.getServiceName());
        }
        //地址
        if (oilEntityVo.getSourceRegion() != null){
            if (oilEntityVo.getSourceRegion() > 0) {
                queryWrapper.eq("source_region", oilEntityVo.getSourceRegion());
            }
        }
        if (oilEntityVo.getDesRegion() != null){
            if (oilEntityVo.getDesRegion() > 0) {
                queryWrapper.eq("des_region", oilEntityVo.getDesRegion());
            }
        }
        if (oilEntityVo.getVerifySts() != null){
            if (oilEntityVo.getVerifySts() > 0) {
                queryWrapper.eq("recharge_state", oilEntityVo.getVerifySts());
            }
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getOrderId())) {
            queryWrapper.eq("order_id", oilEntityVo.getOrderId());
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getCompanyName())) {
            queryWrapper.like("company_name", oilEntityVo.getCompanyName());
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getCarNo())) {
            queryWrapper.eq("car_no", oilEntityVo.getCarNo());
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getBusiCode())) {
            queryWrapper.eq("busi_code", oilEntityVo.getBusiCode());
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getCustomName())) {
            queryWrapper.like("custom_name", oilEntityVo.getCustomName());
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getCarDriverMan())) {
            queryWrapper.like("car_driver_man", oilEntityVo.getCarDriverMan());
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getCarDriverPhone())) {
            queryWrapper.like("car_driver_phone", oilEntityVo.getCarDriverPhone());
        }
        if (oilEntityVo.getVehicleClass() != null) {
            if (oilEntityVo.getVehicleClass() > 0) {
                queryWrapper.eq("vehicle_class", oilEntityVo.getVehicleClass());
            }
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getSourceName())) {
            queryWrapper.like("source_name", oilEntityVo.getSourceName());
        }
        queryWrapper.orderByDesc("create_time");
        Page<OilEntity> ipage = new Page(pageNum, pageSize);
        IPage<OilEntity> page = oilEntityMapper.selectPage(ipage, queryWrapper);
        List<OilEntity> list = page.getRecords();
        List<OilEntityInfoDto> targetList = new ArrayList<OilEntityInfoDto>();
        for (OilEntity oilEntity : list) {
            OilEntityInfoDto oilEntityInfoDto = new OilEntityInfoDto();
            BeanUtils.copyProperties(oilEntity,oilEntityInfoDto);
            if (oilEntity.getVerificationState() == 1){
                oilEntityInfoDto.setVerificationDate(null);
            }else {
                oilEntityInfoDto.setVerificationDate(oilEntity.getVerificationDate());
            }
            oilEntityInfoDto.setDependTime(oilEntity.getDependTime());
            oilEntityInfoDto.setCreateTime(oilEntity.getCreateTime());
            oilEntityInfoDto.setCarStatus(oilEntity.getVehicleClass());
            if (oilEntity.getVoucherAmount() == null){
                oilEntityInfoDto.setVoucherAmount(0L);
            }else {
                oilEntityInfoDto.setVoucherAmount(oilEntityInfoDto.getVoucherAmount());
            }
            //待核销金额
            oilEntityInfoDto.setNoVerificateEntityFee(oilEntity.getNoVerificateEntityFee());
            if (oilEntity.getRechargeState()==5){
                oilEntityInfoDto.setNoVerificateEntityFeeDouble(0);
            }else {
                oilEntityInfoDto.setNoVerificateEntityFeeDouble(CommonUtil.getDoubleFormatLongMoney(oilEntityInfoDto.getNoVerificateEntityFee(), 2));
            }
            if (StringUtils.isNotEmpty(oilEntity.getVehicleLengh())){
                oilEntityInfoDto.setVehicleLenghName(SysStaticDataRedisUtils.getSysStaticDataId(redisUtil,user.getTenantId(),"VEHICLE_LENGTH", Long.valueOf(oilEntity.getVehicleLengh())).getCodeName());
            }
            if (oilEntity.getVehicleStatus() != null){
                oilEntityInfoDto.setVehicleStatusName(SysStaticDataRedisUtils.getSysStaticDataId(redisUtil,user.getTenantId(),"VEHICLE_STATUS", Long.valueOf(oilEntity.getVehicleStatus())).getCodeName());
            }
            oilEntityInfoDto.setVerificationState(oilEntity.getVerificationState());
            oilEntityInfoDto.setVerificationStateName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil,user.getTenantId(),"OIL_ENTITY_RECHARGE_STATE", oilEntity.getRechargeState() + ""));
            //充值金额
            oilEntityInfoDto.setCostEntityOil(oilEntity.getPreOilFee());
            oilEntityInfoDto.setCarStatusName(SysStaticDataRedisUtils.getSysStaticData(redisUtil,user.getTenantId(),"VEHICLE_CLASS", oilEntityInfoDto.getCarStatus()+"").getCodeName());
            if (oilEntityInfoDto.getDesRegion()!=null && (oilEntityInfoDto.getDesRegion())!=0){
                oilEntityInfoDto.setDesRegionName(SysStaticDataRedisUtils.getCityDataList(redisUtil,"SYS_CITY", oilEntityInfoDto.getDesRegion()+"").getName());
            }
            if (oilEntityInfoDto.getSourceRegion()!=null&&(oilEntityInfoDto.getSourceRegion())!=0)
                oilEntityInfoDto.setSourceRegionName(SysStaticDataRedisUtils.getCityDataList(redisUtil,"SYS_CITY", oilEntityInfoDto.getSourceRegion()+"").getName());
            targetList.add(oilEntityInfoDto);
        }
        IPage<OilEntityInfoDto> oilEntitys = new Page<>();
        oilEntitys.setRecords(targetList);
        oilEntitys.setTotal(page.getTotal());
        oilEntitys.setSize(page.getSize());
        return oilEntitys;
    }

    @Override
    public void batchVerificatOrder(String orderIds,String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if (user == null || user.getTenantId() == null) {
            throw new BusinessException("用户未登录！");
        }
        Date updateDate = new Date();
        for (String id : orderIds.split(",")) {
            OilEntity entity = this.getOilEntity(Long.valueOf(id));
            if (entity.getVerificationState() != null && entity.getVerificationState() == Integer.valueOf(OrderConsts.PayOutVerificationState.VERIFICATION_STATE)) {
                throw new BusinessException("勾选记录id为：" + id  + " 的油卡充值记录已核销过了，不可再次核销");
            }
            //反写recharge_info_record
            if (entity.getLineState() != null && entity.getLineState() == OrderAccountConst.OIL_ENTITY.LINE_STATE1) {
                if (entity.getRechargeState() == null || entity.getRechargeState() != OrderAccountConst.OIL_ENTITY.RECHARGE_STATE4) {
                    throw new BusinessException("勾选记录id为：" + id  + " 的油卡充值记录状态为服务商还未充值，不可核销");
                }
                RechargeInfoRecord rir = iRechargeInfoRecordService.getObjectById(entity.getSoNbr());
                if (rir == null) {
                    throw new BusinessException("根据油卡号：" + entity.getOilCarNum()  + " 批次号：" + entity.getSoNbr() + " 未找到充值记录");
                }
                rir.setState(OrderAccountConst.RECHARGE_OIL_CARD.PAY_STATE4);
                rir.setUpdateTime(getDateToLocalDateTime(updateDate));
                iRechargeInfoRecordService.saveOrUpdate(rir);
                sysOperLogService.saveSysOperLog(user, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.rechargeInfoRecord,
                        rir.getId(), SysOperLogConst.OperType.Add,
                        "["+user.getName()+"]确认充值完成",user.getTenantId());
            }
            sysOperLogService.saveSysOperLog(user, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.OilEntity, Long.valueOf(entity.getId()), com.youming.youche.commons.constant.SysOperLogConst.OperType.Update,"["+user.getName()+"]进行手动核销操作");
            if (entity.getOilType() !=null && entity.getOilType() ==  SysStaticDataEnum.OIL_TYPE.OIL_TYPE2) {
                iOilCardManagementService.updateCardBalanceByCardNum(entity.getOilCarNum(),entity.getPreOilFee(),null,accessToken);
                entity.setVerificationState(Integer.valueOf(OrderConsts.PayOutVerificationState.VERIFICATION_STATE));
                entity.setRechargeState(OrderAccountConst.OIL_ENTITY.RECHARGE_STATE5);
                entity.setVerificationDate(LocalDateTime.now());
                this.update(entity);
                continue;
            }
            this.updateOrderVerificationState(entity.getId(),entity.getOrderId(),entity.getOilCarNum(),accessToken);
            OrderInfoExt ext  = iOrderInfoExtService.getOrderInfoExt(entity.getOrderId());
            OrderInfoExtH extH =  iOrderInfoExtHService.getOrderInfoExtH(entity.getOrderId());
            if(ext != null && ext.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE){
                iOilCardManagementService.updateCardBalanceByCardNum(entity.getOilCarNum(),entity.getPreOilFee(),entity.getOrderId(),accessToken);
            }
            if(extH != null && extH.getPaymentWay() == OrderConsts.PAYMENT_WAY.EXPENSE){
                iOilCardManagementService.updateCardBalanceByCardNum(entity.getOilCarNum(),entity.getPreOilFee(),entity.getOrderId(),accessToken);
            }
            entity.setVerificationState(Integer.valueOf(OrderConsts.PayOutVerificationState.VERIFICATION_STATE));
            entity.setVerificationDate(LocalDateTime.now());
            entity.setRechargeState(OrderAccountConst.OIL_ENTITY.RECHARGE_STATE5);
            this.update(entity);
            List<OrderLimit> orderLimits = iOrderLimitService.getOrderLimitsByOrderId(entity.getOrderId(),-1);
            if (orderLimits != null && orderLimits.size() > 0) {
                for (OrderLimit limit : orderLimits) {
                    if (limit.getOrderEntityOil() != null && limit.getOrderEntityOil() > 0) {
                        this.oilEntityVerification(limit.getUserId(), limit.getUserPhone(), limit.getVehicleAffiliation(), entity.getOrderId(), entity.getPreOilFee() == null ? 0L : entity.getPreOilFee(), limit.getTenantId());
                    }
                }
            }
            try {
                orderFeeStatementService.checkOrderAmountByProcess(entity.getOrderId(), entity.getPreOilFee(),accessToken);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    private OilEntity getOilEntity(Long id) {
         OilEntity oilEntity = this.get(id);
         return oilEntity;

    }

    private void oilEntityVerification(long userId, String billId, String vehicleAffiliation, long orderId, long amount, long tenantId) {
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel amountFeeSubjectsRel = new BusiSubjectsRel();
        amountFeeSubjectsRel.setSubjectsId(EnumConsts.SubjectIds.OIL_ENTITY_VERIFICATION);
        amountFeeSubjectsRel.setAmountFee(amount);
        busiList.add(amountFeeSubjectsRel);
        //计算费用集合
        List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.OIL_ENTITY_VERIFICATION, busiList);
        Map<String,String> inParam = this.setParameters(userId, billId, EnumConsts.PayInter.OIL_ENTITY_VERIFICATION, orderId, amount, vehicleAffiliation, "");
        iBaseBusiToOrderService.busiToOrder(inParam, busiSubjectsRelList);
    }

    private Map<String, String> setParameters(long userId,String billId,long businessId,long orderId,long amount,String vehicleAffiliation,String finalPlanDate) {
        Map<String,String> map = new HashMap<String, String>();
        map.put("userId", String.valueOf(userId));
        map.put("billId", String.valueOf(billId));
        map.put("businessId", String.valueOf(businessId));
        map.put("orderId", String.valueOf(orderId));
        map.put("amount", String.valueOf(amount));
        map.put("vehicleAffiliation", String.valueOf(vehicleAffiliation));
        map.put("finalPlanDate",String.valueOf(finalPlanDate));
        return map;
    }

    private void updateOrderVerificationState(Long id, Long orderId, String oilCarNum,String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if (user == null || user.getTenantId() == null) {
            throw new BusinessException("用户未登录！");
        }
        OrderFee orderFee  = iOrderFeeService.getOrderFee(Long.valueOf(orderId));
        OrderFeeH feeH = iOrderFeeHService.getOrderFeeH(Long.valueOf(orderId));
        OrderScheduler order = iOrderSchedulerService.getOrderScheduler(Long.valueOf(orderId));
        OrderSchedulerH orderSchedulerH = iOrderSchedulerService.getOrderSchedulerH(Long.valueOf(orderId));
        //OrderOilCardInfo entity = cardInfoSV.queryOrderOilCardInfoByOrderId(orderId,oilCarNum).get(0);
        if(orderFee !=null && order != null){
            //FIXME 订单油卡需求变更,注释代码需相应更改
            //生成一条使用日志
            List<OilCardManagement> oils = iOilCardManagementService.findByOilCardNum(oilCarNum,orderFee.getTenantId());
            if(!oils.isEmpty()){
                OilCardManagement oil = (OilCardManagement) oils.get(0);
                OilCardLog oilCardLog = new OilCardLog();
                oilCardLog.setCardId(id);
                oilCardLog.setLogType(EnumConsts.OIL_LOG_TYPE.WRITE_OFF);
                oilCardLog.setOrderId(orderFee.getOrderId());
                oilCardLog.setCreateTime(LocalDateTime.now());
                oilCardLog.setTenantId(orderFee.getTenantId());
                if (user.getUserInfoId() != null) {
                    oilCardLog.setUserId(user.getUserInfoId());
                }
                oilCardLog.setCreateTime(LocalDateTime.now());
                oilCardLog.setPlateNumber(order.getPlateNumber());
                oilCardLog.setCarDriverMan(order.getCarDriverMan());
                oilCardLog.setOilFeeMerchant(orderFee.getPreOilFee());
                oilCardLog.setOilFee(orderFee.getPreOilFee());
                oilCardLog.setCarUserType(order.getVehicleClass());
                iOilCardLogService.save(oilCardLog);
            }
        }else {
            //FIXME 订单油卡需求变更,注释代码需相应更改
            //生成一条使用日志
            List<OilCardManagement> oils = iOilCardManagementService.findByOilCardNum(oilCarNum,feeH.getTenantId());
            if(!oils.isEmpty()){
                OilCardManagement oil = (OilCardManagement) oils.get(0);
                OilCardLog oilCardLog = new OilCardLog();
                oilCardLog.setCardId(id);
                oilCardLog.setLogType(EnumConsts.OIL_LOG_TYPE.WRITE_OFF);
                oilCardLog.setOrderId(feeH.getOrderId());
                oilCardLog.setCreateTime(LocalDateTime.now());
                oilCardLog.setTenantId(feeH.getTenantId());
                if (user.getUserInfoId() != null) {
                    oilCardLog.setUserId(user.getUserInfoId());
                }
                oilCardLog.setCreateTime(LocalDateTime.now());
                oilCardLog.setPlateNumber(orderSchedulerH.getPlateNumber());
                oilCardLog.setCarDriverMan(orderSchedulerH.getCarDriverMan());
                oilCardLog.setOilFeeMerchant(feeH.getPreOilFee());
                oilCardLog.setOilFee(feeH.getPreOilFee());
                oilCardLog.setCarUserType(orderSchedulerH.getVehicleClass());
                iOilCardLogService.save(oilCardLog);
            }
        }
    }
    private Date getLocalDateTimeToDate(LocalDateTime localDateTime) {
        Date date;
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        date = Date.from(zdt.toInstant());
        return date;
    }

    private LocalDateTime getDateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }

    @Override
    public void OilCardList1(String accessToken, ImportOrExportRecords record, OilEntityVo oilEntityVo) {
        LoginInfo user = loginUtils.get(accessToken);
        if (user == null || user.getTenantId() == null) {
            throw new BusinessException("用户未登录！");
        }
        //IPage<OilEntityInfoDto> page = oilEntityMapper.selectOr(oilEntityVo,user.getTenantId(),new Page<>(pageNum,pageSize));
        QueryWrapper<OilEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_id", user.getTenantId());

//        if (oilEntityVo.getOilCarNum()!=null ) {
//            if (oilEntityVo.getOilCarNum() > 0) {
//                queryWrapper.eq("oil_car_num", oilEntityVo.getOilCarNum());
//            }
//        }
        if (StringUtils.isNotEmpty(oilEntityVo.getOilCarNum())) {
            queryWrapper.eq("oil_car_num", oilEntityVo.getOilCarNum());
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getStartTime())) {
            queryWrapper.gt("verification_date", oilEntityVo.getStartTime());
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getEndTime())) {
            queryWrapper.lt("verification_date", oilEntityVo.getEndTime());
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getPlateNumber())){
            queryWrapper.like("plate_number", oilEntityVo.getPlateNumber());
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getServiceName())){
            queryWrapper.like("service_name", oilEntityVo.getServiceName());
        }
        //地址
        if (oilEntityVo.getSourceRegion() != null){
            if (oilEntityVo.getSourceRegion() > 0) {
                queryWrapper.eq("source_region", oilEntityVo.getSourceRegion());
            }
        }
        if (oilEntityVo.getDesRegion() != null){
            if (oilEntityVo.getDesRegion() > 0) {
                queryWrapper.eq("des_region", oilEntityVo.getDesRegion());
            }
        }
        if (oilEntityVo.getVerifySts() != null){
            if (oilEntityVo.getVerifySts() > 0) {
                queryWrapper.eq("verification_state", oilEntityVo.getVerifySts());
            }
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getOrderId())) {
            queryWrapper.eq("order_id", oilEntityVo.getOrderId());
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getCompanyName())) {
            queryWrapper.like("company_name", oilEntityVo.getCompanyName());
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getCarNo())) {
            queryWrapper.eq("car_no", oilEntityVo.getCarNo());
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getBusiCode())) {
            queryWrapper.eq("busi_code", oilEntityVo.getBusiCode());
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getCustomName())) {
            queryWrapper.like("custom_name", oilEntityVo.getCustomName());
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getCarDriverMan())) {
            queryWrapper.like("car_driver_man", oilEntityVo.getCarDriverMan());
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getCarDriverPhone())) {
            queryWrapper.like("car_driver_phone", oilEntityVo.getCarDriverPhone());
        }
        if (oilEntityVo.getVehicleClass() != null) {
            if (oilEntityVo.getVehicleClass() > 0) {
                queryWrapper.eq("vehicle_class", oilEntityVo.getVehicleClass());
            }
        }
        if (StringUtils.isNotEmpty(oilEntityVo.getSourceName())) {
            queryWrapper.like("source_name", oilEntityVo.getSourceName());
        }
        queryWrapper.orderByDesc("create_time");

        List<OilEntity> oilEntityList = baseMapper.selectList(queryWrapper);

        List<OilEntityInfoDto> paymentInfoList = new ArrayList<OilEntityInfoDto>();
        for (OilEntity oilEntity : oilEntityList) {
            OilEntityInfoDto oilEntityInfoDto = new OilEntityInfoDto();
            BeanUtils.copyProperties(oilEntity,oilEntityInfoDto);
            if (oilEntity.getVerificationState() == 1){
                oilEntityInfoDto.setVerificationDate(null);
            }else {
                oilEntityInfoDto.setVerificationDate(oilEntity.getVerificationDate());
            }
            oilEntityInfoDto.setDependTime(oilEntity.getDependTime());
            oilEntityInfoDto.setCreateTime(oilEntity.getCreateTime());
            oilEntityInfoDto.setCarStatus(oilEntity.getVehicleClass());
            if (oilEntity.getVoucherAmount() == null){
                oilEntityInfoDto.setVoucherAmount(0L);
            }else {
                oilEntityInfoDto.setVoucherAmount(oilEntityInfoDto.getVoucherAmount());
            }
            //待核销金额
            oilEntityInfoDto.setNoVerificateEntityFee(oilEntity.getNoVerificateEntityFee());
            if (oilEntity.getVehicleLengh() !=null){
                oilEntityInfoDto.setVehicleLenghName(SysStaticDataRedisUtils.getSysStaticDataId(redisUtil,user.getTenantId(),"VEHICLE_LENGTH", Long.valueOf(oilEntity.getVehicleLengh())).getCodeName());
            }
            if (oilEntity.getVehicleStatus() != null){
                oilEntityInfoDto.setVehicleStatusName(SysStaticDataRedisUtils.getSysStaticDataId(redisUtil,user.getTenantId(),"VEHICLE_STATUS", Long.valueOf(oilEntity.getVehicleStatus())).getCodeName());
            }
            oilEntityInfoDto.setVerificationState(oilEntity.getVerificationState());
            oilEntityInfoDto.setVerificationStateName(SysStaticDataRedisUtils.getSysStaticDataCodeName(redisUtil,user.getTenantId(),"OIL_ENTITY_RECHARGE_STATE", oilEntity.getRechargeState() + ""));
            //充值金额
            oilEntityInfoDto.setCostEntityOil(oilEntity.getPreOilFee());
            oilEntityInfoDto.setCarStatusName(SysStaticDataRedisUtils.getSysStaticData(redisUtil,user.getTenantId(),"VEHICLE_CLASS", oilEntityInfoDto.getCarStatus()+"").getCodeName());
            if (oilEntityInfoDto.getDesRegion()!=null && (oilEntityInfoDto.getDesRegion())!=0){
                oilEntityInfoDto.setDesRegionName(SysStaticDataRedisUtils.getCityDataList(redisUtil,"SYS_CITY", oilEntityInfoDto.getDesRegion()+"").getName());
            }
            if (oilEntityInfoDto.getSourceRegion()!=null&&(oilEntityInfoDto.getSourceRegion())!=0)
                oilEntityInfoDto.setSourceRegionName(SysStaticDataRedisUtils.getCityDataList(redisUtil,"SYS_CITY", oilEntityInfoDto.getSourceRegion()+"").getName());
            paymentInfoList.add(oilEntityInfoDto);
        }
//        IPage<OilEntityInfoDto> page = this.getOilEntitys(oilEntityVo,pageSize,pageNum,accessToken);
//
//        List<OilEntityInfoDto> paymentInfoList = page.getRecords();
        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{

                    "油卡卡号", "服务商名称", "创建时间",
                    "充值金额", "代金券金额", "待核销金额",
                    "油卡状态", "核销时间", "订单号",
                    "车牌号码", "司机姓名", "司机手机号",
                    "车辆种类", "靠台时间", "客户名称",
                    "线路起始", "线路到达", "线路名称",
                    "车长", "车型"};
            resourceFild = new String[]{
                    "getOilCarNum", "getServiceName", "getCreateTime",
                    "getCostEntityOilDouble", "getVoucherAmount", "getNoVerificateEntityFeeDouble",
                    "getVerificationStateName", "getVerificationDate", "getOrderId",
                    "getPlateNumber", "getCarDriverMan", "getCarDriverPhone",
                    "getCarStatusName", "getDependTime", "getCustomName",
                    "getSourceRegionName", "getDesRegionName", "getSourceName",
                    "getVehicleLenghName", "getVehicleStatusName"};

            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(paymentInfoList, showName, resourceFild, OilEntityInfoDto.class, null);

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "现金打款.xlsx", inputStream.available());
            os.close();
            inputStream.close();
            record.setMediaUrl(path);
            record.setState(2);
            importOrExportRecordsService.update(record);
        } catch (Exception e) {
            record.setState(3);
            importOrExportRecordsService.update(record);
            e.printStackTrace();
        }
    }

    @Override
    public void doAccountIn(Long userId, Integer state, String remark, Long sourceTenantId, Long userType,String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if (user == null || user.getTenantId() == null) {
            throw new BusinessException("用户未登录！");
        }
        OrderAccountVO accountVO = new OrderAccountVO();
        accountVO.setUserId(userId);
        accountVO.setSourceTenantId(sourceTenantId);
        accountVO.setUserType(userType);
        accountVO.setRemark(remark);
        accountVO.setState(state);
        iOrderAccountService.updateByA(accountVO);
        String msg = "";
        if(state == 1){
            msg = "由["+user.getName()+"]"+"结冻账户,解冻原因:" + remark;
        }else {
            msg = "由["+user.getName()+"]"+"冻结账户,冻结原因:" + remark;
        }
        sysOperLogService.saveSysOperLog(user,SysOperLogConst.BusiCode.AccountQuery, Long.valueOf(userId), SysOperLogConst.OperType.Update,msg,user.getTenantId());
    }

    @Override
    public void updateOilCarNum(String orderId, String oilCarNum, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if (StringUtils.isEmpty(orderId)) {
            throw new BusinessException("查找不到订单信息");
        }
        if (StringUtils.isEmpty(oilCarNum)) {
            throw new BusinessException("请输入卡号信息");
        }

        if(iOilCardManagementService.doCheckOilCardNum(oilCarNum,accessToken) == 2){
            throw new BusinessException("该卡号不存在，不能修改。");
        }else if(iOilCardManagementService.doCheckOilCardNum(oilCarNum,accessToken) == 3){
            throw new BusinessException("该卡号为平台卡号，要走线上打款，不能修改");
        }
        OilEntity entity = this.getOilEntity(Long.valueOf(orderId));
        if(entity == null){
            throw new BusinessException("订单信息不存在");
        }
        if (entity.getVerificationState() != null && entity.getVerificationState() == Integer.valueOf(OrderConsts.PayOutVerificationState.VERIFICATION_STATE)) {
            throw new BusinessException("油卡充值记录已核销，不允许修改卡号");
        }
        if(entity.getLineState() != 0){
            throw new BusinessException("此记录是线上打款不允许修改油卡号！");
        }
        //判断原卡号和修改的卡号是否来源类型一致
        Integer oriCardType = null;
        List<OilCardManagement> oriCardList = iOilCardManagementService.getOilCardManagementByCard(entity.getOilCarNum(),entity.getTenantId());
        if (oriCardList == null || oriCardList.size() <= 0) {
            throw new BusinessException("根据卡号" + entity.getOilCarNum() + "租户id：" + entity.getTenantId() + "未找到卡号信息");
        } else {
            oriCardType = oriCardList.get(0).getCardType();
        }
        Integer upCardType = null;
        List<OilCardManagement> upCardList = iOilCardManagementService.getOilCardManagementByCard(oilCarNum,entity.getTenantId());
        if (upCardList == null || upCardList.size() <= 0) {
            throw new BusinessException("根据卡号" + oilCarNum + "租户id：" + entity.getTenantId() +  "未找到卡号信息");
        } else {
            upCardType = upCardList.get(0).getCardType();
        }
        if (oriCardType == null || upCardType == null || Integer.parseInt(oriCardType + "") != Integer.parseInt(upCardType + "")) {
            throw new BusinessException("原卡号:" + entity.getOilCarNum() + "和修改卡号：" + oilCarNum + "来源类型不一致，不可以修改卡号");
        }
        String msg = "["+user.getName()+"]"+"将油卡号[" + entity.getOilCarNum() + "]修改为[" + oilCarNum + "]";
        ServiceInfo serviceInfo = iOilCardManagementService.getServiceInfoByCardNum(oilCarNum,entity.getTenantId());
        if (serviceInfo != null) {
            entity.setServiceName(serviceInfo.getServiceName());
        }else{
            entity.setServiceName("");
        }
        entity.setOilCarNum(oilCarNum);
        this.update(entity);
        iSysOperLogService.saveSysOperLog(user,SysOperLogConst.BusiCode.OilEntity, Long.valueOf(entity.getId()), SysOperLogConst.OperType.Update,msg,user.getTenantId());
        try {

            StaffDataInfoDto staffDataInfoIn = new StaffDataInfoDto();
            Map paraMap = new HashMap();
            paraMap.put("orderId",entity.getOrderId());
            String url = "../ac/manual/oilEntity.html?orderId=" + entity.getOrderId();
            List<StaffDataInfoDto> outList = iTenantStaffRelService.queryStaffInfoList(staffDataInfoIn);
            if (outList != null && outList.size() > 0) {
                for (StaffDataInfoDto out : outList) {
                    if (StringUtils.isNotBlank(out.getMobilePhone())) {
                        iSysSmsSendService.sendSms(out.getMobilePhone(),
                                EnumConsts.SmsTemplate.UPDATE_OIL_CARD,
                                SysStaticDataEnum.WEB_SMS_TYPE.OPEN_TAB , Long.parseLong("付款核销"),url, paraMap,accessToken);
                    }
                }
            }
        } catch (Exception e) {
            log.error("【推送油卡充值修改卡号提醒失败】"+e.getMessage());
        }
    }

    @Override
    public void revokeOilCharge(String id,String accessToken) {
        OilEntity entity = oilEntityMapper.selectById(Long.valueOf(id));
        LoginInfo user = loginUtils.get(accessToken);
        if (entity == null) {
            throw new BusinessException("根据流水号：" + id + " 未找到充值记录");
        }
        if (entity.getOilType() != null && entity.getOilType() == SysStaticDataEnum.OIL_TYPE.OIL_TYPE1) {
            throw new BusinessException("订单发起的充值，不允许撤销");
        }
        if (entity.getRechargeState() == OrderAccountConst.OIL_ENTITY.RECHARGE_STATE6) {
            throw new BusinessException("该充值记录以及撤销了，无需再次撤销");
        }
        if (entity.getLineState() != null && entity.getLineState() == OrderAccountConst.OIL_ENTITY.LINE_STATE1) {
            if (entity.getRechargeState() != OrderAccountConst.OIL_ENTITY.RECHARGE_STATE1 && entity.getRechargeState() != OrderAccountConst.OIL_ENTITY.RECHARGE_STATE2) {
                throw new BusinessException("该充值记录已经付款了，不允许撤销");
            }
            RechargeInfoRecord rir = rechargeInfoRecordMapper.selectById(entity.getSoNbr());
            if (rir == null) {
                throw new BusinessException("根据流水号：" + entity.getSoNbr() + " 未找到充值记录");
            }
            if (rir.getState() != null && rir.getState() != OrderAccountConst.RECHARGE_OIL_CARD.PAY_STATE1) {
                throw new BusinessException("该充值记录已经付款了，不允许撤销");
            }
            //回退代金券
            if (rir.getVoucherPayAmount() != null && rir.getVoucherPayAmount().longValue() > 0) {
                this.revokeOilCharge(rir,accessToken);
            }
            rir.setUpdateTime(LocalDateTime.now());
            rir.setUpdateOpId(user.getId());
            rir.setState(OrderAccountConst.RECHARGE_OIL_CARD.PAY_STATE5);
            iRechargeInfoRecordService.saveOrUpdate(rir);

            if (rir.getPayCash() != null && rir.getPayCash().longValue() > 0) {
                List<PayoutIntf> list = iPayoutIntfService.getPayoutIntfByBusiCode(rir.getRechargeFlowId(), OrderAccountConst.TXN_TYPE.XX_TXN_TYPE);
                if (list == null || list.size() <= 0) {
                    throw new BusinessException("根据业务编码：" + rir.getRechargeFlowId() + " 未找到打款记录");
                }
                PayoutIntf pay = list.get(0);
                if ((pay.getVerificationState() != 2 && pay.getIsAutomatic() == OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1)
                        || (StringUtils.isNotBlank(pay.getRespCode()) && !HttpsMain.respCodeInvalid.equals(pay.getRespCode()))) {
                    throw new BusinessException("打款记录流水号：" + pay.getId() + " 打款记录已经在打款中，不可以撤销");
                }
                this.revokeOilCharge(pay,accessToken);
                pay.setRespCode(HttpsMain.respCodeInvalid);
                pay.setRespMsg("撤销打款");
                iPayoutIntfService.saveOrUpdate(pay);
                //取消审核流程
                if (pay.getTxnType() == OrderAccountConst.TXN_TYPE.XX_TXN_TYPE && pay.getIsAutomatic() == IS_TURN_AUTOMATIC_0) {
                    try {
                        iAuditOutService.cancelProcess(AuditConsts.AUDIT_CODE.PAY_CASH_CODE, pay.getId(), pay.getPayTenantId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }				}
            }
        } else {
            if (entity.getVerificationState() != null && entity.getVerificationState() == OrderConsts.PayOutVerificationState.VERIFICATION_STATE) {
                throw new BusinessException("该充值记录已经核销了，不允许撤销");
            }
        }
        entity.setRechargeState(OrderAccountConst.OIL_ENTITY.RECHARGE_STATE6);
        oilEntityMapper.updateById(entity);
        iSysOperLogService.saveSysOperLog(user,SysOperLogConst.BusiCode.OilEntity, Long.valueOf(entity.getId()),
                SysOperLogConst.OperType.Update, "[" + user.getName() + "]进行撤销操作",user.getTenantId());

    }

    @Override
    public void revokeOilCharge(RechargeInfoRecord rir,String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);
        if (rir == null) {
            throw new BusinessException("请输入油卡充值记录");
        }
        if (rir.getVoucherPayAmount() != null && rir.getVoucherPayAmount().longValue() > 0) {
            List<VoucherInfoRecord> list = iVoucherInfoService.getVoucherInfoRecord(rir.getId());
            if (list != null && list.size() > 0) {
                for (VoucherInfoRecord vir : list) {
                    Long voucherInfoId = vir.getVoucherInfoId();
                    long amount = vir.getAmount() == null ? 0 : vir.getAmount();
//                    VoucherInfo vi = iVoucherInfoService.getObjectById(VoucherInfo.class, voucherInfoId);
                    VoucherInfo vi = iVoucherInfoService.getById(voucherInfoId);
                    if (vi == null) {
                        throw new BusinessException("根据代金券id：" + voucherInfoId + " ");
                    }
                    vi.setNoPayAmount(vi.getNoPayAmount() + amount);
                    vi.setPaidAmount(vi.getPaidAmount() - amount);
                    vi.setUpdateTime(LocalDateTime.now());
                    iVoucherInfoService.saveOrUpdate(vi);
                    iSysOperLogService.saveSysOperLog(user,SysOperLogConst.BusiCode.voucherInfo, vi.getId(),
                            SysOperLogConst.OperType.Add,
                            "油卡充值撤销代金券回退金额：" + (double) amount / 100,user.getTenantId());
                }
            } else {
                throw new BusinessException("根据油卡充值记录：" + rir.getId() + " 未找到油卡使用代金券记录");
            }
        }
    }

    @Override
    public void revokeOilCharge(PayoutIntf pay, String accessToken) {
        LoginInfo user = loginUtils.get(accessToken);

        if (pay == null) {
            throw new BusinessException("请输入油卡充值记录");
        }
        if ((pay.getVerificationState() != 2 && pay.getIsAutomatic() == OrderAccountConst.IS_AUTOMATIC.AUTOMATIC1)
                || (StringUtils.isNotBlank(pay.getRespCode()) && !HttpsMain.respCodeInvalid.equals(pay.getRespCode()))) {
            throw new BusinessException("打款记录已经在打款中，不可以撤销");
        }
        Long amount = pay.getTxnAmt();
        String vehicleAffiliation = pay.getVehicleAffiliation();
        String oilAffiliation = pay.getOilAffiliation();
        Long tenantId = pay.getPayTenantId();
        Long serviceUserId = pay.getUserId();
        int userType = pay.getUserType();
        Long tenantUserId = pay.getPayObjId();
        int payUserType = pay.getPayUserType();
        SysUser tenantSysOperator = sysUserService.getSysOperatorByUserId(tenantUserId);
        if (tenantSysOperator == null) {
            throw new BusinessException("根据用户id：" + tenantUserId + "没有找到用户信息!");
        }
        SysUser sysOperator = sysUserService.getSysOperatorByUserId(serviceUserId);
        if (sysOperator == null) {
            throw new BusinessException("没有找到用户信息!");
        }
        OrderAccount account = iOrderAccountService.queryOrderAccount(serviceUserId, vehicleAffiliation, 0L, tenantId, oilAffiliation,userType);
        List<BusiSubjectsRel> busiList = new ArrayList<BusiSubjectsRel>();
        //服务商应收逾期
        BusiSubjectsRel amountFeeSubjectsRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.RECHARGE_ENTITY_OIL_RECEIVABLE_OVERDUE_SUB_CANCEL, amount);
        busiList.add(amountFeeSubjectsRel);

        // 计算费用集合
        List<BusiSubjectsRel> busiSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.SERVICE_RECHARGE, busiList);
        // 写入账户明细表并修改账户金额费用
        Long soNbr = CommonUtil.createSoNbr();
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.SERVICE_RECHARGE,
                tenantSysOperator.getOpUserId(), tenantSysOperator.getOpName(), account, busiSubjectsRelList, soNbr, 0L,
                sysOperator.getOpName(), null, tenantId, null, "", orderResponseDto, vehicleAffiliation,user);

        //车队应付逾期
        OrderAccount fleetAccount = iOrderAccountService.queryOrderAccount(tenantUserId, vehicleAffiliation, 0L, tenantId, oilAffiliation,payUserType);
        List<BusiSubjectsRel> fleetBusiList = new ArrayList<BusiSubjectsRel>();
        BusiSubjectsRel payableOverdueRel = iBusiSubjectsRelService.createBusiSubjectsRel(EnumConsts.SubjectIds.RECHARGE_ENTITY_OIL_PAYABLE_OVERDUE_SUB_CANCEL, amount);
        fleetBusiList.add(payableOverdueRel);

        // 计算费用集合
        List<BusiSubjectsRel> fleetSubjectsRelList = iBusiSubjectsRelService.feeCalculation(EnumConsts.PayInter.SERVICE_RECHARGE, fleetBusiList);
        iAccountDetailsService.insetAccDet(EnumConsts.BusiType.INCOMING_CODE, EnumConsts.PayInter.SERVICE_RECHARGE,
                sysOperator.getOpUserId(), sysOperator.getOpName(), fleetAccount, fleetSubjectsRelList, soNbr, 0L,
                tenantSysOperator.getOpName(), null, tenantId, null, "", null, vehicleAffiliation,user);
    }
}
