package com.youming.youche.finance.provider.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.DateUtil;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.finance.api.IAccountDetailsThreeService;
import com.youming.youche.finance.domain.AccountDetails;
import com.youming.youche.finance.dto.AccountQueryDetailDto;
import com.youming.youche.finance.provider.mapper.AccountDetailsMapper;
import com.youming.youche.finance.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.finance.vo.AccountQueryDetailVo;
import com.youming.youche.order.api.order.IOrderAccountService;
import com.youming.youche.order.domain.OrderAccount;
import com.youming.youche.order.domain.order.BusiSubjectsRel;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.system.api.IUserDataInfoService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.domain.UserDataInfo;
import com.youming.youche.system.utils.excel.ExportExcel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zengwen
 * @date 2022/4/12 17:16
 */
@Slf4j
@DubboService(version = "1.0.0")
            public class AccountDetailsThreeServiceImpl extends BaseServiceImpl<AccountDetailsMapper, AccountDetails> implements IAccountDetailsThreeService {

    @Resource
    private RedisUtil redisUtil;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @DubboReference(version = "1.0.0")
    IUserDataInfoService iUserDataInfoService;

    @DubboReference(version = "1.0.0")
    IOrderAccountService iOrderAccountService;

    @Override
    public IPage<AccountQueryDetailDto> getAccountQueryDetails(String accessToken, AccountQueryDetailVo accountQueryDetailVo) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
        Long tenantId = loginInfo.getTenantId();
        accountQueryDetailVo.setYyyyMonth(accountQueryDetailVo.getYyyyMonth().replace("-",""));

        try {
            baseMapper.queryAccountDetailTable(accountQueryDetailVo.getYyyyMonth());
        } catch (Exception e) {
            log.info("数据表还没有 返回没有记录的分页");
            return new Page<>(accountQueryDetailVo.getPageNum(), accountQueryDetailVo.getPageSize());
        }

        // 处理参数
        accountQueryDetailVo.setTenantId(tenantId);

        if (StringUtils.isNotEmpty(accountQueryDetailVo.getStartTime())) {
            accountQueryDetailVo.setStartTime(accountQueryDetailVo.getStartTime() + " 00:00:00");
        }

        if (StringUtils.isNotEmpty(accountQueryDetailVo.getEndTime())) {
            accountQueryDetailVo.setEndTime(accountQueryDetailVo.getEndTime() + " 23:59:59");
        }

        String businessNumbers = "";
        // businessNumber的特殊处理
        if (accountQueryDetailVo.getBusinessNumber() == null) {
            List<SysStaticData> staticDataList = SysStaticDataRedisUtils.getSysStaticDataList(redisUtil, "ACCOUNT_DETAILS_BUSINESS_NUMBER", tenantId);
            List<String> collect = staticDataList.stream().map(SysStaticData::getCodeValue).collect(Collectors.toList());
            businessNumbers = String.join(",", collect);
        }

        // 查询账单明细
        List<AccountQueryDetailDto> list = baseMapper.getAccountQueryDetailsList(accountQueryDetailVo, businessNumbers);
        Integer count = baseMapper.getAccountQueryDetailsCount(accountQueryDetailVo, businessNumbers);

        for (AccountQueryDetailDto accountQueryDetailDto : list) {
            accountQueryDetailDto.setOpDate(accountQueryDetailDto.getCreateDate());
            if (accountQueryDetailDto.getSubjectsId() != null) {
//                accountQueryDetailDto.setBusinessNumberName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(accountQueryDetailDto.getSubjectsId())).getCodeTypeAlias());
                accountQueryDetailDto.setBusinessNumberName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(accountQueryDetailDto.getBusinessNumber())).getCodeTypeAlias());
                accountQueryDetailDto.setBusinessTypesName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "APP_ACCOUNT_DETAILS_OUT", String.valueOf(accountQueryDetailDto.getSubjectsId())).getCodeTypeAlias());
                accountQueryDetailDto.setSubjectsName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "APP_ACCOUNT_DETAILS_OUT", String.valueOf(accountQueryDetailDto.getSubjectsId())).getCodeName());
            }

            if (accountQueryDetailDto.getCostType() != null && accountQueryDetailDto.getCostType() >= 0) {
                accountQueryDetailDto.setCostTypeName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId,"COST_TYPE", String.valueOf(accountQueryDetailDto.getCostType())).getCodeName());
            }

        }

        IPage<AccountQueryDetailDto> page = new Page<>();

        page.setCurrent(accountQueryDetailVo.getPageNum() + 1);
        page.setRecords(list);
        page.setSize(accountQueryDetailVo.getPageSize());
        page.setTotal(count);
        page.setPages(count % accountQueryDetailVo.getPageSize() == 0 ? count / accountQueryDetailVo.getPageSize() : (count / accountQueryDetailVo.getPageSize() + 1));
        return page;
    }

    @Async
    @Override
    public void downloadExcelFile(String accessToken, AccountQueryDetailVo accountQueryDetailVo, ImportOrExportRecords importOrExportRecords) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
        Long tenantId = loginInfo.getTenantId();
        accountQueryDetailVo.setYyyyMonth(accountQueryDetailVo.getYyyyMonth().replace("-",""));

        List<AccountQueryDetailDto> list = new ArrayList<>();
        try {
            baseMapper.queryAccountDetailTable(accountQueryDetailVo.getYyyyMonth());
            accountQueryDetailVo.setTenantId(tenantId);


            if (StringUtils.isNotEmpty(accountQueryDetailVo.getStartTime())) {
                accountQueryDetailVo.setStartTime(accountQueryDetailVo.getStartTime() + " 00:00:00");
            }

            if (StringUtils.isNotEmpty(accountQueryDetailVo.getEndTime())) {
                accountQueryDetailVo.setEndTime(accountQueryDetailVo.getEndTime() + " 23:59:59");
            }

            String businessNumbers = "";
            // businessNumber的特殊处理
            if (accountQueryDetailVo.getBusinessNumber() == null) {
                List<SysStaticData> staticDataList = SysStaticDataRedisUtils.getSysStaticDataList(redisUtil, "ACCOUNT_DETAILS_BUSINESS_NUMBER", tenantId);
                List<String> collect = staticDataList.stream().map(SysStaticData::getCodeValue).collect(Collectors.toList());
                businessNumbers = String.join(",", collect);
            }

            list = baseMapper.getAccountQueryDetailsList(accountQueryDetailVo, businessNumbers);

            for (AccountQueryDetailDto accountQueryDetailDto : list) {
                accountQueryDetailDto.setOpDate(accountQueryDetailDto.getCreateDate());
                if (accountQueryDetailDto.getSubjectsId() != null) {
                    accountQueryDetailDto.setBusinessNumberName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "ACCOUNT_DETAILS_BUSINESS_NUMBER", String.valueOf(accountQueryDetailDto.getBusinessNumber())).getCodeTypeAlias());
                    accountQueryDetailDto.setBusinessTypesName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "APP_ACCOUNT_DETAILS_OUT", String.valueOf(accountQueryDetailDto.getSubjectsId())).getCodeTypeAlias());
                    accountQueryDetailDto.setSubjectsName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId, "APP_ACCOUNT_DETAILS_OUT", String.valueOf(accountQueryDetailDto.getSubjectsId())).getCodeTypeAlias());
                }

                if (accountQueryDetailDto.getCostType() != null && accountQueryDetailDto.getCostType() >= 0) {
                    accountQueryDetailDto.setCostTypeName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, tenantId,"COST_TYPE", String.valueOf(accountQueryDetailDto.getCostType())).getCodeName());
                }
            }
        } catch (Exception e) {
            log.info("数据表还没有 返回没有记录的分页");
        }

        try {
            String[] showName = null;
            String[] resourceFild = null;
            String fileName = null;
            showName = new String[]{
                    "司机姓名", "司机手机", "业务类型",
                    "金额", "操作时间", "备注",
                    "单号"};
            resourceFild = new String[]{
                    "getLinkman", "getMobilePhone", "getBusinessNumberName",
                    "getAmountDouble", "getOpDate", "getNote",
                    "getOrderId"};

            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(list, showName, resourceFild, AccountQueryDetailDto.class,
                    null);

            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "account.xlsx", inputStream.available());
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
    public void saveAccountDetails(AccountDetails details) {
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("yyyyMM", new String[] { DateUtil.formatDate(new Date(), DateUtil.YEAR_MONTH_FORMAT2) });
//        Session session = SysContexts.getEntityManager(map);
//        session.save(details);
//        this.saveAccountDetailsSummary(details);
        //  TODO 流水表(数据汇总不按月存入)  this.saveAccountDetailsSummary(details);
        String tableName = com.youming.youche.util.DateUtil.formatDate(new Date(), com.youming.youche.util.DateUtil.YEAR_MONTH_FORMAT2);
        Integer tableIsExist = baseMapper.tableIsExist(tableName);
        if (tableIsExist != null && tableIsExist > 0) {//表存在
            if (details.getId() == null || details.getId() <= 0) { //记录不存在
                baseMapper.insertTable(details, tableName);
            } else {
                baseMapper.updateTable(details, tableName);
            }
        } else {
            // 新建表
            baseMapper.createTable(tableName);
            baseMapper.insertTable(details, tableName);
        }
//        this.save(details);
    }

    @Override
    public Long insetAccDetOaLoan(Integer businessTypes, Long businessCode, Long otherUserId, String otherName,
                                  String vehicleAffiliation, VehicleDataInfo v, List<BusiSubjectsRel> subjectsList,
                                  Long soNbr, Long orderId, String toUserName, OrderAccount account, Long tenantId, Integer userType, String accessToken) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
