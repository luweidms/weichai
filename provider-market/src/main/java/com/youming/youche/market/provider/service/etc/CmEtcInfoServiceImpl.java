package com.youming.youche.market.provider.service.etc;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.api.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.util.StringUtil;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.domain.SysUser;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.IAccountStatementService;
import com.youming.youche.finance.domain.AccountStatement;
import com.youming.youche.market.api.etc.ICmEtcInfoService;
import com.youming.youche.market.api.etc.IEtcMaintainService;
import com.youming.youche.market.api.etc.etcutil.IOperationEtcService;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.domain.etc.CmEtcInfo;
import com.youming.youche.market.domain.etc.EtcMaintain;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.market.dto.etc.CmEtcInfoDto;
import com.youming.youche.market.dto.etc.CmEtcInfoOutDto;
import com.youming.youche.market.dto.etc.EtcOrderOutDto;
import com.youming.youche.market.provider.mapper.etc.CmEtcInfoMapper;
import com.youming.youche.market.provider.mapper.etc.EtcMaintainMapper;
import com.youming.youche.market.provider.utis.SysStaticDataRedisUtils;
import com.youming.youche.market.vo.etc.CalculatedEtcFeeVo;
import com.youming.youche.market.vo.etc.CmEtcInfoVo;
import com.youming.youche.market.vo.facilitator.FacilitatorVo;
import com.youming.youche.market.vo.facilitator.criteria.ServiceInfoQueryCriteria;
import com.youming.youche.order.api.order.*;
import com.youming.youche.order.commons.OrderConsts;
import com.youming.youche.order.domain.order.*;
import com.youming.youche.record.api.tenant.ITenantVehicleRelService;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.record.domain.tenant.TenantVehicleRel;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.record.dto.order.OrderSchedulerDto;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ISysUserService;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.util.DateUtil;
import com.youming.youche.util.ExcelFilesVaildate;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * ???????????????
 * </p>
 *
 * @author Terry
 * @since 2022-03-11
 */
@DubboService(version = "1.0.0")
@Service
public class CmEtcInfoServiceImpl extends BaseServiceImpl<CmEtcInfoMapper, CmEtcInfo> implements ICmEtcInfoService {

    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Logger log = LoggerFactory.getLogger(CmEtcInfoServiceImpl.class);
    @Resource
    LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Lazy
    @Resource
    EtcMaintainServiceImpl etcMaintainService;

    @Lazy
    @Resource
    EtcMaintainMapper etcMaintainMapper;

    @DubboReference(version = "1.0.0")
    IVehicleDataInfoService iVehicleDataInfoService;
    @DubboReference(version = "1.0.0")
    ITenantVehicleRelService iTenantVehicleRelService;
    @DubboReference(version = "1.0.0")
    IOrderSchedulerService iOrderSchedulerService;

    @DubboReference(version = "1.0.0")
    IOrderSchedulerHService iOrderSchedulerHService;

    @Lazy
    @Resource
    IEtcMaintainService iEtcMaintainService;

    @Lazy
    @Resource
    IServiceInfoService iServiceInfoService;

    @Resource
    RedisUtil redisUtil;

    @DubboReference(version = "1.0.0")
    IAccountStatementService iAccountStatementService;
    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;
    @DubboReference(version = "1.0.0")
    IOrderInfoService iOrderInfoService;
    @DubboReference(version = "1.0.0")
    IOrderInfoHService iOrderInfoHService;
    @DubboReference(version = "1.0.0")
    IOrderOpRecordService iOrderOpRecordService;
    @DubboReference(version = "1.0.0")
    ISysUserService iSysUserService;
    @DubboReference(version = "1.0.0")
    com.youming.youche.record.api.order.IOrderSchedulerService iOrderSchedulerService1;
    @DubboReference(version = "1.0.0")
    IUserDataInfoService iUserDataInfoService;
    @DubboReference(version = "1.0.0")
    IOrderInfoExtService iOrderInfoExtService;

    @Override
    public Page<CmEtcInfo> getAll(CmEtcInfoVo cmEtcInfoVo, String accessToken, Integer pageNum, Integer pageSize) {
        Page<CmEtcInfo> page = new Page<>(pageNum, pageSize);
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (loginInfo.getTenantId() == null) {
            return null;
        }
        UserDataInfo userDataInfo = iUserDataInfoService.selectUserType(loginInfo.getUserInfoId());
        if (userDataInfo.getUserType() != null) {
            if (userDataInfo.getUserType() == SysStaticDataEnum.USER_TYPE.SERVICE_USER) {
                ServiceInfo serviceInfoById = iServiceInfoService.getServiceInfoById(loginInfo.getUserInfoId());
                cmEtcInfoVo.setServiceProviderId(serviceInfoById.getServiceUserId());
            } else {
                cmEtcInfoVo.setTenantId(loginInfo.getTenantId());
            }
        }

        String beginDate = cmEtcInfoVo.getBeginDate();
        String endDate = cmEtcInfoVo.getEndDate();
        //  ??????????????????
        if (beginDate != null && org.apache.commons.lang3.StringUtils.isNotEmpty(beginDate)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String benginDate = beginDate + " 00:00:00";
            LocalDateTime beginApplyTime1 = LocalDateTime.parse(benginDate, dateTimeFormatter);
            cmEtcInfoVo.setBeginDate1(beginApplyTime1);
        }
        //  ??????????????????
        if (endDate != null && org.apache.commons.lang3.StringUtils.isNotEmpty(endDate)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String applyTimeStr = endDate + " 23:59:59";
            LocalDateTime endApplyTime1 = LocalDateTime.parse(applyTimeStr, dateTimeFormatter);
            cmEtcInfoVo.setEndDate1(endApplyTime1);
        }
        String deductBeginDate = cmEtcInfoVo.getDeductBeginDate();
        String deductEndDate = cmEtcInfoVo.getDeductEndDate();
        //????????????
        if (deductBeginDate != null && org.apache.commons.lang3.StringUtils.isNotEmpty(deductBeginDate)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String applyTimeStr = deductBeginDate + "00:00:00";
            LocalDateTime beginApplyTime1 = LocalDateTime.parse(applyTimeStr, dateTimeFormatter);
            cmEtcInfoVo.setDeductBeginDate1(beginApplyTime1);
        }
        //????????????
        if (deductEndDate != null && org.apache.commons.lang3.StringUtils.isNoneBlank(deductEndDate)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String endApplyTimeStr = deductEndDate + "23:59:59";
            LocalDateTime endApplyTime1 = LocalDateTime.parse(endApplyTimeStr, dateTimeFormatter);
            cmEtcInfoVo.setDeductEndDate1(endApplyTime1);
        }
        String billStartDate = cmEtcInfoVo.getBillStartDate();
        String billEndDate = cmEtcInfoVo.getBillEndDate();
        // ????????????
        if (billStartDate != null && org.apache.commons.lang3.StringUtils.isNoneBlank(billStartDate)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String applyTimeStr = billStartDate + "00:00:00";
            LocalDateTime beginApplyTime1 = LocalDateTime.parse(applyTimeStr, dateTimeFormatter);
            cmEtcInfoVo.setBillStartDate1(beginApplyTime1);
        }
        //????????????
        if (billEndDate != null && org.apache.commons.lang3.StringUtils.isNoneBlank(billEndDate)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String endApplyTimeStr = billEndDate + " 23:59:59";
            LocalDateTime endApplyTime1 = LocalDateTime.parse(endApplyTimeStr, dateTimeFormatter);
            cmEtcInfoVo.setBillEndDate1(endApplyTime1);
        }
        Page<CmEtcInfo> all = baseMapper.getAll(page, cmEtcInfoVo, loginInfo.getTenantId());
        List<CmEtcInfo> records = all.getRecords();
        List<Long> collectUserIds = new ArrayList();
        Set<Long> userIdSet = new HashSet<>();
        for (CmEtcInfo c : records) {
            if (c.getCollectUserId() != null && c.getCollectUserId() > 0L) {
                userIdSet.add(c.getCollectUserId());
            }
        }
        collectUserIds.addAll(userIdSet);
        List<UserDataInfo> userDataInfos = null;
        if (collectUserIds != null && collectUserIds.size() > 0) {
            userDataInfos = iUserDataInfoService.getUserDataInfoList(collectUserIds);
        }
        for (CmEtcInfo cmEtcInfo : records) {

            cmEtcInfo.setPaymentWayName(cmEtcInfo.getPaymentWay() == null ? "" : getSysStaticData("PAYMENT_WAY", cmEtcInfo.getPaymentWay().toString()).getCodeName());

            cmEtcInfo.setCardTypeName(cmEtcInfo.getCardType() == null ? "" : getSysStaticData("ETC_CARD_TYPE", cmEtcInfo.getCardType().toString()).getCodeName());

            cmEtcInfo.setPaymentTypeName(cmEtcInfo.getPaymentType() == null ? "" : getSysStaticData("ETC_SERVICE_PAYMENT_TYPE", cmEtcInfo.getCardType().toString()).getCodeName());

            cmEtcInfo.setIsCoverName(cmEtcInfo.getIsCover() == null ? "" : getSysStaticData("IS_COVER_REPORT", cmEtcInfo.getIsCover().toString()).getCodeName());

            cmEtcInfo.setChargingStateName(cmEtcInfo.getChargingState() == null ? "" : getSysStaticData("ETC_CHARGING_STATE", cmEtcInfo.getChargingState().toString()).getCodeName());
            //
            cmEtcInfo.setSourceTypeName(cmEtcInfo.getSourceType() == null ? "" : getSysStaticData("ETC_SOURCE_TYPE", cmEtcInfo.getSourceType().toString()).getCodeName());
            // ????????????
            cmEtcInfo.setEtcUserTypeName(cmEtcInfo.getEtcUserType() == null ? "" : getSysStaticData("VEHICLE_CLASS", cmEtcInfo.getEtcUserType().toString()).getCodeName());
            // ?????? ????????????????????????????????????
//            if (cmEtcInfo!=null && cmEtcInfo.getPlateNumber()!=null){
//                VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicle(cmEtcInfo.getPlateNumber(),null);
//                if (vehicleDataInfo!=null && vehicleDataInfo.getId()!=null){
//                    TenantVehicleRel tenantVehicleRel = iTenantVehicleRelService.getTenantVehicleRel(vehicleDataInfo.getId());
//                    if (tenantVehicleRel!=null && tenantVehicleRel.getDriverUserId()!=null){
//                        UserDataInfo userDataInfo1 = iUserDataInfoService.getUserDataInfo(tenantVehicleRel.getDriverUserId());
//                        cmEtcInfo.setCollectName(userDataInfo1.getLinkman());
//                        cmEtcInfo.setCollectMobile(userDataInfo1.getMobilePhone());
//                    }
//                }
//            }
        }
        return all;
    }


