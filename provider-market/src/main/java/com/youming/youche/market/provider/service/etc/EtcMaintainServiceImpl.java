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
 * ETC表 服务实现类
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
                //操作日志
                sysOperLogService.saveSysOperLog(user, SysOperLogConst.BusiCode.EtcCard, Long.valueOf(String.valueOf(etcMaintain.getId())),  SysOperLogConst.OperType.Update, "服务商失效！");
            }
        }
    }
    /**
     *条件导出所有etc卡信息
     * 聂杰伟
     * 2022-3-11 11：40
     *@author
     */
    @Async
    @Override
    public void etcOutList(EtcMaintainQueryVo etcMaintainQueryVo, String accessToken, ImportOrExportRecords record) {
        LoginInfo loginInfo = loginUtils.get(accessToken);
        etcMaintainQueryVo.setTenantId(loginInfo.getTenantId());
        IPage<EtcMaintainDto> all = this.getAll(1, 1000, etcMaintainQueryVo);//获得导出数据
        List<EtcMaintainDto> records = all.getRecords();
        String StateName = "";
        String CardName="";
        String PaymentName="";
        try {
                String[] showName = null;
                String[] resourceFild = null;
                //TODO 获取数据为空的处理
            if (records != null && records.size() > 0) {

            }

            showName = new String[]{"ETC卡号", "服务商",  "现绑定车辆" };
            resourceFild = new String[]{"getEtcId", "getServiceName", "getBindVehicle"};
            XSSFWorkbook workbook = ExportExcel.getWorkbookXlsx(records, showName, resourceFild, EtcMaintainDto.class,
                    null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            workbook.write(os);
            byte[] b = os.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(b);
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(inputStream, "ETC卡管理信息导出.xlsx", inputStream.available());
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
     * 聂杰伟
     *导入etc卡信息
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
            //移除表头行
            List<String> listsRows = lists.remove(0);
            int maxNum = 300;
            if (lists.size() == 0) {
                reasonFailure.append("导入的数量为0，导入失败!");
            }
            if (lists.size() > maxNum) {
                reasonFailure.append("导入不支持超过" + maxNum + "条数据，您当前条数[" + lists.size() + "]");
            }
            int j = 1;
            int success = 0;
            for (List<String> rowData : lists) {
                j++;
                EtcMaintain etcMaintain = new EtcMaintain();
                EtcMaintainDto  etcMaintainDto = new EtcMaintainDto();
                int count = rowData.size();
                Map<String, Object> inParam = new HashMap();
                String etcId = rowData.get(0).trim();//ETC卡号
                String serviceName = rowData.get(1).trim();//服务商
                String bindVehicle = rowData.get(2).trim();//现绑定的车辆号
//                String state = rowData.get(3).trim();//状态 0，无效 1，有效
//                String vehicleCode = rowData.get(4).trim();//车辆编号
//                String carUserId = rowData.get(5).trim();//车主ID
//                String carOwner = rowData.get(6).trim();//车主
//                String carPhone = rowData.get(7).trim();//车主手机
//                String loginAcct = rowData.get(8).trim();//供应商账号
//                String linkman = rowData.get(9).trim();//供应商真实姓名
//                String userId = rowData.get(10).trim();//供应商用户ID
//                String inviteState = rowData.get(11).trim();//租户id
//                String etcCardType = rowData.get(12).trim();//ETC卡类型 1粤通卡 2鲁通卡 3赣通卡
//                String paymentType = rowData.get(13).trim();//付费类型 1预付费 2后付费
//                String accountPeriodRemark = rowData.get(14).trim();//账期
//                String productName = rowData.get(15).trim();//站点名称
                if(inParam.containsValue(etcId)){
                    reasonFailure.append("第"+j+"行，ETC卡号["+etcId+"]重复；");
                }
                if(serviceName==null){
                    reasonFailure.append("第"+j+"行，服务商名称不能为空；");
                }
//                if (bindVehicle==null){
//                    reasonFailure.append("第"+j+"行，现绑定的车辆不能为空；");
//                }else {
//                }

                // TODO 查询系统中是否有这个车辆 车牌号不是必填项
                if (bindVehicle!=null&& StringUtils.isNotEmpty(bindVehicle)){
                    VehicleDataInfo vehicleDataInfo = iVehicleDataInfoService.getVehicleDataInfo(bindVehicle);
                    if (vehicleDataInfo!=null){
                        etcMaintain.setBindVehicle(bindVehicle); // 车牌号
                        etcMaintain.setVehicleCode(vehicleDataInfo.getId()); // 车辆id
                    }else {
                        reasonFailure.append("第"+j+"行，系统中没有此车辆信息；");
                    }
                }
                if (serviceName!=null){
                    etcMaintain.setServiceName(serviceName);//服务商名称 获取 服务商id
                    ServiceInfoQueryCriteria serviceInfoQueryCriteria = new ServiceInfoQueryCriteria();
                    Page<FacilitatorVo> facilitatorVoPage = iServiceInfoService.queryFacilitator(serviceInfoQueryCriteria,
                            1, 9999, accessToken);
                    List<FacilitatorVo> records1 = facilitatorVoPage.getRecords();
                    List<FacilitatorVo> serviceName1 = records1.stream().filter(facilitatorVo ->
                            facilitatorVo.getServiceName().equals(serviceName) && facilitatorVo.getServiceType() == 3).collect(Collectors.toList());
                    if (serviceName1.size()>0){
                        etcMaintain.setService_name_id(serviceName1.get(0).getServiceUserId());
                    }else {
                        reasonFailure.append("第"+j+"行，系统中没有此服务商；");
                    }
                }


                EtcMaintain etcmaintain1 = this.etcmaintain(etcId);
                if (etcmaintain1!=null){
                    reasonFailure.append("第"+j+"行,导入失败,已经有此ETC卡");
                }
                etcMaintain.setEtcId(etcId);
                if (StrUtil.isEmpty(reasonFailure)) {
//                    if (etcMaintain == null
//                            || StrUtil.isEmpty(etcMaintain.getEtcId())
//                            || StrUtil.isEmpty(etcMaintain.getServiceName())
//                            || etcMaintain.getUserId() == null
//                            || etcMaintain.getState() == null) {
//                       throw  new BusinessException("输入不能为空！");
//                    }
                    LoginInfo loginInfo= this.getLoginUser(accessToken);
                    etcMaintain.setTenantId(loginInfo.getTenantId());
                    etcMaintain.setState(1);// 默认
                    etcMaintain.setPaymentType(1); // 默认
                    boolean save = this.save(etcMaintain);
                    if (save){
                        EtcMaintain byId = this.getById(etcMaintain.getId());
                        sysOperLogService.saveSysOperLog(loginInfo,com.youming.youche.commons.constant.SysOperLogConst.BusiCode.EtcCard,
                                byId.getId(),com.youming.youche.commons.constant.SysOperLogConst.OperType.Add,"导入",loginInfo.getTenantId());
                    }
                    //返回成功数量
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
                showName = new String[]{"ETC卡号", "失败原因"
//                        "状态", "车辆编号", "车主ID",
//                        "车主", "车主手机", "供应商账号",
//                        "供应商真实姓名", "供应商用户ID", "租户id",
//                        "ETC卡类型", "付费类型", "账期",
//                        "站点名称", "邀请状态"
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
                //上传文件
                FastDFSHelper client = FastDFSHelper.getInstance();
                String failureExcelPath = client.upload(inputStream1, "ETC卡信息.xlsx", inputStream1.available());
                os.close();
                is.close();
                records.setFailureUrl(failureExcelPath);
                if (success > 0) {
                    records.setRemarks(success + "条成功" + failureList.size() + "条失败");
                    records.setState(2);
                }
                if (success == 0) {
                    records.setRemarks(failureList.size() + "条失败");
                    records.setState(4);
                }
            } else {
                records.setRemarks(success + "条成功");
                records.setState(3);
            }
            importOrExportRecordsService.update(records);
        } catch (Exception e) {
            records.setState(5);
            records.setRemarks("导入失败，请确认模版数据是否正确！");
            records.setFailureReason("导入失败，请确认模版数据是否正确！");
            e.printStackTrace();

        }
    }



    //导入校验
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

}
