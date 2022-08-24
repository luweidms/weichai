package com.youming.youche.market.business.controller.youka;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.conts.SysStaticDataEnum;
import com.youming.youche.market.api.youka.IOilCardLogService;
import com.youming.youche.market.api.youka.IOilCardManagementService;
import com.youming.youche.market.commons.CommonUtil;
import com.youming.youche.market.commons.OrderUtil;
import com.youming.youche.market.domain.youka.OilCardManagement;
import com.youming.youche.market.domain.youka.ServiceInfo;
import com.youming.youche.market.dto.youca.*;
import com.youming.youche.market.vo.youca.*;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExcelParse;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("youka/card")
public class OilCardManagementsController extends BaseController<OilCardManagement, IOilCardManagementService> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OilCardManagementsController.class);

    @DubboReference(version = "1.0.0")
    private IOilCardManagementService iOilCardManagementService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;
    @DubboReference(version = "1.0.0")
    IOilCardLogService iOilCardLogService;

    @Override
    public IOilCardManagementService getService() {
        return iOilCardManagementService;
    }

    /**
     *  获取油卡管理 列表
     * @param pageNum
     * @param pageSize
     * @param oilCardManagementVo
     * @return
     */
    @GetMapping("getManagementList")
    public ResponseResult getManagementList(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
                                            OilCardManagementVo oilCardManagementVo) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            IPage<OilCardManagementDto> page = iOilCardManagementService.getManagementList(pageNum, pageSize, oilCardManagementVo, accessToken);
            return ResponseResult.success(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * TODO  弃用  直接走 档案模块的 车辆列表
     * 获取油卡可以绑定的车辆列表
     *
     * @return
     * @throws Exception
     */
    @GetMapping("getVehicles")
    public ResponseResult getVehicles(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
                                      TenantVehicleRelVo tenantVehicleRelVo) {

        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            IPage<TenantVehicleRelDto> page = iOilCardManagementService.getVehicles(pageNum, pageSize, tenantVehicleRelVo, accessToken);
            return ResponseResult.success(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("查询异常");
        }
    }


    /**
     * 查询油卡日志信息
     */
    @GetMapping("doQuery")
    public ResponseResult doQuery(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
                                  OilLogVo oilLogVo) {

        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        IPage<OilCardLogDto> page = iOilCardLogService.doQuery(pageNum, pageSize, oilLogVo, accessToken);
        return ResponseResult.success(page);
    }

    /**
     * 新增油卡信息
     */
    @PostMapping("save")
    public ResponseResult save(@RequestBody OilCardManagementVos oilCardManagementVos){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));

        if (StringUtils.isBlank(oilCardManagementVos.getOilCarNum())) {
            throw new BusinessException("卡号不能为空，请输入！");
        }

        if (oilCardManagementVos.getCardType() < 0) {
            throw new BusinessException("请选择来源类型！");
        }

        if (oilCardManagementVos.getOilCardType() == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE && (oilCardManagementVos.getUserId() < 0)) {
            throw new BusinessException("服务商不能为空，请选择！");
        }

        if (oilCardManagementVos.getOilCardStatus() < 0) {
            throw new BusinessException("状态不能为空，请选择！");
        }

        if (oilCardManagementVos.getCardBalance() == null || oilCardManagementVos.getCardBalance() < 0) {
            throw new BusinessException("请输入理论余额！");
        }

        oilCardManagementVos.setCreateDate(LocalDateTime.now());
        OilCardSaveIn oilCardSaveIn = new OilCardSaveIn();
        BeanUtil.copyProperties(oilCardManagementVos, oilCardSaveIn);
        oilCardSaveIn.setUserId(oilCardManagementVos.getUserId());
        iOilCardManagementService.saveIn(oilCardSaveIn, accessToken);
        return ResponseResult.success("添加成功");
    }

    /**
     * 保存/修改油卡信息
     */
    @PostMapping("updates")
    public ResponseResult updates(@RequestBody OilCardManagementVos oilCardManagementVos) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (StringUtils.isBlank(oilCardManagementVos.getOilCarNum())) {
            throw new BusinessException("卡号不能为空，请输入！");
        }

