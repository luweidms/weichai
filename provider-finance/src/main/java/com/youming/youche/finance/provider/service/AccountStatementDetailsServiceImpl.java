package com.youming.youche.finance.provider.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.OrderAccountConst;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.IAccountStatementDetailsService;
import com.youming.youche.finance.api.IAccountStatementService;
import com.youming.youche.finance.api.IAccountStatementTemplateFieldService;
import com.youming.youche.finance.domain.AccountStatement;
import com.youming.youche.finance.domain.AccountStatementDetails;
import com.youming.youche.finance.domain.AccountStatementDetailsExt;
import com.youming.youche.finance.domain.AccountStatementTemplate;
import com.youming.youche.finance.domain.AccountStatementTemplateField;
import com.youming.youche.finance.dto.GetAppDetailListDto;
import com.youming.youche.finance.dto.order.CalculatedEtcFeeDto;
import com.youming.youche.finance.provider.mapper.AccountStatementDetailsExtMapper;
import com.youming.youche.finance.provider.mapper.AccountStatementDetailsMapper;
import com.youming.youche.finance.provider.mapper.AccountStatementMapper;
import com.youming.youche.finance.provider.utils.SysStaticDataRedisUtils;
import com.youming.youche.finance.vo.order.AccountStatementInVo;
import com.youming.youche.market.api.etc.ICmEtcInfoService;
import com.youming.youche.market.domain.etc.CmEtcInfo;
import com.youming.youche.market.vo.etc.CalculatedEtcFeeVo;
import com.youming.youche.record.api.tenant.ITenantVehicleCostRelService;
import com.youming.youche.record.api.tenant.ITenantVehicleRelService;
import com.youming.youche.record.domain.tenant.TenantVehicleCostRel;
import com.youming.youche.record.domain.tenant.TenantVehicleRel;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.util.BeanMapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zengwen
 * @date 2022/4/14 17:17
 */
@DubboService(version = "1.0.0")
public class AccountStatementDetailsServiceImpl extends BaseServiceImpl<AccountStatementDetailsMapper, AccountStatementDetails> implements IAccountStatementDetailsService {

    @Resource
    IAccountStatementTemplateFieldService accountStatementTemplateFieldService;

    @DubboReference(version = "1.0.0")
    ITenantVehicleCostRelService tenantVehicleCostRelService;

    @DubboReference(version = "1.0.0")
    ITenantVehicleRelService tenantVehicleRelService;

    @Resource
    AccountStatementMapper accountStatementMapper;

    @DubboReference(version = "1.0.0")
    ICmEtcInfoService cmEtcInfoService;

    @Resource
    AccountStatementDetailsExtMapper accountStatementDetailsExtMapper;

    @Resource
    IAccountStatementService accountStatementService;

