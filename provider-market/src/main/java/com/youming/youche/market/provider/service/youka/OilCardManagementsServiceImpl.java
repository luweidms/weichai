package com.youming.youche.market.provider.service.youka;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
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
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.api.facilitator.ISysStaticDataMarketService;
import com.youming.youche.market.api.facilitator.ITenantServiceRelService;
import com.youming.youche.market.api.youka.*;
import com.youming.youche.market.commons.OrderAccountConst;
import com.youming.youche.market.commons.OrderConsts;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.market.domain.facilitator.TenantServiceRel;
import com.youming.youche.market.domain.youka.*;
import com.youming.youche.market.dto.facilitator.ServiceInfoFleetDto;
import com.youming.youche.market.dto.youca.*;
import com.youming.youche.market.provider.mapper.youka.OilCardManagementsMapper;
import com.youming.youche.market.provider.mapper.youka.OilCardVehicleRelMapper;
import com.youming.youche.market.provider.mapper.youka.OilEntityMapper;
import com.youming.youche.market.provider.utis.CommonUtil;
import com.youming.youche.market.vo.facilitator.ServiceInfoFleetVo;
import com.youming.youche.market.vo.facilitator.ServiceInfoVo;
import com.youming.youche.market.vo.youca.OilCardManagementVo;
import com.youming.youche.market.vo.youca.TenantVehicleRelVo;
import com.youming.youche.record.api.order.IOrderSchedulerService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.domain.order.OrderScheduler;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.SysOperLog;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.util.ExcelFilesVaildate;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.youming.youche.conts.SysStaticDataEnum.OILCARD_STATUS.STATUS_YES;
import static com.youming.youche.conts.SysStaticDataEnum.OIL_CARD_TYPE.SERVICE;

@DubboService(version = "1.0.0")
public class OilCardManagementsServiceImpl extends BaseServiceImpl<OilCardManagementsMapper, OilCardManagement> implements IOilCardManagementService {
    @Resource
    private LoginUtils loginUtils;
    @Resource
    RedisUtil redisUtil;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;
    @DubboReference(version = "1.0.0")
    IVoucherInfoService iVoucherInfoServicec;
    @Lazy
    @Resource
    IOilCardManagementService iOilCardManagementService;
    @Lazy
    @Resource
    IServiceInfoService iServiceInfoService;
    @Resource
    OilEntityMapper oilEntityMapper;
    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;
    @Lazy
    @Resource
    ITenantServiceRelService iTenantServiceRelService;
    @DubboReference(version = "1.0.0")
    IServiceProductOilsService iServiceProductOilsService;
    @DubboReference(version = "1.0.0")
    IOilCardLogService iOilCardLogService;
    @Resource
    OilCardVehicleRelMapper oilCardVehicleRelMapper;
    @Lazy
    @Resource
    IOilCardVehicleRelService iOilCardVehicleRelService;
    @DubboReference(version = "1.0.0")
    IOrderSchedulerService iOrderSchedulerService;
    @DubboReference(version = "1.0.0")
    IVehicleDataInfoService iVehicleDataInfoService;
    @Lazy
    @Resource
    ISysStaticDataMarketService sysStaticDataService;