    /**
     * ETC????????????
     * ?????????
     *
     * @param cmEtcInfoVo
     * @param accessToken
     * @param record
     */
    @Async
    @Override
    public void etcOutList(CmEtcInfoVo cmEtcInfoVo, String accessToken, ImportOrExportRecords record) {
//        Page<CmEtcInfo> page  = new Page<>(1,20);
        IPage<CmEtcInfo> alla = this.getAll(cmEtcInfoVo, accessToken, 1, 1000);
        List<CmEtcInfo> records = alla.getRecords();

        String StateName = "";
        String CardName = "";
        String PaymentName = "";
        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            if (records != null && records.size() > 0) {
                for (CmEtcInfo cmEtcInfo : records) {
                    cmEtcInfo.setConsumeTime(DateUtil.asDate(cmEtcInfo.getEtcConsumeTime()));
                }
            }
            showName = new String[]{"?????????", "????????????", "????????????", "?????????", "ETC??????", "????????????", "????????????"};
//                    "????????? 1:????????? 2:????????? 3:?????????",
//                    "?????????", "????????????:1????????? 2?????????", "????????????",
//                    "????????????", "??????","????????????",

//                    "???????????????", "?????????????????????",
//                    "????????????","????????????", "????????????",
//                    "????????????","????????????","????????????",
//                    "????????????","????????????"

            resourceFild = new String[]{
                    "getPlateNumber", "getConsumeTime", "getConsumeMoney", "getServiceProviderName", "getEtcCardNo", "getTradingSite", "getOrderId"};
//                    "getEtcConsumeTime","getEtcCardNo", "getPlateNumber",
//                    "getCardType",
//                    "getServiceProviderName", "getPaymentType", "getConsumeMoney",
//                    "getConsumeAfterMoney", "getConsumeProfit", "getTradingSite",
//                    "getOrderId","getCollectName", "getCollectMobile",
//                    "getEtcUserType", "getPaymentWay","getAccountStatementNo",
//                    "getBillStartDate", "getBillEndDate","getOpDate",
//                    "getSourceType","getState"

            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(records, showName, resourceFild, CmEtcInfo.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //????????????
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "ETC????????????.xlsx", inputStream.available());
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
     * ?????????
     * ?????? ETC????????????
     *
     * @param byteBuffer
     * @param records
     * @param
     * @param accessToken
     */
    @Transactional
    @Async
    @Override
    public void etcImport(byte[] byteBuffer, ImportOrExportRecords records, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<CmEtcInfoDto> failureList = new ArrayList<>();
        try {
            StringBuffer reasonFailure = new StringBuffer();
            InputStream is = new ByteArrayInputStream(byteBuffer);
            List<List<String>> lists = etcMaintainService.getExcelContent(is, 1, (ExcelFilesVaildate[]) null);
            //???????????????
            List<String> listsRows = lists.remove(0);
            int maxNum = 300;
            if (lists.size() == 0) {
                reasonFailure.append("??????????????????0???????????????!");
            }
            if (lists.size() > maxNum) {
                reasonFailure.append("?????????????????????" + maxNum + "???????????????????????????[" + lists.size() + "]");
            }
            int j = 1;
            int success = 0;
            for (List<String> rowData : lists) {
                j++;
                CmEtcInfo cmEtcInfo = new CmEtcInfo();
                CmEtcInfoDto cmEtcInfoDto = new CmEtcInfoDto();
                int size = rowData.size();
                Map<String, Object> inParam = new HashMap();
                String PLATE_NUMBER = rowData.get(0).trim();//?????????
                String ETC_CONSUME_TIME = rowData.get(1).trim();//????????????
                String CONSUME_MONEY = rowData.get(2).trim();// ????????????
                // TODO 2022-5-24 ???????????????
//                String CONSUME_AFTER_MONEY = rowData.get(3).trim();// ????????????
//                String CONSUME_PROFIT = rowData.get(4).trim();// ??????
                String service_provider_name = rowData.get(3).trim();// etc?????????
                String ETC_CARD_NO = rowData.get(4).trim();//ETC??????
                String TRADING_SITE = rowData.get(5).trim();// ????????????

                if (StringUtils.isBlank(ETC_CARD_NO)) {
                    reasonFailure.append("???" + j + "??????ETC??????[" + ETC_CARD_NO + "]?????????");
                }
                if (StringUtils.isBlank(ETC_CONSUME_TIME)) {
                    reasonFailure.append("???" + j + "??????????????????[" + ETC_CONSUME_TIME + "]?????????");
                }
                if (StringUtils.isBlank(CONSUME_MONEY)) {
                    reasonFailure.append("???" + j + "??????????????????[" + CONSUME_MONEY + "]?????????");
                }
//                if (StringUtils.isBlank(CONSUME_AFTER_MONEY)){
//                    reasonFailure.append("???"+j+"??????????????????["+CONSUME_AFTER_MONEY+"]?????????");
//                }
//                if (StringUtils.isBlank(CONSUME_PROFIT)){
//                    reasonFailure.append("???"+j+"????????????["+CONSUME_PROFIT+"]?????????");
//                }
                if (StringUtils.isBlank(service_provider_name)) {
                    reasonFailure.append("???" + j + "???????????????[" + service_provider_name + "]?????????");
                } else {
                    // TODO ????????????????????????????????????
                    ServiceInfoQueryCriteria serviceInfoQueryCriteria = new ServiceInfoQueryCriteria();
                    Page<FacilitatorVo> facilitatorVoPage = iServiceInfoService.queryFacilitator(serviceInfoQueryCriteria,
                            1, 9999, accessToken);
                    List<FacilitatorVo> records1 = facilitatorVoPage.getRecords();
                    List<FacilitatorVo> serviceName1 = records1.stream().filter(facilitatorVo ->
                            facilitatorVo.getServiceName().equals(service_provider_name) && facilitatorVo.getServiceType() == 3).collect(Collectors.toList());
                    if (serviceName1.size() <= 0) {
                        reasonFailure.append("???" + j + "??????????????????????????????????????????");
                    }
                }

                if (StringUtils.isBlank(TRADING_SITE)) {
                    reasonFailure.append("???" + j + "??????????????????[" + TRADING_SITE + "]?????????");
                }

                if (PLATE_NUMBER == null) {
                    reasonFailure.append("???" + j + "??????????????????????????????");
                } else {
                    // TODO ????????????????????????????????????
                    VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(PLATE_NUMBER);
                    if (vehicleDataInfo != null) {
                        TenantVehicleRel tenantVehicleRel = iTenantVehicleRelService.getTenantVehicleRel(vehicleDataInfo.getId());
                        if (tenantVehicleRel != null) {
                            // TODO ????????????id ?????????????????????
                            cmEtcInfo.setUserId(tenantVehicleRel.getDriverUserId());
                        }
                        cmEtcInfo.setPlateNumber(PLATE_NUMBER);
                    } else {
                        reasonFailure.append("???" + j + "???????????????????????????????????????");
                    }
                }

                // ?????? ????????????
                LambdaQueryWrapper<EtcMaintain> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(EtcMaintain::getEtcId, ETC_CARD_NO)
                        .eq(EtcMaintain::getTenantId, loginInfo.getTenantId());
                EtcMaintain etcmaintain = etcMaintainMapper.selectOne(wrapper);
                if (ETC_CONSUME_TIME != null && org.apache.commons.lang3.StringUtils.isNotEmpty(ETC_CONSUME_TIME)) {
                    cmEtcInfo.setEtcConsumeTime(LocalDateTime.parse(ETC_CONSUME_TIME, df));
                }
                if (CONSUME_MONEY != null && org.apache.commons.lang3.StringUtils.isNotEmpty(CONSUME_MONEY)) {
                    cmEtcInfo.setConsumeMoney(Long.valueOf(CONSUME_MONEY));
                }
//                cmEtcInfo.setConsumeAfterMoney(Long.valueOf(CONSUME_AFTER_MONEY));
//                cmEtcInfo.setConsumeProfit(Long.valueOf(CONSUME_PROFIT));
                cmEtcInfo.setTradingSite(TRADING_SITE);
                cmEtcInfo.setServiceProviderName(service_provider_name);//???????????????
                cmEtcInfo.setTenantId(loginInfo.getTenantId());//?????????id
                cmEtcInfo.setSourceType(2);// ??????????????????
                if (etcmaintain != null) {
                    cmEtcInfo.setPaymentType(etcmaintain.getPaymentType());//????????????;
                    cmEtcInfo.setCardType(etcmaintain.getEtcCardType());//?????????
                } else {
                    reasonFailure.append("???" + j + "???????????????????????????????????????");
                }

                cmEtcInfo.setEtcCardNo(ETC_CARD_NO);//??????
                if (StrUtil.isEmpty(reasonFailure)) {
                    cmEtcInfo.setTenantId(loginInfo.getTenantId());
                    boolean save = this.save(cmEtcInfo);
                    if (save) {
                        CmEtcInfo cmEtcInfo1 = this.getById(cmEtcInfo.getId());
                        sysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.EtcConsume,
                                cmEtcInfo1.getId(), com.youming.youche.commons.constant.SysOperLogConst.OperType.Add, "??????", loginInfo.getTenantId());
                    }
                    //??????????????????
                    success++;
                } else {
                    cmEtcInfoDto.setEtcCardNo(cmEtcInfo.getEtcCardNo());
                    cmEtcInfoDto.setReasonFailure(reasonFailure.toString());
                    failureList.add(cmEtcInfoDto);
                }
            }

            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(failureList)) {
                String[] showName;
                String[] resourceFild;
                showName = new String[]{"ETC??????", "????????????"
//                        "????????????","ETC??????","?????????",
//                        "????????? 1:????????? 2:????????? 3:?????????",
//                        "?????????", "????????????:1????????? 2?????????", "????????????",
//                        "????????????", "??????", "????????????",
//                        "????????????", "???????????????", "?????????????????????",
//                        "????????????","????????????", "????????????",
//                        "????????????","????????????","????????????",
//                        "????????????","????????????"
                };
                resourceFild = new String[]{
                        "getEtcCardNo", "getReasonFailure"
//                        "getEtcConsumeTime","getEtcCardNo", "getPlateNumber",
//                        "getCardType",
//                        "getServiceProviderName", "getPaymentType", "getConsumeMoney",
//                        "getConsumeAfterMoney", "getConsumeProfit", "getTradingSite",
//                        "getOrderId","getCollectName", "getCollectMobile",
//                        "getEtcUserType", "getPaymentWay","getAccountStatementNo",
//                        "getBillStartDate", "getBillEndDate","getOpDate",
//                        "getSourceType","getState"
                };
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(failureList, showName, resourceFild, CmEtcInfoDto.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream1 = new ByteArrayInputStream(b);
                //????????????
                FastDFSHelper client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream1, "ETC????????????.xlsx", inputStream1.available());
                os.close();
                is.close();
                records.setFailureUrl(failureExcelPath);
                if (success > 0) {
                    records.setRemarks(success + "?????????" + failureList.size() + "?????????");
                    records.setState(2);
                }
                if (success == 0) {
                    records.setRemarks(failureList.size() + "?????????");
                    records.setState(4);
                }
            } else {
                records.setRemarks(success + "?????????");
                records.setState(3);
            }
            importOrExportRecordsService.update(records);
        } catch (Exception e) {
            records.setState(5);
            records.setRemarks("???????????????????????????????????????????????????");
            records.setFailureReason("???????????????????????????????????????????????????");
            importOrExportRecordsService.update(records);
            e.printStackTrace();
        }
    }


    /**
     * ETC???????????? ???????????? ?????????????????? ????????????
     * ?????????
     *
     * @param
     * @return
     */
    @Transactional
    @Override
    public String batchEtcDeduction(String accessToken) {
        String remark = "";
        LoginInfo loginInfo = loginUtils.get(accessToken);
        LambdaQueryWrapper<CmEtcInfo> cmEtcInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cmEtcInfoLambdaQueryWrapper.eq(CmEtcInfo::getTenantId, loginInfo.getTenantId());
        List<CmEtcInfo> cmEtcInfos = baseMapper.selectList(cmEtcInfoLambdaQueryWrapper);
        List<CmEtcInfo> collect = cmEtcInfos.stream().filter(cmEtcInfo -> cmEtcInfo.getOrderId() == null).collect(Collectors.toList());
        // ????????????????????????ETC????????????
        for (CmEtcInfo c : collect) {
            LambdaQueryWrapper<CmEtcInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CmEtcInfo::getId, c.getId());
            //  ?????? etc??????????????????
            OrderScheduler orderScheduler = iOrderSchedulerService.getOrderSchedulerByPlateNumber(c.getPlateNumber());
            if (orderScheduler == null || orderScheduler.getId() == null) {
                OrderSchedulerH orderSchedulerH = iOrderSchedulerHService.getOrderSchedulerByPlateNumber(c.getPlateNumber());
                if (orderSchedulerH != null || orderSchedulerH.getId() != null) {
                    BeanUtil.copyProperties(orderSchedulerH, orderScheduler);
                }
            }
            if (orderScheduler != null && orderScheduler.getId() != null) {
//                Long orderId = orderScheduler.getOrderId();
//                OrderInfoExt orderInfoExt = iOrderInfoExtService.getOrderInfoExt(orderId);
//
//                if(orderScheduler.getVehicleClass() != null && orderInfoExt != null
//                        && orderScheduler.getVehicleClass() == 1 && ){
//
//                }
                // 2022-6-23 ????????? ????????????????????? ??????????????????????????????  ??? ????????????????????????????????????????????? ??????????????????
                Long timeCar = null;
                Long timeEtc = null;
                Long timeArr = null;
                if (orderScheduler.getVerifyDependDate() != null) {
                    timeCar = DateUtil.asDate(orderScheduler.getVerifyDependDate()).getTime(); //??????????????????
                } else if (orderScheduler.getCarDependDate() != null) {
                    timeCar = DateUtil.asDate(orderScheduler.getCarDependDate()).getTime(); // ??????????????????
                }
                if (c.getEtcConsumeTime() != null) {
                    timeEtc = DateUtil.asDate(c.getEtcConsumeTime()).getTime(); // etc ????????????
                }
                if (orderScheduler.getVerifyArriveDate() != null) {
                    timeArr = DateUtil.asDate(orderScheduler.getVerifyArriveDate()).getTime(); //??????????????????
                } else if (orderScheduler.getCarArriveDate() != null) {
                    timeArr = DateUtil.asDate(orderScheduler.getCarArriveDate()).getTime(); //??????????????????
                }
                // etc ??????????????????????????????????????????   -  etc??????????????????????????????????????????
                if (((orderScheduler.getCarDependDate() != null && timeEtc.longValue() >= timeCar.longValue()) &&
                        (orderScheduler.getCarArriveDate() != null && timeEtc.longValue() <= timeArr.longValue()))) {
                    if (orderScheduler.getVehicleClass() != 1) {
                        remark = "????????????????????????????????????????????????";
                        c.setChargingState(2);
                        c.setRemark(remark);
                        baseMapper.update(c, wrapper);
                        sysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.EtcConsume,
                                Long.valueOf(c.getId()), com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, "????????????",
                                loginInfo.getTenantId());
                        continue;
                    }
                    // ??????????????? ????????????
                    OrderInfoExt orderInfoPaymentWay = iOrderInfoExtService.getOrderInfoPaymentWay(orderScheduler.getOrderId(), loginInfo.getTenantId());
                    if (orderInfoPaymentWay != null && orderInfoPaymentWay.getPaymentWay() != null) {
                        if (orderInfoPaymentWay.getPaymentWay() != 2) {
                            remark = "?????????????????????????????????????????????";
                            c.setChargingState(2);
                            c.setRemark(remark);
                            baseMapper.update(c, wrapper);
                            sysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.EtcConsume,
                                    Long.valueOf(c.getId()), com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, "????????????",
                                    loginInfo.getTenantId());
                            continue;
                        }
                        c.setPaymentWay(orderInfoPaymentWay.getPaymentWay());
                    }
                    c.setOrderId(orderScheduler.getOrderId());  // ?????????
                    // ??????????????????????????????
                    c.setEtcUserType(orderScheduler.getVehicleClass()); // ????????????
                    c.setChargingState(3);
                    c.setChargingStateName(c.getChargingState() == null ? "" : getSysStaticData("ETC_CHARGING_STATE", c.getChargingState().toString()).getCodeName());
                    // ?????? ?????????????????? ??????
                    c.setCollectName(orderScheduler.getCarDriverMan());
                    c.setCollectMobile(orderScheduler.getCarDriverPhone());
                    baseMapper.update(c, wrapper);
                    sysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.EtcConsume,
                            Long.valueOf(c.getId()), com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, "????????????",
                            loginInfo.getTenantId());
                } else {
                    remark = "??????ETC????????????????????????????????????";
                    c.setChargingState(2);
                    c.setRemark(remark);
                    baseMapper.update(c, wrapper);
                    sysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.EtcConsume,
                            Long.valueOf(c.getId()), com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, "????????????",
                            loginInfo.getTenantId());
                    continue;
                }
            } else {
                remark = "????????????????????????????????????";
                c.setChargingState(2);
                c.setRemark(remark);
                baseMapper.update(c, wrapper);
                sysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.EtcConsume,
                        Long.valueOf(c.getId()), com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, "????????????",
                        loginInfo.getTenantId());
                continue;
            }

        }
        return "????????????????????????";
    }


    //????????????
    com.youming.youche.market.dto.etc.order.OrderSchedulerDto getOrderEtcInvestment(CmEtcInfo cmEtcInfo, Long tenantId) {
        String etcCardNumber = cmEtcInfo.getEtcCardNo();
        Long amountFee = cmEtcInfo.getConsumeMoney();
        Long objId = cmEtcInfo.getId();
        if (etcCardNumber == null || "".equals(etcCardNumber)) {
            throw new BusinessException("?????????ETC??????!");
        }
        if (amountFee <= 0) {
            throw new BusinessException("ETC??????????????????????????????0!");
        }
        if (cmEtcInfo.getState() == null) {
            throw new BusinessException("ETC???????????????");
        }
        QueryWrapper<EtcMaintain> wrapper = new QueryWrapper<>();
        wrapper.eq("etc_id", etcCardNumber);
        EtcMaintain etcMaintain = etcMaintainMapper.selectOne(wrapper);
        if (etcMaintain == null) {
            throw new BusinessException("ETC????????????????????????");
        }
        Long vehicleCode = etcMaintain.getVehicleCode();
        if (vehicleCode == null || vehicleCode <= 0) {
            throw new BusinessException("ETC???????????????????????????!");
        }
        //????????????
        com.youming.youche.market.dto.etc.order.OrderSchedulerDto orderSchedulerDto = baseMapper.queryOrderInfoByCar(tenantId, objId);
        if (orderSchedulerDto == null) {
            com.youming.youche.market.dto.etc.order.OrderSchedulerDto orderSchedulerDto1 = baseMapper.queryOrderInfoByCarh(tenantId, objId);
            if (orderSchedulerDto1 == null) {
                throw new BusinessException("?????????????????????");
            }
            return orderSchedulerDto1;
        }
        return orderSchedulerDto;
    }

    /**
     * ??????????????????
     * ?????????
     *
     * @param etcCardNo      ETC??????
     * @param consumeMoney   ????????????
     * @param etcConsumeTime ????????????
     * @param tradingSite    ??????
     * @param orderId        ?????????
     * @param accessToken
     * @return
     */
    @Override
    public String updateEtcInfoBy(String id, String etcCardNo, String consumeMoney, String etcConsumeTime, String tradingSite, String orderId, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        String falg = "????????????";
        Long orders = null;
        if (orderId != null && org.apache.commons.lang.StringUtils.isNotEmpty(orderId)) {
            orders = Long.valueOf(orderId);
        }
        //TODO  ????????????????????? ????????????  ????????????????????????
        if (StringUtils.isBlank(orderId)) {
            throw new BusinessException("?????????????????????");
        }
        OrderInfo order = iOrderInfoService.getOrder(orders);
        if (order != null) {
            orders = order.getOrderId();
        } else {
            OrderInfoH orderH = iOrderInfoHService.getOrderH(orders);
            if (orderH == null) {
                throw new BusinessException("????????????????????????");
            }
            orders = orderH.getOrderId();
        }
        //  ?????? etc??????????????????
        LambdaQueryWrapper<CmEtcInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CmEtcInfo::getId, id)
                .eq(CmEtcInfo::getTenantId, loginInfo.getTenantId());
        CmEtcInfo cmEtcInfo = baseMapper.selectOne(queryWrapper);
        cmEtcInfo.setOrderId(orders);
        // ?????????????????????????????????
        OrderScheduler orderScheduler = iOrderSchedulerService.getOrderScheduler(orders);
        if (orderScheduler == null || orderScheduler.getId() == null) {
            OrderSchedulerH orderSchedulerH = iOrderSchedulerHService.getOrderSchedulerH(orders);
            BeanUtil.copyProperties(orderSchedulerH, orderScheduler);
        }
        if (orderScheduler.getId() != null && (orderScheduler.getPlateNumber() != null && StringUtil.isNotEmpty(orderScheduler.getPlateNumber()))) {
            //?????????????????????????????? ?????????etc?????????????????? ???????????? ????????????
            if (!orderScheduler.getPlateNumber().equals(cmEtcInfo.getPlateNumber())) {
                throw new BusinessException("???????????????????????????????????????");
            }
            Long timeCar = null;
            Long timeEtc = null;
            Long timeArr = null;
            if (orderScheduler.getVerifyDependDate() != null) {
                timeCar = DateUtil.asDate(orderScheduler.getVerifyDependDate()).getTime(); //??????????????????
            } else if (orderScheduler.getCarDependDate() != null) {
                timeCar = DateUtil.asDate(orderScheduler.getCarDependDate()).getTime(); // ??????????????????
            }
            if (cmEtcInfo.getEtcConsumeTime() != null) {
                timeEtc = DateUtil.asDate(cmEtcInfo.getEtcConsumeTime()).getTime(); // etc ????????????
            }
            if (orderScheduler.getVerifyArriveDate() != null) {
                timeArr = DateUtil.asDate(orderScheduler.getVerifyArriveDate()).getTime(); //??????????????????
            } else if (orderScheduler.getCarArriveDate() != null) {
                timeArr = DateUtil.asDate(orderScheduler.getCarArriveDate()).getTime(); //??????????????????
            }
            // etc ??????????????????????????????????????????   -  etc??????????????????????????????????????????
            if (((orderScheduler.getCarDependDate() != null && timeEtc.longValue() >= timeCar.longValue()) &&
                    (orderScheduler.getCarArriveDate() != null && timeEtc.longValue() <= timeArr.longValue()))) {
                cmEtcInfo.setOrderId(orders);  // ?????????
                // ??????????????????????????????
                if (orderScheduler.getVehicleClass() != 1) {
                    throw new BusinessException("????????????????????????????????????????????????");
                }
                cmEtcInfo.setEtcUserType(orderScheduler.getVehicleClass()); // ????????????
                cmEtcInfo.setChargingState(1);// ????????????
                // ?????? ?????????????????? ??????
                cmEtcInfo.setCollectName(orderScheduler.getCarDriverMan());
                cmEtcInfo.setCollectMobile(orderScheduler.getCarDriverPhone());
                // ??????????????? ????????????
                OrderInfoExt orderInfoPaymentWay = iOrderInfoExtService.getOrderInfoPaymentWay(orders, loginInfo.getTenantId());
                if (orderInfoPaymentWay != null && orderInfoPaymentWay.getPaymentWay() != null) {
                    if (orderInfoPaymentWay.getPaymentWay() != 2) {
                        throw new BusinessException("?????????????????????????????????????????????");
                    }
                    cmEtcInfo.setPaymentWay(orderInfoPaymentWay.getPaymentWay());
                }
                baseMapper.update(cmEtcInfo, queryWrapper);
                falg = "????????????????????????";
                sysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.EtcConsume,
                        Long.valueOf(id), com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, "??????????????????",
                        loginInfo.getTenantId());
            }
            // etc ???????????? ?????? ?????????????????? ??? ??????????????????
            else {
                throw new BusinessException("?????????????????????????????????????????????");
            }
        }
        return falg;
    }

    public UserDataInfo userDataInfo(CmEtcInfo cmEtcInfo) {
        EtcMaintain etcmaintain = iEtcMaintainService.etcmaintain(cmEtcInfo.getEtcCardNo());
        TenantVehicleRel tenantVehicleRel = null;
        VehicleDataInfo vehicleDataInfo = null;
        UserDataInfo userDataInfo = null;
        if (etcmaintain != null && etcmaintain.getVehicleCode() != null) {
            vehicleDataInfo = iVehicleDataInfoService.get(etcmaintain.getVehicleCode());
            if (vehicleDataInfo != null && vehicleDataInfo.getId() != null && vehicleDataInfo.getTenantId() != null) {
                tenantVehicleRel = iTenantVehicleRelService.getTenantVehicleRel(vehicleDataInfo.getId(), vehicleDataInfo.getTenantId());
            }
            if (tenantVehicleRel != null && tenantVehicleRel.getDriverUserId() != null) {
                userDataInfo = iUserDataInfoService.get(tenantVehicleRel.getDriverUserId());
            }
        }
        return userDataInfo;
    }

    /**
     * ?????? ??????????????????
     *
     * @param ids         ETC
     * @param orderId     ?????????
     * @param accessToken
     * @return
     */
    @Transactional
    @Override
    public String updateEtcBys(List<String> ids, String orderId, String accessToken) {
        String falg = "????????????";
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long orders = null;
        if (orderId != null && org.apache.commons.lang.StringUtils.isNotEmpty(orderId)) {
            orders = Long.valueOf(orderId);
        }
        Long order_id = Long.valueOf(orderId);
        if (order_id == null || order_id <= 0) {
            throw new BusinessException("?????????????????????");
        }
        OrderInfo order = iOrderInfoService.getOrder(orders);
        if (order != null) {
            orders = order.getOrderId();
        } else {
            OrderInfoH orderH = iOrderInfoHService.getOrderH(orders);
            if (orderH == null) {
                throw new BusinessException("????????????????????????");
            }
            orders = orderH.getOrderId();
        }
        for (String id : ids) {
            //  ?????? etc??????????????????
            LambdaQueryWrapper<CmEtcInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(CmEtcInfo::getId, id)
                    .eq(CmEtcInfo::getTenantId, loginInfo.getTenantId());
            CmEtcInfo cmEtcInfo = baseMapper.selectOne(queryWrapper);
            cmEtcInfo.setOrderId(orders);
            // ?????????????????????????????????

            OrderScheduler orderScheduler = iOrderSchedulerService.getOrderScheduler(orders);
            if (orderScheduler == null || orderScheduler.getId() == null) {
                OrderSchedulerH orderSchedulerH = iOrderSchedulerHService.getOrderSchedulerH(orders);
                BeanUtil.copyProperties(orderSchedulerH, orderScheduler);
            }
            if (orderScheduler.getId() != null && (orderScheduler.getPlateNumber() != null && StringUtil.isNotEmpty(orderScheduler.getPlateNumber()))) {
                //?????????????????????????????? ?????????etc?????????????????? ???????????? ????????????
                if (!orderScheduler.getPlateNumber().equals(cmEtcInfo.getPlateNumber())) {
                    throw new BusinessException("???????????????????????????????????????");
                }
                Long timeCar = null;
                Long timeEtc = null;
                Long timeArr = null;
                if (orderScheduler.getVerifyDependDate() != null) {
                    timeCar = DateUtil.asDate(orderScheduler.getVerifyDependDate()).getTime(); //??????????????????
                } else if (orderScheduler.getCarDependDate() != null) {
                    timeCar = DateUtil.asDate(orderScheduler.getCarDependDate()).getTime(); // ??????????????????
                }
                if (cmEtcInfo.getEtcConsumeTime() != null) {
                    timeEtc = DateUtil.asDate(cmEtcInfo.getEtcConsumeTime()).getTime(); // etc ????????????
                }
                if (orderScheduler.getVerifyArriveDate() != null) {
                    timeArr = DateUtil.asDate(orderScheduler.getVerifyArriveDate()).getTime(); //??????????????????
                } else if (orderScheduler.getCarArriveDate() != null) {
                    timeArr = DateUtil.asDate(orderScheduler.getCarArriveDate()).getTime(); //??????????????????
                }
                // etc ??????????????????????????????????????????   -  etc??????????????????????????????????????????
                if (((orderScheduler.getCarDependDate() != null && timeEtc.longValue() >= timeCar.longValue()) &&
                        (orderScheduler.getCarArriveDate() != null && timeEtc.longValue() <= timeArr.longValue()))) {
                    cmEtcInfo.setOrderId(orders);  // ?????????
                    // ??????????????????????????????
                    if (orderScheduler.getVehicleClass() != 1) {
                        throw new BusinessException("????????????????????????????????????????????????");
                    }
                    cmEtcInfo.setEtcUserType(orderScheduler.getVehicleClass()); // ????????????
                    cmEtcInfo.setChargingState(1);// ????????????
                    // ?????? ?????????????????? ??????
                    cmEtcInfo.setCollectName(orderScheduler.getCarDriverMan());
                    cmEtcInfo.setCollectMobile(orderScheduler.getCarDriverPhone());
                    // ??????????????? ????????????
                    OrderInfoExt orderInfoPaymentWay = iOrderInfoExtService.getOrderInfoPaymentWay(orders, loginInfo.getTenantId());
                    if (orderInfoPaymentWay != null && orderInfoPaymentWay.getPaymentWay() != null) {
                        if (orderInfoPaymentWay.getPaymentWay() != 2) {
                            throw new BusinessException("?????????????????????????????????????????????");
                        }
                        cmEtcInfo.setPaymentWay(orderInfoPaymentWay.getPaymentWay());
                    }
                    baseMapper.update(cmEtcInfo, queryWrapper);
                    falg = "????????????";
                    sysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.EtcConsume,
                            Long.valueOf(id), com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, "??????????????????",
                            loginInfo.getTenantId());
                }
                // etc ???????????? ?????? ?????????????????? ??? ??????????????????
                else {
                    throw new BusinessException("?????????????????????????????????????????????");
                }
            }
            continue;
        }
        return falg;
    }


    /**
     * ??????????????????
     * ?????????
     *
     * @param etcid
     * @param coverType ?????????????????? 0???????????? 1?????????
     * @return
     */
    @Transactional
    @Override
    public String coverEtc(String etcid, Integer coverType, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if (StringUtils.isBlank(etcid)) {
            throw new BusinessException("ETC????????????????????????");
        }
        String logRemak = "??????ETC??????";
        if (coverType == 0) {
            logRemak = "????????????ETC??????";
        }
        List<Long> ids = new ArrayList<Long>();
        String[] ss = etcid.split(",");
        for (String s : ss) {
            ids.add(Long.valueOf(s));
            Long aLong = Long.valueOf(s);
            QueryWrapper<CmEtcInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", s); //??????ETCid??????
            List<CmEtcInfo> cmEtcInfos = baseMapper.selectList(queryWrapper);
            for (CmEtcInfo c : cmEtcInfos) {
                // ???????????????????????? ????????????????????????
                if (c.getOrderId() == null) {
                    throw new BusinessException("ETC?????????????????????????????????????????????????????????");
                }
                iOrderOpRecordService.saveOrUpdate(c.getOrderId(), 231, accessToken);//etc??????  ????????????
            }
            sysOperLogService.saveSysOperLog(loginInfo, com.youming.youche.commons.constant.SysOperLogConst.BusiCode.EtcConsume,
                    aLong, com.youming.youche.commons.constant.SysOperLogConst.OperType.Update, logRemak,
                    loginInfo.getTenantId());
        }
        return this.coverTableInfoByIds(ids, coverType, accessToken);
    }


    public String coverTableInfoByIds(List<Long> ids, int isCover, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        for (Long s : ids) {
            LambdaQueryWrapper<CmEtcInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CmEtcInfo::getId, s)
                    .eq(CmEtcInfo::getTenantId, loginInfo.getTenantId());
            CmEtcInfo cmEtcInfo = new CmEtcInfo();
            cmEtcInfo.setIsCover(isCover);
            cmEtcInfo.setOpId(loginInfo.getId());
            cmEtcInfo.setOpDate(LocalDateTime.now());
            cmEtcInfo.setTenantId(loginInfo.getTenantId());
            baseMapper.update(cmEtcInfo, wrapper);
        }
        return "Y";
    }

    /**
     * ?????????
     * ??????ETC
     *
     * @param plate_number     ?????????
     * @param order_id         ?????????
     * @param est_arrive_date  ????????????
     * @param real_arrive_date ????????????
     * @param cost_model       ????????????
     * @param vehicle_type     ????????????
     * @return
     */
    @Override
    public Page<CmEtcInfoDto> OrderETC(String plate_number, String order_id, String est_arrive_date, String real_arrive_date,
                                       Integer cost_model, Integer vehicle_type, String accessToken, Integer pageNum, Integer pageSize) {
        Page<CmEtcInfoDto> page = new Page<>(pageNum, pageSize);
        LoginInfo loginInfo = loginUtils.get(accessToken);
        CmEtcInfoVo vo = new CmEtcInfoVo();
        if (est_arrive_date != null && org.apache.commons.lang.StringUtils.isNotEmpty(est_arrive_date)) {
            vo.setBegCarDependDate(est_arrive_date);
        }
        if (real_arrive_date != null && org.apache.commons.lang.StringUtils.isNotEmpty(real_arrive_date)) {
            vo.setEndCarDependDate(real_arrive_date);
        }
        if (order_id != null && org.apache.commons.lang.StringUtils.isNotEmpty(order_id)) {
            vo.setOrderId(order_id);
        }
        if (plate_number != null && org.apache.commons.lang.StringUtils.isNotEmpty(plate_number)) {
            vo.setPlateNumber(plate_number);
        }
        if (cost_model != null) {
            vo.setPaymentWay(cost_model);
        }
        if (vehicle_type != null) {
            vo.setEtcUserType(vehicle_type);
        }
        String beginApplyTime = vo.getBegCarDependDate();
        String endApplyTime = vo.getEndCarDependDate();
        if (beginApplyTime != null && org.apache.commons.lang.StringUtils.isNotEmpty(beginApplyTime)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String applyTimeStr = beginApplyTime + " 00:00:00";
            LocalDateTime beginApplyTime1 = LocalDateTime.parse(applyTimeStr, dateTimeFormatter);
            vo.setBegCarDependDate1(beginApplyTime1);
        }
        if (endApplyTime != null && org.apache.commons.lang.StringUtils.isNotEmpty(endApplyTime)) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String endApplyTimeStr = endApplyTime + " 23:59:59";
            LocalDateTime endApplyTime1 = LocalDateTime.parse(endApplyTimeStr, dateTimeFormatter);
            vo.setEndCarDependDate1(endApplyTime1);
        }
        List<CmEtcInfoDto> infoDtos = new ArrayList<>();
        List<CmEtcInfoDto> records = baseMapper.OrderETC(vo, loginInfo.getTenantId());
        // ????????? ??????????????????
        List<CmEtcInfoDto> collect = records.stream().filter(cmEtcInfoDto -> cmEtcInfoDto.getOrderId() != null).collect(Collectors.toList());
        for (CmEtcInfoDto dto : collect) {
            dto.setPaymentWayName(dto.getPaymentWay() == null ? "" : getSysStaticData("PAYMENT_WAY", dto.getPaymentWay().toString()).getCodeName());
            // ????????????
            dto.setEtcUserTypeName(dto.getEtcUserType() == null ? "" : getSysStaticData("VEHICLE_CLASS", dto.getEtcUserType().toString()).getCodeName());
            LambdaQueryWrapper<EtcMaintain> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(EtcMaintain::getEtcId, dto.getEtcCardNo())
                    .eq(EtcMaintain::getTenantId, loginInfo.getTenantId());
            EtcMaintain etcMaintain = etcMaintainMapper.selectOne(wrapper);
            VehicleDataInfo vehicleDataInfo = null;
            if (etcMaintain != null && etcMaintain.getVehicleCode() != null) {
//                vehicleDataInfo =  iVehicleDataInfoService.get(etcMaintain.getVehicleCode());
                vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(dto.getPlateNumber());
            }
            if (vehicleDataInfo != null) {
                // ????????????
                if (vehicleDataInfo.getVehicleStatus() != null && vehicleDataInfo.getVehicleStatus() > 0) {
                    vehicleDataInfo.setVehicleStatusName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_STATUS", vehicleDataInfo.getVehicleStatus()));
                }
                //??????
                if (org.apache.commons.lang3.StringUtils.isNotBlank(vehicleDataInfo.getVehicleLength()) && !vehicleDataInfo.getVehicleLength().equals("-1")) {
                    if (vehicleDataInfo.getVehicleStatus() == 8) {
                        vehicleDataInfo.setVehicleLengthName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_STATUS_SUBTYPE", Long.valueOf(vehicleDataInfo.getVehicleLength())));
                    } else {
                        vehicleDataInfo.setVehicleLengthName(SysStaticDataRedisUtils.getSysStaticDataById2String(redisUtil, "VEHICLE_LENGTH", Long.valueOf(vehicleDataInfo.getVehicleLength())));
                    }
                }
                if (vehicleDataInfo.getVehicleStatus() != null) {
                    if (vehicleDataInfo.getVehicleStatus() > 0
                            && org.apache.commons.lang3.StringUtils.isNotBlank(vehicleDataInfo.getVehicleLength())
                            && !vehicleDataInfo.getVehicleLength().equals("-1")) {
                        dto.setVehicleInfo(vehicleDataInfo.getVehicleStatusName() + "/" + vehicleDataInfo.getVehicleLengthName());
                    } else if (vehicleDataInfo.getVehicleStatus() > 0 &&
                            (vehicleDataInfo.getVehicleLength() != null &&
                                    (org.apache.commons.lang3.StringUtils.isNotBlank(vehicleDataInfo.getVehicleLength()) || !vehicleDataInfo.getVehicleLength().equals("-1")))) {
                        dto.setVehicleInfo(vehicleDataInfo.getVehicleStatusName());
                    } else {
                        dto.setVehicleInfo(vehicleDataInfo.getVehicleLengthName());
                    }
                }
            }
//            boolean b = infoDtos.stream().anyMatch(cmEtcInfoDto -> cmEtcInfoDto.getId().equals(dto.getId()));
//            if (!b && dto.getTenantId().equals(loginInfo.getTenantId())) {
//                infoDtos.add(dto);
//            }
        }
        Page<CmEtcInfoDto> page1 = new Page<>(pageNum, pageSize);
        page1.setRecords(collect);
        page1.setTotal(collect.size());
        page1.setCurrent(pageNum);
        page1.setSize(pageSize);
        return page1;
    }

    @Override
    public List<CmEtcInfo> calculatedEtcFee(String accountStatementNo) {
        LambdaQueryWrapper<CmEtcInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CmEtcInfo::getAccountStatementNo, accountStatementNo);

        return baseMapper.selectList(queryWrapper);
    }

    @Override
    public Long calculatedEtcFeeMoney(String accountStatementNo) {
        return baseMapper.calculatedEtcFeeMoney(accountStatementNo);
    }

    @Override
    public List<CmEtcInfo> calculatedEtcFee(CalculatedEtcFeeVo calculatedEtcFeeVo) {
        return baseMapper.calculatedEtcVoList(calculatedEtcFeeVo);
    }

    @Override
    public Long calculatedEtcFeeMoney(CalculatedEtcFeeVo calculatedEtcFeeVo) {
        return baseMapper.calculatedEtcFeeVoMoney(calculatedEtcFeeVo);
    }

    @Override
    public List<CmEtcInfoOutDto> doQueryActualConsumptionEtcApp(Long orderId, Long tenantId) {
        LambdaQueryWrapper<CmEtcInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CmEtcInfo::getOrderId, orderId);

        // ??????ETC????????????
        List<CmEtcInfo> cmEtcInfoList = baseMapper.selectList(queryWrapper);
        List<CmEtcInfoOutDto> cmEtcInfoOutList = new ArrayList<>();
        if (cmEtcInfoList != null && cmEtcInfoList.size() > 0) {
            for (CmEtcInfo cmEtcInfo : cmEtcInfoList) {
                CmEtcInfoOutDto cmEtcInfoOut = new CmEtcInfoOutDto();
                BeanUtil.copyProperties(cmEtcInfo, cmEtcInfoOut);
                cmEtcInfoOut.setConsumeMoneyDouble(CommonUtil.getDoubleFormatLongMoney(cmEtcInfo.getConsumeMoney(), 2));

                if (cmEtcInfoOut.getEtcUserType() != null) {
                    cmEtcInfoOut.setEtcUserTypeName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue(redisUtil, "VEHICLE_CLASS", String.valueOf(cmEtcInfoOut.getEtcUserType())).getCodeName());
                }

                if (cmEtcInfoOut.getPaymentWay() != null) {
                    cmEtcInfoOut.setPaymentWayName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue(redisUtil, "PAYMENT_WAY", String.valueOf(cmEtcInfoOut.getPaymentWay())).getCodeName());
                }

                if (cmEtcInfoOut.getCardType() != null) {
                    cmEtcInfoOut.setCardTypeName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue(redisUtil, "ETC_CARD_TYPE", String.valueOf(cmEtcInfoOut.getCardType())).getCodeName());
                }

                if (cmEtcInfoOut.getPaymentType() != null) {
                    cmEtcInfoOut.setPaymentTypeName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue(redisUtil, "ETC_SERVICE_PAYMENT_TYPE", String.valueOf(cmEtcInfoOut.getPaymentType())).getCodeName());
                }

                if (cmEtcInfoOut.getIsCover() != null) {
                    cmEtcInfoOut.setIsCoverName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue(redisUtil, "IS_COVER_REPORT", String.valueOf(cmEtcInfoOut.getIsCover())).getCodeName());
                }

                if (cmEtcInfoOut.getSourceType() != null) {
                    cmEtcInfoOut.setSourceTypeName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue(redisUtil, "ETC_SOURCE_TYPE", String.valueOf(cmEtcInfoOut.getSourceType())).getCodeName());
                }

                if (cmEtcInfoOut.getChargingState() != null) {
                    cmEtcInfoOut.setChargingStateName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue(redisUtil, "ETC_CHARGING_STATE", String.valueOf(cmEtcInfoOut.getChargingState())).getCodeName());
                }
                cmEtcInfoOutList.add(cmEtcInfoOut);
            }
        }
        return cmEtcInfoOutList;
    }

    /**
     * ???????????????
     * ??????-ETC???????????????51001???
     * niejiewei
     *
     * @param userId etc ??????
     * @return
     */
    @Override
    public IPage<CmEtcInfoOutDto> getETCList(Long userId, String accessToken, Integer pageNum, Integer pageSize) {
        CmEtcInfoVo vo = new CmEtcInfoVo();
        if (userId != null) {
            vo.setUserId(userId);
        }
        Page<CmEtcInfo> all = this.getAll(vo, accessToken, pageNum, pageSize);
        List<CmEtcInfo> records = all.getRecords();
        List<CmEtcInfoOutDto> listOut = new ArrayList<>();
        for (CmEtcInfo c : records) {
            CmEtcInfoOutDto out = new CmEtcInfoOutDto();
            BeanUtil.copyProperties(c, out);
            out.setMarginBalanceDeduct((c.getMarginBalanceDeduct() == null ? 0 : c.getMarginBalanceDeduct()) + (c.getArrearsFee() == null ? 0 : c.getArrearsFee()));
            listOut.add(out);
        }
        if (listOut != null) {
            for (CmEtcInfoOutDto out : listOut) {
                //????????????=etc??????????????????(?????????)+???????????????????????????(?????????)+??????????????????????????????(?????????)+???????????????(?????????)
                out.setConsumeMoneyDetail(
                        (out.getEtcAmountDeduct() == null ? 0 : out.getEtcAmountDeduct()) +
                                (out.getWithdrawalAmountDeduct() == null ? 0 : out.getWithdrawalAmountDeduct()) +
                                (out.getMarginBalanceDeduct() == null ? 0 : out.getMarginBalanceDeduct()) +
                                (out.getAdvanceFee() == null ? 0 : out.getAdvanceFee())
                );
                if (org.apache.commons.lang3.StringUtils.isNotBlank(out.getAccountStatementNo()) && out.getChargingState() == 1) {//?????????????????????????????????
                    out.setIsBillPay(true);
                } else {
                    out.setIsBillPay(false);
                }
                if (out.getIsBillPay() != null && out.getIsBillPay()) {
                    out.setConsumeMoneyDetail(out.getConsumeMoney());
                }
                if (out.getEtcUserType() != null) {
                    if (out.getEtcUserType() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1 && out.getPaymentWay() == OrderConsts.PAYMENT_WAY.CONTRACT) {//?????????????????????
                        out.setIsShowDetails(true);
                    } else {
                        if (out.getEtcUserType() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2 || out.getEtcUserType() == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4) {//??????????????????????????????????????????????????????????????????
                            if (org.apache.commons.lang3.StringUtils.isNotBlank(out.getAccountStatementNo())) {
                                AccountStatement accountStatement = iAccountStatementService.getAccountStatementByBillNumber(out.getAccountStatementNo());
                                if (accountStatement != null) {
                                    if (userId.longValue() == accountStatement.getReceiverUserId()) {
                                        out.setIsShowDetails(true);
                                    } else {
                                        out.setIsShowDetails(false);
                                    }
                                }
                            } else {
                                out.setIsShowDetails(false);
                            }
                        } else {
                            out.setIsShowDetails(false);
                        }
                    }
                }
                if (out.getEtcUserType() != null) {
                    out.setEtcUserTypeName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue(redisUtil, "VEHICLE_CLASS", String.valueOf(out.getEtcUserType())).getCodeName());
                }

                if (out.getPaymentWay() != null) {
                    out.setPaymentWayName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue(redisUtil, "PAYMENT_WAY", String.valueOf(out.getPaymentWay())).getCodeName());
                }

                if (out.getCardType() != null) {
                    out.setCardTypeName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue(redisUtil, "ETC_CARD_TYPE", String.valueOf(out.getCardType())).getCodeName());
                }

                if (out.getPaymentType() != null) {
                    out.setPaymentTypeName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue(redisUtil, "ETC_SERVICE_PAYMENT_TYPE", String.valueOf(out.getPaymentType())).getCodeName());
                }

                if (out.getIsCover() != null) {
                    out.setIsCoverName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue(redisUtil, "IS_COVER_REPORT", String.valueOf(out.getIsCover())).getCodeName());
                }

                if (out.getSourceType() != null) {
                    out.setSourceTypeName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue(redisUtil, "ETC_SOURCE_TYPE", String.valueOf(out.getSourceType())).getCodeName());
                }

                if (out.getChargingState() != null) {
                    out.setChargingStateName(SysStaticDataRedisUtils.getSysStaticDataByCodeValue(redisUtil, "ETC_CHARGING_STATE", String.valueOf(out.getChargingState())).getCodeName());
                }
                out.setWithdrawalAmountDeduct((out.getWithdrawalAmountDeduct() == null ? 0 : out.getWithdrawalAmountDeduct()) +
                        (out.getArrearsFee() == null ? 0 : out.getArrearsFee()));
            }
        }
        Page<CmEtcInfoOutDto> page = new Page<>(pageNum, pageSize);
        page.setRecords(listOut);
        page.setTotal(all.getTotal());
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        return page;
    }

    @Override
    public List<CmEtcInfo> getCmEtcAccountStatement(String plateNumber, Long tenantId, String month, String receiverPhone, Long receiverUserId) {
        return baseMapper.getCmEtcAccountStatement(plateNumber, tenantId, month, receiverPhone, receiverUserId);
    }

    public void updateCmEtcAccountStatement(String plateNumber, Long tenantId, String month, String receiverPhone, Long receiverUserId, String billNumber) {
        baseMapper.updateCmEtcAccountStatement(plateNumber, tenantId, month, receiverPhone, receiverUserId, billNumber);
    }


    /**
     * ????????????
     *
     * @param date
     * @return
     */
    public LocalDateTime localDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }


    EtcOrderOutDto getOrderEtcInvestment1(CmEtcInfo cmEtcInfo, Long tenantId) {
        String etcCardNumber = cmEtcInfo.getEtcCardNo();
        long amountFee = cmEtcInfo.getConsumeMoney();
        long objId = cmEtcInfo.getId();
        log.info("ETC????????????:etcCardNumber=" + etcCardNumber + "amountFee=" + amountFee + "objId=" + objId);
        if (etcCardNumber == null || "".equals(etcCardNumber)) {
            throw new BusinessException("?????????ETC??????!");
        }
        if (amountFee <= 0) {
            throw new BusinessException("ETC??????????????????????????????0!");
        }
        if (cmEtcInfo.getState() != null) {
            // ETC ???  0????????????1??????
            if (cmEtcInfo.getState() != 1) {
                throw new BusinessException("ETC???????????????");
            }
        }
        // ?????? etc ????????????
        LambdaQueryWrapper<EtcMaintain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EtcMaintain::getEtcId, etcCardNumber)
                .eq(EtcMaintain::getTenantId, tenantId);
        EtcMaintain em = etcMaintainMapper.selectOne(wrapper);
        if (em == null) {
            throw new BusinessException("ETC????????????????????????");
        }

        // ?????????????????????????????????ETC????????????????????????????????????????????????????????????????????????
        // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        // A-B?????????B??????????????????Etc??????????????????????????????????????????????????????????????????????????????ETC???
        // B-D??????????????????????????????????????????????????????????????????????????????ETC

        /**
         * 1 ???????????????????????? ??? ??????
         * 2 ????????????????????????????????? ???????????? ???????????? ???????????????
         *  3 ??? etc ??????????????? ?????????????????? ?????????????????? ????????? ???????????????????????????
         *  ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
         *  ????????????????????????????????????
         */
        Long vehicleCode = em.getVehicleCode();
        if (vehicleCode == null || vehicleCode <= 0) {
            throw new BusinessException("ETC???????????????????????????!");
        }
        VehicleDataInfo vehicle = iVehicleDataInfoService.getVehicleDataInfo(em.getBindVehicle());
        List<OrderSchedulerDto> list = iOrderSchedulerService1.queryOrderInfoByCar(vehicle.getId(), tenantId, 0, 0, null, em.getBindVehicle());
        if (list == null || list.size() <= 0) {
            return null;
        }

        Date etcConsumeTime = DateUtil.asDate(cmEtcInfo.getEtcConsumeTime());// etc????????????
        if (etcConsumeTime == null) {
            throw new BusinessException("etc??????????????????!");
        }
        long carDriverId = 0l;
        long orderId = 0l;
        long toTenantId = 0l;
        String vehicleAffiliation = null;
        Map<String, Object> objTmp = null;
