package com.youming.youche.system.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.SysStaticData;
import com.youming.youche.system.dto.SysStsticDataDto;

import java.util.List;

/**
 * <p>
 * 静态数据表 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-11
 */
public interface ISysStaticDataService extends IBaseService<SysStaticData> {

	/**
	 * 方法实现说明  查询静态数据列表
	 *  通过用户id查询车队id
	 * @author      terry
	 * @param codeType
	 * @param userId
	 * @return      java.util.List<com.youming.youche.commons.domain.SysStaticData>
	 * @exception
	 * @date        2022/5/31 14:20
	 */
	List<SysStaticData> getSysStaticDataList(String codeType,Long userId);

	/**
	 * 方法实现说明 查询静态数据
	 * @author terry
	 * @param codeType 类型
	 * @param codeValue 变量值
	 * @return com.youming.youche.commons.domain.SysStaticData 静态数据
	 * @exception
	 * @date 2022/5/31 14:28
	 */
	SysStaticData getSysStaticData(String codeType, String codeValue);

	/**
	 * 方法实现说明  查询车辆长度的静态数据列表
	 * @author      terry
	 * @param vehicleLength
	 * @return      java.util.List<com.youming.youche.commons.domain.SysStaticData>
	 * @exception
	 * @date        2022/5/31 14:29
	 */
	List<SysStaticData> getSysStaticDataListByCodeName(String vehicleLength);

	/**
	 * 方法实现说明 查询全部静态数据
	 * @author      terry
	 * @param
	 * @return      java.util.List<com.youming.youche.commons.domain.SysStaticData>
	 * @exception
	 * @date        2022/5/31 14:30
	 */
	List<SysStaticData> selectAll();

	/**
	 * 方法实现说明 根据状态查询全部静态数据
	 * @author      terry
	 * @param state 状态：1有效，0无效
	 * @return      java.util.List<com.youming.youche.commons.domain.SysStaticData>
	 * @exception
	 * @date        2022/5/31 14:30
	 */
	List<SysStaticData> selectAllByState(Integer state);

	/**
	 * 方法实现说明 根据状态查询全部静态数据
	 * @author      terry
	 * @param codeType 静态枚举类型
	 * @return      java.util.List<com.youming.youche.commons.domain.SysStaticData>
	 * @exception
	 * @date        2022/5/31 14:30
	 */
	List<SysStaticData> get(String codeType);

	/**
	 * 方法实现说明 根据类型查询全部静态数据
	 * @author      terry
	 * @param codeTypes 静态枚举类型数据
	 * @return      java.util.List<java.util.List<com.youming.youche.commons.domain.SysStaticData>>
	 * @exception
	 * @date        2022/5/31 14:30
	 */
    List<List<SysStaticData>> get(String[] codeTypes);

	/**
	 * 方法实现说明 查询静态数据
	 * @author terry
	 * @param codeType 类型
	 * @param codeValue 变量值
	 * @return java.lang.String 静态数据getCodeName
	 * @exception
	 * @date 2022/5/31 14:28
	 */
	String getSysStaticDatas(String codeType, Integer codeValue);

	/**
	 * 方法实现说明 查询静态数据
	 * @author terry
	 * @param codeType 类型
	 * @param codeValue 变量值
	 * @return java.lang.String 静态数据getCodeName
	 * @exception
	 * @date 2022/5/31 14:28
	 */
	String getSysStaticDataId(String codeType, String codeValue);
	/***
	 *
	 * 接口编码:11005
	 * 接口入参：
	 *       codeType 静态数据名称 （可以传单个、可以传多个(传值是数组数据)）
	 *
	 * 接口出参：
	 *       具体出参格式看报文
	 *       codeName  数据名称
	 *       codeValue 数据值
	 *       sortId    排序
	 *       codeDesc  数据描述
	 * APP获取静态参数数据（批量获取、单个获取）
	 *
	 */
    List<SysStsticDataDto> getSysStaticDataInfo(List<String> codeType,Long tenantId);
}
