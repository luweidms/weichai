package com.youming.youche.market.business.controller.etc;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.web.Header;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.market.api.etc.ICmEtcInfoService;
import com.youming.youche.market.api.facilitator.IServiceInfoService;
import com.youming.youche.market.domain.etc.CmEtcInfo;
import com.youming.youche.market.dto.etc.CmEtcInfoDto;
import com.youming.youche.market.dto.etc.CmEtcInfoOutDto;
import com.youming.youche.market.dto.facilitator.ServiceInfoFleetDto;
import com.youming.youche.market.vo.etc.CmEtcInfoVo;
import com.youming.youche.market.vo.facilitator.ServiceInfoFleetVo;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExcelParse;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 *  ETC管理表
 *  聂杰伟
 * </p>
 * @author Terry
 * @since 2022-03-11
 */
@RestController
@RequestMapping("cm/etc/info")
public class CmEtcInfoController extends BaseController<CmEtcInfo, ICmEtcInfoService> {

    private  static  final Logger LOGGER = LoggerFactory.getLogger(CmEtcInfoController.class);

    @DubboReference(version = "1.0.0")
    ICmEtcInfoService iCmEtcInfoService;

    @DubboReference(version = "1.0.0")
    ImportOrExportRecordsService importOrExportRecordsService;

    @DubboReference(version = "1.0.0")
    IServiceInfoService iServiceInfoService;

    @Resource
    LoginUtils loginUtils;


    @Override
    public ICmEtcInfoService getService() {
        return iCmEtcInfoService;
    }

    /**
     * 接口说明 分页查询 ETC消费记录
     * 聂杰伟
     * @param pageNum 分页参数
     * @param pageSize 分页参数
     * @param cmEtcInfoVo etc消费记录传入的值
     * @return
     */
    @GetMapping("/getAll")
    public ResponseResult getAll(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                 @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
                                  CmEtcInfoVo cmEtcInfoVo){
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            Page<CmEtcInfo> all = iCmEtcInfoService.getAll( cmEtcInfoVo, accessToken,pageNum,pageSize);
            return  ResponseResult.success(all);

    }