    @Override
    public IPage<OilCardManagementDto> getManagementList(Integer pageNum, Integer pageSize, OilCardManagementVo oilCardManagementVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Page<OilCardManagementDto> pageInfo = new Page<>(pageNum, pageSize);
        baseMapper.seletcyoukaByAll(pageInfo, oilCardManagementVo, loginInfo.getTenantId());
        if (pageInfo.getRecords() != null && pageInfo.getRecords().size() > 0) {
            for (OilCardManagementDto o : pageInfo.getRecords()
            ) {
                try {
                    o.setOilCardStatusName(sysStaticDataService.getSysStaticData("OILCARD_STATUS", o.getOilCardStatus() == null ? "" : String.valueOf(o.getOilCardStatus()), accessToken).getCodeName());
                    o.setCardTypeName(sysStaticDataService.getSysStaticData("OIL_CARD_TYPE", o.getCardType() == null ? "" : String.valueOf(o.getCardType()), accessToken).getCodeName());
                    o.setOilCardTypeName(sysStaticDataService.getSysStaticData("SERV_OIL_CARD_TYPE", o.getOilCardType() == null ? "" : String.valueOf(o.getOilCardType()), accessToken).getCodeName());
                    long cardBalance = o.getCardBalance() == null ? -1 : o.getCardBalance();
                    if (cardBalance > 0) {
                        o.setCardBalaceStr(CommonUtil.divide(cardBalance));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return pageInfo;
    }

    @Override
    public IPage<TenantVehicleRelDto> getVehicles(Integer pageNum, Integer pageSize, TenantVehicleRelVo tenantVehicleRelVo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Page<OilCardManagementDto> pageInfo = new Page<>(pageNum, pageSize);

        return baseMapper.seletcyoukaByCar(pageInfo, tenantVehicleRelVo, loginInfo.getTenantId());
    }

    @Override
    public boolean add(OilCardManagement oilCardManagement, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        oilCardManagement.setTenantId(loginInfo.getTenantId());
        baseMapper.insert(oilCardManagement);
        sysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.OilCard,
                oilCardManagement.getId(), SysOperLogConst.OperType.Add, "新增");
        return true;
    }

    @Override
    public boolean upd(OilCardManagement oilCardManagement, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        oilCardManagement.setTenantId(loginInfo.getTenantId());
        baseMapper.updateById(oilCardManagement);
        sysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.OilCard,
                oilCardManagement.getId(), SysOperLogConst.OperType.Update, "修改");
        return true;
    }

    @Override
    public boolean saveOilCharge(String oilCarNum, long objToLongMul100, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<OilCardManagement> list = baseMapper.findByOilCardNum(oilCarNum, loginInfo.getTenantId());
        if (list == null || list.size() == 0) {
            throw new BusinessException("充值油卡不存在或无效！");
        }
        if (list.size() > 1) {
            throw new BusinessException("油卡数据有误，当前存在多条该油卡号的数据，请检查！");
        }
        OilCardManagement oilCard = list.get(0);

//        if (SysStaticDataEnum.OIL_CARD_TYPE.CUSTOMER == oilCard.getCardType()) {
//            throw new BusinessException("客户油卡不能进行充值！");
//        }
        //服务商油卡需要进行核销
        if (SERVICE == oilCard.getCardType()) {
            ServiceInfoVo serviceInfo = null;
            if (oilCard.getUserId() != null && oilCard.getUserId() > 0) {
//                serviceInfo = serviceInfoMapper.selectById(oilCard.getUserId());
                serviceInfo = iServiceInfoService.getServiceInfo(oilCard.getUserId(), loginInfo);
            }
            if (serviceInfo == null) {
                throw new BusinessException("该油卡服务商不存在！");
            }
            OilEntity oilEntity = new OilEntity();
            oilEntity.setOilCarNum(oilCarNum);
            oilEntity.setOilType(SysStaticDataEnum.OIL_TYPE.OIL_TYPE2);
            oilEntity.setPreOilFee(objToLongMul100);
            oilEntity.setTenantId(loginInfo.getTenantId());
            oilEntity.setServiceName(serviceInfo.getServiceName());
            oilEntity.setVerificationState(OrderConsts.PayOutVerificationState.INIT);
            oilEntity.setCreationTime(LocalDateTime.now());
            oilEntity.setRechargeState(OrderAccountConst.OIL_ENTITY.RECHARGE_STATE1);
            oilEntity.setLineState(OrderAccountConst.OIL_ENTITY.LINE_STATE0);
            oilEntityMapper.insert(oilEntity);
        } else  if (SysStaticDataEnum.OIL_CARD_TYPE.OWN==oilCard.getCardType() ) {
            QueryWrapper<OilCardManagement> wrapper = new QueryWrapper<>();
            wrapper.eq("id", oilCard.getId());
            oilCard.setCardBalance(oilCard.getCardBalance() + objToLongMul100);
            baseMapper.update(oilCard, wrapper);
        } else {
            throw new BusinessException("此类卡暂不支持充值操作！");
        }
//        else if (SysStaticDataEnum.OIL_CARD_TYPE.OWN == oilCard.getCardType()) {
//            oilCard.setCardBalance(oilCard.getCardBalance() + objToLongMul100);
//            baseMapper.updateById(oilCard);
//        }
        sysOperLogService.saveSysOperLog(loginInfo,
                com.youming.youche.commons.constant.SysOperLogConst.BusiCode.OilCard, oilCard.getId(),
                com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, "充值油卡"+objToLongMul100/100.00+"元",
                loginInfo.getTenantId());
        return true;
    }

    @Override
    public void updOilCardBlace(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Set<Long> serviceUserIdSet = new HashSet<>();
        List<OilCardManagement> oilCardManagementMapList = iOilCardManagementService.getOilCardManagementMapList(loginInfo.getTenantId());
        List<String> strList = new ArrayList<>();
        for (int i = 0; i < oilCardManagementMapList.size(); i++) {
            OilCardManagement oilCardManagement = oilCardManagementMapList.get(i);
            String oilCarNum = oilCardManagement.getOilCarNum();
            Long userId = oilCardManagement.getUserId();
            strList.add(oilCarNum);
            if (userId > -1) {
                serviceUserIdSet.add(userId);
            }
        }
        if (strList.isEmpty()) {
            return;
        }
        for (Long serviceUserId : serviceUserIdSet) {
            List<VoucherInfoDto> rechargeConsumeRecordList = iVoucherInfoServicec.doQueryRechargeConsumeRecordList(strList, loginInfo.getTenantId(), serviceUserId);
            if (null == rechargeConsumeRecordList || rechargeConsumeRecordList.isEmpty()) {
                return;
            }
            iOilCardManagementService.updBlace(rechargeConsumeRecordList, loginInfo.getTenantId());
        }
    }

    @Override
    public void updBlace(List<VoucherInfoDto> rechargeConsumeRecordList, Long tenantId) {
        for (int i = 0; i < rechargeConsumeRecordList.size(); i++) {
            String cardNum = null;
            Long balance = null;
            for (VoucherInfoDto voucherInfoDto : rechargeConsumeRecordList) {

                cardNum = voucherInfoDto.getCardNum();
                balance = voucherInfoDto.getBalance();
            }
            if (!cardNum.isEmpty()) {
                baseMapper.updateManagement(cardNum, balance, tenantId);
            }

        }
    }

    @Override
    public OilCardManagement saveIn(OilCardSaveIn oilCardSaveIn, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        oilCardSaveIn.setTenantId(loginInfo.getTenantId());
        oilCardSaveIn.setOpId(loginInfo.getId());
        oilCardSaveIn.setOpName(loginInfo.getName());
        String oilCarNum = oilCardSaveIn.getOilCarNum();
        Integer oilCardStatus = oilCardSaveIn.getOilCardStatus();
        Long userId = oilCardSaveIn.getUserId();
        Integer cardType = oilCardSaveIn.getCardType();
        Long cardBalance = oilCardSaveIn.getCardBalance();
        if (org.apache.commons.lang3.StringUtils.isBlank(oilCarNum)) {
            throw new BusinessException("请输入油卡号");
        }
        if (oilCardStatus == null || oilCardStatus < 0) {
            throw new BusinessException("请选择油卡状态");
        }
        if (cardType == null || cardType < 0) {
            throw new BusinessException("请选择来源类型！");
        }
        if (cardType == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE && (userId == null || userId < 0)) {
            throw new BusinessException("请选择服务商");
        }
        boolean isFleet = oilCardSaveIn.getIsFleet() == null || oilCardSaveIn.getIsFleet() != 1;

        if (isFleet && (cardBalance == null || cardBalance < 0)) {
            throw new BusinessException("请输入理论金额！");
        }
        Long cardId = oilCardSaveIn.getCardId();
        boolean isUpdate = false;
        long tenantId = oilCardSaveIn.getTenantId();


        int oldCardType = -1;
        long oldCardBalance = 0;

        OilCardManagement oilCardManagement = null;
        if (cardType == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {

            ServiceInfo serviceInfo = iServiceInfoService.getServiceInfoByServiceUserId(userId);
            if (null == serviceInfo) {
                throw new BusinessException("服务商信息不存在,请联系管理员！");
            }
            if (null != serviceInfo.getServiceType() && serviceInfo.getServiceType().intValue() != 100) {
                TenantServiceRel tenantServiceRel = iTenantServiceRelService.getTenantServiceRel(tenantId, userId);
                if (tenantServiceRel == null) {
                    throw new BusinessException("未和该服务商有合作关系，不能添加相关服务商的油卡信息！");
                } else {
                    if (tenantServiceRel.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO) {
                        throw new BusinessException("该服务商的关系是无效，不能操作该油卡信息！");
                    }
                }
            }
        } else {
            oilCardSaveIn.setUserId(null);
        }
        List list = iOilCardManagementService.getOilCardManagementByCard(oilCarNum, tenantId);
        if (cardId != null && cardId > 0) {
            isUpdate = true;
            oilCardManagement = iOilCardManagementService.getById(cardId);
            if (null == oilCardManagement) {
                throw new BusinessException("油卡信息不存在");
            }
            if (null != oilCardManagement.getOilCardStatus() && oilCardManagement.getOilCardStatus().intValue() == 3) {
                throw new BusinessException("该油卡已被服务商回收");
            }
            if (org.apache.commons.lang3.StringUtils.isBlank(oilCardManagement.getOilCarNum())) {
                throw new BusinessException("数据有问题[油卡号为空],请尽快修复]");
            }
            if (!oilCardManagement.getOilCarNum().equals(oilCarNum) && list.size() > 0) {
                throw new BusinessException("油卡号已存在，请填写其他油卡号");
            }

            oldCardType = oilCardManagement.getCardType();
            oldCardBalance = oilCardManagement.getCardBalance() == null ? 0L : oilCardManagement.getCardBalance();
        } else {
            if (list.size() > 0) {
                throw new BusinessException("油卡号已存在，请填写其他油卡号");
            }
            OilCardInfo oilCardInfo = new OilCardInfo();
            oilCardInfo.setOilCardNum(oilCarNum);
            if (oilCardSaveIn.getIsSkipVerify() == null || !oilCardSaveIn.getIsSkipVerify()) {

                long count = iServiceProductOilsService.getOilCardInfo(oilCardInfo);
                if (count > 0) {
                    throw new BusinessException("该卡号为平台服务商拥有，不允许自行增加，请联系服务商处理。");
                }
            }
            oilCardManagement = new OilCardManagement();
        }

        BeanUtil.copyProperties(oilCardSaveIn, oilCardManagement);
        oilCardManagement.setLinkman(oilCardSaveIn.getServiceName());// 服务商名称

        if (!isUpdate) {
            oilCardManagement.setCreateDate(LocalDateTime.now());
            oilCardManagement.setTenantId(tenantId);
            oilCardManagement.setId(createOilCardId());
            oilCardManagement.setUserId(oilCardSaveIn.getUserId());
        }
        iOilCardManagementService.saveOrUpdates(oilCardManagement, isUpdate);

        this.batchSaveOilCardVehicleRel(oilCardSaveIn.getVehicleNumberStr(), oilCardManagement.getId(), oilCardManagement.getOilCarNum(), tenantId, isUpdate);

        if (isFleet) {

            long balance = 0;
            String logDesc = "";
            boolean isLog = false;
            if (isUpdate) {
                if (oldCardType == SysStaticDataEnum.OIL_CARD_TYPE.OWN && oilCardManagement.getCardType() == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE) {
                    balance = -oldCardBalance;
                    isLog = true;
                    logDesc = "[" + loginInfo.getName() + "]" + "减少了￥" + CommonUtil.getDoubleFormatLongMoney(-balance, 2);
                }
                if (oldCardType == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE && oilCardManagement.getCardType() == SysStaticDataEnum.OIL_CARD_TYPE.OWN) {
                    balance = oilCardManagement.getCardBalance();
                    isLog = true;
                    logDesc = "[" + loginInfo.getName() + "]" + "增加了￥" + CommonUtil.getDoubleFormatLongMoney(balance, 2);
                }
                if (oldCardType == SysStaticDataEnum.OIL_CARD_TYPE.OWN
                        && oilCardManagement.getCardType() == SysStaticDataEnum.OIL_CARD_TYPE.OWN
                        && oldCardBalance != oilCardManagement.getCardBalance().longValue()) {
                    balance = oilCardManagement.getCardBalance().longValue() - oldCardBalance;
                    isLog = true;
                    if (balance > 0) {
                        logDesc = "[" + loginInfo.getName() + "]" + "增加了￥" + CommonUtil.getDoubleFormatLongMoney(balance, 2);
                    } else {
                        logDesc = "[" + loginInfo.getName() + "]" + "减少了￥" + CommonUtil.getDoubleFormatLongMoney(-balance, 2);
                    }
                }
            } else if (oilCardManagement.getCardType() == SysStaticDataEnum.OIL_CARD_TYPE.OWN) {
                balance = oilCardManagement.getCardBalance().longValue();
                isLog = true;
                logDesc = "[" + loginInfo.getName() + "]" + "增加了￥" + CommonUtil.getDoubleFormatLongMoney(balance, 2);
            }
            if (isLog) {
                saveCardLog(oilCardManagement.getId(), balance, logDesc, null, accessToken);
            }
        }
        if (isUpdate) {
            sysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.OilCard, oilCardSaveIn.getId(), com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, "修改", loginInfo.getTenantId());// 添加日志
            // 添加日志

        } else {
            sysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.OilCard, oilCardManagement.getId(), com.youming.youche.commons.constant.SysOperLogConst.OperType.Add, "新增", loginInfo.getTenantId());// 添加日志

        }

        return oilCardManagement;
    }

    private void saveCardLog(Long id, long balance, String logDesc, Long orderId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        OilCardLog oilCardLog = new OilCardLog();
        oilCardLog.setCardId(id);
        oilCardLog.setLogDesc(logDesc);
        oilCardLog.setTenantId(loginInfo.getTenantId());
        oilCardLog.setUserId(loginInfo.getUserInfoId());
        oilCardLog.setOilFee(balance);
        if (orderId != null && orderId > 0) {
            oilCardLog.setOrderId(orderId);

            OrderScheduler orderScheduler = iOrderSchedulerService.getOrderScheduler(orderId);
            if (orderScheduler != null) {
                oilCardLog.setPlateNumber(orderScheduler.getPlateNumber());
                oilCardLog.setCarDriverMan(orderScheduler.getCarDriverMan());
            }
        }
        iOilCardLogService.saveOrdUpdate(oilCardLog, EnumConsts.OIL_LOG_TYPE.ADD_OR_REDUCE);
    }

    /**
     * 保存油卡车辆关系
     *
     * @param vehicleNumberStr
     * @param id
     * @param oilCarNum
     * @param tenantId
     */
    private void batchSaveOilCardVehicleRel(String vehicleNumberStr, Long id, String oilCarNum, long tenantId, boolean isUpdate) {
        OilCardVehicleRel rel = new OilCardVehicleRel();
        rel.setOilCardNum(oilCarNum);
        rel.setCardId(id);
        rel.setTenantId(tenantId);
        rel.setVehicleNumber(vehicleNumberStr);
        //  TODO  根据 油卡id  查询
        QueryWrapper<OilCardVehicleRel> wrapper = new QueryWrapper<>();
        wrapper.eq("card_id", id);
        OilCardVehicleRel oilCardVehicleRel = oilCardVehicleRelMapper.selectOne(wrapper);
        if (oilCardVehicleRel == null) { // 新增
            oilCardVehicleRelMapper.insert(rel);
        } else { // 修改
            QueryWrapper<OilCardVehicleRel> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", oilCardVehicleRel.getId());
            oilCardVehicleRelMapper.update(rel, queryWrapper);
        }
    }


    /**
     * 生成油卡号
     */
    static final long OILCARD_ID = 20000001L;

    public long createOilCardId() {
        return OILCARD_ID + redisUtil.incr(EnumConsts.RemoteCache.OILCARD_ID);
    }

    @Override
    public List getOilCardManagementByCard(String oilCarNum, long tenantId) {
        return baseMapper.getOilCardManagementByCard(oilCarNum, tenantId);
    }


    @Override
    public void deleteOilCardVehicleRelByCardId(String vehicleNumber, Long id, String oilCarNum, Long tenantId) {
        if (id == null || id <= 0) {
            //ceshi
            throw new BusinessException("油卡Id不能为空");
        }
        OilCardVehicleRel oilCardVehicleRel = new OilCardVehicleRel();
        oilCardVehicleRel.setCardId(id);
        oilCardVehicleRel.setOilCardNum(oilCarNum);
        oilCardVehicleRel.setVehicleNumber(vehicleNumber);
        oilCardVehicleRel.setTenantId(tenantId);
        iOilCardVehicleRelService.saveOrUpdate(oilCardVehicleRel);
    }


    @Override
    public List<OilCardManagement> getOilCardManagementMapList(Long tenantId) {

        return baseMapper.getOilCardManagementList(tenantId);
    }

    @Override
    public List<OilCardManagementDto> OilCardList(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);

        return baseMapper.OilCardList(loginInfo.getTenantId());
    }