    @Resource
    RedisUtil redisUtil;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @Override
    public void batchSaveDetails(AccountStatement accountStatement, List<AccountStatementInVo> list, LoginInfo loginInfo) {
        long accountStatementId = accountStatement.getId();
        long tenantId = accountStatement.getTenantId();
        String[] months = accountStatement.getBillMonth().split(",");
        int monthCount = months.length;//?????????????????????

        //??????????????????????????????
        AccountStatementTemplate accountStatementTemplate = accountStatementTemplateFieldService.getTenantTemplate(accountStatement.getVer(), tenantId);
        List<AccountStatementTemplateField> fieldList = accountStatementTemplateFieldService.getFieldsByVer(accountStatement.getVer(), tenantId);

        List<String> fieldCodeList=new ArrayList<String>();
        for(AccountStatementTemplateField field:fieldList){
            if(field.getIsSelect()==1){
                fieldCodeList.add(field.getFieldCode());
            }
        }

        for(AccountStatementInVo in:list) {
            String plateNumber = in.getPlateNumber();//?????????

            TenantVehicleCostRel tenantVehicleCostRel = tenantVehicleCostRelService.getTenantVehicleCostRel(plateNumber, tenantId);
            long managementCost = 0L;//?????????
            long loanInterest = 0L;//????????????
            long otherFee = 0L;//????????????
            long totalFee = 0L;//?????????
            if (tenantVehicleCostRel != null) {
                managementCost = tenantVehicleCostRel.getManagementCost() == null ? 0L : tenantVehicleCostRel.getManagementCost();
                loanInterest = tenantVehicleCostRel.getLoanInterest() == null ? 0L : tenantVehicleCostRel.getLoanInterest();
                otherFee = tenantVehicleCostRel.getOtherFee() == null ? 0L : tenantVehicleCostRel.getOtherFee();
            }

            AccountStatementDetails accountStatementDetails = new AccountStatementDetails();
            accountStatementDetails.setAccountStatementId(accountStatementId);
            accountStatementDetails.setPlateNumber(in.getPlateNumber());
            accountStatementDetails.setVehicleCode(in.getVehicleCode());
            accountStatementDetails.setCarDriverId(in.getDriverUserId());
            accountStatementDetails.setCarDriverName(in.getLinkman());
            accountStatementDetails.setCarDriverPhone(in.getMobilePhone());

            TenantVehicleRel tenantVehicleRel = tenantVehicleRelService.getTenantVehicleRel(plateNumber, tenantId);

            if (tenantVehicleRel != null) {
                //???????????????
                accountStatementDetails.setAnswerId(tenantVehicleRel.getBillReceiverUserId());
                accountStatementDetails.setAnswerName(tenantVehicleRel.getBillReceiverName());
                accountStatementDetails.setAnswerPhone(tenantVehicleRel.getBillReceiverMobile());
                accountStatementDetails.setVehicleClass(tenantVehicleRel.getVehicleClass());
                if (StringUtils.isEmpty(accountStatementDetails.getVehicleClassName())) {
                    accountStatementDetails.setVehicleClassName(SysStaticDataRedisUtils.getSysStaticData(redisUtil, loginInfo.getTenantId(), "VEHICLE_CLASS", tenantVehicleRel.getVehicleClass().toString()).getCodeName());
                }
            }
            accountStatementDetails.setTenantId(tenantId);
            accountStatementDetails.setManageFee(managementCost * monthCount);//?????????
            accountStatementDetails.setLoanInterestFee(loanInterest * monthCount);//????????????
            accountStatementDetails.setOtherFee(otherFee * monthCount);//????????????
            accountStatementDetails.setCountType(1);

            CalculatedEtcFeeDto calculatedEtcFeeDto = this.calculatedEtcFee(months, accountStatement.getReceiverUserId(), tenantId, plateNumber, accountStatementId);
            Long money = calculatedEtcFeeDto.getMoney();
            accountStatementDetails.setEtcFee(money);
            accountStatementDetails.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);

            Map dataMap = BeanMapUtils.beanToMap(accountStatement);
            Iterator<Map.Entry<Integer, Integer>> entries = dataMap.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<Integer, Integer> entry = entries.next();
                String key = entry.getKey() + "";
                if (fieldCodeList.contains(key) && !key.equals("totalFee")) {//????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    if (key.indexOf("Fee") != -1 || key.indexOf("fieldExt") != -1) {

                        long value = StringUtils.isBlank(entry.getValue() + "") ? 0L : Long.valueOf(entry.getValue() + "");
                        totalFee += value;
                    }
                }
            }
            accountStatementDetails.setTotalFee(totalFee);

            if (accountStatementDetails.getId() != null && baseMapper.selectById(accountStatementDetails.getId()) != null) {
                baseMapper.updateById(accountStatementDetails);
            } else {
                baseMapper.insert(accountStatementDetails);
            }

            AccountStatementDetailsExt accountStatementDetailsExt = new AccountStatementDetailsExt();
            accountStatementDetailsExt.setAccountStatementId(accountStatementId);
            accountStatementDetailsExt.setRelSeq(accountStatementDetails.getId());

            accountStatementDetailsExtMapper.insert(accountStatementDetailsExt);
        }

        accountStatement.setVer(accountStatementTemplate.getVer());//???????????????????????????????????????
        if (accountStatement.getId() != null || accountStatementMapper.selectById(accountStatement.getId()) != null) {
            accountStatementMapper.updateById(accountStatement);
        } else {
            accountStatementMapper.insert(accountStatement);
        }
        //????????????????????????????????????????????????????????????
        List<AccountStatementDetails> details = baseMapper.getAccountStatementDetails(accountStatementId, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        accountStatementService.saveOrUpdateAccountStatementDetail(accountStatement, details, OrderAccountConst.ACCOUNT_STATEMENT.INSERT,true, loginInfo);
        matchEtcFeeToBill(accountStatement.getTenantId(), accountStatement.getReceiverPhone(), accountStatement.getReceiverUserId(), loginInfo);
    }

    /**
     * ???????????????
     * @param months
     * @param receiverUserId
     * @param tenantId
     * @param plateNumber
     * @param flowId
     * @return
     */
    public CalculatedEtcFeeDto calculatedEtcFee(String[] months,Long receiverUserId,long tenantId,String plateNumber,long flowId) {
        List<CmEtcInfo> etcList = new ArrayList<>();
        Long money = 0L;
        if(StringUtils.isNotBlank(plateNumber)) {
            AccountStatement accountStatement = accountStatementMapper.selectById(flowId);
            if (accountStatement.getState() == OrderAccountConst.ACCOUNT_STATEMENT.STATE4) { //???????????????????????????????????????????????????ETC????????????

                etcList = cmEtcInfoService.calculatedEtcFee(accountStatement.getBillNumber());

                // ????????????????????????????????????
                money = cmEtcInfoService.calculatedEtcFeeMoney(accountStatement.getBillNumber());
            } else {
                /**
                 * ?????????????????????ETC??????????????????
                 ??????????????????????????????????????????????????????????????????ETC??????????????????????????????????????????????????????????????????????????????????????????????????????ETC????????????????????????
                 ??????????????????????????????????????????????????????????????????????????????????????????ETC?????????0 ???
                 ???????????????????????????????????????????????????ETC??????????????????????????????????????????????????????????????????
                 ????????????????????????????????????????????????ETC??????????????????????????????
                 */
                List vailMonths = new ArrayList();

                //?????????????????????????????????????????????????????????????????????
                for(String month : months){
                    if (Objects.equals(accountStatementMapper.queryLastBillForMonth(flowId, tenantId, plateNumber, accountStatement.getReceiverPhone(), receiverUserId, month), flowId)) {
                        vailMonths.add(month);
                    }
                }

                if(vailMonths.size()>0) {
                    months = new String[vailMonths.size()];
                    vailMonths.toArray(months);

                    CalculatedEtcFeeVo calculatedEtcFeeVo = new CalculatedEtcFeeVo();
                    calculatedEtcFeeVo.setPlateNumber(plateNumber);
                    calculatedEtcFeeVo.setTenantId(tenantId);
                    calculatedEtcFeeVo.setMonths(Arrays.stream(months).collect(Collectors.toList()));
                    calculatedEtcFeeVo.setReceiverUserId(receiverUserId);
                    calculatedEtcFeeVo.setReceiverPhone(accountStatement.getReceiverPhone());
                    etcList = cmEtcInfoService.calculatedEtcFee(calculatedEtcFeeVo);
                    money = cmEtcInfoService.calculatedEtcFeeMoney(calculatedEtcFeeVo);
                }
            }
        }

        CalculatedEtcFeeDto calculatedEtcFeeDto = new CalculatedEtcFeeDto();
        calculatedEtcFeeDto.setItems(etcList);
        calculatedEtcFeeDto.setMoney(money);
        calculatedEtcFeeDto.setCount(etcList.size());
        calculatedEtcFeeDto.setTotalNum(etcList.size());
        calculatedEtcFeeDto.setPage(1);
        calculatedEtcFeeDto.setHasNext(false);
        return calculatedEtcFeeDto;
    }

    /**
     * ?????????ETC????????????????????????????????????????????????????????????????????????????????????????????????
     */
    public void matchEtcFeeToBill(Long tenantId, String receiverPhone, Long receiverUserId, LoginInfo loginInfo) {
        //1???????????????????????????????????????????????????????????????????????????????????????ETC????????????????????????
        List<AccountStatementDetails> clearEtcFeeList = baseMapper.getMatchEtcFeeToBill(tenantId, receiverUserId, receiverPhone);
        //2?????????????????????????????????????????????????????????????????????????????????ETC?????????????????????????????????
        if(clearEtcFeeList!=null && clearEtcFeeList.size()>0){
            for (AccountStatementDetails map : clearEtcFeeList) {
                Long accountStatementId = map.getAccountStatementId();
                Long accountStatementDetailsId = map.getId();

                AccountStatement accountStatement = accountStatementMapper.selectById(accountStatementId);
                AccountStatementDetails accountStatementDetails = baseMapper.selectById(accountStatementDetailsId);
                String[] months = accountStatement.getBillMonth().split(",");

                Long money = this.calculatedEtcFee(months,accountStatement.getReceiverUserId(),accountStatementDetails.getTenantId(),accountStatementDetails.getPlateNumber(),accountStatementId).getMoney();
                accountStatementDetails.setEtcFee(money);

                LambdaQueryWrapper<AccountStatementDetailsExt> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(AccountStatementDetailsExt::getRelSeq, accountStatementDetailsId);

                AccountStatementDetailsExt  accountStatementDetailsExt = accountStatementDetailsExtMapper.selectOne(queryWrapper);
                long totalFee = 0L;
                List<AccountStatementTemplateField> fieldList = accountStatementTemplateFieldService.getFieldsByVer(accountStatement.getVer(), accountStatement.getTenantId());
                List<String> fieldCodeList=new ArrayList<String>();
                for(AccountStatementTemplateField field:fieldList){
                    if(field.getIsSelect()==1){
                        fieldCodeList.add(field.getFieldCode());
                    }
                }

                Map dataMap = BeanMapUtils.beanToMap(accountStatementDetails);
                Map extMap = BeanMapUtils.beanToMap(accountStatementDetailsExt);
                dataMap.putAll(extMap);
                Iterator<Map.Entry<Integer, Integer>> entries = dataMap.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry<Integer, Integer> entry = entries.next();
                    String key = entry.getKey()+"";
                    if(fieldCodeList.contains(key)&&!key.equals("totalFee")){//????????????????????????????????????????????????????????????????????????????????????????????????????????????
                        if(key.indexOf("Fee")!=-1||key.indexOf("fieldExt")!=-1){

                            long value = StringUtils.isBlank(entry.getValue()+"")?0L:Long.valueOf(entry.getValue()+"");
                            totalFee+=value;
                        }
                    }
                }
                accountStatementDetails.setTotalFee(totalFee);
                if (accountStatementDetails.getId() != null || baseMapper.selectById(accountStatementDetails.getId()) != null) {
                    baseMapper.updateById(accountStatementDetails);
                } else {
                    baseMapper.insert(accountStatementDetails);
                }

                List<AccountStatementDetails> details = baseMapper.getAccountStatementDetails(accountStatementId, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
                accountStatementService.saveOrUpdateAccountStatementDetail(accountStatement, details, OrderAccountConst.ACCOUNT_STATEMENT.UPDATE,false, loginInfo);
            }
        }
    }

    @Override
    public void refreshEtcFeeForBill(long flowId, LoginInfo loginInfo) {
        AccountStatement accountStatement = accountStatementMapper.selectById(flowId);
        List<AccountStatementDetails> details = baseMapper.getAccountStatementDetails(flowId, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        if(details!=null && details.size()>0) {
            String[] months = accountStatement.getBillMonth().split(",");
            for (AccountStatementDetails detail : details) {
                //ETC?????????????????????
                if (detail.getCountType() != null && detail.getCountType() == 1) {
                    // ?????????????????????
                    CalculatedEtcFeeDto calculatedEtcFeeDto = calculatedEtcFee(months, accountStatement.getReceiverUserId(), detail.getTenantId(), detail.getPlateNumber(), flowId);
                    Long money = calculatedEtcFeeDto.getMoney();
                    detail.setEtcFee(money);

                    long totalFee = 0L;
                    List<AccountStatementTemplateField> fieldList = accountStatementTemplateFieldService.getFieldsByVer(accountStatement.getVer(), accountStatement.getTenantId());
                    List<String> fieldCodeList = new ArrayList<String>();
                    for(AccountStatementTemplateField field : fieldList) {
                        if(field.getIsSelect() == 1){
                            fieldCodeList.add(field.getFieldCode());
                        }
                    }

                    Map dataMap = BeanMapUtils.beanToMap(detail);
                    LambdaQueryWrapper<AccountStatementDetailsExt> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(AccountStatementDetailsExt::getRelSeq, detail.getId());
                    AccountStatementDetailsExt  accountStatementDetailsExt = accountStatementDetailsExtMapper.selectOne(queryWrapper);
                    Map extMap = BeanMapUtils.beanToMap(accountStatementDetailsExt);
                    dataMap.putAll(extMap);

                    Iterator<Map.Entry<Integer, Integer>> entries = dataMap.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry<Integer, Integer> entry = entries.next();
                        String key = entry.getKey()+"";
                        if(fieldCodeList.contains(key) && !key.equals("totalFee")){//????????????????????????????????????????????????????????????????????????????????????????????????????????????
                            if(key.indexOf("Fee") != -1 || key.indexOf("fieldExt") != -1){

                                long value = StringUtils.isBlank(entry.getValue() + "") ? 0L : Long.valueOf(entry.getValue() + "");
                                totalFee+=value;
                            }
                        }
                    }
                    detail.setTotalFee(totalFee);
                    baseMapper.updateById(detail);
                }
            }
            accountStatementService.saveOrUpdateAccountStatementDetail(accountStatement, details, OrderAccountConst.ACCOUNT_STATEMENT.UPDATE,false, loginInfo);
        }
    }

    @Override
    public void removeDetail(long id, LoginInfo loginInfo) {
        AccountStatementDetails accountStatementDetails = baseMapper.selectById(id);
        long accountStatementId = accountStatementDetails.getAccountStatementId();
        accountStatementDetails.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO);//??????????????????????????????
        baseMapper.updateById(accountStatementDetails);

        //????????????????????????????????????????????????????????????
        AccountStatement accountStatement = accountStatementMapper.selectById(accountStatementId);
        List<AccountStatementDetails> details = baseMapper.getAccountStatementDetails(accountStatementId, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);

        accountStatementService.saveOrUpdateAccountStatementDetail(accountStatement, details, OrderAccountConst.ACCOUNT_STATEMENT.DELETE,true, loginInfo);
    }

    @Override
    public Page<GetAppDetailListDto> getAppDetailList(Long accountStatementId, String carDriverName, String carDriverPhone, String plateNumber, Integer vehicleClass, Integer pageNum, Integer pageSize) {
        Page<AccountStatementDetails> ipage = new Page<>(pageNum, pageSize);
        Page<AccountStatementDetails> appDetailList = baseMapper.getAppDetailList(accountStatementId, plateNumber, carDriverName, carDriverPhone, null, null, -1L, vehicleClass, ipage);
        List<GetAppDetailListDto> list = new ArrayList<>();
        for (AccountStatementDetails record : appDetailList.getRecords()) {
            GetAppDetailListDto dto = new GetAppDetailListDto();
            dto.setId(record.getId());
            dto.setCarDriverName(record.getCarDriverName());
            dto.setCarDriverPhone(record.getCarDriverPhone());
            dto.setPlateNumber(record.getPlateNumber());
            dto.setTotalFee(record.getTotalFee());
            list.add(dto);
        }

        Page<GetAppDetailListDto> dtoPage = new Page<>();
        dtoPage.setCurrent(appDetailList.getCurrent());
        dtoPage.setSize(appDetailList.getSize());
        dtoPage.setTotal(appDetailList.getTotal());
        dtoPage.setPages(appDetailList.getPages());
        dtoPage.setRecords(list);
        return dtoPage;
    }

    @Override
    public void updateEtcToBill(AccountStatement as, LoginInfo loginInfo) {
        long flowId = as.getId();
        AccountStatement accountStatement = accountStatementMapper.selectById(flowId);
        List<AccountStatementDetails> details = baseMapper.getAccountStatementDetails(flowId, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);

        if(details!=null && details.size()>0) {
            String[] months = accountStatement.getBillMonth().split(",");
            for (AccountStatementDetails detail : details) {
                for(String month : months){
                    List<Long> flowIds = accountStatementMapper.selectLastBillForMonth(detail.getPlateNumber(), detail.getTenantId(), flowId, accountStatement.getReceiverPhone(), accountStatement.getReceiverUserId(), month);

                    if (Objects.equals(flowId, flowIds.get(0))) {
                        List<CmEtcInfo> etcFeeList = cmEtcInfoService.getCmEtcAccountStatement(detail.getPlateNumber(), detail.getTenantId(), month, accountStatement.getReceiverPhone(), accountStatement.getReceiverUserId());
                        cmEtcInfoService.updateCmEtcAccountStatement(detail.getPlateNumber(), detail.getTenantId(), month, accountStatement.getReceiverPhone(), accountStatement.getReceiverUserId(), as.getBillNumber());

                        //??????Etc????????????????????????
                        if(etcFeeList != null && etcFeeList.size() > 0){

                            for (CmEtcInfo cmEtcInfo : etcFeeList) {
                                sysOperLogService.saveSysOperLog(loginInfo, SysOperLogConst.BusiCode.EtcConsume, cmEtcInfo.getId(), SysOperLogConst.OperType.Update, "?????????????????????");
                            }
                        }
                    }
                }
            }
        }
    }
}
