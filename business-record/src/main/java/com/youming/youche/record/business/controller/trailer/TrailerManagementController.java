package com.youming.youche.record.business.controller.trailer;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.record.api.trailer.ITrailerManagementService;
import com.youming.youche.record.domain.trailer.TrailerManagement;
import com.youming.youche.record.dto.TrailerManagementDto;
import com.youming.youche.record.vo.OrderInfoVo;
import com.youming.youche.record.vo.driver.TrailerManagementVo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExcelParse;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-19
 */
@RestController
@RequestMapping("trailer/management")
public class TrailerManagementController extends BaseController<TrailerManagement, ITrailerManagementService> {

    @DubboReference(version = "1.0.0")
    ITrailerManagementService iTrailerManagementService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @DubboReference(version = "1.0.0")
    ISysOperLogService sysOperLogService;

    @Override
    public ITrailerManagementService getService() {
        return iTrailerManagementService;
    }

    /**
     * 实现功能: 分页查询挂车列表
     *
     * @param trailerNumber   挂车牌
     * @param isState         状态 1为在途 2为在台
     * @param sourceProvince  出发省
     * @param sourceRegion    出发市
     * @param sourceCounty    出发区
     * @param trailerMaterial 材质 1复合板、2铁柜、3铝柜、4冷柜、5其他、6未定
     * @param pageNum         分页参数
     * @param pageSize        分页参数
     * @return
     */
    @GetMapping("queryTrailerManagement")
    private ResponseResult queryTrailerManagement(@RequestParam("trailerNumber") String trailerNumber,
                                                  @RequestParam("isState") Integer isState,
                                                  @RequestParam("sourceProvince") Integer sourceProvince,
                                                  @RequestParam("sourceRegion") Integer sourceRegion,
                                                  @RequestParam("sourceCounty") Integer sourceCounty,
                                                  @RequestParam("trailerMaterial") Integer trailerMaterial,
                                                  @RequestParam("pageNum") Integer pageNum,
                                                  @RequestParam("pageSize") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<com.youming.youche.record.vo.TrailerManagementVo> page = new Page<>(pageNum, pageSize);
        Page<com.youming.youche.record.vo.TrailerManagementVo> managementVoPage = iTrailerManagementService.doQueryTrailerList(page, trailerNumber, isState, sourceProvince, sourceRegion, sourceCounty, trailerMaterial, accessToken);
        return ResponseResult.success(managementVoPage);
    }

    /**
     * 实现功能: 录入订单页面：通过部门id分页查询挂车列表
     *
     * @param trailerNumber   挂车牌
     * @param isState         状态 1为在途 2为在台
     * @param sourceProvince  出发省
     * @param sourceRegion    出发市
     * @param sourceCounty    出发区
     * @param trailerMaterial 材质 1复合板、2铁柜、3铝柜、4冷柜、5其他、6未定
     * @param orgId           部门id
     * @param pageNum         分页参数
     * @param pageSize        分页参数
     * @return
     */
    @GetMapping("queryTrailerManagementByorgId")
    private ResponseResult queryTrailerManagementByorgId(@RequestParam("trailerNumber") String trailerNumber,
                                                  @RequestParam("isState") Integer isState,
                                                  @RequestParam("sourceProvince") Integer sourceProvince,
                                                  @RequestParam("sourceRegion") Integer sourceRegion,
                                                  @RequestParam("sourceCounty") Integer sourceCounty,
                                                  @RequestParam("trailerMaterial") Integer trailerMaterial,
                                                  @RequestParam("orgId") Long orgId,
                                                  @RequestParam("pageNum") Integer pageNum,
                                                  @RequestParam("pageSize") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<com.youming.youche.record.vo.TrailerManagementVo> page = new Page<>(pageNum, pageSize);
        Page<com.youming.youche.record.vo.TrailerManagementVo> managementVoPage = iTrailerManagementService.doQueryTrailerListByorgId(page, trailerNumber, isState, sourceProvince, sourceRegion, sourceCounty, trailerMaterial, accessToken,orgId);
        return ResponseResult.success(managementVoPage);
    }

