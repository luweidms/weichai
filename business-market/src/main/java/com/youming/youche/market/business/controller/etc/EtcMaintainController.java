package com.youming.youche.market.business.controller.etc;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.constant.SysOperLogConst;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.util.LoginUtils;
import com.youming.youche.commons.web.Header;
import com.youming.youche.components.fdfs.FastDFSHelper;
import com.youming.youche.market.api.etc.IEtcMaintainService;
import com.youming.youche.market.domain.etc.EtcMaintain;
import com.youming.youche.market.dto.etc.EtcBindVehicleDto;
import com.youming.youche.market.dto.etc.EtcMaintainDto;
import com.youming.youche.market.vo.etc.EtcMaintainQueryVo;
import com.youming.youche.system.api.ISysOperLogService;
import com.youming.youche.system.api.ioer.ImportOrExportRecordsService;
import com.youming.youche.system.utils.excel.ExcelParse;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;


/**
 * <p>
 * ETC表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
@RestController
@RequestMapping("etc/maintain")
public class EtcMaintainController extends BaseController<EtcMaintain, IEtcMaintainService> {

	private  static  final Logger LOGGER = LoggerFactory.getLogger(EtcMaintainController.class);

	@DubboReference(version = "1.0.0")
	IEtcMaintainService service;

	@DubboReference(version = "1.0.0")
	ImportOrExportRecordsService importOrExportRecordsService;

	@Resource
	LoginUtils loginUtils;

	@DubboReference(version = "1.0.0")
	ISysOperLogService sysOperLogService;

	@Override
	public IEtcMaintainService getService() {
		return service;
	}

	/**
	 * @description 分页获取ETC卡列表
	 * @author zag
	 * @date 2022/1/23 14:47
	 * @param pageNum
	 * @param pageSize
	 * @param etcMaintainQueryVo
	 * @return com.youming.youche.commons.response.ResponseResult
	 */
	@GetMapping({ "getAll" })
	public ResponseResult getAll(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
								 @RequestParam(value = "pageSize", defaultValue = "20", required = false) Integer pageSize,
								 EtcMaintainQueryVo etcMaintainQueryVo){
		try {
			String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
			LoginInfo loginInfo= loginUtils.get(accessToken);
			etcMaintainQueryVo.setTenantId(loginInfo.getTenantId());
			IPage<EtcMaintainDto> page= service.getAll(pageNum,pageSize,etcMaintainQueryVo);
			return ResponseResult.success(page);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseResult.failure("查询异常");
		}
	}

	/**
	 * @description 模糊查夜车辆列表
	 * @author zag
	 * @date 2022/1/23 14:48
	 * @param pageNum
	 * @param pageSize
	 * @param plateNumber 车牌号
	 * @return com.youming.youche.commons.response.ResponseResult
	 */
	@GetMapping("getVehicleList")
	public ResponseResult getVehicleByPlateNumber(@RequestParam(value = "pageNum", defaultValue = "1", required = false) Integer pageNum,
												  @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
												  @RequestParam (value = "plateNumber")String plateNumber){
		try {
			if(StringUtils.isBlank(plateNumber)){
				return ResponseResult.failure("查询车牌号不能为空！");
			}
			String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
			LoginInfo loginInfo= service.getLoginUser(accessToken);
			IPage<EtcBindVehicleDto> page= service.getVehicleByPlateNumber(pageNum,pageSize,loginInfo.getTenantId(),plateNumber);
			return ResponseResult.success(page);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseResult.failure("查询异常");
		}
	}

	/**
	 * @description 检查ETC卡绑定车辆
	 * @author zag
	 * @date 2022/1/23 17:15
	 * @param bindVehicle 车牌号
	 * @return com.youming.youche.commons.response.ResponseResult
	 */
	@GetMapping("checkEtcBindVehicle")
	public ResponseResult checkEtcBindVehicle(@RequestParam(value = "bindVehicle") String bindVehicle){
		try {
			if(StringUtils.isBlank(bindVehicle)){
				return ResponseResult.failure("绑定车牌号不能为空！");
			}
			String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
			LoginInfo loginInfo= loginUtils.get(accessToken);
			if(service.checkEtcBindVehicle(loginInfo.getTenantId(), bindVehicle)){
				return ResponseResult.failure("该车辆已经被绑定，不能重复绑定！");
			}
			return ResponseResult.success();
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseResult.failure("查询异常");
		}
	}

	/**
	 * 创建etc卡
	 * 聂杰伟
	 * @param domain
	 * @return
	 */
	@PostMapping({ "create" })
	public ResponseResult create( @RequestBody EtcMaintain domain) {
		try {
			if (domain == null
					|| StringUtils.isBlank(domain.getEtcId())
						|| StringUtils.isBlank(domain.getServiceName())
					|| domain.getUserId() == null
					|| domain.getState() == null) {
				return ResponseResult.failure("输入不能为空！");
			}
			String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
			LoginInfo loginInfo= loginUtils.get(accessToken);
			EtcMaintain etcmaintain = service.etcmaintain(domain.getEtcId()); // 查询是否存在
			if (etcmaintain!=null){
				throw  new BusinessException("etc创建失败,已有ETC卡号请重新输入");
			}
			domain.setTenantId(loginInfo.getTenantId());
			boolean created = this.service.save(domain);
			if (created){
				EtcMaintain etcmaintain1 = service.etcmaintain(domain.getEtcId());
				sysOperLogService.saveSysOperLog(loginInfo,com.youming.youche.commons.constant.SysOperLogConst.BusiCode.EtcCard,
						etcmaintain1.getId(),com.youming.youche.commons.constant.SysOperLogConst.OperType.Add,"新增",loginInfo.getTenantId());
			}
			return created ? ResponseResult.success("创建成功") : ResponseResult.failure("etc创建失败,已有ETC卡号请重新输入");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseResult.failure("etc创建失败,已有ETC卡号请重新输入");
		}
	}

	/**
	 * 删除etc 卡管理
	 * @param Id etc表主键id
	 * @return
	 */
	@DeleteMapping({ "remove/{Id}" })
	public ResponseResult remove(@PathVariable Long Id) {
			boolean deleted = this.service.removeById(Id);
			return deleted ? ResponseResult.success("删除成功") : ResponseResult.failure("删除异常");
	}



	/**
	 * 修改etc 卡管理
	 * @return
	 */
	@PostMapping("updateEtc")
	public ResponseResult update_etc(@RequestBody EtcMaintain domain) {
			if (domain == null
//					|| domain.getId()==null
					|| StringUtils.isBlank(domain.getEtcId())
					|| StringUtils.isBlank(domain.getServiceName())
//					|| domain.getUserId() == null
					|| domain.getState() == null) {
				return ResponseResult.failure("输入不能为空！");
			}
			String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
			LoginInfo loginInfo= loginUtils.get(accessToken);
			domain.setTenantId(loginInfo.getTenantId());
			boolean updated = this.service.saveOrUpdate(domain);
			sysOperLogService.saveSysOperLog(loginInfo,com.youming.youche.commons.constant.SysOperLogConst.BusiCode.EtcCard,domain.getId(), SysOperLogConst.OperType.Update,"修改",loginInfo.getTenantId());
			return updated ? ResponseResult.success("修改成功") : ResponseResult.failure("修改失败");
	}

	/**
	 *  查看单挑数据
	 * @param Id etc表主键id
	 * @return
	 */
	@GetMapping({ "get/{Id}" })
	public ResponseResult get(@PathVariable Long Id) {
		try {
			EtcMaintain domain = this.service.getById(Id);
			return ResponseResult.success(domain);
		}
		catch (Exception e) {
			e.printStackTrace();
			return ResponseResult.failure("查询异常");
		}
	}

	/***
	 * etc卡信息导出
	 * 聂杰伟
	 * 2022-3-11 11：00
	 * @return
	 * @throws Exception
	 */
	@GetMapping({"/exportData"})
	public ResponseResult etcExport(EtcMaintainQueryVo etcMaintainQueryVo) {
		try {
			String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
			Long id = (long)13;
			etcMaintainQueryVo.setTenantId(id);
			ImportOrExportRecords record = new ImportOrExportRecords();
			record.setName("ETC卡管理信息导出");
			record.setBussinessType(2);
			record.setState(1);
			record = importOrExportRecordsService.saveRecords(record, accessToken);
			service.etcOutList(etcMaintainQueryVo, accessToken, record);
			return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
		} catch (Exception e) {
			LOGGER.error("etc信息导出异常" + e);
			return ResponseResult.failure("etc信息导出异常");
		}
	}


	/***
	 * etc卡信息导入
	 * 聂杰伟
	 * 2022-3-11 15:33
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/importData")
	public ResponseResult etcImport(@RequestParam("file") MultipartFile file) {
		try {
			String accessToken = Header.getAuthorization(request.getHeader("Authorization"));
			LoginInfo loginInfo= loginUtils.get(accessToken);
			Long tenantId = loginInfo.getTenantId();
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
			record.setName("ETC卡管理导入");
			record.setMediaName(file.getOriginalFilename());
			record.setBussinessType(1);
			record.setState(1);
			record = importOrExportRecordsService.saveRecords(record, accessToken);
			service.etcImport(file.getBytes(), record,tenantId,accessToken);
			return ResponseResult.success("正在导出文件,您可以在“系统设置-导入导出结果查询”查看导出结果");
		} catch (Exception e) {
			LOGGER.error("(ETC卡)档案导入异常" + e);
			return ResponseResult.failure("导入异常");
		}
	}




}
