package com.youming.youche.market.provider.service.facilitator;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.util.StringUtil;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.facilitator.IConsumeOilFlowService;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.api.user.IUserRepairInfoService;
import com.youming.youche.market.api.youka.IConsumeOilFlowExtService;
import com.youming.youche.market.api.youka.IOilCardManagementService;
import com.youming.youche.market.api.youka.IRechargeConsumeRecordService;
import com.youming.youche.market.api.youka.IVoucherInfoService;
import com.youming.youche.market.domain.facilitator.ConsumeOilFlow;
import com.youming.youche.market.domain.facilitator.OilTransaction;
import com.youming.youche.market.domain.facilitator.ServiceInfo;
import com.youming.youche.market.domain.user.UserRepairInfo;
import com.youming.youche.market.domain.youka.ConsumeOilFlowExt;
import com.youming.youche.market.domain.youka.OilCardManagement;
import com.youming.youche.market.domain.youka.RechargeConsumeRecord;
import com.youming.youche.market.dto.facilitator.ConsumeOilFlowDto;
import com.youming.youche.market.dto.facilitator.ServiceUnexpiredDetailDto;
import com.youming.youche.market.dto.facilitator.UserRepairInfoDto;
import com.youming.youche.market.dto.youca.ConsumeOilFlowDtos;
import com.youming.youche.market.dto.youca.RechargeConsumeRecordDto;
import com.youming.youche.market.dto.youca.RechargeConsumeRecordOut;
import com.youming.youche.market.provider.mapper.facilitator.ConsumeOilFlowMapper;
import com.youming.youche.market.provider.mapper.youka.ConsumeOilFlowExtMapper;
import com.youming.youche.market.provider.mapper.youka.OilCardManagementsMapper;
import com.youming.youche.market.provider.mapper.youka.RechargeConsumeRecordMapper;
import com.youming.youche.market.provider.utis.CommonUtil;
import com.youming.youche.market.provider.utis.LocalDateTimeUtil;
import com.youming.youche.market.provider.utis.SysStaticDataRedisUtils;
import com.youming.youche.market.vo.facilitator.FacilitatorVo;
import com.youming.youche.market.vo.facilitator.criteria.ServiceInfoQueryCriteria;
import com.youming.youche.market.vo.youca.ConsumeOilFlowVo;
import com.youming.youche.order.api.order.IUserRepairMarginService;
import com.youming.youche.order.domain.order.UserRepairMargin;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.util.ExcelFilesVaildate;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-02-09
 */
@DubboService(version = "1.0.0")
@Service
public class ConsumeOilFlowServiceImpl extends ServiceImpl<ConsumeOilFlowMapper, ConsumeOilFlow> implements IConsumeOilFlowService {
    @Resource
    private ConsumeOilFlowMapper consumeOilFlowMapper;
    private static Pattern linePattern = Pattern.compile("_(\\w)");
    @Resource
    ConsumeOilFlowExtMapper consumeOilFlowExtMapper;
    @DubboReference(version = "1.0.0")
    IConsumeOilFlowExtService iConsumeOilFlowExtService;
    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;
    @Resource
    RechargeConsumeRecordMapper rechargeConsumeRecordMapper;
    @Lazy
    @Resource
    IRechargeConsumeRecordService iRechargeConsumeRecordService;
    @Lazy
    @Resource
    private IOilCardManagementService iOilCardManagementService;
    @Resource
    OilCardManagementsMapper oilCardManagementsMapper;
    @Lazy
    @Resource
    IServiceInfoService iServiceInfoServicel;
    @Lazy
    @Resource
    IUserRepairInfoService userRepairInfoService;

    @DubboReference(version = "1.0.0")
    IUserRepairMarginService userRepairMarginService;

    @DubboReference(version = "1.0.0")
    IVoucherInfoService iVoucherInfoService;

    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter dfs = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
    @Resource
    LoginUtils loginUtils;

    @Resource
    RedisUtil redisUtil;

    @Override
    public IPage queryConsumeOilFlow(Integer pageSize, Integer pageNum,OilTransaction oilTransaction, String accessToken) {
        LoginInfo baseUser = loginUtils.get(accessToken);
        LocalDateTime now = LocalDateTime.now();
        Long tenantId = baseUser.getTenantId();
        if (oilTransaction.getServiceType() == 1 || oilTransaction.getServiceType()  == null) {
            IPage<ConsumeOilFlowDto> pagination = null;
            Page<ConsumeOilFlowDto> userDataInfoPage = new Page<>(pageNum, pageSize);
            pagination = consumeOilFlowMapper.getUserOilFlow(userDataInfoPage,oilTransaction, -1, -1,tenantId);
            List<ConsumeOilFlowDto> list = pagination.getRecords();
            if (list != null && list.size() > 0) {
                for (ConsumeOilFlowDto dto : list) {
                    Integer sts = dto.getState();
                    if (sts == 0) {
//                        if(StringUtils.isNotBlank(dto.getGetDate()) && now.isAfter(LocalDateTimeUtil.convertStringToDate(dto.getGetDate()))){
//                             LambdaQueryWrapper<ConsumeOilFlow> lambda= Wrappers.lambdaQuery();
//                             lambda.eq(ConsumeOilFlow::getOrderId,dto.getOrderId()).orderByDesc(ConsumeOilFlow::getId).last("limit 1");
//                            ConsumeOilFlow oilFlow = this.getOne(lambda);
//                            if(oilFlow != null){
//                                oilFlow.setState(1);
//                                oilFlow.setGetResult("已到期");
//                                this.updateById(oilFlow);
//                            }
//                            dto.setStateName("已到期");
//                        }else{
//                            dto.setStateName("未到期");
//                        }
                    dto.setStateName("未到期");
                    } else if (sts == 1) {
                        dto.setStateName("已到期");
                    }
                }
            }
            pagination.setRecords(list);
            return pagination;
        } else if (oilTransaction.getServiceType()  == 2) {
            IPage<UserRepairInfoDto> pagination = null;
            Page<UserRepairInfoDto> userDataInfoPage = new Page<>(pageNum, pageSize);
            pagination = consumeOilFlowMapper.getUserRepairList(userDataInfoPage, oilTransaction,tenantId,0);
            List<UserRepairInfoDto> list = pagination.getRecords();
            if (list != null && list.size() > 0) {
                for (UserRepairInfoDto dto : list) {
                    Integer sts = dto.getState();
                    if (sts == 0) {
//                        if(StringUtils.isNotBlank(dto.getGetDate()) && now.isAfter(LocalDateTimeUtil.convertStringToDate(dto.getGetDate()))){
//                            UserRepairMargin repairMargin = userRepairMarginService.getUserRepairMarginByRepairCode(dto.getRepairCode());
//                            if(repairMargin != null){
//                                repairMargin.setState(1);
//                                repairMargin.setGetResult("已到期");
//                                userRepairMarginService.updateById(repairMargin);
//                                UserRepairInfo repairInfo = userRepairInfoService.getById(repairMargin.getRepairId());
//                                if(repairInfo != null && repairInfo.getRepairState() != null && repairInfo.getRepairState()== 6){
//                                    repairInfo.setRepairState(7);
//                                    repairInfo.setGetResult("已到期");
//                                    userRepairInfoService.updateById(repairInfo);
//                                }
//                            }
//                            dto.setStateName("已到期");
//                        }else{
//                            dto.setStateName("未到期");
//                        }
                        dto.setStateName("未到期");
                    } else if (sts == 1) {
                        dto.setStateName("已到期");
                    }
                }
            }
            pagination.setRecords(list);
            return pagination;
        }
        return null;
    }