//        Collections.sort(list, new Comparator<Map<String, Object>>() {
//            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
//                long map1value = ((Date)o1.get("carDependDate")).getTime();
//                long map2value = ((Date)o2.get("carDependDate")).getTime();
////                    return map1value - map2value;
//                return String.valueOf(map1value).compareTo(String.valueOf(map2value));
//            }
//        });
        OrderSchedulerDto Tmp = null;
        for (int i = 0; i < list.size(); i++) {
            OrderSchedulerDto dto = list.get(i);
            Long timeCar = dto.getCarDependDate().getTime(); // ??????????????????
            Long timeEtc = etcConsumeTime.getTime(); // etc ????????????
            Long timeArr = dto.getCarArriveDate().getTime(); //??????????????????
            if (((dto.getCarDependDate() != null && (timeCar <= timeEtc)) && (dto.getCarArriveDate() != null && (timeEtc <= timeArr)))) {
                //??????????????????<=ETC????????????<=????????????????????????
                if (dto.getCarDependDate() != null) {
                    carDriverId = dto.getCarDriverId();
                }
                if (dto.getOrderId() != null) {
                    orderId = dto.getOrderId();
                }
                if (dto.getToTenantId() != null) {
                    toTenantId = dto.getToTenantId();
                }
                vehicleAffiliation = dto.getVehicleAffiliation();
                log.info("??????id???" + orderId);
                break;
            } else {
                //??????????????????<=ETC????????????<=????????????????????????
                if ((dto.getCarDependDate() != null && (dto.getCarArriveDate() != null && (timeEtc <= timeCar)))) {
                    if (objTmp == null) {
                        // ????????????????????????????????????ETC????????????<????????????????????????
                        if (dto.getCarDriverId() != null) {
                            carDriverId = dto.getCarDriverId();
                        }
                        if (dto.getOrderId() != null) {
                            orderId = dto.getOrderId();
                        }
                        if (dto.getToTenantId() != null) {
                            toTenantId = dto.getToTenantId();
                        }
                        vehicleAffiliation = dto.getVehicleAffiliation();
                        log.info("??????id???" + orderId);
                        break;
                    } else if (objTmp != null && (objTmp.get("carArriveDate") != null
                            && (etcConsumeTime.getTime() > ((Date) objTmp.get("carArriveDate")).getTime()))) {
                        // ETC????????????<???????????????????????? ??? ????????????ETC????????????>??????????????????
                        if (objTmp.get("carDriverId") != null) {
                            carDriverId = ((Number) objTmp.get("carDriverId")).longValue();
                        }
                        if (objTmp.get("orderId") != null) {
                            orderId = ((Number) objTmp.get("orderId")).longValue();
                        }
                        if (dto.getToTenantId() != null) {
                            toTenantId = dto.getToTenantId();
                        }
                        vehicleAffiliation = String.valueOf(objTmp.get("vehicleAffiliation"));
                        log.info("??????id???" + orderId);
                        break;
                    }
                }
            }
            Tmp = dto;
            if (i == (list.size() - 1)) {
                if (dto != null && (dto.getCarArriveDate() != null && (etcConsumeTime.getTime() > dto.getCarArriveDate().getTime()))) {
                    //????????????????????????????????????????????????????????????????????????
//                    if (Tmp.getCarDriverId() != null) {
//                        carDriverId = Tmp.getCarDriverId();
//                    }
//                    if (Tmp.getOrderId()!= null) {
//                        orderId = Tmp.getOrderId();
//                    }
//                    vehicleAffiliation =Tmp.getVehicleAffiliation();
//                    log.info("??????id???" + orderId);
                    break;
                }
            }
        }
        if (orderId == 0l) {
            return null;
        }
        EtcOrderOutDto etcOrderOut = new EtcOrderOutDto();
        etcOrderOut.setCarDriverId(carDriverId);
        etcOrderOut.setOrderId(orderId);
        etcOrderOut.setVehicleAffiliation(vehicleAffiliation);
        etcOrderOut.setEtcCardNumber(etcCardNumber);
        etcOrderOut.setPlateNumber(em.getBindVehicle());
        // ??????????????? ????????????
        OrderInfoExt orderInfoPaymentWay = iOrderInfoExtService.getOrderInfoPaymentWay(list.get(0).getOrderId(), tenantId);
        if (orderInfoPaymentWay != null && orderInfoPaymentWay.getPaymentWay() != null) {
            etcOrderOut.setPaymentWay(orderInfoPaymentWay.getPaymentWay());
        }
        SysUser sysOperator = iSysUserService.getSysOperatorByUserIdOrPhone(carDriverId, null, -1L);
        if (sysOperator == null) {
            throw new BusinessException("????????????????????????!");
        }
        etcOrderOut.setCarPhone(sysOperator.getBillId());
        etcOrderOut.setToTenantId(toTenantId);
        return etcOrderOut;

    }

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
