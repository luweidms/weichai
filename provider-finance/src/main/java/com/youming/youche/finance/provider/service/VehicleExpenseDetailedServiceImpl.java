package com.youming.youche.finance.provider.service;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.nacos.shaded.com.google.common.base.Splitter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.AuditConsts;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.finance.api.IVehicleExpenseDetailedService;
import com.youming.youche.finance.api.IVehicleExpenseDetailedVerService;
import com.youming.youche.finance.api.IVehicleExpenseService;
import com.youming.youche.finance.api.IVehicleExpenseVerService;
import com.youming.youche.finance.commons.util.CommonUtil;
import com.youming.youche.finance.constant.OaLoanConsts;
import com.youming.youche.finance.domain.OaLoan;
import com.youming.youche.finance.domain.VehicleExpense;
import com.youming.youche.finance.domain.VehicleExpenseDetailed;
import com.youming.youche.finance.domain.VehicleExpenseDetailedVer;
import com.youming.youche.finance.domain.VehicleExpenseVer;
import com.youming.youche.finance.domain.vehicle.VehicleMiscellaneouFeeDto;
import com.youming.youche.finance.dto.CancelVehicleExpenseDto;
import com.youming.youche.finance.dto.CreateVehicleExpenseDto;
import com.youming.youche.finance.dto.GetVehicleExpenseDto;
import com.youming.youche.finance.dto.VehicleExpenseDetailedDto;
import com.youming.youche.finance.provider.mapper.VehicleExpenseDetailedMapper;
import com.youming.youche.finance.vo.GetVehicleExpenseVo;
import com.youming.youche.finance.vo.VehicleExpenseVo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.audit.IAuditOutService;
import com.youming.youche.system.api.audit.IAuditService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExportExcel;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
* <p>
    * 车辆费用明细表 服务实现类
    * </p>
* @author liangyan
* @since 2022-04-19
*/
@DubboService(version = "1.0.0")
public class VehicleExpenseDetailedServiceImpl extends BaseServiceImpl<VehicleExpenseDetailedMapper, VehicleExpenseDetailed> implements IVehicleExpenseDetailedService {

    @Resource
    LoginUtils loginUtils;
    @Resource
    private IVehicleExpenseService vehicleExpenseService;
    @Resource
    private VehicleExpenseDetailedMapper vehicleExpenseDetailedMapper;
    @Resource
    private IVehicleExpenseDetailedService vehicleExpenseDetailedService;
    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;
    @Resource
    private IVehicleExpenseVerService vehicleExpenseVerService;
    @Resource
    private IVehicleExpenseDetailedVerService vehicleExpenseDetailedVerService;
    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;
    @DubboReference(version = "1.0.0")
    IAuditService auditService;
    @DubboReference(version = "1.0.0")
    IAuditOutService auditOutService;
    @Resource
    RedisUtil redisUtil;