    @Override
    public List<UserRepairInfoDto> queryUserRepairInfo(Long serviceUserId,String statrtTime,String endTime,Long tenantId) {

        List<UserRepairInfoDto> list = consumeOilFlowMapper.getUserRepairListByUserId(serviceUserId,statrtTime,endTime,tenantId);
//        if (list != null && list.size() > 0) {
//            for (UserRepairInfoDto dto : list) {
//                Integer sts = dto.getState();
//                if (sts == 0) {
//                    dto.setStateName("未到期");
//                } else if (sts == 1) {
//                    dto.setStateName("已到期");
//                }
//            }
//        }

        return list;
    }

    @Override
    public List<ConsumeOilFlowDto> queryConsumeOilFlow(Long serviceUserId,String statrtTime,String endTime,Long tenantId) {
            List<ConsumeOilFlowDto> list = consumeOilFlowMapper.getUserOilFlowByUserId(serviceUserId,statrtTime,endTime,tenantId);
//            if (list != null && list.size() > 0) {
//                for (ConsumeOilFlowDto dto : list) {
//                    Integer sts = dto.getState();
//                    if (sts == 0) {
//                        dto.setStateName("未到期");
//                    } else if (sts == 1) {
//                        dto.setStateName("已到期");
//                    }
//                }
//            }
            return list;
    }

    @Override
    public Object totalOilTransaction(Integer pageSize, Integer pageNum, OilTransaction oilTransaction, String accessToken) throws Exception {

        LoginInfo baseUser = loginUtils.get(accessToken);
        Long tenantId = baseUser.getTenantId();
        //1.油站   2.维保
        if (oilTransaction.getServiceType() == 1 || oilTransaction.getServiceType() == null) {
            ConsumeOilFlowDto oilFlowDto = consumeOilFlowMapper.totalUserOilFlow(oilTransaction,-1,-1,tenantId);
            Map<String, String> map = new HashMap<>();
            map.put("amountAll", "0");
            map.put("amountUse", "0");
            if (oilFlowDto != null) {
                map.put("amountAll", oilFlowDto.getAmountAll().toString());
                map.put("amountUse", oilFlowDto.getAmountUse().toString());
            }
            return map;
        } else if (oilTransaction.getServiceType() == 2) {
            List<UserRepairInfoDto> list = consumeOilFlowMapper.getUserRepairListCount(oilTransaction,tenantId,1);
            Map<String, String> map = new HashMap<>();
            map.put("amountAll", "0");
            map.put("amountUse", "0");
            if (list != null && list.size() != 0) {
                Double amountAll = list.get(0).getAmountAll();
                map.put("amountAll", amountAll.toString());
                map.put("amountUse", list.get(0).getAmountUse().toString());
            }
            return map;
        }
        Map<String, String> map = new HashMap<>();
        map.put("amountAll", "0");
        map.put("amountUse", "0");
        return map;
    }

