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
                oilCardManagement.getId(), SysOperLogConst.OperType.Add, "??????");
        return true;
    }

    @Override
    public boolean upd(OilCardManagement oilCardManagement, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        oilCardManagement.setTenantId(loginInfo.getTenantId());
        baseMapper.updateById(oilCardManagement);
        sysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.OilCard,
                oilCardManagement.getId(), SysOperLogConst.OperType.Update, "??????");
        return true;
    }

    @Override
    public boolean saveOilCharge(String oilCarNum, long objToLongMul100, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<OilCardManagement> list = baseMapper.findByOilCardNum(oilCarNum, loginInfo.getTenantId());
        if (list == null || list.size() == 0) {
            throw new BusinessException("?????????????????????????????????");
        }
        if (list.size() > 1) {
            throw new BusinessException("???????????????????????????????????????????????????????????????????????????");
        }
        OilCardManagement oilCard = list.get(0);

//        if (SysStaticDataEnum.OIL_CARD_TYPE.CUSTOMER == oilCard.getCardType()) {
//            throw new BusinessException("?????????????????????????????????");
//        }
        //?????????????????????????????????
        if (SERVICE == oilCard.getCardType()) {
            ServiceInfoVo serviceInfo = null;
            if (oilCard.getUserId() != null && oilCard.getUserId() > 0) {
//                serviceInfo = serviceInfoMapper.selectById(oilCard.getUserId());
                serviceInfo = iServiceInfoService.getServiceInfo(oilCard.getUserId(), loginInfo);
            }
            if (serviceInfo == null) {
                throw new BusinessException("??????????????????????????????");
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
            throw new BusinessException("????????????????????????????????????");
        }
//        else if (SysStaticDataEnum.OIL_CARD_TYPE.OWN == oilCard.getCardType()) {
//            oilCard.setCardBalance(oilCard.getCardBalance() + objToLongMul100);
//            baseMapper.updateById(oilCard);
//        }
        sysOperLogService.saveSysOperLog(loginInfo,
                com.youming.youche.commons.constant.SysOperLogConst.BusiCode.OilCard, oilCard.getId(),
                com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, "????????????"+objToLongMul100/100.00+"???",
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
            throw new BusinessException("??????????????????");
        }
        if (oilCardStatus == null || oilCardStatus < 0) {
            throw new BusinessException("?????????????????????");
        }
        if (cardType == null || cardType < 0) {
            throw new BusinessException("????????????????????????");
        }
        if (cardType == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE && (userId == null || userId < 0)) {
            throw new BusinessException("??????????????????");
        }
        boolean isFleet = oilCardSaveIn.getIsFleet() == null || oilCardSaveIn.getIsFleet() != 1;

        if (isFleet && (cardBalance == null || cardBalance < 0)) {
            throw new BusinessException("????????????????????????");
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
                throw new BusinessException("????????????????????????,?????????????????????");
            }
            if (null != serviceInfo.getServiceType() && serviceInfo.getServiceType().intValue() != 100) {
                TenantServiceRel tenantServiceRel = iTenantServiceRelService.getTenantServiceRel(tenantId, userId);
                if (tenantServiceRel == null) {
                    throw new BusinessException("?????????????????????????????????????????????????????????????????????????????????");
                } else {
                    if (tenantServiceRel.getState() == SysStaticDataEnum.SYS_STATE_DESC.STATE_NO) {
                        throw new BusinessException("???????????????????????????????????????????????????????????????");
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
                throw new BusinessException("?????????????????????");
            }
            if (null != oilCardManagement.getOilCardStatus() && oilCardManagement.getOilCardStatus().intValue() == 3) {
                throw new BusinessException("??????????????????????????????");
            }
            if (org.apache.commons.lang3.StringUtils.isBlank(oilCardManagement.getOilCarNum())) {
                throw new BusinessException("???????????????[???????????????],???????????????]");
            }
            if (!oilCardManagement.getOilCarNum().equals(oilCarNum) && list.size() > 0) {
                throw new BusinessException("?????????????????????????????????????????????");
            }

            oldCardType = oilCardManagement.getCardType();
            oldCardBalance = oilCardManagement.getCardBalance() == null ? 0L : oilCardManagement.getCardBalance();
        } else {
            if (list.size() > 0) {
                throw new BusinessException("?????????????????????????????????????????????");
            }
            OilCardInfo oilCardInfo = new OilCardInfo();
            oilCardInfo.setOilCardNum(oilCarNum);
            if (oilCardSaveIn.getIsSkipVerify() == null || !oilCardSaveIn.getIsSkipVerify()) {

                long count = iServiceProductOilsService.getOilCardInfo(oilCardInfo);
                if (count > 0) {
                    throw new BusinessException("???????????????????????????????????????????????????????????????????????????????????????");
                }
            }
            oilCardManagement = new OilCardManagement();
        }

        BeanUtil.copyProperties(oilCardSaveIn, oilCardManagement);
        oilCardManagement.setLinkman(oilCardSaveIn.getServiceName());// ???????????????

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
                    logDesc = "[" + loginInfo.getName() + "]" + "????????????" + CommonUtil.getDoubleFormatLongMoney(-balance, 2);
                }
                if (oldCardType == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE && oilCardManagement.getCardType() == SysStaticDataEnum.OIL_CARD_TYPE.OWN) {
                    balance = oilCardManagement.getCardBalance();
                    isLog = true;
                    logDesc = "[" + loginInfo.getName() + "]" + "????????????" + CommonUtil.getDoubleFormatLongMoney(balance, 2);
                }
                if (oldCardType == SysStaticDataEnum.OIL_CARD_TYPE.OWN
                        && oilCardManagement.getCardType() == SysStaticDataEnum.OIL_CARD_TYPE.OWN
                        && oldCardBalance != oilCardManagement.getCardBalance().longValue()) {
                    balance = oilCardManagement.getCardBalance().longValue() - oldCardBalance;
                    isLog = true;
                    if (balance > 0) {
                        logDesc = "[" + loginInfo.getName() + "]" + "????????????" + CommonUtil.getDoubleFormatLongMoney(balance, 2);
                    } else {
                        logDesc = "[" + loginInfo.getName() + "]" + "????????????" + CommonUtil.getDoubleFormatLongMoney(-balance, 2);
                    }
                }
            } else if (oilCardManagement.getCardType() == SysStaticDataEnum.OIL_CARD_TYPE.OWN) {
                balance = oilCardManagement.getCardBalance().longValue();
                isLog = true;
                logDesc = "[" + loginInfo.getName() + "]" + "????????????" + CommonUtil.getDoubleFormatLongMoney(balance, 2);
            }
            if (isLog) {
                saveCardLog(oilCardManagement.getId(), balance, logDesc, null, accessToken);
            }
        }
        if (isUpdate) {
            sysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.OilCard, oilCardSaveIn.getId(), com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, "??????", loginInfo.getTenantId());// ????????????
            // ????????????

        } else {
            sysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.OilCard, oilCardManagement.getId(), com.youming.youche.commons.constant.SysOperLogConst.OperType.Add, "??????", loginInfo.getTenantId());// ????????????

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
     * ????????????????????????
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
        //  TODO  ?????? ??????id  ??????
        QueryWrapper<OilCardVehicleRel> wrapper = new QueryWrapper<>();
        wrapper.eq("card_id", id);
        OilCardVehicleRel oilCardVehicleRel = oilCardVehicleRelMapper.selectOne(wrapper);
        if (oilCardVehicleRel == null) { // ??????
            oilCardVehicleRelMapper.insert(rel);
        } else { // ??????
            QueryWrapper<OilCardVehicleRel> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", oilCardVehicleRel.getId());
            oilCardVehicleRelMapper.update(rel, queryWrapper);
        }
    }


    /**
     * ???????????????
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
            throw new BusinessException("??????Id????????????");
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
     * ????????????
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
            showName = new String[]{"????????????", "????????????", "???????????????", "???????????????", "????????????"};
            resourceFild = new String[]{"getOilCarNum", "getCardTypeName", "getCompanyName", "getBindVehicle", "getCardBalaceStr"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(records, showName, resourceFild, OilCardManagementDto.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //????????????
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "????????????.xlsx", inputStream.available());
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
     * ??????????????????
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

            //???????????????
            List<String> listsRows = lists.remove(0);
            int maxNum = 300;
            if (lists.size() == 0) {
                throw new BusinessException("??????????????????0???????????????");
            }
            if (lists.size() > maxNum) {
                throw new BusinessException("?????????????????????" + maxNum + "???????????????????????????[" + lists.size() + "]");
            }
            int j = 1;
            int success = 0;
            for (List<String> l : lists) {
                StringBuffer reasonFailure = new StringBuffer();
                OilCardManagementVo oilCardManagementVo = new OilCardManagementVo();
                OilCardManagementDto oilCardManagementDto = new OilCardManagementDto();
                j++;
                String tmpMsg = "";
                String getOilCarNum = l.get(0);// ????????????
                String getCardType = l.get(1);// ????????????
                String getCompanyName = l.get(2);// ?????????
                String getBindVehicle = l.get(3);// ???????????????
                String getCardBalance = l.get(4);//????????????
                if (getOilCarNum == null || StringUtils.isBlank(getOilCarNum)) {
                    reasonFailure.append("???" + j + "?????????????????????!");
                } else {
                    oilCardManagementVo.setOilCarNum(getOilCarNum);
                }
                if (getCardType == null || StringUtils.isBlank(getCardType)) {
                    reasonFailure.append("???" + j + "?????????????????????!");
                } else {
                    if (getCardType.equals("???????????????")) {
                        oilCardManagementVo.setCardType(1);
                    } else if (getCardType.equals("????????????")) {
                        oilCardManagementVo.setCardType(2);
                        if (getCompanyName != null && StringUtils.isNotEmpty(getCompanyName)) {
                            reasonFailure.append("???" + j + "????????????????????????????????????????????????????????????");
                        }
                    } else if (getCardType.equals("????????????")) {
                        oilCardManagementVo.setCardType(3);
                        if (getCompanyName != null && StringUtils.isNotEmpty(getCompanyName)) {
                            reasonFailure.append("???" + j + "????????????????????????????????????????????????????????????");
                        }
                    } else if (getCardType.equals("????????????")) {
                        oilCardManagementVo.setCardType(4);
                        if (getCompanyName != null && StringUtils.isNotEmpty(getCompanyName)) {
                            reasonFailure.append("???" + j + "????????????????????????????????????????????????????????????");
                        }
                    } else {
                        reasonFailure.append("???" + j + "?????????????????????????????????");
                    }
                }
                // ???????????????  ????????? ??????????????????
                if (getCardType.equals("???????????????") && (getCompanyName == null || StringUtils.isBlank(getCompanyName))) {
                    reasonFailure.append("???" + j + "????????????????????????????????? ,???????????????????????????!");
                }

                // ?????? ??????????????? ????????????????????????
                if (getCardType.equals("???????????????") && (getCompanyName != null && StringUtils.isNotEmpty(getCompanyName))) {
                    // TODO ???????????? ?????????????????????
                    ServiceInfoFleetDto dto = new ServiceInfoFleetDto();
                    Page<ServiceInfoFleetVo> serviceInfoFleetVoPage = iServiceInfoService.queryServiceInfoPage(999, 1, dto, accessToken);
                    List<ServiceInfoFleetVo> records1 = serviceInfoFleetVoPage.getRecords();
                    if (records1 == null) {
                        reasonFailure.append("???" + j + "??????????????????????????????????????????");
                    } else {
                        List<ServiceInfoFleetVo> serviceName1 = records1.stream()
                                .filter(ServiceInfoFleetVo -> ServiceInfoFleetVo.getServiceName() != null)
                                .filter(ServiceInfoFleetVo ->
                                        ServiceInfoFleetVo.getServiceName().equals(getCompanyName) && ServiceInfoFleetVo.getServiceType() == 1
                                                && ServiceInfoFleetVo.getState()==1).collect(Collectors.toList()); // ??????????????????
                        if (serviceName1.size() <= 0) {
                            reasonFailure.append("???" + j + "??????????????????????????????????????????");
                        } else {
                            ServiceInfoFleetVo facilitatorVo = serviceName1.get(0);
                            oilCardManagementVo.setUserId(facilitatorVo.getServiceUserId()); // ?????????id
                            oilCardManagementVo.setCompanyName(facilitatorVo.getServiceName()); // ???????????????
                        }
                    }
                }


                if (getCardBalance == null || StringUtils.isBlank(getCardBalance)) {
                    reasonFailure.append("???" + j + "?????????????????????!)");
                } else {
                    oilCardManagementVo.setCardBalance(Long.parseLong(getCardBalance));
                }
                QueryWrapper<OilCardManagement> wrapper = new QueryWrapper<>();
                wrapper.eq("oil_car_num", getOilCarNum)
                        .eq("tenant_id", loginInfo.getTenantId());
                OilCardManagement oilCardManagement = baseMapper.selectOne(wrapper);
                if (oilCardManagement != null) {
                    reasonFailure.append("???" + j + "????????????!");
                }

                // TODO ????????? 20220 -5-26 ??????????????????
                if (getBindVehicle != null && StringUtils.isNotEmpty(getBindVehicle)) {
                    // TODO????????????????????????????????????
                    VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(getBindVehicle);
                    if (vehicleDataInfo == null) {
                        reasonFailure.append("???" + j + "???????????????????????????????????????");
                    }
                }
                OilCardSaveIn oilCardSaveIn = new OilCardSaveIn();
                oilCardSaveIn.setOilCarNum(oilCardManagementVo.getOilCarNum()); //????????????
                oilCardSaveIn.setCardType(oilCardManagementVo.getCardType());// ????????????
                if (oilCardManagementVo.getCardBalance()!=null){
                    oilCardSaveIn.setCardBalance(oilCardManagementVo.getCardBalance() * 100);//????????????
                }
                oilCardSaveIn.setVehicleNumberStr(getBindVehicle);
                oilCardSaveIn.setOilCardStatus(1);//???????????? ????????????1
                oilCardSaveIn.setServiceName(oilCardManagementVo.getCompanyName());// ???????????????
                oilCardSaveIn.setUserId(oilCardManagementVo.getUserId()); // ?????????id
                if (StrUtil.isEmpty(reasonFailure)) {
                    this.saveIn(oilCardSaveIn, accessToken);
                    LambdaQueryWrapper<OilCardManagement> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(OilCardManagement::getOilCarNum, getOilCarNum);
                    OilCardManagement oilCardManagement1 = baseMapper.selectOne(queryWrapper);
                    if (oilCardManagement1 != null) {
                        this.batchSaveOilCardVehicleRel(getBindVehicle, oilCardManagement1.getId(), getOilCarNum, loginInfo.getTenantId(), true);// ????????????????????????
                    }
                    //??????????????????
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
                showName = new String[]{"????????????", "??????????????????"
//                    "????????????", "???????????????", "???????????????", "??????"
                };
                resourceFild = new String[]{"getOilCarNum", "getReasonFailure"
//                    "getCompanyName", "getBindVehicle", "getCardBalance"
                };
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(failureList, showName, resourceFild, OilCardManagementDto.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream1 = new ByteArrayInputStream(b);
                //????????????
                FastDFSHelper client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream1, "????????????.xlsx", inputStream1.available());
                os.close();
                inputStream.close();
                inputStream1.close();
                record.setFailureUrl(failureExcelPath);
                if (success > 0) {
                    record.setRemarks(success + "?????????" + failureList.size() + "?????????");
                    record.setState(2);
                }
                if (success == 0) {
                    record.setRemarks(failureList.size() + "?????????");
                    record.setState(4);
                }
            } else {
                record.setRemarks(success + "?????????");
                record.setState(3);
            }
            importOrExportRecordsService.update(record);
        } catch (Exception e) {
            record.setState(5);
            record.setRemarks("???????????????????????????????????????????????????");
            record.setFailureReason("???????????????????????????????????????????????????");
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
            throw new BusinessException("excel??????????????????????????????????????????????????????" + 1000);
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
                            throw new BusinessException("??????????????????200????????????????????????");
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
                                    throw new BusinessException("???" + (i + beginRow - 1) + "???,???" + (j + 1) + "????????????????????????" + validates[j].getErrorMsg());
                                }

                                Pattern p = Pattern.compile(validates[j].getPattern());
                                Matcher m = p.matcher(cellValue);
                                if (!m.matches()) {
                                    throw new BusinessException("???" + (i + beginRow - 1) + "???,???" + (j + 1) + "????????????????????????" + validates[j].getErrorMsg());
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
                //????????????
                sysOperLogService.saveSysOperLog(user, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.OilCard, oilCardManagement.getId(), com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, "??????");
            }
        }
    }


    /**
     * ????????????
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
        //??????pc??? ???????????????
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
     * ???????????????
     * niejiewei
     * ???????????????????????????????????????
     *
     * @param plateNumber
     * @return
     */
    @Override
    public List<OilCardManagementOutDto> getOilCardsByPlateNumber(String plateNumber, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (org.apache.commons.lang3.StringUtils.isBlank(plateNumber)) {
            throw new BusinessException("?????????????????????");
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
