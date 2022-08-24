package com.youming.youche.system.business.controller;

import com.youming.youche.commons.base.BaseController;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.response.ResponseCode;
import com.youming.youche.commons.response.ResponseResult;
import com.youming.youche.commons.web.Header;
import com.youming.youche.system.api.ISysStaticDataService;
import com.youming.youche.system.dto.SysStsticDataDto;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 静态数据表 前端控制器
 * </p>
 *
 * @author Terry
 * @since 2022-01-11
 */
@RestController
@RequestMapping("/sys/static/data")
public class SysStaticDataController extends BaseController<SysStaticData, ISysStaticDataService> {

	@DubboReference(version = "1.0.0")
	ISysStaticDataService sysStaticDataService;

	@Override
	public ISysStaticDataService getService() {
		return sysStaticDataService;
	}

	/**
	 * 方法实现说明 根据类型查询静态数据
	 * @author      terry
	 * @param codeType 数据类型
	 * @return      com.youming.youche.commons.response.ResponseResult
	 * @exception
	 * @date        2022/5/31 14:17
	 */
	@GetMapping({ "gets/{codeType}" })
	public ResponseResult get(@PathVariable String codeType) {
		if (null == codeType) {
			throw new BusinessException(ResponseCode.PARAM_IS_INVALID);
		}
		String[] codeTypes = codeType.split(",");
		if (codeTypes.length==1){
			return ResponseResult.success(sysStaticDataService.get(codeType));
		}
		return ResponseResult.success(sysStaticDataService.get(codeTypes));
	}
	/**
	 * 方法实现说明 根据类型查询静态数据
	 * @author      terry
	 * @param codeType 类型数组
	 * @return      com.youming.youche.commons.response.ResponseResult
	 * @exception
	 * @date        2022/5/31 14:17
	 */
	@GetMapping("getSysStaticDataInfo")
	public List<SysStsticDataDto> getSysStaticDataInfo(String[] codeType,Long tenantId){
		if(codeType.length == 0){
			throw new BusinessException("请输入查询数据值不能为空！");
		}
		List<SysStsticDataDto> sysStsticDataDto = sysStaticDataService.getSysStaticDataInfo(Arrays.stream(codeType).collect(Collectors.toList()),tenantId);
		return sysStsticDataDto;
	}

}
