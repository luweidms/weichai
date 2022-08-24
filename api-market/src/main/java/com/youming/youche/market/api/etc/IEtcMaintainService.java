package com.youming.youche.market.api.etc;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.market.domain.etc.EtcMaintain;
import com.youming.youche.market.dto.etc.EtcBindVehicleDto;
import com.youming.youche.market.dto.etc.EtcMaintainDto;
import com.youming.youche.market.vo.etc.EtcMaintainQueryVo;


/**
 * <p>
 * ETC表 服务类
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface IEtcMaintainService extends IBaseService<EtcMaintain> {
    /**
     * 获取当前用户信息
     * @param accessToken
     * @return
     */
    LoginInfo getLoginUser(String accessToken);

    /**
     * 分页查询 etc卡管理信息
     * @param pageNum
     * @param pageSize
     * @param etcMaintainQueryVo
     * @return
     */
    IPage<EtcMaintainDto> getAll(Integer pageNum, Integer pageSize, EtcMaintainQueryVo etcMaintainQueryVo);

    /**
     * @description 模糊查夜车辆列表
     * @author zag
     * @date 2022/1/23 14:48
     * @param pageNum
     * @param pageSize
     * @param plateNumber
     * @return com.youming.youche.commons.response.ResponseResult
     */
    IPage<EtcBindVehicleDto> getVehicleByPlateNumber(Integer pageNum, Integer pageSize, Long tenantId, String plateNumber);

    /**
     * @description 检查ETC卡绑定车辆
     * @author zag
     * @date 2022/1/23 17:15
     * @param bindVehicle
     * @return com.youming.youche.commons.response.ResponseResult
     */
    boolean checkEtcBindVehicle(Long tenantId, String bindVehicle);


    /**
     *
     * @param etcId ETC 卡号
     * @return
     */
    EtcMaintain  etcmaintain(String etcId);

    /**
     * 失效etc
     * @param serviceUserId
     */
    void loseEtc(Long serviceUserId,LoginInfo user);

    /**
     *条件导出所有etc卡信息
     *@author
     */
    void etcOutList(EtcMaintainQueryVo etcMaintainQueryVo, String accessToken, ImportOrExportRecords record);


    /**
     *导入所有etc卡信息
     */
    void etcImport(byte[] bytes, ImportOrExportRecords records,Long tenantId,String accessToken);



}
