package com.youming.youche.record.business.controller.cm;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.record.api.cm.ICmCustomerInfoService;
import com.youming.youche.record.api.cm.ICmCustomerLineService;
import com.youming.youche.record.common.CommonUtil;
import com.youming.youche.record.common.DirectionDto;
import com.youming.youche.record.common.GpsUtil;
import com.youming.youche.record.common.QueryDistance;
import com.youming.youche.record.common.SysStaticDataEnum;
import com.youming.youche.record.domain.cm.CmCustomerInfo;
import com.youming.youche.record.domain.cm.CmCustomerLine;
import com.youming.youche.record.domain.cm.CmCustomerLineSubway;
import com.youming.youche.record.dto.cm.CmCustomerLineDto;
import com.youming.youche.record.dto.cm.CmCustomerLineOutDto;
import com.youming.youche.record.dto.cm.QueryLineByTenantForWXDto;
import com.youming.youche.record.vo.cm.CmCustomerLineVo;
import com.youming.youche.record.vo.cm.QueryLineByTenantForWXVo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExcelParse;
import com.youming.youche.util.JsonHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 客户线路信息表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-14
 */
@RestController
@RequestMapping("/customer/line")
public class CmCustomerLineController extends BaseController<CmCustomerLine, ICmCustomerLineService> {

    @DubboReference(version = "1.0.0")
    ICmCustomerLineService iCmCustomerLineService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @DubboReference(version = "1.0.0")
    ICmCustomerInfoService cmCustomerInfoService;

    @Override
    public ICmCustomerLineService getService() {
        return iCmCustomerLineService;
    }