    @Transactional
    @Override
    public String doSaveOrUpdateNew(CreateVehicleExpenseDto domain, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        String falg = "Y";
        if(domain.getCarUserId() == null){
            throw new BusinessException("司机不能为空！");
        }
        if(domain.getVehicleId() == null){
            throw new BusinessException("车牌不能为空！");
        }
        List<VehicleExpenseDetailedDto> vehicleExpenseDetailedStr = domain.getVehicleExpenseDetailedStr();
        if(vehicleExpenseDetailedStr == null || vehicleExpenseDetailedStr.size() <1){
            throw new BusinessException("数据有误不能保存！");
        }else {
            VehicleExpense vehicleExpense= new VehicleExpense();
            vehicleExpense.setPlateNumber(domain.getPlateNumber());
            vehicleExpense.setUserName(domain.getUserName());
            vehicleExpense.setLinkPhone(domain.getLinkPhone());
            vehicleExpense.setVehicleId(domain.getVehicleId());
            vehicleExpense.setCarUserId(domain.getCarUserId());
            long incr = redisUtil.incr(EnumConsts.RemoteCache.APPLY_NO);
            vehicleExpense.setApplyNo(CommonUtil.createApplyNo(incr));
            vehicleExpense.setTenantId(loginInfo.getTenantId());
            vehicleExpense.setOpId(loginInfo.getId());
            vehicleExpense.setMileageDepartureInstrument(domain.getMileageDepartureInstrument());
            vehicleExpense.setMileageReceivingInstrument(domain.getMileageReceivingInstrument());
            vehicleExpense.setCreateTime(LocalDateTime.now());
            vehicleExpense.setUpdateTime(LocalDateTime.now());
            vehicleExpense.setVehicleId(domain.getVehicleId());
            vehicleExpense.setOrgId(domain.getOrgId());
            vehicleExpense.setExpenseDepartment(domain.getExpenseDepartment());
            vehicleExpense.setEndKmUrl(domain.getEndKmUrl());
            vehicleExpense.setStartKmUrl(domain.getStartKmUrl());
            //状态：1待审核，2审核中，3待支付，4已完成
            vehicleExpense.setState(1);
            vehicleExpense.setId(null);
            VehicleExpenseVer vehicleExpenseVer = new VehicleExpenseVer();
            BeanUtil.copyProperties(vehicleExpense, vehicleExpenseVer);
            vehicleExpenseService.saveOrUpdate(vehicleExpense);
            vehicleExpenseVer.setId(vehicleExpense.getId());
            vehicleExpenseVerService.saveOrUpdate(vehicleExpenseVer);
            for (VehicleExpenseDetailedDto vehicleExpenseDetailedDto: vehicleExpenseDetailedStr) {
                VehicleExpenseDetailed vehicleExpenseDetailed = new VehicleExpenseDetailed();
                vehicleExpenseDetailed.setCreateTime(LocalDateTime.now());
                vehicleExpenseDetailed.setUpdateTime(LocalDateTime.now());
                vehicleExpenseDetailed.setApplyNo(CommonUtil.createApplyNo(incr));
                java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$"); // 判断小数点后2位的数字的正则表达式
                java.util.regex.Matcher match=pattern.matcher(vehicleExpenseDetailedDto.getApplyAmount());
                if(match.matches()==false){
                    throw new BusinessException("金额格式有问题，不能新增！");
                }
                else{
//                    Float aFloat = Float.valueOf(vehicleExpenseDetailedDto.getApplyAmount())*100;
//                    long l = new BigDecimal(aFloat).longValue();
//                    vehicleExpenseDetailed.setApplyAmount(l);
                }
                String applyAmount = vehicleExpenseDetailedDto.getApplyAmount();
                if(StringUtils.isNotEmpty(applyAmount)){
                    long applyAmountL = (long) (Double.parseDouble(applyAmount)*100);
                    vehicleExpenseDetailed.setApplyAmount(applyAmountL);
                }else {
                    vehicleExpenseDetailed.setApplyAmount(0L);
                }
                vehicleExpenseDetailed.setType(vehicleExpenseDetailedDto.getTypeId());
                vehicleExpenseDetailed.setTypeName(vehicleExpenseDetailedDto.getTypeName());
                vehicleExpenseDetailed.setApplyId(loginInfo.getId());
                vehicleExpenseDetailed.setApplyName(loginInfo.getName());
                vehicleExpenseDetailed.setApplyTime(LocalDateTime.now());
                vehicleExpenseDetailed.setIntroduce(vehicleExpenseDetailedDto.getIntroduce());

                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                if(vehicleExpenseDetailedDto.getExpenseTime() == null){
                    throw new BusinessException("归属日期不能为空！");
                }
                String expenseTime = vehicleExpenseDetailedDto.getExpenseTime();
                LocalDateTime parse = LocalDateTime.parse(expenseTime, dateTimeFormatter);
                vehicleExpenseDetailed.setExpenseTime(parse);
                vehicleExpenseDetailed.setApplyTime(LocalDateTime.now());
                vehicleExpenseDetailed.setFile(vehicleExpenseDetailedDto.getFile());
                vehicleExpenseDetailed.setPaymentType(vehicleExpenseDetailedDto.getPaymentType());
                vehicleExpenseDetailed.setOilCardNumber(vehicleExpenseDetailedDto.getOilCardNumber());
                String refuelingMileage = vehicleExpenseDetailedDto.getRefuelingMileage();
                if(StringUtils.isNotEmpty(refuelingMileage)){
                    long c = (long)(Double.parseDouble(refuelingMileage)*1000);
                    vehicleExpenseDetailed.setRefuelingMileage(c);
                }else {
                    vehicleExpenseDetailed.setRefuelingMileage(0L);
                }
                vehicleExpenseDetailed.setTenantId(loginInfo.getTenantId());
                String refuelingLiters = vehicleExpenseDetailedDto.getRefuelingLiters();
                if(StringUtils.isNotEmpty(refuelingLiters)){
                    long refuelingLitersLone = (long) (Double.parseDouble(refuelingLiters)*1000);
                    vehicleExpenseDetailed.setRefuelingLiters(refuelingLitersLone);
                }else {
                    vehicleExpenseDetailed.setRefuelingLiters(0L);
                }
                vehicleExpenseDetailed.setId(null);
                VehicleExpenseDetailedVer vehicleExpenseDetailedVer = new VehicleExpenseDetailedVer();
                BeanUtil.copyProperties(vehicleExpenseDetailed, vehicleExpenseDetailedVer);
                this.saveOrUpdate(vehicleExpenseDetailed);
                vehicleExpenseDetailedVerService.saveOrUpdate(vehicleExpenseDetailedVer);
            }

            sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.vehicleExpenseCode, Long.valueOf(vehicleExpense.getId()), SysOperLogConst.OperType.Update,"车辆费用上报成功",loginInfo.getTenantId());

            //启动审核流程
            //审核流程
            Map inMap = new HashMap();
//            inMap.put("svName", "ICmCustomerInfoService");
            boolean bool = false;
            try {
                bool = auditService.startProcess(AuditConsts.AUDIT_CODE.VEHICLE_EXPENSE_CODE, vehicleExpense.getId(), SysOperLogConst.BusiCode.vehicleExpenseCode, inMap, accessToken);
                if (!bool) {
                    log.error("启动审核流程失败！");
                    throw new BusinessException("启动审核流程失败！");
                }
            } catch (Exception e) {
                falg = "F";
                e.printStackTrace();
            }
            return falg;
        }
    }

    @Override
    public VehicleExpenseVo getVehicleExpense(String applyNo, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if(!StringUtils.isNotEmpty(applyNo)){
            return null;
        }
        VehicleExpenseVo vehicleExpenseVo = new VehicleExpenseVo();
        QueryWrapper<VehicleExpenseDetailed> vehicleExpenseDetailedQueryWrapper = new QueryWrapper<>();
        vehicleExpenseDetailedQueryWrapper.eq("apply_no",applyNo)
                .eq("tenant_id",loginInfo.getTenantId());
        List<VehicleExpenseDetailed> list = vehicleExpenseDetailedService.list(vehicleExpenseDetailedQueryWrapper);
        vehicleExpenseVo.setVehicleExpenseDetailedStr(list);

        QueryWrapper<VehicleExpense> vehicleExpenseQueryWrapper = new QueryWrapper<>();
        vehicleExpenseQueryWrapper.eq("apply_no",applyNo)
                .eq("tenant_id",loginInfo.getTenantId());
        List<VehicleExpense> list1 = vehicleExpenseService.list(vehicleExpenseQueryWrapper);
        if(list1 != null && list1.size() > 0 ){
            VehicleExpense vehicleExpense = list1.get(0);
            BeanUtils.copyProperties(vehicleExpense, vehicleExpenseVo);
        }
//        vehicleExpenseVos.add(vehicleExpenseVo);
//        //待审数据（历史表）
//        VehicleExpenseVo vehicleExpenseVoVer = new VehicleExpenseVo();
//        QueryWrapper<VehicleExpenseDetailedVer> vehicleExpenseDetailedVerQueryWrapper = new QueryWrapper<>();
//        vehicleExpenseDetailedVerQueryWrapper.eq("apply_no",applyNo)
//                .eq("tenant_id",loginInfo.getTenantId());
//        List<VehicleExpenseDetailedVer> listVer = vehicleExpenseDetailedVerService.list(vehicleExpenseDetailedVerQueryWrapper);
//        List<VehicleExpenseDetailed> verList = new ArrayList<>();
//        VehicleExpenseDetailed vehicleExpenseDetailed = new VehicleExpenseDetailed();
//        for (VehicleExpenseDetailedVer vehicleExpenseDetailedVer: listVer) {
//            BeanUtils.copyProperties(vehicleExpenseDetailedVer, vehicleExpenseDetailed);
//            verList.add(vehicleExpenseDetailed);
//        }
//        vehicleExpenseVoVer.setVehicleExpenseDetailedStr(verList);
//
//        QueryWrapper<VehicleExpenseVer> vehicleExpenseVerQueryWrapper = new QueryWrapper<>();
//        vehicleExpenseVerQueryWrapper.eq("apply_no",applyNo)
//                .eq("tenant_id",loginInfo.getTenantId());
//        List<VehicleExpenseVer> list1Ver = vehicleExpenseVerService.list(vehicleExpenseVerQueryWrapper);
//        if(list1Ver != null && list1Ver.size() > 0 ){
//            VehicleExpenseVer vehicleExpenseVer = list1Ver.get(0);
//            BeanUtils.copyProperties(vehicleExpenseVer, vehicleExpenseVoVer);
//            vehicleExpenseVos.add(vehicleExpenseVoVer);
//        }


        return vehicleExpenseVo;
    }

    @Override
    public IPage<GetVehicleExpenseVo> getVehicleExpenseDetailedList(GetVehicleExpenseDto dto, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        Page<GetVehicleExpenseVo> page =new Page<>(dto.getPageNum(),dto.getPageSize());
        dto.setTenantId(loginInfo.getTenantId());
        String beginApplyTime = dto.getBeginApplyTime();
        String endApplyTime = dto.getEndApplyTime();
        if(beginApplyTime != null && StringUtils.isNotEmpty(beginApplyTime)){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String applyTimeStr = beginApplyTime+" 00:00:00";
            LocalDateTime beginApplyTime1 = LocalDateTime.parse(applyTimeStr, dateTimeFormatter);
            dto.setBeginApplyTime1(beginApplyTime1);
        }
        if(endApplyTime != null && StringUtils.isNotEmpty(endApplyTime)){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String endApplyTimeStr = endApplyTime+" 23:59:59";
            LocalDateTime endApplyTime1 = LocalDateTime.parse(endApplyTimeStr, dateTimeFormatter);
            dto.setEndApplyTime1(endApplyTime1);
        }
        IPage<GetVehicleExpenseVo> vehicleExpenseDetailedList = vehicleExpenseDetailedMapper.getVehicleExpenseDetailedList(page,dto);
        return vehicleExpenseDetailedList;
    }

    @Override
    public Long getSumApplyAmount(GetVehicleExpenseDto dto, String accessToken) {
        Page<GetVehicleExpenseVo> page =new Page<>(dto.getPageNum(),dto.getPageSize());
        LoginInfo loginInfo = loginUtils.get(accessToken);
        dto.setTenantId(loginInfo.getTenantId());
        String beginApplyTime = dto.getBeginApplyTime();
        String endApplyTime = dto.getEndApplyTime();
        if(beginApplyTime != null && StringUtils.isNotEmpty(beginApplyTime)){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String applyTimeStr = beginApplyTime+" 00:00:00";
            LocalDateTime beginApplyTime1 = LocalDateTime.parse(applyTimeStr, dateTimeFormatter);
            dto.setBeginApplyTime1(beginApplyTime1);
        }
        if(endApplyTime != null && StringUtils.isNotEmpty(endApplyTime)){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String endApplyTimeStr = endApplyTime+" 23:59:59";
            LocalDateTime endApplyTime1 = LocalDateTime.parse(endApplyTimeStr, dateTimeFormatter);
            dto.setEndApplyTime1(endApplyTime1);
        }
        Long sumApplyAmount = vehicleExpenseDetailedMapper.getSumApplyAmount(dto);
        return sumApplyAmount;
    }

    @Override
    public void vehicleExpenseDetailedList(GetVehicleExpenseDto dto, String accessToken, ImportOrExportRecords record) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        dto.setTenantId(loginInfo.getTenantId());
        dto.setPageSize(99999);
        IPage<GetVehicleExpenseVo> vehicleExpenseDetailedList = getVehicleExpenseDetailedList(dto, accessToken);
        List<GetVehicleExpenseVo> records = vehicleExpenseDetailedList.getRecords();
        try {
            String[] showName = null;
            String[] resourceFild = null;
            if (records != null && records.size() > 0) {
                for (GetVehicleExpenseVo getVehicleExpenseVo: records) {
                    Long applyAmount1 = getVehicleExpenseVo.getApplyAmount();
                    String applyAmountStr = "";
                    if(applyAmount1 == null){
                        applyAmountStr = "0";
                    }else {
                        double applyAmount=applyAmount1.doubleValue()/100;
                        applyAmountStr = String.valueOf(applyAmount);
                    }
                    getVehicleExpenseVo.setApplyAmountStr(applyAmountStr);
                }
            }
			showName = new String[]{"车牌号码", "费用类型",  "申请金额（元）", "申请时间", "费用归属日期", "申请人", "费用归属", "状态" };
			resourceFild = new String[]{"getPlateNumber", "getTypeName", "getApplyAmountStr", "getApplyTime", "getExpenseTime", "getApplyName", "getExpenseDepartment", "getStateStr"};

            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(records, showName, resourceFild, GetVehicleExpenseVo.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "车辆费用明细导出.xlsx", inputStream.available());
            os.close();
            inputStream.close();
            record.setMediaUrl(path);
            record.setState(2);
            importOrExportRecordsService.saveOrUpdate(record);
        } catch (Exception e) {
            record.setState(3);
            importOrExportRecordsService.saveOrUpdate(record);
            e.printStackTrace();
        }


    }
    @Transactional
    @Override
    public void updateVehicleExpenseDetailed(CreateVehicleExpenseDto domain, String accessToken) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        if(domain.getCarUserId() == null){
            throw new BusinessException("司机不能为空！");
        }
        if(domain.getVehicleId() == null){
            throw new BusinessException("车牌不能为空！");
        }
        List<VehicleExpenseDetailedDto> vehicleExpenseDetailedStr = domain.getVehicleExpenseDetailedStr();
        if(vehicleExpenseDetailedStr == null || vehicleExpenseDetailedStr.size() <1){
            throw new BusinessException("数据有误不能保存！");
        }else {
//            HashMap<String, Object> map = new HashMap<>();
//            map.put("apply_no",domain.getApplyNo());
//            boolean b = vehicleExpenseVerService.removeByMap(map);
            VehicleExpense vehicleExpense= new VehicleExpense();
            vehicleExpense.setId(domain.getId());
            vehicleExpense.setPlateNumber(domain.getPlateNumber());
            vehicleExpense.setUserName(domain.getUserName());
            vehicleExpense.setLinkPhone(domain.getLinkPhone());
            vehicleExpense.setVehicleId(domain.getVehicleId());
            vehicleExpense.setCarUserId(domain.getCarUserId());
            vehicleExpense.setApplyNo(domain.getApplyNo());
            vehicleExpense.setTenantId(loginInfo.getTenantId());
            vehicleExpense.setOpId(loginInfo.getId());
            vehicleExpense.setMileageDepartureInstrument(domain.getMileageDepartureInstrument());
            vehicleExpense.setMileageReceivingInstrument(domain.getMileageReceivingInstrument());
            vehicleExpense.setUpdateTime(LocalDateTime.now());
            vehicleExpense.setVehicleId(domain.getVehicleId());
            vehicleExpense.setOrgId(domain.getOrgId());
            vehicleExpense.setExpenseDepartment(domain.getExpenseDepartment());
            vehicleExpense.setEndKmUrl(domain.getEndKmUrl());
            vehicleExpense.setStartKmUrl(domain.getStartKmUrl());
            //状态：1待审核，2审核中，3待支付，4已完成
            vehicleExpense.setState(1);
            vehicleExpenseService.saveOrUpdate(vehicleExpense);
            HashMap<String, Object> map1 = new HashMap<>();
            map1.put("apply_no",vehicleExpense.getApplyNo());
            boolean b1 = vehicleExpenseDetailedService.removeByMap(map1);
            for (VehicleExpenseDetailedDto vehicleExpenseDetailedDto: vehicleExpenseDetailedStr) {
                VehicleExpenseDetailed vehicleExpenseDetailed = new VehicleExpenseDetailed();
                vehicleExpenseDetailed.setCreateTime(LocalDateTime.now());
                vehicleExpenseDetailed.setUpdateTime(LocalDateTime.now());
                vehicleExpenseDetailed.setApplyNo(domain.getApplyNo());
                java.util.regex.Pattern pattern=java.util.regex.Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$"); // 判断小数点后2位的数字的正则表达式
                java.util.regex.Matcher match=pattern.matcher(vehicleExpenseDetailedDto.getApplyAmount());
                if(match.matches()==false){
                    throw new BusinessException("金额格式有问题，不能新增！");
                }
                else{
//                    Float aFloat = Float.valueOf(vehicleExpenseDetailedDto.getApplyAmount())*100;
//                    long l = new BigDecimal(aFloat).longValue();
//                    vehicleExpenseDetailed.setApplyAmount(l);
                }
                String applyAmount = vehicleExpenseDetailedDto.getApplyAmount();
                if(StringUtils.isNotEmpty(applyAmount)){
                    long applyAmountL = (long) (Double.parseDouble(applyAmount)*100);
                    vehicleExpenseDetailed.setApplyAmount(applyAmountL);
                }else {
                    vehicleExpenseDetailed.setApplyAmount(0L);
                }
                vehicleExpenseDetailed.setType(vehicleExpenseDetailedDto.getType());
                vehicleExpenseDetailed.setTypeName(vehicleExpenseDetailedDto.getTypeName());
                vehicleExpenseDetailed.setApplyId(vehicleExpenseDetailedDto.getApplyId());
                vehicleExpenseDetailed.setApplyName(vehicleExpenseDetailedDto.getApplyName());
                vehicleExpenseDetailed.setIntroduce(vehicleExpenseDetailedDto.getIntroduce());

                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                if(vehicleExpenseDetailedDto.getExpenseTime() == null){
                    throw new BusinessException("归属日期不能为空！");
                }
                String expenseTime = vehicleExpenseDetailedDto.getExpenseTime();
                LocalDateTime parse = LocalDateTime.parse(expenseTime, dateTimeFormatter);
                vehicleExpenseDetailed.setExpenseTime(parse);
                LocalDateTime parse1 = LocalDateTime.parse(vehicleExpenseDetailedDto.getApplyTime(), dateTimeFormatter);
                vehicleExpenseDetailed.setApplyTime(parse1);
                vehicleExpenseDetailed.setFile(vehicleExpenseDetailedDto.getFile());
                vehicleExpenseDetailed.setPaymentType(vehicleExpenseDetailedDto.getPaymentType());
                vehicleExpenseDetailed.setOilCardNumber(vehicleExpenseDetailedDto.getOilCardNumber());
                String refuelingLiters = vehicleExpenseDetailedDto.getRefuelingLiters();
                if(StringUtils.isNotEmpty(refuelingLiters)){
                    long refuelingLitersL = (long) (Double.parseDouble(refuelingLiters)*1000);
                    vehicleExpenseDetailed.setRefuelingLiters(refuelingLitersL);
                }else {
                    vehicleExpenseDetailed.setRefuelingLiters(0L);
                }
                String refuelingMileage = vehicleExpenseDetailedDto.getRefuelingMileage();
                if(StringUtils.isNotEmpty(refuelingMileage)){
                    long refuelingMileageL = (long) (Double.parseDouble(refuelingMileage)*1000);
                    vehicleExpenseDetailed.setRefuelingMileage(refuelingMileageL);
                }else {
                    vehicleExpenseDetailed.setRefuelingMileage(0L);
                }
                vehicleExpenseDetailed.setTenantId(loginInfo.getTenantId());
                vehicleExpenseDetailed.setId(vehicleExpenseDetailedDto.getVehicleExpenseDetailedId());
                vehicleExpenseDetailedService.saveOrUpdate(vehicleExpenseDetailed);
            }
            sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.vehicleExpenseCode, Long.valueOf(vehicleExpense.getId()), SysOperLogConst.OperType.Update,"车辆费用修改成功",loginInfo.getTenantId());
        }
    }

    @Transactional
    @Override
    public void doPublicVehicleExpense(Long vehicleExpenseId, int authState, String auditContent,String accessToken) {

        LoginInfo loginInfo = loginUtils.get(accessToken);
        VehicleExpense vehicleExpense = vehicleExpenseService.getById(vehicleExpenseId);

        QueryWrapper<VehicleExpenseDetailed> vehicleExpenseDetailedQueryWrapper = new QueryWrapper<>();
        vehicleExpenseDetailedQueryWrapper.eq("apply_no",vehicleExpense.getApplyNo());
        List<VehicleExpenseDetailed> list1 = this.list(vehicleExpenseDetailedQueryWrapper);
        if(list1 != null && list1.size() > 0){

            //判断审核是通过还是审核不通过
            if(authState == SysStaticDataEnum.AUTH_STATE.AUTH_STATE3){
                //状态：1待审核，2审核中，3审核通过，4审核不通过
                vehicleExpense.setState(4);
                vehicleExpenseService.saveOrUpdate(vehicleExpense);
//                sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.vehicleExpenseCode, Long.valueOf(vehicleExpense.getId()), SysOperLogConst.OperType.Update,"车辆费用审核不通过",loginInfo.getTenantId());

            }else if(authState == SysStaticDataEnum.AUTH_STATE.AUTH_STATE2){
                //审核通过，将历史表数据更新到主表中
//                QueryWrapper<VehicleExpenseVer> vehicleExpenseVerQueryWrapper = new QueryWrapper<>();
//                vehicleExpenseVerQueryWrapper.eq("apply_no",vehicleExpense.getApplyNo());
//                List<VehicleExpenseVer> vehicleExpenseVerList = vehicleExpenseVerService.list(vehicleExpenseVerQueryWrapper);
//                HashMap<String, Object> map1 = new HashMap<>();
//                map1.put("apply_no",vehicleExpense.getApplyNo());
//                boolean b = vehicleExpenseService.removeByMap(map1);
//                if(vehicleExpenseVerList != null && vehicleExpenseVerList.size() > 0){
//                    VehicleExpenseVer vehicleExpenseVer = vehicleExpenseVerList.get(0);
//                    BeanUtil.copyProperties(vehicleExpenseVer,vehicleExpense);
                    vehicleExpense.setState(3);
                    vehicleExpenseService.saveOrUpdate(vehicleExpense);
//                    vehicleExpenseService.saveOrUpdate(vehicleExpense);
//                }
//                QueryWrapper<VehicleExpenseDetailedVer> vehicleExpenseDetailedVerQueryWrapper = new QueryWrapper<>();
//                vehicleExpenseDetailedVerQueryWrapper.eq("apply_no",vehicleExpense.getApplyNo());
//                List<VehicleExpenseDetailedVer> vehicleExpenseDetailedVerList = vehicleExpenseDetailedVerService.list(vehicleExpenseDetailedVerQueryWrapper);
//                if(vehicleExpenseDetailedVerList != null && vehicleExpenseDetailedVerList.size() > 0){
//                    HashMap<String, Object> map = new HashMap<>();
//                    map.put("apply_no",vehicleExpense.getApplyNo());
//                    boolean b1 = this.removeByMap(map);
//                    for (VehicleExpenseDetailedVer vehicleExpenseDetailedVer: vehicleExpenseDetailedVerList) {
//                        VehicleExpenseDetailed vehicleExpenseDetailed = new VehicleExpenseDetailed();
//                        BeanUtil.copyProperties(vehicleExpenseDetailedVer,vehicleExpenseDetailed);
//                        this.save(vehicleExpenseDetailed);
//                    }
//                }
                sysOperLogService.saveSysOperLog(loginInfo,SysOperLogConst.BusiCode.vehicleExpenseCode, Long.valueOf(vehicleExpense.getId()), SysOperLogConst.OperType.Update,"车辆费用审核成功",loginInfo.getTenantId());
            }

        }
    }

    @Transactional
    @Override
    public String cancelVehicleExpense(CancelVehicleExpenseDto cancelVehicleExpenseDto, String accessToken) {

        LoginInfo loginInfo = loginUtils.get(accessToken);
        VehicleExpense vehicleExpense = vehicleExpenseService.getById(cancelVehicleExpenseDto.getVehicleExpenseId());
        if(vehicleExpense!=null){
            //状态：1待审核，2审核中，3审核通过，4审核不通过,8撤销申请
            if(vehicleExpense.getState() == 1 || vehicleExpense.getState() == 4){
                vehicleExpense.setState(8);
            }else{
                throw new BusinessException("该审核状态已更新，请刷新后再试");
            }
        }else{
            throw new BusinessException("查询不到车辆费用信息");
        }
        vehicleExpenseService.saveOrUpdate(vehicleExpense);
        SysOperLogConst.BusiCode busi_code = SysOperLogConst.BusiCode.vehicleExpenseCode;//费用上报
        String auditCode = AuditConsts.AUDIT_CODE.VEHICLE_EXPENSE_CODE;
        sysOperLogService.saveSysOperLog(loginInfo,busi_code,vehicleExpense.getId(), SysOperLogConst.OperType.Del,"车辆费用审核取消");
        //结束审核流程
        try{
            auditOutService.cancelProcess(auditCode,vehicleExpense.getId(),loginInfo.getTenantId());
        }catch (Exception e){
            e.printStackTrace();
        }
        return "取消成功";
    }

    @Override
    public void doPublicVehicleExpenseState(Long vehicleExpenseId) {
        VehicleExpense vehicleExpense = vehicleExpenseService.getById(vehicleExpenseId);
        vehicleExpense.setState(2);
        vehicleExpenseService.saveOrUpdate(vehicleExpense);
    }

    @Override
    public List<VehicleMiscellaneouFeeDto> getVehicleFeeByMonth(String plateNumber, Long tenantId, String month) {
        return baseMapper.getVehicleFeeByMonth(plateNumber, tenantId, month);
    }

}