//        if (oilCardManagementVos.getOilCardType() < 0){
//            throw new BusinessException("请选择来源类型！");
//        }
//
//        if (oilCardManagementVos.getOilCardType() == SysStaticDataEnum.OIL_CARD_TYPE.SERVICE && (oilCardManagementVos.getUserId() < 0)){
//            throw new BusinessException("服务商不能为空，请选择！");
//        }

        if (oilCardManagementVos.getOilCardStatus() < 0) {
            throw new BusinessException("状态不能为空，请选择！");
        }

        if (oilCardManagementVos.getCardBalance() == null || oilCardManagementVos.getCardBalance() < 0) {
            throw new BusinessException("请输入理论余额！");
        }
        oilCardManagementVos.setCreateDate(LocalDateTime.now());
        OilCardSaveIn oilCardSaveIn = new OilCardSaveIn();
        BeanUtil.copyProperties(oilCardManagementVos, oilCardSaveIn);
        oilCardSaveIn.setUserId(oilCardManagementVos.getUserId());
        oilCardSaveIn.setCardId(oilCardManagementVos.getId());
        oilCardSaveIn.setId(oilCardManagementVos.getId());
        oilCardSaveIn.setVehicleNumberStr(oilCardManagementVos.getVehicleNumberStr());// 车牌号
        oilCardSaveIn.setCardBalance(oilCardManagementVos.getCardBalance());//
        iOilCardManagementService.saveIn(oilCardSaveIn, accessToken);
        return ResponseResult.success("编辑成功");
    }

    /**
     * 主键查询油卡信息
     */
    @GetMapping("get/{id}")
    public ResponseResult get(@PathVariable Long id) {
        try {
            OilCardManagement domain = iOilCardManagementService.getById(id);
            return ResponseResult.success(domain);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("查询异常");
        }

    }

    /**
     * 服務商信息
     *
     * @param cardType 油卡类型
     */
    @GetMapping("getServiceInfo/{cardType}")
    public ResponseResult ServiceInfo(@PathVariable Integer cardType) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<ServiceInfo> domain = iOilCardManagementService.getServiceInfo(cardType, accessToken);
        return ResponseResult.success(domain);
    }

    /**
     * 保存充值
     *
     * @return
     * @throws Exception
     */
    @PostMapping("saveOilCharge")
    public ResponseResult saveOilCharge(@RequestBody CardManagementVo cardManagementVo) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        if (StringUtils.isBlank(cardManagementVo.getOilCarNum())) {
            throw new BusinessException("请选择充值油卡！");
        }
        if (StringUtils.isBlank(cardManagementVo.getCardBalance()) || !CommonUtil.isNumber(cardManagementVo.getCardBalance())) {
            throw new BusinessException("充值金额不能为空或格式不正确！");
        }
        double balance = Double.parseDouble(cardManagementVo.getCardBalance());
        if (balance <= 0) {
            throw new BusinessException("充值金额不能小于等于0！");
        }

        boolean created = iOilCardManagementService.saveOilCharge(cardManagementVo.getOilCarNum(), OrderUtil.objToLongMul100(cardManagementVo.getCardBalance()), accessToken);
        return created ? ResponseResult.success("添加成功") : ResponseResult.failure("此类卡暂不支持充值操作");
    }

    /**
     * 更新余额
     *
     * @return
     */
    @GetMapping("updOilCardBlace")
    public ResponseResult updOilCardBlace() {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        try {
            iOilCardManagementService.updOilCardBlace(accessToken);
            return ResponseResult.success("更新余额成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * 根据tooken 查询 油卡信息
     * @param accessToken
     * @return
     */
    @GetMapping("OilCardList")
    public ResponseResult OilCardList(OilCardManagementVo oilCardManagementVo) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            List<OilCardManagementDto> page = iOilCardManagementService.OilCardList(accessToken);
            return ResponseResult.success(page);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.failure("查询异常");
        }
    }

    /**
     * 油卡导出
     *
     * @return
     */
    @GetMapping("export")
    public ResponseResult export(OilCardManagementVo oilCardManagementVo) {
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            //TODO 根据token获取用户信息
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("油卡导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iOilCardManagementService.OilCardList1(accessToken, record, oilCardManagementVo);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导出油卡列表异常" + e);
            return ResponseResult.failure("导出油卡列表异常");
        }
    }


    /***
     * 导入管理/油卡导入
     * @return
     * @throws Exception
     */
    @PostMapping("import")
    public ResponseResult driverImport(@RequestParam("file") MultipartFile file) {
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        try {
            ExcelParse parse = new ExcelParse();
            parse.loadExcel(file.getOriginalFilename(), file.getInputStream());
            // 获取真实行数
            int sheetNo = 1;
            int rows = parse.getRealRowCount(sheetNo);
            if (rows > 300) {
                throw new BusinessException("最多一次性导入300条数据");
            }
            ImportOrExportRecords record = new ImportOrExportRecords();
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String mediaExcelPath = client.upload(file.getInputStream(), "油卡列表.xlsx", file.getSize());
            record.setMediaUrl(mediaExcelPath);
            record.setName("油卡管理导入");
            record.setMediaName(file.getOriginalFilename());
            record.setBussinessType(1);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record, accessToken);
            iOilCardManagementService.batchImport(file.getBytes(), record, accessToken);
            return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        } catch (Exception e) {
            LOGGER.error("导入油卡列表异常" + e);
            return ResponseResult.failure("文件导入失败，请重试！");
        }
    }


    /**
     * 40035
     * 司机小程序
     * niejiewei
     * 根据车牌查找有效的油卡集合
     * @param plateNumber 车牌号码
     * @return
     */
    @GetMapping("/getDriverOilCardByPlateNumber")
    public  ResponseResult  getDriverOilCardByPlateNumber(@RequestParam(value = "plateNumber") String plateNumber){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<OilCardManagementOutDto> oilCardsByPlateNumber = iOilCardManagementService.getOilCardsByPlateNumber(plateNumber, accessToken);
        return  ResponseResult.success(oilCardsByPlateNumber);
    }

    /**
     * 40036
     * niejeiwei
     * 获取油卡信息
     * 司机小程序
     * @param oilCardNum 实体油卡卡号
     * @return
     */
    @GetMapping("/getCardInfoByCardNum")
    public  ResponseResult  getCardInfoByCardNum(@RequestParam(value = "oilCardNum") String oilCardNum){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        List<OilCardManagement> cardInfoByCardNum = iOilCardManagementService.getCardInfoByCardNum(oilCardNum, accessToken);
        return  ResponseResult.success(cardInfoByCardNum);
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
}