    @Override
    public void queryConsumeOilFlowExport(ImportOrExportRecords importOrExportRecords, String orderId, String tradeTimeStart, String tradeTimeEnd, String consumerName, String consumerBill, String settlTimeStart, String settlTimeEnd, Integer state, Long productId, String accessToken, Integer serviceType) {
        LoginInfo baseUser = loginUtils.get(accessToken);
        Long tenantId = baseUser.getTenantId();
        if (serviceType == 1 || serviceType == null) {
            List<ConsumeOilFlowDto> consumeOilFlowDto = consumeOilFlowMapper.getUserOilFlowExport(orderId, tradeTimeStart, tradeTimeEnd, consumerName, consumerBill, settlTimeStart,
                    settlTimeEnd, state, tenantId, productId, -1, -1);
            if (consumeOilFlowDto != null && consumeOilFlowDto.size() > 0) {
                for (ConsumeOilFlowDto dto : consumeOilFlowDto) {
                    Integer sts = dto.getState();
                    if (sts == 0) {
                        dto.setStateName("未到期");
                    } else if (sts == 1) {
                        dto.setStateName("已到期");
                    }
                    dto.setAmount(dto.getAmount() != null? dto.getAmount()/100 : 0.0);
                    dto.setOilPriceStr(dto.getOilPrice() != null ? dto.getOilPrice().doubleValue()/100:0.0);
                }
            }
            try {
                String[] showName = null;
                String[] resourceFild = null;
                String fileName = null;
                showName = new String[]{"单号", "交易时间", "消费金额(元)", "单价(元/升)", "加油升数(升)",
                        "消费人", "消费人手机", "车牌号", "消费地址", "结算时间", "状态"};
                resourceFild = new String[]{"getOrderId", "getCreateDate", "getAmount", "getOilPriceStr", "getOilRise",
                        "getOtherName", "getOtherUserBill", "getPlateNumber", "getAddress", "getGetDate", "getStateName"};
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(consumeOilFlowDto, showName, resourceFild, ConsumeOilFlowDto.class,
                        null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();

                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream = new ByteArrayInputStream(b);
                //上传文件
                FastDFSHelper client = FastDFSHelper.getInstance();
                String path = client.upload(inputStream, "交易记录-油站.xlsx", inputStream.available());
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
        } else if (serviceType == 2) {
            List<UserRepairInfoDto> userRepairInfoDto = consumeOilFlowMapper.getUserRepairListExport(orderId, tradeTimeStart, tradeTimeEnd, consumerName, consumerBill, settlTimeStart, settlTimeEnd, state, tenantId, productId, 0);

            if (userRepairInfoDto != null && userRepairInfoDto.size() > 0) {
                for (UserRepairInfoDto dto : userRepairInfoDto) {
                    Integer sts = dto.getState();
                    if (sts == 0) {
                        dto.setStateName("未到期");
                    } else if (sts == 1) {
                        dto.setStateName("已到期");
                    }
                    dto.setAmount(dto.getAmount() != null ? dto.getAmount()/100:0.0);
                }
            }
            try {
                String[] showName = null;
                String[] resourceFild = null;
                String fileName = null;
                showName = new String[]{"单号", "交易时间", "消费金额(元)",
                        "消费人", "消费人手机", "车牌号", "消费地址", "结算时间", "状态"};
                resourceFild = new String[]{"getOrderId", "getCreateTime", "getAmount",
                        "getOtherName", "getOtherUserBill", "getPlateNumber", "getAddress", "getGetDate", "getStateName"};
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(userRepairInfoDto, showName, resourceFild, UserRepairInfoDto.class,
                        null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();

                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream = new ByteArrayInputStream(b);
                //上传文件
                FastDFSHelper client = FastDFSHelper.getInstance();
                String path = client.upload(inputStream, "交易记录-维保.xlsx", inputStream.available());
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
    }

    @Override
    public IPage<com.youming.youche.market.dto.youca.ConsumeOilFlowDto> queryConsumeOilFlowList(Page<com.youming.youche.market.dto.youca.ConsumeOilFlowDto> page, ConsumeOilFlowVo consumeOilFlowVo, String accessToken) {
        LoginInfo baseUser = loginUtils.get(accessToken);
        List<Long> voucherInfoList = baseMapper.queryTenantId(baseUser.getTenantId());
//        List<Long> flowIds = new ArrayList<Long>();
//        for (Object obj : voucherInfoList) {
//            flowIds.add(Long.valueOf(obj + ""));
//        }
        List<Long> extFlowIds = new ArrayList<Long>();
        if (consumeOilFlowVo.getRebate()!=null && consumeOilFlowVo.getRebate() > -1) {
            if (voucherInfoList != null && voucherInfoList.size() > 0) {
                consumeOilFlowExtMapper.selectByIds(voucherInfoList);
            }
            if (consumeOilFlowVo.getRebate() > -1) {
                consumeOilFlowExtMapper.selectQuaryState(consumeOilFlowVo.getRebate());

            }
            List<ConsumeOilFlowExt> extList = iConsumeOilFlowExtService.list();
            for (ConsumeOilFlowExt ext : extList) {
                extFlowIds.add(ext.getFlowId());
            }
        }
        Page<com.youming.youche.market.dto.youca.ConsumeOilFlowDto> voucherInfoPage = baseMapper.queryAll(page, baseUser.getTenantId(), consumeOilFlowVo);
        List<com.youming.youche.market.dto.youca.ConsumeOilFlowDto> records = voucherInfoPage.getRecords();
        List<Long> resultFlowIds = new ArrayList<Long>();
        for (com.youming.youche.market.dto.youca.ConsumeOilFlowDto record : records) {
            resultFlowIds.add(Long.valueOf(record.getId() + ""));
        }
        List<Long> extMap = new ArrayList<Long>();
        if (resultFlowIds != null && resultFlowIds.size() > 0) {
            List<ConsumeOilFlowExt> extList = consumeOilFlowExtMapper.selectByresultFlowIds(resultFlowIds);
            if (extList != null && extList.size() > 0) {
                for (ConsumeOilFlowExt ext : extList) {
                    extMap.add(Long.valueOf(ext.getFlowId()+""));
                }
            }
        }
//        List<com.youming.youche.market.dto.youca.ConsumeOilFlowDto> retList = new ArrayList<com.youming.youche.market.dto.youca.ConsumeOilFlowDto>();
        for (com.youming.youche.market.dto.youca.ConsumeOilFlowDto consumeOilFlowDto : records) {
            long flowId = Long.valueOf(consumeOilFlowDto.getId() + "");
            Matcher matcher = linePattern.matcher(consumeOilFlowDto + "");
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
            }
            matcher.appendTail(sb);


//			if(extMap.get(flowId)!=null){
//				ConsumeOilFlowExt ext = (ConsumeOilFlowExt) extMap.get(flowId);
//				dataMap.put("cardType", ext.getOilConsumer());
//				dataMap.put("cardTypeName", SysStaticDataUtil.getSysStaticData("RECHARGE_CONSUME_RECORD_SOURCE_TYPE",String.valueOf(ext.getOilConsumer())).getCodeName());
//			}else{
//				dataMap.put("cardType", 4);
//				dataMap.put("cardTypeName", SysStaticDataUtil.getSysStaticData("4","RECHARGE_CONSUME_RECORD_SOURCE_TYPE").getCodeName());
//			}

            consumeOilFlowDto.setConsumeDate(consumeOilFlowDto.getCreateTime());
            consumeOilFlowDto.setCardHolder(consumeOilFlowDto.getOtherName());
            consumeOilFlowDto.setCardHolderBill(consumeOilFlowDto.getOtherUserBill());
            if (consumeOilFlowDto.getState()!=null){
                if (consumeOilFlowDto.getState()==1) {
                    consumeOilFlowDto.setIsExpire("已到期");
                }else {
                    consumeOilFlowDto.setIsExpire("未到期");
                }
            }
            consumeOilFlowDto.setConsuemStation(consumeOilFlowDto.getAddress());
            consumeOilFlowDto.setSourceTypeName("扫码加油");//来源类型
            consumeOilFlowDto.setCardTypeName(getSysStaticDataByCodeValue("ETC_CARD_TYPE", String.valueOf(consumeOilFlowDto.getCardType())).getCodeName());//卡类型
            consumeOilFlowDto.setCardHolderBill(consumeOilFlowDto.getOtherUserBill());
            if (extMap!= null && extMap.size()>0) {
                LambdaQueryWrapper<ConsumeOilFlowExt> queryWrapper=new LambdaQueryWrapper<>();
                queryWrapper.eq(ConsumeOilFlowExt::getFlowId,flowId);
                List<ConsumeOilFlowExt> consumeOilFlowExts = consumeOilFlowExtMapper.selectList(queryWrapper);
                ConsumeOilFlowExt consumeOilFlowExt =null;
                if (consumeOilFlowExts!=null) {
                    consumeOilFlowExt = consumeOilFlowExts.get(0);
                }
//                ConsumeOilFlowExt consumeOilFlowExt = consumeOilFlowExtMapper.selectById(flowId);
                consumeOilFlowDto.setFleetRebateAmountDouble(CommonUtil.getDoubleFormatLongMoney(consumeOilFlowExt.getRebate() == null ? 0 : consumeOilFlowExt.getRebate(), 2).toString());
            }
            if (consumeOilFlowDto.getIsShare() == null) {
                consumeOilFlowDto.setIsShare(2);
            } else {
                consumeOilFlowDto.setIsShare(consumeOilFlowDto.getIsShare());
            }
//            int isShare = consumeOilFlowDto.getIsShare();
            consumeOilFlowDto.setOilStationTypeName(getSysStaticDataByCodeValue("OIL_STATION_TYPE", String.valueOf(consumeOilFlowDto.getIsShare())).getCodeName());//油站类型
            consumeOilFlowDto.setCardNum(consumeOilFlowDto.getOtherUserBill());//卡号
            consumeOilFlowDto.setOilRiseDouble(consumeOilFlowDto.getOilRise());//加油升数
            consumeOilFlowDto.setUnitPriceDouble(CommonUtil.getDoubleFormatLongMoney(consumeOilFlowDto.getOilPrice() == null ? 0 : consumeOilFlowDto.getOilPrice(), 2).toString());//单价
            consumeOilFlowDto.setAmountDouble(CommonUtil.getDoubleFormatLongMoney(consumeOilFlowDto.getAmount() == null ? 0 : consumeOilFlowDto.getAmount(), 2).toString());//金额
            consumeOilFlowDto.setBalanceDouble(CommonUtil.getDoubleFormatLongMoney(consumeOilFlowDto.getBalance() == null ? 0 : consumeOilFlowDto.getBalance(), 2).toString());//卡余额
        }

        return page.setRecords(records);
    }

    /**
     * 油卡消费 导出
     *
     * @param accessToken
     * @param record
     * @param consumeOilFlowVo
     */
    @Override
    @Async
    public void queryAllListExport(String accessToken, ImportOrExportRecords record, ConsumeOilFlowVo consumeOilFlowVo, Integer pageNum,
                                   Integer pageSize) {
//        LoginInfo loginInfo = loginUtils.get(accessToken);
//        List<com.youming.youche.market.dto.youca.ConsumeOilFlowDto> list = baseMapper.queryAllListExport(loginInfo.getTenantId());
//        IPage<RechargeConsumeRecordOut> rechargeConsumeRecords = iVoucherInfoService.getRechargeConsumeRecords(consumeOilFlowVo.getOrderId(),
//                "", consumeOilFlowVo.getRecordStartTime(), consumeOilFlowVo.getRecordEndTime(),
//                "2", consumeOilFlowVo.getCardType(), "", consumeOilFlowVo.getCardNum(),
//                consumeOilFlowVo.getVoucherId(), consumeOilFlowVo.getPlateNumber(), consumeOilFlowVo.getFromType(),
//                consumeOilFlowVo.getServiceName(), consumeOilFlowVo.getRebate(), accessToken, consumeOilFlowVo.getAddress(),
//                consumeOilFlowVo.getDealRemark() , pageNum,
//                 9999);
//        List<RechargeConsumeRecordOut> records = rechargeConsumeRecords.getRecords();
//        for (RechargeConsumeRecordOut out :records) {
//
//        }
        List<com.youming.youche.market.dto.youca.ConsumeOilFlowDto> consumeOilFlowDtos=null;
        if(consumeOilFlowVo.getFromType()==4){//扫码加油
            LoginInfo baseUser = loginUtils.get(accessToken);
            List<Long> voucherInfoList = baseMapper.queryTenantId(baseUser.getTenantId());
            List<Long> extFlowIds = new ArrayList<Long>();
            if (consumeOilFlowVo.getRebate()!=null && consumeOilFlowVo.getRebate() > -1) {
                if (voucherInfoList != null && voucherInfoList.size() > 0) {
                    consumeOilFlowExtMapper.selectByIds(voucherInfoList);
                }
                if (consumeOilFlowVo.getRebate() > -1) {
                    consumeOilFlowExtMapper.selectQuaryState(consumeOilFlowVo.getRebate());
                }
                List<ConsumeOilFlowExt> extList = iConsumeOilFlowExtService.list();
                for (ConsumeOilFlowExt ext : extList) {
                    extFlowIds.add(ext.getFlowId());
                }
            }
            consumeOilFlowDtos = baseMapper.queryAllExport(baseUser.getTenantId(), consumeOilFlowVo);
            List<Long> resultFlowIds = new ArrayList<Long>();
            for (com.youming.youche.market.dto.youca.ConsumeOilFlowDto con : consumeOilFlowDtos) {
                resultFlowIds.add(Long.valueOf(con.getId() + ""));
            }
            List<Long> extMap = new ArrayList<Long>();
            if (resultFlowIds != null && resultFlowIds.size() > 0) {
                List<ConsumeOilFlowExt> extList = consumeOilFlowExtMapper.selectByresultFlowIds(resultFlowIds);
                if (extList != null && extList.size() > 0) {
                    for (ConsumeOilFlowExt ext : extList) {
                        extMap.add(Long.valueOf(ext.getFlowId()+""));
                    }
                }
            }
//        List<com.youming.youche.market.dto.youca.ConsumeOilFlowDto> retList = new ArrayList<com.youming.youche.market.dto.youca.ConsumeOilFlowDto>();
            for (com.youming.youche.market.dto.youca.ConsumeOilFlowDto consumeOilFlowDto : consumeOilFlowDtos) {
                long flowId = Long.valueOf(consumeOilFlowDto.getId() + "");
                Matcher matcher = linePattern.matcher(consumeOilFlowDto + "");
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
                }
                matcher.appendTail(sb);


//			if(extMap.get(flowId)!=null){
//				ConsumeOilFlowExt ext = (ConsumeOilFlowExt) extMap.get(flowId);
//				dataMap.put("cardType", ext.getOilConsumer());
//				dataMap.put("cardTypeName", SysStaticDataUtil.getSysStaticData("RECHARGE_CONSUME_RECORD_SOURCE_TYPE",String.valueOf(ext.getOilConsumer())).getCodeName());
//			}else{
//				dataMap.put("cardType", 4);
//				dataMap.put("cardTypeName", SysStaticDataUtil.getSysStaticData("4","RECHARGE_CONSUME_RECORD_SOURCE_TYPE").getCodeName());
//			}

                consumeOilFlowDto.setConsumeDate(consumeOilFlowDto.getCreateTime());
                consumeOilFlowDto.setCardHolder(consumeOilFlowDto.getOtherName());
                consumeOilFlowDto.setCardHolderBill(consumeOilFlowDto.getOtherUserBill());
                if (consumeOilFlowDto.getState()!=null){
                    if (consumeOilFlowDto.getState()==1) {
                        consumeOilFlowDto.setIsExpire("已到期");
                    }else {
                        consumeOilFlowDto.setIsExpire("未到期");
                    }
                }
                consumeOilFlowDto.setConsuemStation(consumeOilFlowDto.getAddress());
                consumeOilFlowDto.setSourceTypeName("扫码加油");//来源类型
                consumeOilFlowDto.setCardTypeName(getSysStaticDataByCodeValue("ETC_CARD_TYPE", String.valueOf(consumeOilFlowDto.getCardType())).getCodeName());//卡类型
                consumeOilFlowDto.setCardHolderBill(consumeOilFlowDto.getOtherUserBill());
                if (extMap!= null && extMap.size()>0) {
                    LambdaQueryWrapper<ConsumeOilFlowExt> queryWrapper=new LambdaQueryWrapper<>();
                    queryWrapper.eq(ConsumeOilFlowExt::getFlowId,flowId);
                    List<ConsumeOilFlowExt> consumeOilFlowExts = consumeOilFlowExtMapper.selectList(queryWrapper);
                    ConsumeOilFlowExt consumeOilFlowExt =null;
                    if (consumeOilFlowExts!=null) {
                        consumeOilFlowExt = consumeOilFlowExts.get(0);
                    }
//                ConsumeOilFlowExt consumeOilFlowExt = consumeOilFlowExtMapper.selectById(flowId);
                    consumeOilFlowDto.setFleetRebateAmountDouble(CommonUtil.getDoubleFormatLongMoney(consumeOilFlowExt.getRebate() == null ? 0 : consumeOilFlowExt.getRebate(), 2).toString());
                }
                if (consumeOilFlowDto.getIsShare() == null) {
                    consumeOilFlowDto.setIsShare(2);
                } else {
                    consumeOilFlowDto.setIsShare(consumeOilFlowDto.getIsShare());
                }
                consumeOilFlowDto.setOilStationTypeName(getSysStaticDataByCodeValue("OIL_STATION_TYPE", String.valueOf(consumeOilFlowDto.getIsShare())).getCodeName());//油站类型
                consumeOilFlowDto.setCardNum(consumeOilFlowDto.getOtherUserBill());//卡号
                consumeOilFlowDto.setOilRiseDouble(consumeOilFlowDto.getOilRise());//加油升数
                consumeOilFlowDto.setUnitPriceDouble(CommonUtil.getDoubleFormatLongMoney(consumeOilFlowDto.getOilPrice() == null ? 0 : consumeOilFlowDto.getOilPrice(), 2).toString());//单价
                consumeOilFlowDto.setAmountDouble(CommonUtil.getDoubleFormatLongMoney(consumeOilFlowDto.getAmount() == null ? 0 : consumeOilFlowDto.getAmount(), 2).toString());//金额
                consumeOilFlowDto.setBalanceDouble(CommonUtil.getDoubleFormatLongMoney(consumeOilFlowDto.getBalance() == null ? 0 : consumeOilFlowDto.getBalance(), 2).toString());//卡余额
            }
        }

        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{
                    "单号", "消费时间", "卡号", "来源类型", "服务商", "车牌号 ", "司机", "司机手机", "加油量(升)", "单价(元)", "金额", "卡余额",
                    "卡类型","商品类别","商品名称","油站名称","油站类型","消费地点","到期类型","结算时间"};
            resourceFild = new String[]{
                    "getOrderId", "getCreateTime", "getCardNum", "getSourceTypeName", "getServiceName", "getPlateNumber", "getOtherName", "getOtherUserBill", "getOilRiseDouble", "getUnitPriceDouble", "getAmountDouble","getBalanceDouble",
                    "getCardTypeName","getGoodsType","getGoodsName","getProductName","getOilStationTypeName","getConsuemStation","getIsExpire","getGetDate"
//                    "getConsumeDate", "getCardNum", "getSourceTypeName",
//                    "getPlateNumber", "getCardHolder", "getOilRise",
//                    "getOilPrice", "getAmount", "getCardType",
//                    "getProductName", "getOilStationTypeName", "getConsuemStation",
//                    "getIsExpire"
            };
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(consumeOilFlowDtos, showName, resourceFild,
                    com.youming.youche.market.dto.youca.ConsumeOilFlowDto.class, null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "油卡消费记录.xlsx", inputStream.available());
            os.close();
            inputStream.close();
            record.setMediaUrl(path);
            record.setState(2);
            importOrExportRecordsService.saveRecords(record, accessToken);
        } catch (Exception e) {
            record.setState(3);
            try {
                importOrExportRecordsService.saveRecords(record, accessToken);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            e.printStackTrace();
        }
    }


    /**
     * 中石化记录导入
     *
     * @param bytes
     * @param record
     * @param accessToken
     */
    @Async
    @Transactional
    @Override
    public void batchImport(byte[] bytes, ImportOrExportRecords record, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<RechargeConsumeRecordDto> failureList = new ArrayList<>();
        InputStream inputStream = new ByteArrayInputStream(bytes);
        List<List<String>> lists = new ArrayList<>();
        try {
            lists = getExcelContent(inputStream, 4, (ExcelFilesVaildate[]) null);
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
            //List<Map> paramList = new ArrayList<>();
            //Map<String, Integer> checkMap = new HashMap<String, Integer>();
            int success = 0;
            for (List<String> l : lists) {
                StringBuffer reasonFailure = new StringBuffer();
                if(l.size() != 13){
                    continue;
                }
                j++;
                RechargeConsumeRecord rechargeConsumeRecord = new RechargeConsumeRecord();
                RechargeConsumeRecordDto rechargeConsumeRecordDto = new RechargeConsumeRecordDto();
                String cardNum = l.get(0);// 卡号
                String cardHolder = l.get(1);// 持卡人
                String consumeDate = l.get(2); // 交易时间
                String recordingName = l.get(3); //交易类型
                String amount = l.get(4);//金额
                String goodsName = l.get(5);// 油品
                String oilRise = l.get(6);//数量 (加油升)
                String unitPrice = l.get(7);//单价
                String integral = l.get(8);//奖励积分
                String balance = l.get(9);//余额
                String consuemStation = l.get(10);// 地点
                if (StringUtil.isEmpty(cardNum)) {
                    reasonFailure.append("第" + l + "油卡卡号未输入!");
                }

                if (StringUtil.isEmpty(cardHolder)) {
                    reasonFailure.append("第" + l + "持卡人未输入!");
                }

                if (consumeDate == null) {
                    reasonFailure.append("第" + l + "交易时间未输入!");
                }

                if (recordingName != null) {
                    if (!recordingName.equals(SysStaticDataEnum.RECORDING_TYPE.RECORDING_TYPE_NAME3)) {
                        reasonFailure.append("第" + l + "交易类型输入有勿!");
                    }
                    if (recordingName.equals(SysStaticDataEnum.RECORDING_TYPE.RECORDING_TYPE_NAME3)) {
                        rechargeConsumeRecord.setRecordingName(recordingName);
                        rechargeConsumeRecord.setRecordType(SysStaticDataEnum.RECORDING_TYPE.RECORDING_TYP2);
                    } else {
                        rechargeConsumeRecord.setRecordingName(recordingName);
                        rechargeConsumeRecord.setRecordType(SysStaticDataEnum.RECORDING_TYPE.RECORDING_TYP1);
                    }
                } else {
                    reasonFailure.append("第" + l + "交易类型未输入!");
                }

//                Number num = Float.parseFloat(amount) * 100;
//                int oamount = num.intValue();
                if (amount == null && com.alibaba.nacos.api.utils.StringUtils.isBlank(amount)) {
                    reasonFailure.append("第" + l + "金额未输入!");
                }

                if (goodsName == null && com.alibaba.nacos.api.utils.StringUtils.isBlank(goodsName)) {
                    reasonFailure.append("第" + l + "油品未输入!");
                }

                if (oilRise == null&& com.alibaba.nacos.api.utils.StringUtils.isBlank(oilRise)) {
                    reasonFailure.append("第" + l + "数量未输入!");
                }

                if (unitPrice == null && com.alibaba.nacos.api.utils.StringUtils.isBlank(unitPrice)) {
                    reasonFailure.append("第" + l + "单价未输入!");
                }

                if (integral == null && com.alibaba.nacos.api.utils.StringUtils.isBlank(integral)) {
                    reasonFailure.append("第" + l + "奖励积分未输入!");
                }

                if (balance == null && com.alibaba.nacos.api.utils.StringUtils.isBlank(balance)) {
                    reasonFailure.append("第" + l + "余额未输入!");
                }

                if (consuemStation == null  && com.alibaba.nacos.api.utils.StringUtils.isBlank(consuemStation)) {
                    reasonFailure.append("第" + l + "地点未输入!");
                }
                // TODO 待处理
                try {
                    if (consumeDate!=null&&  StringUtils.isNotEmpty(consumeDate)){
                        rechargeConsumeRecord.setConsumeDate(LocalDateTime.parse(consumeDate, df));
                    }
                }catch (Exception e){
                    reasonFailure.append("第" + l + "时间格式不对!");
                }
                // 根据卡号 查询消费记录 一个卡号不能有两条相同消费时间的 消费数据
                LambdaQueryWrapper<RechargeConsumeRecord>  queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(RechargeConsumeRecord::getCardNum,cardNum)
                        .eq(RechargeConsumeRecord::getTenantId,loginInfo.getTenantId())
                        .eq(RechargeConsumeRecord::getConsumeDate,rechargeConsumeRecord.getConsumeDate());
                List<RechargeConsumeRecord> rechargeConsumeRecords = rechargeConsumeRecordMapper.selectList(queryWrapper);
                // 说明数据库已经有了这个卡号 这个消费时间 的数据
                if (rechargeConsumeRecords!=null && rechargeConsumeRecords.size()>0){
                    reasonFailure.append("第" + l + "一个卡号不能有两条相同时间的消费记录!");
                }
                rechargeConsumeRecord.setCardNum(cardNum);
                rechargeConsumeRecord.setCardHolder(cardHolder);
                rechargeConsumeRecord.setGoodsName(goodsName);
                rechargeConsumeRecord.setConsuemStation(consuemStation);
                rechargeConsumeRecord.setCardType(SysStaticDataEnum.SERVIER_TYPE.SERVIER_TYPE2);
                rechargeConsumeRecord.setRecordSource(1);// 来源类型车队
                rechargeConsumeRecord.setPlatformRebateAmount(0L);
                rechargeConsumeRecord.setFleetRebateAmount(0L);
                rechargeConsumeRecord.setServiceRebateAmount(0L);
                rechargeConsumeRecord.setProductId(0L);
                rechargeConsumeRecord.setTenantId(loginInfo.getTenantId());
                rechargeConsumeRecord.setOpId(loginInfo.getId());
                if (unitPrice!=null && StringUtils.isNotEmpty(unitPrice)){
                    Double anew = Double.parseDouble(unitPrice);
                    Long b = new Double(anew * 100.00).longValue();
                    rechargeConsumeRecord.setUnitPrice(b);// 单价
                }
                if (oilRise!=null &&  StringUtils.isNotEmpty(oilRise) ){
                    Double anew = Double.parseDouble(oilRise);
                    Long b = new Double(anew * 100.00).longValue();
                    rechargeConsumeRecord.setOilRise(b);// 加油升数
                }
                if (amount!=null &&  StringUtils.isNotEmpty(amount) ){
                    Double anew = Double.parseDouble(amount);
                    Long b = new Double(anew * 100.00).longValue();
                    rechargeConsumeRecord.setAmount(b);//金额
                }
                if (balance!=null &&  StringUtils.isNotEmpty(balance) ){
                    Double anew = Double.parseDouble(balance);
                    Long b = new Double(anew * 100.00).longValue();
                    rechargeConsumeRecord.setBalance(b);//余额
                }
                // TODO 查系统中的 油卡
                LambdaQueryWrapper<OilCardManagement> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(OilCardManagement::getTenantId, loginInfo.getTenantId());
                List<OilCardManagement> oilCardManagements = oilCardManagementsMapper.selectList(wrapper);
                List<OilCardManagement> collect = oilCardManagements.stream()
                        .filter(oilCardManagement -> oilCardManagement.getOilCarNum()
                                .equals(cardNum) && oilCardManagement.getTenantId() == loginInfo.getTenantId())
                        .collect(Collectors.toList());
                if (collect != null && collect.size() > 0) {
                    rechargeConsumeRecord.setSourceType(collect.get(0).getCardType());
                    rechargeConsumeRecord.setServiceUserId(collect.get(0).getUserId());
                    // TODO  查询 服务商信息
                    ServiceInfoQueryCriteria criteria = new ServiceInfoQueryCriteria();
                    //
                    Page<FacilitatorVo> facilitatorVoPage = iServiceInfoServicel.queryFacilitator(criteria, 1, 999, accessToken);
                    List<FacilitatorVo> records = facilitatorVoPage.getRecords();
                    List<FacilitatorVo> collect1 = records.stream().filter(facilitatorVo -> facilitatorVo.getServiceUserId()
                            .equals(rechargeConsumeRecord.getServiceUserId())).collect(Collectors.toList());
                    FacilitatorVo facilitatorVo = null;
                    if (collect1 != null && collect1.size() > 0) {
                        facilitatorVo = collect1.get(0);
                    }
                    if (facilitatorVo != null) {
                        rechargeConsumeRecord.setServiceName(facilitatorVo.getServiceName());
                        rechargeConsumeRecord.setServiceUserId(facilitatorVo.getServiceUserId());
                    } else {
//                        reasonFailure.append("第" + l + "系统中没有此服务商信息!");
                    }
                } else {//如果充值时油卡不存在，则新建油卡，油卡的来源类型为自购油卡。
                    rechargeConsumeRecord.setSourceType(SysStaticDataEnum.OIL_CARD_TYPE.OWN);
//                if (SysStaticDataEnum.RECORDING_TYPE.RECORDING_TYP1 == rechargeConsumeRecord.getRecordType()) {
                    OilCardManagement oilCardManagement = new OilCardManagement();
                    oilCardManagement.setTenantId(loginInfo.getTenantId());
                    oilCardManagement.setOilCardStatus(1);
                    oilCardManagement.setCardType(SysStaticDataEnum.OIL_CARD_TYPE.OWN);
                    oilCardManagement.setOilCarNum(rechargeConsumeRecord.getCardNum());
                    if (balance!=null &&  StringUtils.isNotEmpty(balance) ){
                        Double anew = Double.parseDouble(balance);
                        Long b = new Double(anew * 100.00).longValue();
                        oilCardManagement.setCardBalance(b);// 余额
                    }else {
                        oilCardManagement.setCardBalance(0L);// 余额
                    }
                    iOilCardManagementService.add(oilCardManagement, accessToken);
//                }
                }
                if (StrUtil.isEmpty(reasonFailure)) {
                    iRechargeConsumeRecordService.save(rechargeConsumeRecord);
                    //返回成功数量
                    success++;
                } else {
                    rechargeConsumeRecordDto.setReasonFailure(reasonFailure.toString());
                    rechargeConsumeRecordDto.setCardNum(rechargeConsumeRecord.getCardNum());
                    failureList.add(rechargeConsumeRecordDto);
                }
            }
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(failureList)) {
                String[] showName;
                String[] resourceFild;
                showName = new String[]{
                        "卡号", "导入失败原因"
//                    "持卡人", "交易时间", "交易类型",
//                    "金额", "油品", "数量", "单价", "奖励积分", "余额", "地点"
                };
                resourceFild = new String[]{
                        "getCardNum", "getReasonFailure"
//                    "getCardHolder", "getConsumeDate", "getRecordingName",
//                    "getAmount", "getGoodsName", "getOilRise", "getUnitPrice", "getIntegral", "getBalance", "getConsuemStation"
                };
                try {
                    XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(failureList, showName, resourceFild, RechargeConsumeRecordDto.class, null);
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    workbook.write(os);
                    byte[] b = os.toByteArray();
                    InputStream inputStream1 = new ByteArrayInputStream(b);
                    //上传文件
                    FastDFSHelper client = FastDFSHelper.getInstance();
                    String failureExcelPath = client.upload(inputStream1, "中石化记录.xls", inputStream1.available());
                    os.close();
                    inputStream.close();
                    record.setFailureUrl(failureExcelPath);
                }catch (Exception e) {
                    e.printStackTrace();
                }
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


    /**
     * 中石油记录导入
     *
     * @param bytes
     * @param record
     * @param accessToken
     */
    @Async
    @Transactional
    @Override
    public void importYou(byte[] bytes, ImportOrExportRecords record, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        List<RechargeConsumeRecordDto> failureList = new ArrayList<>();
        InputStream inputStream = new ByteArrayInputStream(bytes);
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
                j++;
                RechargeConsumeRecord rechargeConsumeRecord = new RechargeConsumeRecord();
                RechargeConsumeRecordDto rechargeConsumeRecordDto = new RechargeConsumeRecordDto();
                String id = l.get(0);// 交易流水号
                String cardNum = l.get(1);//卡号
                String recordingName = l.get(2);//交易类型
                String amount = l.get(3);// 金额
                String balance = l.get(4);//余额
                String unitPrice = l.get(5);//单位
                String consumetype = l.get(6);//交易状态
                String consumeDate = l.get(7);// 交易时间
                String consuemStation = l.get(8);//交易地点
                String goodstype = l.get(9);//商品类别
                String goodsName = l.get(10);//商品名称
                String oilRise = l.get(11);//油量


                if (StringUtil.isEmpty(id)) {
                    reasonFailure.append("第" + l + "流水号未输入!");
                }

                if (StringUtil.isEmpty(cardNum)) {
                    reasonFailure.append("第" + l + "卡号未输入!");
                }

                if (recordingName != null) {// 交易类型
                    if (recordingName.equals(SysStaticDataEnum.RECORDING_TYPE.RECORDING_TYPE_NAME3)) {
                        rechargeConsumeRecord.setRecordType(SysStaticDataEnum.RECORDING_TYPE.RECORDING_TYP2);
                    } else {
                        rechargeConsumeRecord.setRecordType(SysStaticDataEnum.RECORDING_TYPE.RECORDING_TYP1);
                    }
                } else {
                    reasonFailure.append("第" + l + "交易类型未输入!");
                }


                if (amount == null || StringUtils.isBlank(amount)) {
                    reasonFailure.append("第" + l + "金额未输入!");
                }

                if (balance == null || StringUtils.isBlank(balance)) {
                    reasonFailure.append("第" + l + "余额未输入!");
                }

                if (unitPrice == null ||  StringUtils.isBlank(unitPrice)) {
                    reasonFailure.append("第" + l + "单位未输入!");
                }

                if (consumetype == null || StringUtils.isBlank(consumetype)) {
                    reasonFailure.append("第" + l + "交易状态未输入!");
                }

                if (consumetype == null ||  StringUtils.isBlank(consumetype)) {

                    reasonFailure.append("第" + l + "消费時間未输入!");
                }

                if (consuemStation == null  || StringUtils.isBlank(consuemStation)) {
                    reasonFailure.append("第" + l + "交易地点未输入!");
                }

                if (goodstype == null || StringUtils.isBlank(goodstype)) {
                    reasonFailure.append("第" + l + "商品类别未输入!");
                }
                if (goodsName == null|| StringUtils.isBlank(goodsName)) {
                    reasonFailure.append("第" + l + "油品名称未输入!");
                }

                if (oilRise == null || StringUtils.isBlank(oilRise)) {
                    reasonFailure.append("第" + l + "油量未输入!");
                }
//                rechargeConsumeRecord.setOrderId(Long.valueOf(id));// 交易流水号
                rechargeConsumeRecord.setCardNum(cardNum);//  卡号
                // TODO 待处理
                try {
                    if (consumeDate!=null&&  StringUtils.isNotEmpty(consumeDate)){
                        rechargeConsumeRecord.setConsumeDate(LocalDateTime.parse(consumeDate, df));// 消费时间
                    }
                }catch (Exception e){
                    reasonFailure.append("第" + l + "时间格式不对!");
                }
                // 根据卡号 查询消费记录 一个卡号不能有两条相同消费时间的 消费数据
                LambdaQueryWrapper<RechargeConsumeRecord>  queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(RechargeConsumeRecord::getCardNum,cardNum)
                        .eq(RechargeConsumeRecord::getTenantId,loginInfo.getTenantId())
                        .eq(RechargeConsumeRecord::getConsumeDate,rechargeConsumeRecord.getConsumeDate());
                List<RechargeConsumeRecord> rechargeConsumeRecords = rechargeConsumeRecordMapper.selectList(queryWrapper);
                // 说明数据库已经有了这个卡号 这个消费时间 的数据
                if (rechargeConsumeRecords!=null && rechargeConsumeRecords.size()>0){
                    reasonFailure.append("第" + l + "一个卡号不能有两条相同时间的消费记录!");
                }
                rechargeConsumeRecord.setConsuemStation(consuemStation);// 消费地点
                rechargeConsumeRecord.setGoodsType(goodstype);// 商品类别
                rechargeConsumeRecord.setGoodsName(goodsName);// 油品名称
                rechargeConsumeRecord.setCardType(SysStaticDataEnum.SERVIER_TYPE.SERVIER_TYPE1);
                rechargeConsumeRecord.setProductId(0l);
                rechargeConsumeRecord.setOpId(loginInfo.getId());
                rechargeConsumeRecord.setRecordSource(1);// 来源类型
                rechargeConsumeRecord.setTenantId(loginInfo.getTenantId());
                rechargeConsumeRecord.setPlatformRebateAmount(0L);
                rechargeConsumeRecord.setFleetRebateAmount(0L);
                rechargeConsumeRecord.setServiceRebateAmount(0L);
                if (oilRise!=null &&  StringUtils.isNotEmpty(oilRise) ){
                    Double anew = Double.parseDouble(oilRise);
                    Long b = new Double(anew * 100.00).longValue();
                    rechargeConsumeRecord.setOilRise(b);// 加油升数
                }
                if (amount!=null &&  StringUtils.isNotEmpty(amount) ){
                    Double anew = Double.parseDouble(amount);
                    Long b = new Double(anew * 100.00).longValue();
                    rechargeConsumeRecord.setAmount(b);//金额
                }
                if (balance!=null &&  StringUtils.isNotEmpty(balance) ){
                    Double anew = Double.parseDouble(balance);
                    Long b = new Double(anew * 100.00).longValue();
                    rechargeConsumeRecord.setBalance(b);//余额
                }
                // TODO 获取住户下的油卡信息
                LambdaQueryWrapper<OilCardManagement> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(OilCardManagement::getTenantId, loginInfo.getTenantId());
                List<OilCardManagement> oilCardManagements = oilCardManagementsMapper.selectList(wrapper);
                List<OilCardManagement> collect = oilCardManagements.stream()
                        .filter(oilCardManagement -> oilCardManagement.getOilCarNum()
                                .equals(cardNum) && oilCardManagement.getTenantId() == loginInfo.getTenantId())
                        .collect(Collectors.toList());

                if (collect != null && collect.size() > 0) {
                    rechargeConsumeRecord.setSourceType(collect.get(0).getCardType());
                    rechargeConsumeRecord.setServiceUserId(collect.get(0).getUserId());
                    // TODO  查询 服务商信息
                    ServiceInfoQueryCriteria criteria = new ServiceInfoQueryCriteria();
                    Page<FacilitatorVo> facilitatorVoPage = iServiceInfoServicel.queryFacilitator(criteria, 1,
                            999, accessToken);
                    List<FacilitatorVo> records = facilitatorVoPage.getRecords();
                    List<FacilitatorVo> collect1 = records.stream().filter(facilitatorVo -> facilitatorVo.getServiceUserId()
                            .equals(rechargeConsumeRecord.getServiceUserId())).collect(Collectors.toList());
                    FacilitatorVo facilitatorVo = null;
                    if (collect1 != null && collect1.size() > 0) {
                        facilitatorVo = collect1.get(0);
                    }
                    if (facilitatorVo != null) {
                        rechargeConsumeRecord.setServiceName(facilitatorVo.getServiceName());
                        rechargeConsumeRecord.setServiceUserId(facilitatorVo.getServiceUserId());
                    }
//                    else {
//                        reasonFailure.append("第" + l + "系统中没有此服务商信息!");
//                    }
                } else { //  //如果充值时油卡不存在，则新建油卡，油卡的来源类型为自购油卡。
                    rechargeConsumeRecord.setSourceType(SysStaticDataEnum.OIL_CARD_TYPE.OWN);
//                if (SysStaticDataEnum.RECORDING_TYPE.RECORDING_TYP1== rechargeConsumeRecord.getRecordType()){
                    OilCardManagement oilCardManagement = new OilCardManagement();
                    oilCardManagement.setTenantId(loginInfo.getTenantId());
                    oilCardManagement.setOilCardStatus(1);
                    oilCardManagement.setCardType(SysStaticDataEnum.OIL_CARD_TYPE.OWN);
                    oilCardManagement.setOilCarNum(rechargeConsumeRecord.getCardNum());
                    if (balance!=null &&  StringUtils.isNotEmpty(balance) ){
                        Double anew = Double.parseDouble(balance);
                        Long b = new Double(anew * 100.00).longValue();
                        oilCardManagement.setCardBalance(b);// 余额
                    }else {
                        oilCardManagement.setCardBalance(0L);// 余额
                    }
                    iOilCardManagementService.add(oilCardManagement, accessToken);
//                }
                }
                if (StrUtil.isEmpty(reasonFailure)) {
                    iRechargeConsumeRecordService.save(rechargeConsumeRecord);
                    //返回成功数量
                    success++;
                } else {
                    rechargeConsumeRecordDto.setReasonFailure(reasonFailure.toString());
                    rechargeConsumeRecordDto.setCardNum(rechargeConsumeRecord.getCardNum());
                    failureList.add(rechargeConsumeRecordDto);
                }
            }
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(failureList)) {
                String[] showName;
                String[] resourceFild;
                showName = new String[]{"卡号", "导入失败原因"
//                    "交易流水号", "卡号", "交易类型",
//                    "金额", "余额",
//                    "交易时间", "交易地点",
//                    "商品名称", "油量"
                };
                resourceFild = new String[]{"getCardNum", "getReasonFailure"

//                    "getId", "getCardNum", "getRecordType",
//                    "getAmount", "getBalance",
//                    "getConsumeDate", "getConsuemStation",
//                    "getGoodsName", "getOilRise"
                };
                try {
                    XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(failureList, showName, resourceFild, RechargeConsumeRecordDto.class, null);
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    workbook.write(os);
                    byte[] b = os.toByteArray();
                    InputStream inputStream1 = new ByteArrayInputStream(b);
                    //上传文件
                    FastDFSHelper client = FastDFSHelper.getInstance();
                    String failureExcelPath = client.upload(inputStream1, "中石油记录.xlsx", inputStream1.available());
                    os.close();
                    inputStream.close();
                    inputStream1.close();
                    record.setFailureUrl(failureExcelPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    @Override
    public List sumConsumeOilFlow(ConsumeOilFlowVo consumeOilFlowVo, String accessToken) {
        LoginInfo baseUser = loginUtils.get(accessToken);

        Long tenantId = null;
        tenantId = (tenantId == null || tenantId <= 0L) ? baseUser.getTenantId() : tenantId;
        List<Long> voucherInfoList = baseMapper.queryTenantId(baseUser.getTenantId());
        List<Long> flowIds = new ArrayList<Long>();
        for (Long obj : voucherInfoList) {
            flowIds.add(Long.valueOf(obj + ""));
        }
        List<Long> extFlowIds = new ArrayList<Long>();
        if (consumeOilFlowVo.getRebate()!=null && consumeOilFlowVo.getRebate() > -1) {
            if (flowIds != null && flowIds.size() > 0) {
                consumeOilFlowExtMapper.selectByresultFlowIds(flowIds);
            }
            if (consumeOilFlowVo.getRebate() > -1) {
                consumeOilFlowExtMapper.selectQuaryState(consumeOilFlowVo.getRebate());

            }
            List<ConsumeOilFlowExt> extList = iConsumeOilFlowExtService.list();
            for (ConsumeOilFlowExt ext : extList) {
                extFlowIds.add(ext.getFlowId());
            }
        }

        List<ConsumeOilFlowDtos> list = baseMapper.queryAllSum(baseUser.getTenantId(), consumeOilFlowVo);
        return list;
    }

    @Override
    public IPage<ServiceUnexpiredDetailDto> getServiceUnexpiredDetail(String orderId, String name, String sourceTenantId, String accessToken, Integer pageNum, Integer pageSize) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Long userId = loginInfo.getUserInfoId();
        ServiceInfo serviceInfo = iServiceInfoServicel.getServiceInfoById(userId);
        Integer serviceType = serviceInfo.getServiceType();
        Page<ServiceUnexpiredDetailDto> page = new Page<ServiceUnexpiredDetailDto>(pageNum, pageSize);

        return baseMapper.getServiceUnexpiredDetail(page, orderId, name, sourceTenantId, userId, serviceType);
    }

    @Override
    public List<ConsumeOilFlowVo> getConsumeOilFlowProductIds(List<Long> productIds) {
        return baseMapper.getConsumeOilFlowProductIds(productIds);
    }

    /**
     *
     * 聂杰伟
     * 统计支付次数
     * @return
     */
    @Override
    public Integer paymentTimes(String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String format = sdf.format(date);
        LambdaQueryWrapper<ConsumeOilFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConsumeOilFlow::getOtherUserId,loginInfo.getUserInfoId());
        wrapper.like(ConsumeOilFlow::getCreateTime,format);
        List<ConsumeOilFlow> consumeOilFlows = baseMapper.selectList(wrapper);
        return consumeOilFlows.size();
    }

    @Override
    public Long getTotalMarginBalance(Long serviceUserId, Long tenantId) {
        ServiceInfo serviceInfo = iServiceInfoServicel.getServiceInfoById(serviceUserId);
        if (serviceInfo.getServiceType() == null || serviceInfo.getServiceType() == 1) {
            return baseMapper.getTotalMarginBalance(serviceUserId, tenantId);
        } else if (serviceInfo.getServiceType() == 2){
            return baseMapper.getTotalMarginBalanceTwo(serviceUserId, tenantId);
        } else {
            return 0L;
        }
    }

    public SysStaticData getSysStaticDataByCodeValue(String codeType, String codeValue) {
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

