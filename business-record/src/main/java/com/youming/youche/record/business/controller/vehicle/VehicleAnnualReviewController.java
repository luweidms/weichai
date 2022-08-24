package com.youming.youche.record.business.controller.vehicle;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.util.StringUtil;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.record.api.vehicle.IVehicleAnnualReviewService;
import com.youming.youche.record.domain.vehicle.VehicleAnnualReview;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExcelParse;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * hzx
 *
 * @description 车辆年审
 * @date 2022/1/14 16:49
 */
@RestController
@RequestMapping(value = "vehicle/annual/review")
public class VehicleAnnualReviewController extends BaseController<VehicleAnnualReview, IVehicleAnnualReviewService> {

    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleAnnualReviewController.class);

    @DubboReference(version = "1.0.0")
    private IVehicleAnnualReviewService vehicleAnnualReviewService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @Override
    public IVehicleAnnualReviewService getService() {
        return vehicleAnnualReviewService;
    }

    /**
     * 新增和修改
     */
    @PostMapping("insertOrUpdate")
    public ResponseResult insertOrUpdate(VehicleAnnualReview vehicleAnnualReview) {

        // 判断当前车辆号码是否有对应的车辆
        if (vehicleAnnualReview.getId() == null && StringUtil.isEmpty(vehicleAnnualReview.getRequestNo())) {
            throw new BusinessException("请填写申请单号");
        }
        if (vehicleAnnualReview.getId() == null && StringUtil.isEmpty(vehicleAnnualReview.getVehicleCode())) {
            throw new BusinessException("请填写车牌号");
        }
        if (vehicleAnnualReview.getId() == null && StringUtil.isEmpty(vehicleAnnualReview.getAnnualreviewType())) {
            throw new BusinessException("请填写年审类型");
        }
        if (StringUtil.isEmpty(vehicleAnnualReview.getAnnualreviewData())) {
            throw new BusinessException("请填写车辆年审日期");
        }
        if (StringUtil.isEmpty(vehicleAnnualReview.getAnnualreviewCost())) {
            throw new BusinessException("请填写车辆年审费用");
        }
        if (StringUtil.isEmpty(vehicleAnnualReview.getEffectiveDate())) {
            throw new BusinessException("请填写车辆年审的有效期截止日期");
        }
        if (StringUtil.isEmpty(vehicleAnnualReview.getServiceProvider())) {
            throw new BusinessException("请填写代办年审的服务商");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Integer state = vehicleAnnualReviewService.insertOrUpdate(vehicleAnnualReview, accessToken);
        if (state == 666666) {
            throw new BusinessException("当前车辆年审信息存在");
        }
        if (state == 777777) {
            throw new BusinessException("当前年审申请单号存在");
        }
        return ResponseResult.success();

    }

    /**
     * 车辆年审列表查询
     *
     * @param vehicleAnnualReview - startTime 年审开始
     *                            - endTime 年审结束
     *                            - vehicleCode 车牌号
     *                            - serviceProvider 服务商
     *                            - requestNo 申请单号
     */
    @GetMapping("list")
    public ResponseResult getVehicleAnnualReviewList(VehicleAnnualReview vehicleAnnualReview,
                                                     @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<VehicleAnnualReview> objectPage = new Page<>(pageNum, pageSize);
        Page<VehicleAnnualReview> list = vehicleAnnualReviewService.getVehicleAnnualReviewList(objectPage, vehicleAnnualReview, accessToken);
        return ResponseResult.success(list);

    }

    /**
     * 查询详情
     * @param id 车辆年审主键id
     */
    @PostMapping("queryDetails")
    public ResponseResult getVehicleAnnualReviewList(@RequestParam("id") Long id) {

        return ResponseResult.success(vehicleAnnualReviewService.queryVehicleAnnualReviewDetails(id));

    }

    /**
     * 车辆年设记录导出
     *
     * @param vehicleAnnualReview - startTime 年审开始
     *                            - endTime 年审结束
     *                            - vehicleCode 车牌号
     *                            - serviceProvider 服务商
     *                            - requestNo 申请单号
     */
    @GetMapping("export")
    public ResponseResult export(VehicleAnnualReview vehicleAnnualReview) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("车辆年审信息导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            vehicleAnnualReviewService.getVehicleAnnualReviewListExport(vehicleAnnualReview, accessToken, record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出失败车辆年审列表异常" + e);
            return ResponseResult.failure("导出成功车辆年审异常");
        }
    }

    /**
     * 车辆年审导入（批量新增）
     */
    @PostMapping("batchImport")
    public ResponseResult batchImport(@RequestParam("file") MultipartFile file) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        try {
            ExcelParse parse = new ExcelParse();
            parse.loadExcel(file.getOriginalFilename(), file.getInputStream());
            // 获取真实行数
            int sheetNo = 1;
            if (!"车牌号码".equals(parse.readExcelByRowAndCell(sheetNo, 1, 1))) {
                throw new BusinessException("模板格式错误");
            }
            if (!"申请单号(必填)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 2))) {
                throw new BusinessException("模板格式错误");
            }
            if (!"年审类型(必填)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 3))) {
                throw new BusinessException("模板格式错误");
            }
            if (!"年审开始日期（必填）".equals(parse.readExcelByRowAndCell(sheetNo, 1, 4))) {
                throw new BusinessException("模板格式错误");
            }
            if (!"年审截止日期（必填）".equals(parse.readExcelByRowAndCell(sheetNo, 1, 5))) {
                throw new BusinessException("模板格式错误");
            }
            if (!"年审费用（元）（必填）".equals(parse.readExcelByRowAndCell(sheetNo, 1, 6))) {
                throw new BusinessException("模板格式错误");
            }
            if (!"代办服务商（必填）".equals(parse.readExcelByRowAndCell(sheetNo, 1, 7))) {
                throw new BusinessException("模板格式错误");
            }
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(file.getInputStream(), file.getOriginalFilename(), file.getSize());
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("车辆年审");
            record.setMediaName(file.getOriginalFilename());
            record.setMediaUrl(path);
            record.setBussinessType(1);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            vehicleAnnualReviewService.batchImport(file.getBytes(), file.getOriginalFilename(), record, accessToken);
            return ResponseResult.success("正在导入文件,您可以在“系统设置-导入导出结果查询”查看导入结果");
        } catch (Exception e) {
            LOGGER.error("导入失败车辆年审异常" + e);
            return ResponseResult.failure("文件导入失败，请重试！");
        }
    }

    //获取流文件
    private void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 年审：新增年审记录车辆查询，只录入自有车
     *
     * @param plateNumber 车牌号码
     */
    @GetMapping("queryVehicleFromAnnualReview")
    public ResponseResult queryVehicleFromAnnualReview(String plateNumber) {

        if (StringUtils.isEmpty(plateNumber)) {
            return ResponseResult.failure("车牌号为空");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page page = vehicleAnnualReviewService.queryVehicleFromAnnualReview(plateNumber, accessToken);
        return ResponseResult.success(page);

    }

}
