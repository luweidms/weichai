package com.youming.youche.market.provider.service.etc;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseServiceImpl;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.etc.IEtcMaintainService;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.domain.etc.EtcMaintain;
import com.youming.youche.market.dto.etc.EtcBindVehicleDto;
import com.youming.youche.market.dto.etc.EtcMaintainDto;
import com.youming.youche.market.provider.mapper.etc.EtcMaintainMapper;
import com.youming.youche.market.vo.etc.EtcMaintainQueryVo;
import com.youming.youche.market.vo.facilitator.FacilitatorVo;
import com.youming.youche.market.vo.facilitator.criteria.ServiceInfoQueryCriteria;
import com.youming.youche.record.api.vehicle.IVehicleDataInfoService;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExportExcel;
import com.youming.youche.util.ExcelFilesVaildate;
import org.apache.commons.lang3.StringUtils;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 * ETC??? ???????????????
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@DubboService(version = "1.0.0")
public class EtcMaintainServiceImpl extends BaseServiceImpl<EtcMaintainMapper, EtcMaintain> implements IEtcMaintainService {

    @Resource
    private LoginUtils loginUtils;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @Resource
    EtcMaintainMapper etcMaintainMapper;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Lazy
    @Resource
    IServiceInfoService iServiceInfoService;

    @Lazy
    @DubboReference(version = "1.0.0")
    IVehicleDataInfoService iVehicleDataInfoService;


    @Override
    public LoginInfo getLoginUser(String accessToken) {
        return loginUtils.get(accessToken);
    }

    @Override
    public IPage<EtcMaintainDto> getAll(Integer pageNum, Integer pageSize, EtcMaintainQueryVo etcMaintainQueryVo) {
        Page<EtcMaintainDto> pageInfo = new Page<>(pageNum, pageSize);
        return etcMaintainMapper.selectAll(pageInfo, etcMaintainQueryVo);
    }

    @Override
    public IPage<EtcBindVehicleDto> getVehicleByPlateNumber(Integer pageNum, Integer pageSize,Long tenantId,String plateNumber){
        Page<EtcBindVehicleDto> pageInfo = new Page<>(pageNum, pageSize);
        return etcMaintainMapper.seletcVehicleByPlateNumber(pageInfo,tenantId,plateNumber);
    }

    @Override
    public boolean checkEtcBindVehicle(Long tenantId, String bindVehicle) {
        return etcMaintainMapper.checkEtcBindVehicle(tenantId, bindVehicle) > 0 ? true : false;
    }