    /**
     * ETC消费记录导出
     * 聂杰伟
     * @param cmEtcInfoVo etc消费记录传入的值
     * @return
     */
    @GetMapping("/exportData")
    public  ResponseResult etcExport( CmEtcInfoVo cmEtcInfoVo){
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            Long id = (long)13;
            cmEtcInfoVo.setTenantId(id);
            ImportOrExportRecords record = new ImportOrExportRecords();
            record.setName("ETC消费记录导出");
            record.setBussinessType(2);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record,accessToken);
            iCmEtcInfoService.etcOutList(cmEtcInfoVo,accessToken,record);
            return  ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
        }catch (Exception e ){
            LOGGER.error("ETC消费记录导入异常");
            return  ResponseResult.failure("ETC信息导出异常");
        }
    }


    /**
     * ETC消费记录导入
     * @param file
     * @return
     */
    @PostMapping("/importData")
    public ResponseResult importData (@RequestParam("file")MultipartFile file){
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            ExcelParse parse = new ExcelParse();
            parse.loadExcel(file.getOriginalFilename(), file.getInputStream());
            // 获取真实行数
            int sheetNo = 1;
            int rows = parse.getRowCount(sheetNo);
            //int rowss = parse.getRealRowCount(sheetNo);
            if (rows > 300) {
                throw new BusinessException("最多一次性导入300条数据");
            }
            ImportOrExportRecords record = new ImportOrExportRecords();
            //上传文件
            FastDFSHelper client = FastDFSHelper.getInstance();
            String mediaExcelPath = client.upload(file.getInputStream(), "ETC卡管理.xlsx", file.getSize());
            record.setMediaUrl(mediaExcelPath);
            record.setName("ETC消费记录导入");
            record.setMediaName(file.getOriginalFilename());
            record.setBussinessType(1);
            record.setState(1);
            record = importOrExportRecordsService.saveRecords(record,accessToken);
            iCmEtcInfoService.etcImport(file.getBytes(),record,accessToken);
            return ResponseResult.success();
        }catch (Exception e){
            LOGGER.error("ETC消费记录导入异常"+e);
            return  ResponseResult.failure("ETC消费记录导入异常");
        }
    }


    /**
     *ETC消费记录 反写订单  反写订单 就是老代码的 上报费用
     * 聂杰伟
     * @param etcIds
     * @return
     */
    @PostMapping("/updateEtcInfoByEtcId")
    private ResponseResult updateEtcInfoByEtcId(){
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            return ResponseResult.success( iCmEtcInfoService.batchEtcDeduction(accessToken));
    }

    /**
     * 手动选择匹配 单条
     * 聂杰伟
     * @param etcCardNo ETC号
     * @param consumeMoney 消费金额
     * @param etcConsumeTime 消费时间
     * @param tradingSite 交易地点
     * @param orderId  订单号
     *          id 主键
     * @return
     */
  @PostMapping("/updateEtcInfoBy")
  private ResponseResult updateEtcInfoBy( @RequestBody CmEtcInfoVo vo ){
      String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
      return  ResponseResult.success(iCmEtcInfoService.updateEtcInfoBy(vo.getId(),vo.getEtcCardNo(),
              vo.getConsumeMoney(),vo.getEtcConsumeTime(),vo.getTradingSite(),vo.getOrderId(),accessToken));
  }


    /**
     * 手动选择匹配 批量
     * 聂杰伟
     * @param ids
     * @param orderId  订单号
     * @return
     */
    @PostMapping("/updateEtcBys")
    private ResponseResult updateEtcBys( @RequestBody CmEtcInfoVo vo){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        return  ResponseResult.success(iCmEtcInfoService.updateEtcBys(vo.getIds(),vo.getOrderId(),accessToken));
    }



    /**
     *  覆盖上报费用
     *  聂杰伟
     * @param etcid
     * @param coverType 判断是否覆盖 0：不覆盖 1：覆盖
     * @return
     */
    @PostMapping("/coverEtc")
    private  ResponseResult coverEtc(@RequestBody CmEtcInfoVo vo){
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
           Integer coverTypes = Integer.valueOf(vo.getCoverType());
            iCmEtcInfoService.coverEtc(vo.getEtcid(), coverTypes, accessToken);
            return  ResponseResult.success("覆盖上报费用成功");
        }catch (Exception e ){
            LOGGER.error("覆盖上报费用失败"+e);
            return  ResponseResult.failure("覆盖上报费用失败");
        }
    }


    /**
     *  聂杰伟
     *  订单ETC
     * @param plate_number 车牌号
     * @param order_id 订单号
     * @param est_arrive_date 靠台开始
     * @param real_arrive_date  考台结束
     * @param cost_model  成本模式
     * @param vehicle_type 车辆类型
     * @return
     */
    @GetMapping("/ordeETC")
    public  ResponseResult order_etc(@RequestParam(value = "plate_number") String plate_number,
                                     @RequestParam(value = "order_id") String order_id,
                                     @RequestParam(value = "est_arrive_date") String est_arrive_date,
                                     @RequestParam(value = "real_arrive_date") String real_arrive_date,
                                     @RequestParam(value = "cost_model") Integer cost_model,
                                     @RequestParam(value = "vehicle_type") Integer vehicle_type,
                                     @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                     @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize){
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            Page<CmEtcInfoDto> orderDtoIPage = iCmEtcInfoService.OrderETC(plate_number, order_id, est_arrive_date,
                    real_arrive_date, cost_model, vehicle_type, accessToken, pageNum,pageSize);
            return ResponseResult.success(orderDtoIPage);
        } catch (Exception e) {
            LOGGER.error("查询订单ETC失败" + e);
            return ResponseResult.failure("查询订单ETC失败");
        }
    }


    /**
     * 查询服务商列表
     * @return
     */
    @GetMapping("/getserviceinfo")
    public  ResponseResult getService_info (){
        try {
            String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
            // 服务商
            ServiceInfoFleetDto serviceInfoFleetDto= new ServiceInfoFleetDto();
            serviceInfoFleetDto.setServiceName("");
            serviceInfoFleetDto.setLinkman("");
            serviceInfoFleetDto.setLoginAcct("");
            serviceInfoFleetDto.setServiceType(3);
            Page<ServiceInfoFleetVo> serviceInfoFleetVoPage = iServiceInfoService.queryServiceInfoPage(999, 1, serviceInfoFleetDto, accessToken);
            List<ServiceInfoFleetVo> records1 = serviceInfoFleetVoPage.getRecords();
            // ETC的服务商只要
            return ResponseResult.success(records1);
        }catch (Exception e){
            LOGGER.error("服务商查询失败"+e);
            return ResponseResult.failure("服务商查询失败");
        }
    }

    /**
     * 实际消费ETC  28312
     *
     * @param orderId 自有车回写的订单ID
     */
    @GetMapping("doQueryActualConsumptionEtcApp")
    public ResponseResult doQueryActualConsumptionEtcApp(Long orderId) {
        List<CmEtcInfoOutDto> cmEtcInfoOutDtos = iCmEtcInfoService.doQueryActualConsumptionEtcApp(orderId, null);
        return ResponseResult.success(cmEtcInfoOutDtos);
    }

    /**
     * 司机小程序
     * 服务-ETC消费记录（51001）
     * niejiewei
     * @param userId etc 卡号
     * @return
     */
    @GetMapping("/getETCList")
    public  ResponseResult getETCList(  @RequestParam("userId")Long userId,
                                       @RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
                                       @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize){
        String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
        IPage<CmEtcInfoOutDto> etcList = iCmEtcInfoService.getETCList(userId, accessToken, pageNum, pageSize);
        return ResponseResult.success(etcList);
    }
}