    /**
     * 油卡导出
     *
     * @param accessToken
     * @param record
     * @param oilCardManagementVo
     */
    @Override
    @Async
    public void OilCardList1(String accessToken, ImportOrExportRecords record, OilCardManagementVo oilCardManagementVo) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        IPage<OilCardManagementDto> managementList = this.getManagementList(1, 9999, oilCardManagementVo, accessToken);
        List<OilCardManagementDto> records = managementList.getRecords();
//        List<OilCardManagementDto> oilCardManagementDtos = new ArrayList<>();
//        for (OilCardManagementDto oilCardManagementDto : records) {
//            oilCardManagementDto.setCardTypeName(oilCardManagementDto.getCardType()==null?"":sysStaticDataService.
//                    getSysStaticDatas("OIL_CARD_TYPE",oilCardManagementDto.getCardType()));
//            oilCardManagementDtos.add(oilCardManagementDto);
//        }
        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{"油卡卡号", "来源类型", "服务商名称", "现绑定车辆", "理论余额"};
            resourceFild = new String[]{"getOilCarNum", "getCardTypeName", "getCompanyName", "getBindVehicle", "getCardBalaceStr"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(records, showName, resourceFild, OilCardManagementDto.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "油卡管理.xlsx", inputStream.available());
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

    /**
     * 油卡管理导入
     *
     * @param byteBuffer
     * @param record
     * @param accessToken
     */
    @Async
    @Transactional
    @Override
    public void batchImport(byte[] byteBuffer, ImportOrExportRecords record, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<OilCardManagementDto> failureList = new ArrayList<>();
        InputStream inputStream = new ByteArrayInputStream(byteBuffer);
        List<List<String>> lists = new ArrayList<>();
        try {
            lists = getExcelContent(inputStream, 1, (ExcelFilesVaildate[]) null);

            //移除表头行
            List<String> listsRows = lists.remove(0);
            int maxNum = 300;
            if (lists.size() == 0) {
                throw new BusinessException("导入的数量为0，导入失败");
            }
            if (lists.size() > maxNum) {
                throw new BusinessException("导入不支持超过" + maxNum + "条数据，您当前条数[" + lists.size() + "]");
            }
            int j = 1;
            int success = 0;
            for (List<String> l : lists) {
                StringBuffer reasonFailure = new StringBuffer();
                OilCardManagementVo oilCardManagementVo = new OilCardManagementVo();
                OilCardManagementDto oilCardManagementDto = new OilCardManagementDto();
                j++;
                String tmpMsg = "";
                String getOilCarNum = l.get(0);// 油卡卡号
                String getCardType = l.get(1);// 油卡类型
                String getCompanyName = l.get(2);// 服务商
                String getBindVehicle = l.get(3);// 现绑定车辆
                String getCardBalance = l.get(4);//理论余额
                if (getOilCarNum == null || StringUtils.isBlank(getOilCarNum)) {
                    reasonFailure.append("第" + j + "油卡卡号未输入!");
                } else {
                    oilCardManagementVo.setOilCarNum(getOilCarNum);
                }
                if (getCardType == null || StringUtils.isBlank(getCardType)) {
                    reasonFailure.append("第" + j + "油卡类型未输入!");
                } else {
                    if (getCardType.equals("供应商油卡")) {
                        oilCardManagementVo.setCardType(1);
                    } else if (getCardType.equals("自购油卡")) {
                        oilCardManagementVo.setCardType(2);
                        if (getCompanyName != null && StringUtils.isNotEmpty(getCompanyName)) {
                            reasonFailure.append("第" + j + "行，当选择自购油卡的时候不能填写服务商；");
                        }
                    } else if (getCardType.equals("客户油卡")) {
                        oilCardManagementVo.setCardType(3);
                        if (getCompanyName != null && StringUtils.isNotEmpty(getCompanyName)) {
                            reasonFailure.append("第" + j + "行，当选择自购油卡的时候不能填写服务商；");
                        }
                    } else if (getCardType.equals("扫码加油")) {
                        oilCardManagementVo.setCardType(4);
                        if (getCompanyName != null && StringUtils.isNotEmpty(getCompanyName)) {
                            reasonFailure.append("第" + j + "行，当选择扫码加油的时候不能填写服务商；");
                        }
                    } else {
                        reasonFailure.append("第" + j + "行，油卡类型输入有勿；");
                    }
                }
                // 供应商类型  服务商 名称不能为空
                if (getCardType.equals("供应商油卡") && (getCompanyName == null || StringUtils.isBlank(getCompanyName))) {
                    reasonFailure.append("第" + j + "行，油卡类型为供应商时 ,服务商名称不能为空!");
                }

                // 等于 供应商油卡 而且服务商不为空
                if (getCardType.equals("供应商油卡") && (getCompanyName != null && StringUtils.isNotEmpty(getCompanyName))) {
                    // TODO 查詢系統 是否有此服务商
                    ServiceInfoFleetDto dto = new ServiceInfoFleetDto();
                    Page<ServiceInfoFleetVo> serviceInfoFleetVoPage = iServiceInfoService.queryServiceInfoPage(999, 1, dto, accessToken);
                    List<ServiceInfoFleetVo> records1 = serviceInfoFleetVoPage.getRecords();
                    if (records1 == null) {
                        reasonFailure.append("第" + j + "行，系统中没有此服务商信息；");
                    } else {
                        List<ServiceInfoFleetVo> serviceName1 = records1.stream()
                                .filter(ServiceInfoFleetVo -> ServiceInfoFleetVo.getServiceName() != null)
                                .filter(ServiceInfoFleetVo ->
                                        ServiceInfoFleetVo.getServiceName().equals(getCompanyName) && ServiceInfoFleetVo.getServiceType() == 1
                                                && ServiceInfoFleetVo.getState()==1).collect(Collectors.toList()); // 状态为有效的
                        if (serviceName1.size() <= 0) {
                            reasonFailure.append("第" + j + "行，系统中没有此服务商信息；");
                        } else {
                            ServiceInfoFleetVo facilitatorVo = serviceName1.get(0);
                            oilCardManagementVo.setUserId(facilitatorVo.getServiceUserId()); // 服务商id
                            oilCardManagementVo.setCompanyName(facilitatorVo.getServiceName()); // 服务商名称
                        }
                    }
                }


                if (getCardBalance == null || StringUtils.isBlank(getCardBalance)) {
                    reasonFailure.append("第" + j + "理论余额未输入!)");
                } else {
                    oilCardManagementVo.setCardBalance(Long.parseLong(getCardBalance));
                }
                QueryWrapper<OilCardManagement> wrapper = new QueryWrapper<>();
                wrapper.eq("oil_car_num", getOilCarNum)
                        .eq("tenant_id", loginInfo.getTenantId());
                OilCardManagement oilCardManagement = baseMapper.selectOne(wrapper);
                if (oilCardManagement != null) {
                    reasonFailure.append("第" + j + "油卡重复!");
                }

                // TODO 新需求 20220 -5-26 车牌可以为空
                if (getBindVehicle != null && StringUtils.isNotEmpty(getBindVehicle)) {
                    // TODO查询系统中是否有这个车辆
                    VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(getBindVehicle);
                    if (vehicleDataInfo == null) {
                        reasonFailure.append("第" + j + "行，系统中没有此车辆信息；");
                    }
                }
                OilCardSaveIn oilCardSaveIn = new OilCardSaveIn();
                oilCardSaveIn.setOilCarNum(oilCardManagementVo.getOilCarNum()); //油卡卡号
                oilCardSaveIn.setCardType(oilCardManagementVo.getCardType());// 油卡类型
                if (oilCardManagementVo.getCardBalance()!=null){
                    oilCardSaveIn.setCardBalance(oilCardManagementVo.getCardBalance() * 100);//理论余额
                }
                oilCardSaveIn.setVehicleNumberStr(getBindVehicle);
                oilCardSaveIn.setOilCardStatus(1);//油卡类型 默认有效1
                oilCardSaveIn.setServiceName(oilCardManagementVo.getCompanyName());// 服务商名称
                oilCardSaveIn.setUserId(oilCardManagementVo.getUserId()); // 服务商id
                if (StrUtil.isEmpty(reasonFailure)) {
                    this.saveIn(oilCardSaveIn, accessToken);
                    LambdaQueryWrapper<OilCardManagement> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(OilCardManagement::getOilCarNum, getOilCarNum);
                    OilCardManagement oilCardManagement1 = baseMapper.selectOne(queryWrapper);
                    if (oilCardManagement1 != null) {
                        this.batchSaveOilCardVehicleRel(getBindVehicle, oilCardManagement1.getId(), getOilCarNum, loginInfo.getTenantId(), true);// 油卡和车俩表添加
                    }
                    //返回成功数量
                    success++;
                } else {
                    oilCardManagementDto.setReasonFailure(reasonFailure.toString());
                    oilCardManagementDto.setOilCarNum(oilCardManagementVo.getOilCarNum());
                    failureList.add(oilCardManagementDto);
                }
            }
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(failureList)) {
                String[] showName;
                String[] resourceFild;
                showName = new String[]{"油卡卡号", "导入失败原因"
//                    "油卡类型", "服务商名称", "现绑定车辆", "余额"
                };
                resourceFild = new String[]{"getOilCarNum", "getReasonFailure"
//                    "getCompanyName", "getBindVehicle", "getCardBalance"
                };
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(failureList, showName, resourceFild, OilCardManagementDto.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream1 = new ByteArrayInputStream(b);
                //上传文件
                FastDFSHelper client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream1, "油卡管理.xlsx", inputStream1.available());
                os.close();
                inputStream.close();
                inputStream1.close();
                record.setFailureUrl(failureExcelPath);
                if (success > 0) {
                    record.setRemarks(success + "条成功" + failureList.size() + "条失败");
                    record.setState(2);
                }
                if (success == 0) {
                    record.setRemarks(failureList.size() + "条失败");
                    record.setState(4);
                }
            } else {
                record.setRemarks(success + "条成功");
                record.setState(3);
            }
            importOrExportRecordsService.update(record);
        } catch (Exception e) {
            record.setState(5);
            record.setRemarks("导入失败，请确认模版数据是否正确！");
            record.setFailureReason("导入失败，请确认模版数据是否正确！");
            importOrExportRecordsService.update(record);
            e.printStackTrace();

        }
    }

