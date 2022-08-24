package com.youming.youche.record.business.controller.license;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.record.api.license.ILicenseService;
import com.youming.youche.record.domain.license.License;
import com.youming.youche.record.dto.LicenseDto;
import com.youming.youche.record.dto.license.LicenseDetailsDto;
import com.youming.youche.record.dto.license.ZzxqDto;
import com.youming.youche.record.vo.LicenseDetailsVo;
import com.youming.youche.record.vo.LicenseVo;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("license")
public class LicenseController extends BaseController<License, ILicenseService> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LicenseController.class);
    @DubboReference(version = "1.0.0")
    private ILicenseService iLicenseService;
    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;
    @Override
    public ILicenseService getService() {
        return iLicenseService;
    }
    /**
     * 查询证照到期信息
     */
    @GetMapping("/find/list")
    public ResponseResult getCustomerList(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                          @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                          LicenseDto licenseDto) {
//        注释掉之前陈晋东的代码逻辑
//        try {
//            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
//            Page<LicenseVo> page = new Page<>(pageNum, pageSize);
//            Page<LicenseVo> page1 = iLicenseService.queryAllFindByType(page, accessToken, licenseDto);
//            return ResponseResult.success(page1);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseResult.failure("查询失败");
//        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<LicenseVo> page=iLicenseService.selectListByType(new Page<LicenseVo>(pageNum,pageSize),accessToken,licenseDto);
        return ResponseResult.success(page);

    }

    /**
     * 查询车队指定牌照类型车辆年审信息
     *
     * @param licenseDto 牌照类型、证照类型
     */
    @GetMapping("/list")
    public ResponseResult getList(LicenseDto licenseDto) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            List<LicenseVo> page1 = iLicenseService.queryAll(accessToken, licenseDto);
            return ResponseResult.success(page1);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("查询失败");
        }
    }
    /**
     * 查询证照详情信息
     */
    @GetMapping("/find/license")
    public ResponseResult getLicenseList() {
//        注释掉之前陈晋东的代码逻辑
//        try {
//             String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
//            List<ZzxqDto> page1 = iLicenseService.queryAllFindByAll(accessToken);
//            return ResponseResult.success(page1);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseResult.failure("查询失败");
//        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<LicenseDetailsVo> list = iLicenseService.selectLicenseDetails(accessToken);
        return ResponseResult.success(list);
    }

//    @RequestMapping("/outPutExcel")
//    public ResponseResult outPutExcel(HttpServletResponse response) throws Exception {
//        // 每次写100行数据，就刷新数据出缓存
//        SXSSFWorkbook wb = new SXSSFWorkbook(100); // keep 100 rows in memory,
//        Sheet sh = wb.createSheet();
//        // 这个是业务数据\
//        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
//        List<ZzxqDto> tmps = iLicenseService.queryAllFindByAll(accessToken);
//        String[] titles = {"类型一", "车辆品牌", "车辆型号", "年审已处理", "年审未处理", "商业险已处理", "商业险未处理", "交强险已处理", "交强险未处理", "违章已处理", "违章未处理", "事故已处理", "事故未处理", "其他险已处理", "其他险未处理"};
//        Row row = sh.createRow(0);
//        // 第一行设置标题
//        for (int i = 0; i < titles.length; i++) {
//            String title = titles[i];
//            Cell cell1 = row.createCell(i);
//            cell1.setCellValue(title);
//        }
//
//        // 导出数据
//        for (int rowNum = 0; rowNum < tmps.size(); rowNum++) {
//
//            Row rowData = sh.createRow(rowNum + 1);
//            // TbClass 这个是我的业务类，这个是根据业务来进行填写数据
//            ZzxqDto zzxqDto = tmps.get(rowNum);
//            // 第一列
//            Cell cellDataA = rowData.createCell(0);
//            cellDataA.setCellValue(zzxqDto.getLicenceType());
//            // 第二列
//            Cell cellDataB = rowData.createCell(1);
//            cellDataB.setCellValue(zzxqDto.getBrandModel());
//            // 第三列
//            Cell cellDataC = rowData.createCell(2);
//            cellDataC.setCellValue(zzxqDto.getVehicleModel());
//            // 第四列
//            Cell cellDataD = rowData.createCell(3);
//            cellDataD.setCellValue(zzxqDto.getNsDealNum());
//            // 第五列
//            Cell cellDataE = rowData.createCell(4);
//            cellDataE.setCellValue(zzxqDto.getNsNotDealNum());
//            // 第六列
//            Cell cellDataF = rowData.createCell(5);
//            cellDataF.setCellValue(zzxqDto.getSyxDealNum());
//            // 第七列
//            Cell cellDataG = rowData.createCell(6);
//            cellDataG.setCellValue(zzxqDto.getSyxNotDealNum());
//            // 第八列
//            Cell cellDataH = rowData.createCell(7);
//            cellDataH.setCellValue(zzxqDto.getJxxDealNum());
//            // 第九列
//            Cell cellDataI = rowData.createCell(8);
//            cellDataI.setCellValue(zzxqDto.getJxxNotDealNum());
//            // 第10列
//            Cell cellDataJ = rowData.createCell(9);
//            cellDataJ.setCellValue(zzxqDto.getWzDealNum());
//            // 11列
//            Cell cellDataK = rowData.createCell(10);
//            cellDataK.setCellValue(zzxqDto.getWzNotDealNum());
//            // 第12列
//            Cell cellDataL = rowData.createCell(11);
//            cellDataL.setCellValue(zzxqDto.getSgDealNum());
//            // 第13列
//            Cell cellDataM = rowData.createCell(12);
//            cellDataM.setCellValue(zzxqDto.getSgNotDealNum());
//            // 第13列
//            Cell cellDataN = rowData.createCell(13);
//            cellDataN.setCellValue(zzxqDto.getOtherDealNum());
//            // 第13列
//            Cell cellDataO = rowData.createCell(14);
//            cellDataO.setCellValue(zzxqDto.getOtherNotDealNum());
//        }
//
//        String fileName = "证照详情导出.xlsx";
//        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
//        wb.write(response.getOutputStream());
//        wb.close();
//        }

    /**
     * 证照详情导出
     */
    @GetMapping("export")
    public ResponseResult export() {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("证照详情导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            //iLicenseService.queryAllFindByAllListExport(accessToken, record);
            iLicenseService.exportDetails(accessToken,record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出失败证照详情列表异常" + e);
            return ResponseResult.failure("导出失败证照详情异常");
        }
    }

    /**
     * 证照过期信息导出
     */
    @GetMapping("export/list")
    public ResponseResult exportList(LicenseDto licenseDto) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("证照过期导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            //iLicenseService.queryAlllist(accessToken, record,licenseDto);
            iLicenseService.exportList(accessToken,record,licenseDto);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出失败证照到期列表异常" + e);
            return ResponseResult.failure("导出失败证照到期异常");
        }
    }
}