//        AccountDetailsSV accountDetailsSV = (AccountDetailsSV)  SysContexts.getBean("accountDetailsSV");
//        log.info(subjectsList);
        long amountFeeSum = 0;
        // 志鸿账户流水金额
        if (subjectsList != null && subjectsList.size() > 0) {
            for (int i = 0; i < subjectsList.size(); i++) {
                BusiSubjectsRel busiSubjectsRel = subjectsList.get(i);
                // 写入账户明细表
                AccountDetails accDet = new AccountDetails();
//                accDet.setAccountId(account.getAccId());
                accDet.setAccountId(account.getId());
                accDet.setUserId(otherUserId);
                //会员体系开始
                accDet.setUserType(userType);
                //会员体系结束
                accDet.setBusinessTypes(businessTypes);
                accDet.setBusinessNumber(businessCode);
                accDet.setSubjectsId(busiSubjectsRel.getSubjectsId());
                accDet.setSubjectsName(busiSubjectsRel.getSubjectsName());
                // 支出
                if (busiSubjectsRel.getSubjectsType() == EnumConsts.PayInter.FEE_OUT) {
                    accDet.setAmount(-busiSubjectsRel.getAmountFee());
                    accDet.setCurrentAmount(0l);
                }
                // 支入
                if (busiSubjectsRel.getSubjectsType() == EnumConsts.PayInter.FEE_IN) {
                    accDet.setAmount(busiSubjectsRel.getAmountFee());
                    accDet.setCurrentAmount(0l);
                }
                accDet.setCostType(busiSubjectsRel.getSubjectsType());
                accDet.setCreateTime(DateUtil.dateTransition(new Date()));
                accDet.setNote(busiSubjectsRel.getSubjectsName());
                accDet.setSoNbr(soNbr);
                accDet.setOrderId(orderId + "");
                accDet.setOtherUserId(otherUserId);
                accDet.setOtherName(otherName);
                accDet.setBookType(Long.parseLong(busiSubjectsRel.getBookType()));
                accDet.setTenantId(tenantId);
                if (v != null) {
                    accDet.setEtcNumber(v.getEtcCardNumber());
                }
                accDet.setVehicleAffiliation(vehicleAffiliation);
//                accountDetailsSV.saveAccountDetails(accDet);
                this.saveAccountDetails(accDet);
            }
        }
        return amountFeeSum;
    }

    @Override
    public OrderAccount queryOrderAccount(Long userId, String vehicleAffiliation, Long tenantId, Long sourceTenantId,
                                          String oilAffiliation, Integer userType,String accessToken) {
        LoginInfo loginInfo = (LoginInfo) redisUtil.get("loginInfo:" + accessToken);
//        IUserSV userSV = (UserSV) SysContexts.getBean("userSV");
//        IOrderAccountSV orderAccountSV = (OrderAccountSV) SysContexts.getBean("orderAccountSV");
//        log.info("userId=" + userId + "资金渠道类型=" + vehicleAffiliation + "tenantId=" + tenantId +  "sourceTenantId=" + sourceTenantId);
        if (userId <= 0L) {
            throw new BusinessException("用户id有误");
        }
        if (sourceTenantId <= 0L) {
            throw new BusinessException("请输入资金来源租户id");
        }
        if (org.apache.commons.lang.StringUtils.isBlank(vehicleAffiliation)) {
            throw new BusinessException("请输入资金渠道");
        }
        if (org.apache.commons.lang.StringUtils.isBlank(oilAffiliation)) {
            throw new BusinessException("请输入油资金渠道");
        }
        if (userType == null || userType <= 0) {
            throw new BusinessException("请输入用户类型");
        }
        // 通过userid获取用户信息
//        UserDataInfo user = userSV.getUserDataInfo(userId);
        UserDataInfo userDataInfo = iUserDataInfoService.getUserDataInfo(userId);
        if (userDataInfo == null) {
            throw new BusinessException("没有找用户信息!");
        }
//        OrderAccount orderAccount = orderAccountSV.queryOrderAccount(userId, vehicleAffiliation, sourceTenantId, oilAffiliation,userType);
        OrderAccount orderAccount = iOrderAccountService.queryOrderAccount(userId, vehicleAffiliation, sourceTenantId, oilAffiliation, userType);
        if (orderAccount == null) {
            OrderAccount newOrderAccount = new OrderAccount();
            newOrderAccount.setUserId(userId);
            //会员体系改造开始
            newOrderAccount.setUserType(userType);
            //会员体系改造结束
            newOrderAccount.setVehicleAffiliation(vehicleAffiliation);
            newOrderAccount.setAccState(1);//有效
            newOrderAccount.setCreateDate(DateUtil.dateTransition(new Date()));
            newOrderAccount.setAccLevel(1);
            newOrderAccount.setTenantId(loginInfo.getTenantId());
            newOrderAccount.setSourceTenantId(sourceTenantId);
            newOrderAccount.setOilAffiliation(oilAffiliation);
//            orderAccountSV.saveOrUpdate(newOrderAccount);
            iOrderAccountService.saveOrUpdate(newOrderAccount);
            return newOrderAccount;
        } else {
            return orderAccount;
        }
    }


}