    public Map<String, String> getOilCardVehicleRelList(List<String> oilCardNums, Long tenantId) {

        Map<String, String> out = new HashMap<>();
        if (oilCardNums == null || oilCardNums.size() == 0) {
            return out;
        }

        List<OilCardVehicleRel> list = baseMapper.getOilCardVehicleRelList(oilCardNums, tenantId);
        if (list != null && list.size() > 0) {
            for (OilCardVehicleRel map : list) {
//                String oilCardNum = DataFormat.getStringKey(map, "oilCardNum");
//                String plateNumber = DataFormat.getStringKey(map, "plateNumber");
                String oilCardNum =map.getOilCardNum();
                String plateNumber = map.getVehicleNumber();
                if (StringUtils.isNotBlank(oilCardNum)) {
                    out.put(oilCardNum, plateNumber);
                }
            }
        }
        return out;
    }

    List<List<String>> getExcelContent(InputStream inputStream, int beginRow, ExcelFilesVaildate[] validates) throws Exception {
        DataFormatter dataFormatter = new DataFormatter();
        List<List<String>> fileContent = new ArrayList();
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        int rows = sheet.getLastRowNum() + 1;
        if (rows >= 1000 + beginRow) {
            throw new BusinessException("excel文件的行数超过系统允许导入最大行数：" + 1000);
        } else {
            if (rows >= beginRow) {
                List rowList = null;
                Row row = null;

                for (int i = beginRow - 1; i < rows; ++i) {
                    row = sheet.getRow(i);
                    rowList = new ArrayList();
                    fileContent.add(rowList);
                    if (row != null) {
                        int cells = row.getLastCellNum();
                        if (cells > 200) {
                            throw new BusinessException("文件列数超过200列，请检查文件！");
                        }

                        for (int j = 0; j < cells; ++j) {
                            Cell cell = row.getCell(j);
                            String cellValue = "";
                            if (cell != null) {
                                log.debug("Reading Excel File row:" + i + ", col:" + j + " cellType:" + cell.getCellType());
                                switch (cell.getCellType()) {
                                    case 0:
                                        if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
                                            cellValue = com.youming.youche.util.DateUtil.formatDateByFormat(cell.getDateCellValue(), com.youming.youche.util.DateUtil.DATETIME_FORMAT);
                                        } else {
                                            cellValue = dataFormatter.formatCellValue(cell);
                                        }
                                        break;
                                    case 1:
                                        cellValue = cell.getStringCellValue();
                                        break;
                                    case 2:
                                        cellValue = String.valueOf(cell.getNumericCellValue());
                                        break;
                                    case 3:
                                        cellValue = "";
                                        break;
                                    case 4:
                                        cellValue = String.valueOf(cell.getBooleanCellValue());
                                        break;
                                    case 5:
                                        cellValue = String.valueOf(cell.getErrorCellValue());
                                }
                            }

                            if (validates != null && validates.length > j) {
                                if (cellValue == null) {
                                    throw new BusinessException("第" + (i + beginRow - 1) + "行,第" + (j + 1) + "列数据校验出错：" + validates[j].getErrorMsg());
                                }

                                Pattern p = Pattern.compile(validates[j].getPattern());
                                Matcher m = p.matcher(cellValue);
                                if (!m.matches()) {
                                    throw new BusinessException("第" + (i + beginRow - 1) + "行,第" + (j + 1) + "列数据校验出错：" + validates[j].getErrorMsg());
                                }
                            }

                            rowList.add(cellValue);
                        }
                    }
                }
            }

            return fileContent;
        }
    }

    @Override
    public void loseOilCard(Long serviceUserId, LoginInfo user) {
        LambdaQueryWrapper<OilCardManagement> lambda = new QueryWrapper<OilCardManagement>().lambda();
        lambda.eq(OilCardManagement::getUserId, serviceUserId)
                .eq(OilCardManagement::getOilCardStatus, STATUS_YES)
                .eq(OilCardManagement::getTenantId, user.getTenantId());
        List<OilCardManagement> list = this.list(lambda);
        if (list != null && list.size() > 0) {
            for (OilCardManagement oilCardManagement : list) {
                oilCardManagement.setOilCardStatus(SysStaticDataEnum.OILCARD_STATUS.STATUS_NO);
                this.updateById(oilCardManagement);
                //操作日志
                sysOperLogService.saveSysOperLog(user, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.OilCard, oilCardManagement.getId(), com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, "失效");
            }
        }
    }


    /**
     * 记录日志
     */
    private void saveSysOperLog(com.youming.youche.commons.constant.SysOperLogConst.BusiCode busiCode, com.youming.youche.commons.constant.SysOperLogConst.OperType operType, String operCommet, String accessToken, Long busid) {
        SysOperLog operLog = new SysOperLog();
        operLog.setBusiCode(busiCode.getCode());
        operLog.setBusiName(busiCode.getName());
        operLog.setBusiId(busid);
        operLog.setOperType(operType.getCode());
        operLog.setOperTypeName(operType.getName());
        operLog.setOperComment(operCommet);
        sysOperLogService.save(operLog, accessToken);
    }

    @Override
    public void saveOrUpdates(OilCardManagement oilCardManagement, boolean isUpdate) {

        if (isUpdate) {
//            baseMapper.updateById(oilCardManagement);
            this.saveOrUpdate(oilCardManagement);
        } else {
            baseMapper.insert(oilCardManagement);
        }
    }

    @Override
    public List<com.youming.youche.market.domain.youka.ServiceInfo> getServiceInfo(Integer cardType, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        com.youming.youche.market.domain.youka.ServiceInfo serviceInfo = new com.youming.youche.market.domain.youka.ServiceInfo();
        List<com.youming.youche.market.domain.youka.ServiceInfo> list = new ArrayList<>();
        ServiceInfoFleetDto dto = new ServiceInfoFleetDto();
        //查询pc端 服务商列表
        Page<ServiceInfoFleetVo> serviceInfoFleetVoPage = iServiceInfoService.queryServiceInfoPage(999, 1, dto, accessToken);
        List<ServiceInfoFleetVo> records1 = serviceInfoFleetVoPage.getRecords();
        if (records1!=null){
            List<ServiceInfoFleetVo> collect = records1.stream().filter(serviceInfoFleetVo -> serviceInfoFleetVo.getServiceType() == 1)
                    .collect(Collectors.toList());
            for (ServiceInfoFleetVo vo : collect) {
                BeanUtil.copyProperties(vo, serviceInfo);
                serviceInfo.setId(vo.getServiceUserId());
                serviceInfo.setServiceName(vo.getServiceName());
                list.add(serviceInfo);
            }
        }
        return baseMapper.getServiceInfo(cardType, loginInfo.getTenantId());
    }

    /**
     * 40035
     * 司机小程序
     * niejiewei
     * 根据车牌查找有效的油卡集合
     *
     * @param plateNumber
     * @return
     */
    @Override
    public List<OilCardManagementOutDto> getOilCardsByPlateNumber(String plateNumber, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (org.apache.commons.lang3.StringUtils.isBlank(plateNumber)) {
            throw new BusinessException("请传入车牌号！");
        }
        List<OilCardManagementOutDto> byPlateNumber = baseMapper.findByPlateNumber(plateNumber, loginInfo.getTenantId(), STATUS_YES);
        for (OilCardManagementOutDto oil:byPlateNumber) {
            oil.setOilCardStatusName( oil.getOilCardStatus() == null ?"" :getSysStaticData("OILCARD_STATUS"  ,String.valueOf(oil.getOilCardStatus())).getCodeName());
            oil.setCardTypeName(oil.getCardType() == null ? "" : getSysStaticData("OIL_CARD_TYPE", String.valueOf(oil.getCardType())).getCodeName());
            oil.setOilCardTypeName( oil.getOilCardType() == null ? "" :getSysStaticData("SERV_OIL_CARD_TYPE", String.valueOf(oil.getOilCardType())).getCodeName());
        }
        return byPlateNumber;
    }

    @Override
    public List<OilCardManagement> getCardInfoByCardNum(String oilCardNum, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        QueryWrapper<OilCardManagement> wrapper = new QueryWrapper<>();
        wrapper.eq("oil_car_num", oilCardNum)
                .eq("tenant_id", loginInfo.getTenantId())
                .eq("oil_card_status", STATUS_YES);
        List<OilCardManagement> oilCardManagements = baseMapper.selectList(wrapper);
        for (OilCardManagement oil:oilCardManagements) {
            oil.setOilCardStatusName( oil.getOilCardStatus() == null ?"" :getSysStaticData("OILCARD_STATUS"  ,String.valueOf(oil.getOilCardStatus())).getCodeName());
            oil.setCardTypeName(oil.getCardType() == null ? "" : getSysStaticData("OIL_CARD_TYPE", String.valueOf(oil.getCardType())).getCodeName());
            oil.setOilCardTypeName( oil.getOilCardType() == null ? "" :getSysStaticData("SERV_OIL_CARD_TYPE", String.valueOf(oil.getOilCardType())).getCodeName());
        }
        return oilCardManagements;
    }


//    public void executeUpdate(String sql,Map map)throws Exception{
//        Session session = SysContexts.getEntityManager();
//        Query exec = session.createSQLQuery(sql).setProperties(map);
//        exec.executeUpdate();
//    }
public SysStaticData getSysStaticData(String codeType, String codeValue) {
    List<SysStaticData> list = (List<SysStaticData>) redisUtil.get(SysStaticDataEnum.SYS_STATIC_DATA_NAME.concat(codeType));
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