    /**
     * 实现功能: 分页获取线路信息
     *
     * @param lineCode 线路编号
     * @return
     */
    @GetMapping("/getCustomerLineByLineCode")
    private ResponseResult getCustomerLineByLineCode(@RequestParam("lineCode") String lineCode,
                                                     @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        try {
            Page<CmCustomerLine> page = new Page<>(pageNum, pageSize);
            Page<CmCustomerLine> page1 = iCmCustomerLineService.getCustomerLineByLineCode(page, lineCode, accessToken);
            return ResponseResult.success(page1);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * @return java.lang.String
     * @author 向子俊
     * @Description //TODO 查询所有线路信息
     * @date 16:48 2022/1/6 0006
     * @Param [customerLine]
     */
    @GetMapping("/customer/getLine")
    public ResponseResult getLineAll(CmCustomerLineVo customerLine,
                                     @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            Page<CmCustomerLineDto> linePage = new Page<>(pageNum, pageSize);
            Page<CmCustomerLineDto> allLine = iCmCustomerLineService.findAllLine(linePage, customerLine, accessToken);
            return ResponseResult.success(allLine);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * @return com.youming.youche.commons.response.ResponseResult
     * @author 向子俊
     * @Description //TODO 根据线路id查询线路信息
     * @date 19:19 2022/1/22 0022
     * @Param [lineCodeRule]
     */
    @GetMapping("/get/lineById")
    public ResponseResult getLineById(Long lineId, Integer isEdit) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (lineId == null || lineId < 0) {
            throw new BusinessException("线路编号不能为空!");
        }
        try {
            List<CmCustomerLineDto> lineByCode = iCmCustomerLineService.findLineById(lineId, isEdit, accessToken);
            return ResponseResult.success(lineByCode);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("查询异常!");
        }
    }

    /**
     * 新增或修改线路信息
     *
     * @param customerLine 线路信息
     * @return
     * @throws Exception
     */
    @PostMapping("/save/line")
    public ResponseResult saveOrUpdateLine(@RequestBody CmCustomerLineVo customerLine) {
        //获取用户信息
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Long customerId = customerLine.getCustomerId();
        String taiwanDate = customerLine.getTaiwanDate();
        Integer payWay = customerLine.getPayWay();
        String priceUnit = String.valueOf(customerLine.getPriceUnit());
        Integer priceEnum = customerLine.getPriceEnum();
        //String priceUnitDouble = customerLine.getpriceDataFormat.getStringKey(inParam, "priceUnitDouble");
        Integer vehicleStatus = customerLine.getVehicleStatus();
        String arriveTimeStr = String.valueOf(customerLine.getArriveTime());
        Integer reciveTime = customerLine.getReciveTime();
        Integer recondTime = customerLine.getRecondTime();
        Integer invoiceTime = customerLine.getInvoiceTime();
        Integer collectionTime = customerLine.getCollectionTime();
        String vehicleLength = customerLine.getVehicleLength();
        String guidePrice = String.valueOf(customerLine.getGuidePrice());
        String guideMerchant = String.valueOf(customerLine.getGuideMerchant());
        LocalDate effectBegin = customerLine.getEffectBegin();
        LocalDate effectEnd = customerLine.getEffectEnd();
        String lineTel = customerLine.getLineTel();
        String lineName = customerLine.getLineName();
        String mileageNumber = String.valueOf(customerLine.getMileageNumber());
        String receivePhone = customerLine.getReceivePhone();
        String goodsVolume = String.valueOf(customerLine.getGoodsVolume());
        String goodsWeight = String.valueOf(customerLine.getGoodsWeight());
        String emptyOilCostPer = String.valueOf(customerLine.getEmptyOilCostPer());
        String oilCostPer = String.valueOf(customerLine.getOilCostPer());
        String pontagePer = String.valueOf(customerLine.getPontagePer());
        String contractUrl = customerLine.getContractUrl();
        Integer isRevertGoods = customerLine.getIsRevertGoods();
        String backhaulGuideFee = String.valueOf(customerLine.getBackhaulGuideFee());
        String backhaulGuideName = customerLine.getBackhaulGuideName();
        String backhaulGuidePhone = customerLine.getBackhaulGuidePhone();
        //Long lineId = customerLine.getId();
        String reciveAddress = customerLine.getReciveAddress();
        Long reciveProvinceId = customerLine.getReciveProvinceId();
        Long reciveCityId = customerLine.getReciveCityId();
        Long orgId = customerLine.getOrgId();
        Integer reciveTimeDay = customerLine.getReciveTimeDay();
        Integer recondTimeDay = customerLine.getRecondTimeDay();
        Integer invoiceTimeDay = customerLine.getRecondTimeDay();
        Integer collectionTimeDay = customerLine.getCollectionTimeDay();
        Integer collectionTimeMonth = customerLine.getCollectionTimeMonth();
        if(collectionTimeMonth == null){
            collectionTimeMonth = -1;
        }
        if (orgId == null || orgId < 0) {
            throw new BusinessException("亲，请输入线路归属部门！");
        }
        if (org.apache.commons.lang.StringUtils.isEmpty(reciveAddress)) {
            throw new BusinessException("亲，请输入回单地址！");
        }
        if (reciveProvinceId == null || reciveProvinceId < 0) {
            throw new BusinessException("亲，请输入回单地址！");
        }
        if (reciveCityId == null) {
            throw new BusinessException("亲，请输入回单地址！");
        }
        if (customerId == null) {
            throw new BusinessException("亲，请选择客户！");
        }
        if (org.apache.commons.lang.StringUtils.isEmpty(lineName)) {
            throw new BusinessException("亲，请输入联系人姓名！");
        }
        if (org.apache.commons.lang.StringUtils.isNotBlank(lineTel)) {
            if (!CommonUtil.isCheckMobiPhoneNew(lineTel) && !CommonUtil.isCheckMobiPhone(lineTel)) {
                throw new BusinessException("亲，输入的联系电话格式错误，请重新输入！");
            }

        } else {
            throw new BusinessException("亲，请输入联系电话！");
        }

        if (effectBegin == null) {
            throw new BusinessException("亲，请选择生效日期！！");
        }
        if (effectEnd == null) {
            throw new BusinessException("亲，请选择有效期截止日期！");
        }
//        } else if (effectEnd.indexOf("00:00:00") > 0) {
//            effectEnd = effectEnd.replace("00:00:00", "23:59:59");
//        } else {
//            effectEnd = effectEnd.trim() + " 23:59:59";
//        }
        customerLine.setEffectEnd(effectEnd);

        if (org.apache.commons.lang.StringUtils.isBlank(contractUrl)) {
            throw new BusinessException("请上传合同附件！");
        }
        String goodsName = customerLine.getGoodsName();
        if (org.apache.commons.lang.StringUtils.isBlank(goodsName)) {
            throw new BusinessException("亲，请输入货物名称！");
        }

        if (!CommonUtil.isNumber(goodsWeight)) {
            throw new BusinessException("亲，请输入正确的重量！");
        }
        if (!CommonUtil.isNumber(goodsVolume)) {
            throw new BusinessException("亲，请输入正确的体积！");
        }

        if (org.apache.commons.lang.StringUtils.isBlank(vehicleLength) || "-1".equals(vehicleLength)) {
            throw new BusinessException("亲，请选择车长！");
        }
        if (vehicleStatus == null) {
            throw new BusinessException("亲，请选择车型！");
        }
        if (org.apache.commons.lang.StringUtils.isNotBlank(receivePhone)) {
            if (!CommonUtil.isCheckMobiPhoneNew(receivePhone) && !CommonUtil.isCheckMobiPhone(receivePhone)) {
                throw new BusinessException("亲，输入的收货电话格式错误，请重新输入！");
            }
        }
        if (org.apache.commons.lang.StringUtils.isBlank(taiwanDate)) {
            throw new BusinessException("亲，请选择靠站时间！");
        }
        float arriveTime = -1f;
        if (!CommonUtil.isNumber(arriveTimeStr)) {
            throw new BusinessException("亲，请输入正确的到达时限！");
        }

        if (!CommonUtil.isNumber(mileageNumber)) {
            throw new BusinessException("亲，请输入正确的公里数！");
        }
        String cmMileageNumber = String.valueOf(customerLine.getCmMileageNumber());
        if (!CommonUtil.isNumber(cmMileageNumber)) {
            throw new BusinessException("亲，请输入正确的公里数！");
        }

        if (!CommonUtil.isNumber(guidePrice)) {
            throw new BusinessException("亲，请输入正确的拦标价！");
        }

        if (!CommonUtil.isNumber(guideMerchant)) {
            throw new BusinessException("亲，请输入正确的承包价！");
        }

        //勾选需要找回货才需要校验
        if (isRevertGoods == SysStaticDataEnum.IS_REVERT_GOODS.REVERT_GOODS_YES) {
            if (org.apache.commons.lang.StringUtils.isBlank(backhaulGuideFee)) {
                throw new BusinessException("亲，请输入回货指导价!");
            }

            if (org.apache.commons.lang.StringUtils.isBlank(backhaulGuideName)) {
                throw new BusinessException("亲，请输入回货联系人!");
            }

            if (org.apache.commons.lang.StringUtils.isBlank(backhaulGuidePhone)) {
                throw new BusinessException("亲，请输入回货联系人电话!");
            }
        }
        if (org.apache.commons.lang.StringUtils.isNotBlank(backhaulGuideFee) && !CommonUtil.isNumber(backhaulGuideFee)) {
            throw new BusinessException("亲，请输入正确的回货指导价!");
        }
        // && !CommonUtil.isCheckPhone(backhaulGuidePhone)
        if (org.apache.commons.lang.StringUtils.isNotBlank(backhaulGuidePhone) && !CommonUtil.isCheckMobiPhoneNew(backhaulGuidePhone)) {
            throw new BusinessException("亲，输入的回货联系人电话格式错误，请重新输入!");
        }

        if (!CommonUtil.isNumber(pontagePer)) {
            throw new BusinessException("亲，请输入正确的路桥费！");
        }

        if (!CommonUtil.isNumber(emptyOilCostPer)) {
            throw new BusinessException("亲，请输入正确的空载油耗！");
        }

        if (!CommonUtil.isNumber(oilCostPer)) {
            throw new BusinessException("亲，请输入正确的载重油耗！");
        }
        if (!CommonUtil.isNumber(priceUnit)) {
            throw new BusinessException("亲，请输入正确的单价！");
        }

        if (priceEnum == null) {
            throw new BusinessException("亲，请选择单价的计价方式！");
        }

        String estimateIncome = String.valueOf(customerLine.getEstimateIncome());
        if (!CommonUtil.isNumber(estimateIncome)) {
            throw new BusinessException("亲，请输入正确的预估应收价！");
        }

        if (payWay == null) {
            throw new BusinessException("亲，请选择结算方式！");
        }
//		if(recondTime <= 0){
//			throw new BusinessException("亲，请输入对账日！");
//		}
        if (reciveTime == null) {
            throw new BusinessException("亲，请输入正确的回单时限！");
        }

//		if (invoiceTime <= 0) {
//			throw new BusinessException("亲，请输入正确的开票期限！");
//		}

        if (payWay > SysStaticDataEnum.BALANCE_TYPE.PRE_ALL && (collectionTime <= 0 && collectionTimeMonth <= 0)) {
            throw new BusinessException("亲，请输入正确的收款期限！");
        }

//        if (payWay == SysStaticDataEnum.BALANCE_TYPE.PRE_AFTER_MONTH) {
////            int reciveTimeDay = customerLine.getReciveTimeDay();
////            int recondTimeDay = customerLine.getRecondTimeDay();
////            int invoiceTimeDay = customerLine.getInvoiceTimeDay();
////            int collectionTimeDay = customerLine.getCollectionTimeDay();
////            customerLine.setReciveTimeDay(null);
////            customerLine.setRecondTimeDay(null);
////            customerLine.setInvoiceTimeDay(null);
////            customerLine.setCollectionTimeDay(null);
//        }

        String flag = null;
        flag = iCmCustomerLineService.saveOrUpdateLine(customerLine, accessToken);
        if ("Y".equals(flag)) {
            return ResponseResult.success("新增线路成功!");
        } else if ("YM".equals(flag)) {
            return ResponseResult.success("修改线路成功!");
        } else {
            return ResponseResult.failure("操作失败!");
        }

    }

    /**
     * @return com.youming.youche.commons.response.ResponseResult
     * @author 向子俊
     * @Description //TODO 自动创建往返编码
     * @date 0:41 2022/2/19 0019
     * @Param []
     */
    @GetMapping("/createBackhaulNumber")
    public ResponseResult createBackhaulNumber() {
        try {
            String backhaulNumber = iCmCustomerLineService.createBackhaulNumber();
            return ResponseResult.success(backhaulNumber);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("创建编码异常");
        }
    }

    /**
     * @return java.lang.String
     * @author 向子俊
     * @Description //TODO 查询已有编码
     * @date 23:46 2022/2/21 0021
     * @Param []
     */
    @GetMapping("/getCustomerLineByBackhaul")
    public ResponseResult getCustomerLineByBackhaul(CmCustomerLineVo customerLine,
                                                    @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) throws Exception {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<CmCustomerLineDto> backhaulPage = new Page<>(pageNum, pageSize);
        Page<CmCustomerLineDto> customerLineByBackhaul = iCmCustomerLineService.getCustomerLineByBackhaul(backhaulPage, customerLine, accessToken);
        return ResponseResult.success(customerLineByBackhaul);
    }

    /**
     * 驾驶路线规划
     * @param distance 计算距离入参
     * @return
     */
    @PostMapping("/queryDistance")
    public ResponseResult queryDistance(@RequestBody QueryDistance distance) {
        if (distance != null) {
            String originLat = distance.getOriginLat();//纬度
            String originLng = distance.getOriginLng();//经度
            String destLat = distance.getDestLat();
            String destLng = distance.getDestLng();
            String originRegion = distance.getOriginRegion();
            String destRegion = distance.getDestRegion();
            List<CmCustomerLineSubway> lineSubwayList = distance.getLineSubwayList();//途经地
            if (StringUtils.isEmpty(originLat)) {
                throw new BusinessException("起点纬度不能为空");
            }
            if (StringUtils.isEmpty(originLng)) {
                throw new BusinessException("起点经度不能为空");
            }
            if (StringUtils.isEmpty(destLat)) {
                throw new BusinessException("终点纬度不能为空");
            }
            if (StringUtils.isEmpty(destLng)) {
                throw new BusinessException("终点经度不能为空");
            }
            if (StringUtils.isEmpty(originRegion)) {
                throw new BusinessException("起始点所在城市不能为空");
            }
            if (StringUtils.isEmpty(destRegion)) {
                throw new BusinessException("终点所在城市不能为空");
            }
            String[] waypoints = new String[]{};
            if (lineSubwayList != null && lineSubwayList.size() > 0) {
                waypoints = new String[lineSubwayList.size()];
                for (int i = 0; i < lineSubwayList.size(); i++) {
                    CmCustomerLineSubway customerLineSubway = lineSubwayList.get(i);
                    String desEnd = customerLineSubway.getDesEnd();
                    String desNand = customerLineSubway.getDesNand();
                    if (StringUtils.isEmpty(desEnd)) {
                        throw new BusinessException("途经地经度不能为空");
                    }
                    if (StringUtils.isEmpty(desNand)) {
                        throw new BusinessException("途经地经度不能为空");
                    }
                    waypoints[i] = desNand + "," + desEnd;
                }
            }
            DirectionDto map = GpsUtil.getDirection(originLat, originLng, destLat, destLng, "driving",
                    waypoints, 11, originRegion, destRegion);
            map.setOriginLat(originLat);
            map.setOriginLng(originLng);
            map.setDestLat(destLat);
            map.setDestLng(destLng);
            if (lineSubwayList != null && lineSubwayList.size() > 0) {
                map.setLineSubwayList(lineSubwayList);
            }
            return ResponseResult.success(map);
        } else {
            return ResponseResult.success("查询入参不能为空");
        }
    }

    /**
     * @return java.lang.String
     * @author 向子俊
     * @Description //TODO 导入线路档案
     * @date 15:51 2022/1/15 0015
     * @Param [file]
     */
    @PostMapping("/batchImport/line")
    public ResponseResult importLine(@RequestParam("file") MultipartFile file) {
        ExcelParse parse = new ExcelParse();
        try {
            parse.loadExcel(file.getOriginalFilename(), file.getInputStream());
            // 获取真实行数
            int sheetNo = 1;
            int rows = parse.getRealRowCount(sheetNo);
            if (rows > 300) {
                throw new BusinessException("最多一次性导入300条数据");
            }
            if (!"客户全称".equals(parse.readExcelByRowAndCell(sheetNo, 1, 1))) {
                throw new BusinessException("第1行1列模板格式错误");
            }
            if (!"有效期至".equals(parse.readExcelByRowAndCell(sheetNo, 1, 2))) {
                throw new BusinessException("第1行2列模板格式错误");
            }
            if (!"货品名称".equals(parse.readExcelByRowAndCell(sheetNo, 1, 3))) {
                throw new BusinessException("第1行3列模板格式错误");
            }
            if (!"货品类型".equals(parse.readExcelByRowAndCell(sheetNo, 1, 4))) {
                throw new BusinessException("第1行4列模板格式错误");
            }
            if (!"重量(吨)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 5))) {
                throw new BusinessException("第1行5列模板格式错误");
            }
            if (!"体积(方)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 6))) {
                throw new BusinessException("第1行6列模板格式错误");
            }
            if (!"车型要求".equals(parse.readExcelByRowAndCell(sheetNo, 1, 7))) {
                throw new BusinessException("第1行7列模板格式错误");
            }
            if (!"车型要求车长".equals(parse.readExcelByRowAndCell(sheetNo, 1, 8))) {
                throw new BusinessException("第1行8列模板格式错误");
            }
            if (!"靠台时间".equals(parse.readExcelByRowAndCell(sheetNo, 1, 9))) {
                throw new BusinessException("第1行9列模板格式错误");
            }
            if (!"始发地址".equals(parse.readExcelByRowAndCell(sheetNo, 1, 10))) {
                throw new BusinessException("第1行10列模板格式错误");
            }
            if (!"目的地址".equals(parse.readExcelByRowAndCell(sheetNo, 1, 11))) {
                throw new BusinessException("第1行11列模板格式错误");
            }
            if (!"到达时限".equals(parse.readExcelByRowAndCell(sheetNo, 1, 12))) {
                throw new BusinessException("第1行12列模板格式错误");
            }
            if (!"线路名称".equals(parse.readExcelByRowAndCell(sheetNo, 1, 13))) {
                throw new BusinessException("第1行13列模板格式错误");
            }
            if (!"客户公里数(KM)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 14))) {
                throw new BusinessException("第1行14列模板格式错误");
            }
            if (!"供应商公里数(KM)".equals(parse.readExcelByRowAndCell(sheetNo, 1, 15))) {
                throw new BusinessException("第1行15列模板格式错误");
            }
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

            ImportOrExportRecords record = new ImportOrExportRecords();
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String mediaExcelPath = client.upload(file.getInputStream(), "客户线路.xlsx", file.getSize());
            record.setMediaUrl(mediaExcelPath);
            record.setName("客户线路档案导入");
            record.setMediaName(file.getOriginalFilename());
            record.setBussinessType(1);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iCmCustomerLineService.batchImportCustomerLine(file.getBytes(), record, accessToken);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("模板格式错误");
        }
        return ResponseResult.success("正在导入文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
    }

    /**
     * 线路信息导出
     *
     * @param customerLine 客户线路信息
     * @return
     */
    @GetMapping("/export/customerLine")
    public ResponseResult exportCustomerLine(CmCustomerLineVo customerLine) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        ImportOrExportRecords record = new ImportOrExportRecords();
        record.setName("线路信息导出");
        record.setBussinessType(2);
        record.setState(1);
        try {
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iCmCustomerLineService.exportCustomerLine(record, customerLine, accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
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
     * 聂杰伟
     * 根据客户ID 获取客户信息
     *
     * @param lineId 线路Id
     * @param isEdit
     * @param workId
     * @return
     */
    @GetMapping({"getCustomerLineInfo"})
    public ResponseResult getCustomerLineInfo(@RequestParam("lineId") Long lineId,
                                              @RequestParam("isEdit") Integer isEdit,
                                              @RequestParam("workId") Long workId) {
        if (lineId < 0 && workId < 0) {
            throw new BusinessException("请输入线路Id或工单Id");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (lineId > 0) {
            // 根据id 查询 客户信息
            CmCustomerLineOutDto customerLineInfo = iCmCustomerLineService.getCustomerLineInfo(lineId, workId, isEdit, accessToken);
            return ResponseResult.success(customerLineInfo == null ? "" : customerLineInfo);
        } else {
            // TODO: 2022/3/29 查询线路  百世工单线路信息转化成本平台线路  暂时不需要做
//            IWorkOrderTF workOrderTF = CallerProxy.getSVBean(IWorkOrderTF.class, "workOrderTF");
//            Map resultMap = workOrderTF.getLineDataByWorkId(workId);
//            return JsonHelper.json(resultMap == null ? "" : resultMap);
        }
//        return ResponseResult.success(domain);
        return null;
    }

    /**
     * 13500
     * 根据租户查询线路快速开单--微信接口
     */
    @GetMapping("doQueryLineByTenantForWX")
    public ResponseResult doQueryLineByTenantForWX(QueryLineByTenantForWXVo vo,
                                                   @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                                   @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        Page<QueryLineByTenantForWXDto> dto = iCmCustomerLineService.doQueryLineByTenantForWX(vo, pageNum, pageSize, accessToken);
        return ResponseResult.success(dto);
    }

    /**
     * 13501
     * 根据线路id查询-小程序接口
     *
     * @param lineId 线路主键
     * @return
     */
    @GetMapping("getCustomerLineInfoForWx")
    public ResponseResult getCustomerLineInfoForWx(Long lineId) {
        if (lineId <= 0) {
            throw new BusinessException("线路ID异常！");
        }
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        CmCustomerLineOutDto customerLineInfoForWx = iCmCustomerLineService.getCustomerLineInfoForWx(lineId, accessToken);
        if (customerLineInfoForWx != null && customerLineInfoForWx.getCustomerId() != null && customerLineInfoForWx.getCustomerId() > 0) {
            CmCustomerInfo cmCustomerInfo = cmCustomerInfoService.getInfoById(customerLineInfoForWx.getCustomerId(), accessToken);
            if (null != cmCustomerInfo) {
                customerLineInfoForWx.setCompanyName(cmCustomerInfo.getCompanyName());
            }
        }
        return ResponseResult.success(customerLineInfoForWx);
    }

}