    /**
     * 挂车信息导出
     *
     * @param trailerNumber   挂车牌
     * @param isState         状态 1为在途 2为在台
     * @param sourceProvince  出发省
     * @param sourceRegion    出发市
     * @param sourceCounty    出发区
     * @param trailerMaterial 材质 1复合板、2铁柜、3铝柜、4冷柜、5其他、6未定
     * @return
     */
    @GetMapping("queryTrailerManagement/export")
    private ResponseResult queryTrailerManagementExport(@RequestParam("trailerNumber") String trailerNumber,
                                                        @RequestParam("isState") Integer isState,
                                                        @RequestParam("sourceProvince") Integer sourceProvince,
                                                        @RequestParam("sourceRegion") Integer sourceRegion,
                                                        @RequestParam("sourceCounty") Integer sourceCounty,
                                                        @RequestParam("trailerMaterial") Integer trailerMaterial) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("挂车信息导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iTrailerManagementService.doQueryTrailerListExport(trailerNumber, isState, sourceProvince,
                    sourceRegion, sourceCounty, trailerMaterial, accessToken, record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            return ResponseResult.failure("导出异常");
        }

    }

    /**
     * 实现功能: 查看挂车详情
     *
     * @param trailerId 挂车id
     * @return
     */
    @GetMapping("getTrailerByTrailerId")
    private ResponseResult getTrailerByTrailerId(@RequestParam("trailerId") Long trailerId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        TrailerManagementVo trailerByTrailerId = iTrailerManagementService.getTrailerByTrailerId(trailerId,accessToken);
        return ResponseResult.success(trailerByTrailerId);
    }

    /**
     * 实现功能: 查看挂车历史详情
     *
     * @param trailerId 挂车id
     * @return
     */
    @GetMapping("getTrailerVerByTrailerId")
    public ResponseResult getTrailerVerByTrailerId(@RequestParam("trailerId") Long trailerId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        TrailerManagementVo trailerVerByTrailerId = iTrailerManagementService.getTrailerVerByTrailerId(trailerId, accessToken);
        return ResponseResult.success(trailerVerByTrailerId);
    }

    /**
     * 实现功能: 分页查询挂车历史列表
     *
     * @param trailerNumber   挂车牌
     * @param isState         状态 1为在途 2为在台
     * @param sourceProvince  出发省
     * @param sourceRegion    出发市
     * @param sourceCounty    出发区
     * @param trailerMaterial 材质 1复合板、2铁柜、3铝柜、4冷柜、5其他、6未定
     * @param deleteFlag      删除标识
     * @param pageNum         分页参数
     * @param pageSize        分页参数
     * @return
     */
    @GetMapping("queryTrailerManagementDel")
    private ResponseResult queryTrailerManagement(@RequestParam("trailerNumber") String trailerNumber,
                                                  @RequestParam("isState") Integer isState,
                                                  @RequestParam("sourceProvince") Integer sourceProvince,
                                                  @RequestParam("sourceRegion") Integer sourceRegion,
                                                  @RequestParam("sourceCounty") Integer sourceCounty,
                                                  @RequestParam("trailerMaterial") Integer trailerMaterial,
                                                  @RequestParam("deleteFlag") Integer deleteFlag,
                                                  @RequestParam("pageNum") Integer pageNum,
                                                  @RequestParam("pageSize") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<com.youming.youche.record.vo.TrailerManagementVo> page = new Page<>(pageNum, pageSize);
        Page<com.youming.youche.record.vo.TrailerManagementVo> managementVoPage = iTrailerManagementService.doQueryTrailerListDel(page, trailerNumber, isState, sourceProvince, sourceRegion, sourceCounty, trailerMaterial, deleteFlag, accessToken);
        return ResponseResult.success(managementVoPage);
    }

    /**
     * 挂车历史信息导出
     *
     * @param trailerNumber   挂车牌
     * @param isState         状态 1为在途 2为在台
     * @param sourceProvince  出发省
     * @param sourceRegion    出发市
     * @param sourceCounty    出发区
     * @param trailerMaterial 材质 1复合板、2铁柜、3铝柜、4冷柜、5其他、6未定
     * @param deleteFlag      删除标识
     * @return
     */
    @GetMapping("queryTrailerManagementDel/export")
    public ResponseResult queryTrailerManagementDelExport(@RequestParam("trailerNumber") String trailerNumber,
                                                          @RequestParam("isState") Integer isState,
                                                          @RequestParam("sourceProvince") Integer sourceProvince,
                                                          @RequestParam("sourceRegion") Integer sourceRegion,
                                                          @RequestParam("sourceCounty") Integer sourceCounty,
                                                          @RequestParam("trailerMaterial") Integer trailerMaterial,
                                                          @RequestParam("deleteFlag") Integer deleteFlag) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("挂车历史信息导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iTrailerManagementService.doQueryTrailerListDelExport(trailerNumber, isState, sourceProvince, sourceRegion,
                    sourceCounty, trailerMaterial, deleteFlag, accessToken, record);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            return ResponseResult.failure("导出异常");
        }
    }


    /**
     * 实现功能: 删除挂车
     *
     * @param trailerId 挂车主键id
     * @return
     */
    @DeleteMapping("deleteTrailer/{trailerId}")
    public ResponseResult deleteTrailer(@PathVariable Long trailerId) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Integer i = iTrailerManagementService.deleteTrailer(trailerId,accessToken);
        return ResponseResult.success(i);
    }

    /**
     * 实现功能: 新增或者修改挂车信息
     *
     * @param trailerManagementDto 挂车信息
     * @return
     */
    @PostMapping("doSaveOrUpdate")
    public ResponseResult doSaveOrUpdate(@RequestBody TrailerManagementDto trailerManagementDto) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return iTrailerManagementService.doSaveOrUpdate(trailerManagementDto, accessToken);
    }

    /**
     * 实现功能: 启动闲置
     *
     * @param flag 1，闲置、 其他，启动
     * @param vid  挂车id
     * @return
     */
    @PostMapping("doSaveIdle")
    public ResponseResult doSaveIdle(@RequestParam(value = "flag", defaultValue = "0") Short flag, @RequestParam("vid") Long vid) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        TrailerManagement trailerManagement1 = iTrailerManagementService.get(vid);
        boolean s = iTrailerManagementService.updateTrailerManagement(flag,vid,trailerManagement1.getTrailerNumber(),accessToken);
        if (flag==1){
            sysOperLogService.save(SysOperLogConst.BusiCode.Trailer,vid,
                    SysOperLogConst.OperType.Idle, "挂车-" + trailerManagement1.getTrailerNumber() + "-闲置", accessToken);
        }else {
            sysOperLogService.save(SysOperLogConst.BusiCode.Trailer,vid,
                    SysOperLogConst.OperType.Enable, "挂车-" + trailerManagement1.getTrailerNumber() + "-启用", accessToken);
        }
        return s ? ResponseResult.success("操作成功") : ResponseResult.failure("操作失败");
    }

    /**
     * 实现功能: 查询挂车订单信息
     *
     * @param trailerPlate    挂车车牌
     * @param plateNumber     车牌
     * @param orderId         订单号
     * @param carUserName     司机名称
     * @param carUserPhone    司机手机号
     * @param beginDependTime 超始靠台时间
     * @param endDependTime   结束靠台时间
     * @param sourceRegion    超始地
     * @param destRegion      目标地
     * @return
     */
    @GetMapping("getTrailerUsedList")
    private ResponseResult getTrailerUsedList(@RequestParam("trailerPlate") String trailerPlate,
                                              @RequestParam("plateNumber") String plateNumber,
                                              @RequestParam("orderId") Long orderId,
                                              @RequestParam("carUserName") String carUserName,
                                              @RequestParam("carUserPhone") String carUserPhone,
                                              @RequestParam("beginDependTime") String beginDependTime,
                                              @RequestParam("endDependTime") String endDependTime,
                                              @RequestParam("sourceRegion") Integer sourceRegion,
                                              @RequestParam("destRegion") Integer destRegion,
                                              @RequestParam("pageNum") Integer pageNum,
                                              @RequestParam("pageSize") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<OrderInfoVo> page = new Page<>(pageNum, pageSize);
        Page<OrderInfoVo> orderInfoVoPage = iTrailerManagementService.queryTrailerOrderList(page, accessToken, trailerPlate, plateNumber, carUserName, carUserPhone,
                orderId, sourceRegion, destRegion, beginDependTime, endDependTime);
        return ResponseResult.success(orderInfoVoPage);
    }

    /**
     * 批量导入挂车信息
     *
     * @param file 挂车信息
     * @return
     */
    @PostMapping("batchImport")
    public ResponseResult batchImport(@RequestParam("file") MultipartFile file) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ExcelParse parse = new ExcelParse();
            parse.loadExcel(file.getOriginalFilename(), file.getInputStream());
            // 获取真实行数
            int sheetNo = 1;
            int rows = parse.getRealRowCount(sheetNo);
            if (rows > 300) {
                throw new BusinessException("最多一次性导入300条数据");
            }
            if (!"车牌号码(必填)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 1)))
                throw new BusinessException("模板格式错误");
            if (!"所有权(必选)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 2)))
                throw new BusinessException("模板格式错误");
            if (!"材质(必选)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 3)))
                throw new BusinessException("模板格式错误");
            if (!"载重(吨)(必填)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 4)))
                throw new BusinessException("模板格式错误");
            if (!"容积(立方米)(必填)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 5)))
                throw new BusinessException("模板格式错误");
            if (!"长(米)(必填)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 6)))
                throw new BusinessException("模板格式错误");
            if (!"宽(米)(必填)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 7)))
                throw new BusinessException("模板格式错误");
            if (!"高(米)(必填)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 8)))
                throw new BusinessException("模板格式错误");
            if (!"状态(必填)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 9)))
                throw new BusinessException("模板格式错误");
            if (!"起始位置(必填)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 10)))
                throw new BusinessException("模板格式错误");
            if (!"归属部门".equals(parse.readExcelByRowAndCell(sheetNo, 1, 11)))
                throw new BusinessException("模板格式错误");
            if (!"归属人手机号".equals(parse.readExcelByRowAndCell(sheetNo, 1, 12)))
                throw new BusinessException("模板格式错误");
            if (!"线路绑定".equals(parse.readExcelByRowAndCell(sheetNo, 1, 13)))
                throw new BusinessException("模板格式错误");
            if (!"价格(元)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 14)))
                throw new BusinessException("模板格式错误");
            if (!"残值(元）".equals(parse.readExcelByRowAndCell(sheetNo, 1, 15)))
                throw new BusinessException("模板格式错误");
            if (!"贷款利息(元/月)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 16)))
                throw new BusinessException("模板格式错误");
            if (!"还款期数(月)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 17)))
                throw new BusinessException("模板格式错误");
            if (!"已还期数(月)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 18)))
                throw new BusinessException("模板格式错误");
            if (!"购买时间".equals(parse.readExcelByRowAndCell(sheetNo, 1, 19)))
                throw new BusinessException("模板格式错误");
            if (!"折旧月数".equals(parse.readExcelByRowAndCell(sheetNo, 1, 20)))
                throw new BusinessException("模板格式错误");
            if (!"保险(元/月)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 21)))
                throw new BusinessException("模板格式错误");
            if (!"审车费(元/月)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 22)))
                throw new BusinessException("模板格式错误");
            if (!"保养费(元/公里)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 23)))
                throw new BusinessException("模板格式错误");
            if (!"维修费(元/公里)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 24)))
                throw new BusinessException("模板格式错误");
            if (!"轮胎费(元/公里)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 25)))
                throw new BusinessException("模板格式错误");
            if (!"其他费用(元/公里)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 26)))
                throw new BusinessException("模板格式错误");
            if (!"上次年审时间".equals(parse.readExcelByRowAndCell(sheetNo, 1, 27)))
                throw new BusinessException("模板格式错误");
            if (!"年审到期时间".equals(parse.readExcelByRowAndCell(sheetNo, 1, 28)))
                throw new BusinessException("模板格式错误");
            if (!"上次季审时间".equals(parse.readExcelByRowAndCell(sheetNo, 1, 29)))
                throw new BusinessException("模板格式错误");
            if (!"季审到期时间".equals(parse.readExcelByRowAndCell(sheetNo, 1, 30)))
                throw new BusinessException("模板格式错误");
            if (!"上次商业险时间".equals(parse.readExcelByRowAndCell(sheetNo, 1, 31)))
                throw new BusinessException("模板格式错误");
            if (!"商业险到期时间".equals(parse.readExcelByRowAndCell(sheetNo, 1, 32)))
                throw new BusinessException("模板格式错误");
            if (!"商业险单号".equals(parse.readExcelByRowAndCell(sheetNo, 1, 33)))
                throw new BusinessException("模板格式错误");
            if (!"上次交强险时间".equals(parse.readExcelByRowAndCell(sheetNo, 1, 34)))
                throw new BusinessException("模板格式错误");
            if (!"交强险到期时间".equals(parse.readExcelByRowAndCell(sheetNo, 1, 35)))
                throw new BusinessException("模板格式错误");
            if (!"交强险单号".equals(parse.readExcelByRowAndCell(sheetNo, 1, 36)))
                throw new BusinessException("模板格式错误");
            if (!"上次其他险时间".equals(parse.readExcelByRowAndCell(sheetNo, 1, 37)))
                throw new BusinessException("模板格式错误");
            if (!"其他险到期时间".equals(parse.readExcelByRowAndCell(sheetNo, 1, 38)))
                throw new BusinessException("模板格式错误");
            if (!"其他险单号".equals(parse.readExcelByRowAndCell(sheetNo, 1, 39)))
                throw new BusinessException("模板格式错误");
            if (!"保养周期(公里)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 40)))
                throw new BusinessException("模板格式错误");
            if (!"保养预警公里(公里)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 41)))
                throw new BusinessException("模板格式错误");
            if (!"上次保养时间".equals(parse.readExcelByRowAndCell(sheetNo, 1, 42)))
                throw new BusinessException("模板格式错误");
            if (!"登记日期".equals(parse.readExcelByRowAndCell(sheetNo, 1, 43)))
                throw new BusinessException("模板格式错误");
            if (!"登记证号".equals(parse.readExcelByRowAndCell(sheetNo, 1, 44)))
                throw new BusinessException("模板格式错误");
            if (!"行驶证生效时间".equals(parse.readExcelByRowAndCell(sheetNo, 1, 45)))
                throw new BusinessException("模板格式错误");
            if (!"行驶证失效时间".equals(parse.readExcelByRowAndCell(sheetNo, 1, 46)))
                throw new BusinessException("模板格式错误");
            if (!"营运证生效时间".equals(parse.readExcelByRowAndCell(sheetNo, 1, 47)))
                throw new BusinessException("模板格式错误");
            if (!"营运证失效时间".equals(parse.readExcelByRowAndCell(sheetNo, 1, 48)))
                throw new BusinessException("模板格式错误");
            FastDFSHelper client = FastDFSHelper.getInstance();
            String path = client.upload(file.getInputStream(), file.getOriginalFilename(), file.getSize());

            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("挂车信息");
            record.setMediaName(file.getOriginalFilename());
            record.setMediaUrl(path);
            record.setBussinessType(1);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iTrailerManagementService.batchImport(file.getBytes(), record, accessToken, file.getOriginalFilename());
            return ResponseResult.success("正在导入文件,您可以在“系统设置-导入导出结果查询”查看导入结果");
        } catch (Exception e) {
            System.out.println(e);
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
     * 14509 模糊查询挂车信息
     *
     * @param sourceRegion  出发市编号
     * @param trailerNumber 挂车车牌
     */
    @GetMapping("getNotUsedTrailer")
    public ResponseResult getNotUsedTrailer(Integer sourceRegion, String trailerNumber,
                                            @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize) {
        if (null == sourceRegion || sourceRegion == -1) {
            throw new BusinessException("城市编号不能为空!");
        }
        if (null == trailerNumber) {
            throw new BusinessException("车牌号不能为空!");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<TrailerManagement> localNotUsedTrailerPage = iTrailerManagementService.getLocalNotUsedTrailerPage(sourceRegion, trailerNumber, pageNum, pageSize, accessToken);
        return ResponseResult.success(localNotUsedTrailerPage);
    }

}