    @Override
    public EtcMaintain etcmaintain(String etcId) {
        QueryWrapper<EtcMaintain> wrapper = new QueryWrapper<>();
        wrapper.eq("etc_id", etcId);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public void loseEtc(Long serviceUserId,LoginInfo user) {
        LambdaQueryWrapper<EtcMaintain> lambda=new QueryWrapper<EtcMaintain>().lambda();
        lambda.eq(EtcMaintain::getUserId,serviceUserId)
                .eq(EtcMaintain::getTenantId,user.getTenantId())
                .eq(EtcMaintain::getState, SysStaticDataEnum.SYS_STATE_DESC.STATE_YES);
        List<EtcMaintain> list = this.list(lambda);
        if(list != null && list.size() > 0){
            for(EtcMaintain etcMaintain : list){
                etcMaintain.setState(SysStaticDataEnum.SYS_STATE_DESC.STATE_NO);
                saveOrUpdate(etcMaintain);
                //????????????
                sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.EtcCard, Long.valueOf(String.valueOf(etcMaintain.getId())),  SysOperLogConst.OperType.Update, "??????????????????");
            }
        }
    }
    /**
     *??????????????????etc?????????
     * ?????????
     * 2022-3-11 11???40
     *@author
     */
    @Async
    @Override
    public void etcOutList(EtcMaintainQueryVo etcMaintainQueryVo, String accessToken, ImportOrExportRecords record) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        etcMaintainQueryVo.setTenantId(loginInfo.getTenantId());
        IPage<EtcMaintainDto> all = this.getAll(1, 1000, etcMaintainQueryVo);//??????????????????
        List<EtcMaintainDto> records = all.getRecords();
        String StateName = "";
        String CardName="";
        String PaymentName="";
        try {
                String[] showName = null;
                String[] resourceFild = null;
                //TODO ???????????????????????????
            if (records != null && records.size() > 0) {

            }

            showName = new String[]{"ETC??????", "?????????",  "???????????????" };
            resourceFild = new String[]{"getEtcId", "getServiceName", "getBindVehicle"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(records, showName, resourceFild, EtcMaintainDto.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //????????????
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "ETC?????????????????????.xlsx", inputStream.available());
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
     *??????etc?????????
     */
    @Transactional
    @Async
    @Override
    public void etcImport(byte[] byteBuffer, ImportOrExportRecords records, Long tenantId,String accessToken) {
        List<EtcMaintainDto> failureList = new ArrayList<>();
        try {
            StringBuffer reasonFailure = new StringBuffer();
            InputStream is = new ByteArrayInputStream(byteBuffer);
            List<List<String>> lists = this.getExcelContent(is, 1, (ExcelFilesVaildate[]) null);
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
                EtcMaintain etcMaintain = new EtcMaintain();
                EtcMaintainDto  etcMaintainDto = new EtcMaintainDto();
                int count = rowData.size();
                Map<String, Object> inParam = new HashMap();
                String etcId = rowData.get(0).trim();//ETC??????
                String serviceName = rowData.get(1).trim();//?????????
                String bindVehicle = rowData.get(2).trim();//?????????????????????
//                String state = rowData.get(3).trim();//?????? 0????????? 1?????????
//                String vehicleCode = rowData.get(4).trim();//????????????
//                String carUserId = rowData.get(5).trim();//??????ID
//                String carOwner = rowData.get(6).trim();//??????
//                String carPhone = rowData.get(7).trim();//????????????
//                String loginAcct = rowData.get(8).trim();//???????????????
//                String linkman = rowData.get(9).trim();//?????????????????????
//                String userId = rowData.get(10).trim();//???????????????ID
//                String inviteState = rowData.get(11).trim();//??????id
//                String etcCardType = rowData.get(12).trim();//ETC????????? 1????????? 2????????? 3?????????
//                String paymentType = rowData.get(13).trim();//???????????? 1????????? 2?????????
//                String accountPeriodRemark = rowData.get(14).trim();//??????
//                String productName = rowData.get(15).trim();//????????????
                if(inParam.containsValue(etcId)){
                    reasonFailure.append("???"+j+"??????ETC??????["+etcId+"]?????????");
                }
                if(serviceName==null){
                    reasonFailure.append("???"+j+"????????????????????????????????????");
                }
//                if (bindVehicle==null){
//                    reasonFailure.append("???"+j+"???????????????????????????????????????");
//                }else {
//                }

                // TODO ???????????????????????????????????? ????????????????????????
                if (bindVehicle!=null&& StringUtils.isNotEmpty(bindVehicle)){
                    VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(bindVehicle);
                    if (vehicleDataInfo!=null){
                        etcMaintain.setBindVehicle(bindVehicle); // ?????????
                        etcMaintain.setVehicleCode(vehicleDataInfo.getId()); // ??????id
                    }else {
                        reasonFailure.append("???"+j+"???????????????????????????????????????");
                    }
                }
                if (serviceName!=null){
                    etcMaintain.setServiceName(serviceName);//??????????????? ?????? ?????????id
                    ServiceInfoQueryCriteria serviceInfoQueryCriteria = new ServiceInfoQueryCriteria();
                    Page<FacilitatorVo> facilitatorVoPage = iServiceInfoService.queryFacilitator(serviceInfoQueryCriteria,
                            1, 9999, accessToken);
                    List<FacilitatorVo> records1 = facilitatorVoPage.getRecords();
                    List<FacilitatorVo> serviceName1 = records1.stream().filter(facilitatorVo ->
                            facilitatorVo.getServiceName().equals(serviceName) && facilitatorVo.getServiceType() == 3).collect(Collectors.toList());
                    if (serviceName1.size()>0){
                        etcMaintain.setService_name_id(serviceName1.get(0).getServiceUserId());
                    }else {
                        reasonFailure.append("???"+j+"????????????????????????????????????");
                    }
                }


                EtcMaintain etcmaintain1 = this.etcmaintain(etcId);
                if (etcmaintain1!=null){
                    reasonFailure.append("???"+j+"???,????????????,????????????ETC???");
                }
                etcMaintain.setEtcId(etcId);
                if (StrUtil.isEmpty(reasonFailure)) {
//                    if (etcMaintain == null
//                            || StrUtil.isEmpty(etcMaintain.getEtcId())
//                            || StrUtil.isEmpty(etcMaintain.getServiceName())
//                            || etcMaintain.getUserId() == null
//                            || etcMaintain.getState() == null) {
//                       throw  new BusinessException("?????????????????????");
//                    }
                    LoginInfo loginInfo= this.getLoginUser(accessToken);
                    etcMaintain.setTenantId(loginInfo.getTenantId());
                    etcMaintain.setState(1);// ??????
                    etcMaintain.setPaymentType(1); // ??????
                    boolean save = this.save(etcMaintain);
                    if (save){
                        EtcMaintain byId = this.getById(etcMaintain.getId());
                        sysOperLogService.saveSysOperLog(loginInfo,com.youming.youche.commons.constant.SysOperLogConst.BusiCode.EtcCard,
                                byId.getId(),com.youming.youche.commons.constant.SysOperLogConst.OperType.Add,"??????",loginInfo.getTenantId());
                    }
                    //??????????????????
                    success++;
                } else {
                    etcMaintainDto.setEtcId(etcMaintain.getEtcId());
                    etcMaintainDto.setReasonFailure(reasonFailure.toString());
                    failureList.add(etcMaintainDto);
                }
            }

            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(failureList)) {
                String[] showName;
                String[] resourceFild;
                showName = new String[]{"ETC??????", "????????????"
//                        "??????", "????????????", "??????ID",
//                        "??????", "????????????", "???????????????",
//                        "?????????????????????", "???????????????ID", "??????id",
//                        "ETC?????????", "????????????", "??????",
//                        "????????????", "????????????"
                };
                resourceFild = new String[]{"getEtcId", "getReasonFailure"
////                        "getState", "getVehicleCode", "getCarUserId",
//                        "getCarOwner", "getCarPhone", "getLoginAcct",
//                        "getLinkman", "getUserId", "getTenantId",
//                        "getEtcCardType", "getPaymentType", "getAccountPeriodRemark",
//                        "getProductName", "getInviteState"
                };
                XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(failureList, showName, resourceFild, EtcMaintainDto.class, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                workbook.write(os);
                byte[] b = os.toByteArray();
                InputStream inputStream1 = new ByteArrayInputStream(b);
                //????????????
                FastDFSHelper client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream1, "ETC?????????.xlsx", inputStream1.available());
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
            e.printStackTrace();

        }
    }



    //????????????
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

}
