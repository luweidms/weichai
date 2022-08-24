package com.youming.youche.market.api.youka;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.commons.domain.ImportOrExportRecords;
import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.market.domain.youka.OilCardManagement;
import com.youming.youche.market.dto.youca.*;
import com.youming.youche.market.vo.youca.OilCardManagementVo;
import com.youming.youche.market.vo.youca.TenantVehicleRelVo;

import java.util.List;

public interface IOilCardManagementService  extends IBaseService<OilCardManagement> {


    /**
     *  获取油卡管理 列表
     * @param pageNum
     * @param pageSize
     * @param oilCardManagementVo
     * @return
     */
    IPage<OilCardManagementDto> getManagementList(Integer pageNum, Integer pageSize, OilCardManagementVo oilCardManagementVo,String accessToken);


    /**
     * TODO  弃用  直接走 档案模块的 车辆列表
     * 获取油卡可以绑定的车辆列表
     *
     * @return
     * @throws Exception
     */
    IPage<TenantVehicleRelDto> getVehicles(Integer pageNum, Integer pageSize, TenantVehicleRelVo tenantVehicleRelVo, String accessToken);

    /**
     * 新增 油卡管理记录
     * @param oilCardManagement
     * @param accessToken
     * @return
     */
    boolean add(OilCardManagement oilCardManagement, String accessToken);

    /**
     * 修改 油卡管理
     * @param oilCardManagement
     * @param accessToken
     * @return
     */
    boolean upd(OilCardManagement oilCardManagement, String accessToken);

    /**
     * 保持油卡充值
     * @param oilCarNum 油卡卡号
     * @param objToLongMul100 充值金额
     * @param accessToken
     * @return
     */
    boolean saveOilCharge(String oilCarNum, long objToLongMul100,String accessToken);

    /**
     * 更新余额
     * @return
     */
    void updOilCardBlace(String accessToken);

    /**
     * 根据租户id 查询 油卡列表
     * @param tenantId
     * @return
     */
    List<OilCardManagement> getOilCardManagementMapList(Long tenantId);

    /**
     * 根据tooken 查询 油卡信息
     * @param accessToken
     * @return
     */
    List<OilCardManagementDto> OilCardList( String accessToken);

    /**
     * 油卡导出
     * @return
     */
    void OilCardList1(String accessToken, ImportOrExportRecords record,OilCardManagementVo oilCardManagementVo);


    /***
     * 导入管理/油卡导入
     * @return
     * @throws Exception
     */
    void batchImport(byte[] bytes, ImportOrExportRecords record, String accessToken);

    /**
     * 修改 油卡列表
     * @param rechargeConsumeRecordList
     * @param tenantId
     */
    void updBlace(List<VoucherInfoDto> rechargeConsumeRecordList, Long tenantId);

    /**
     * 保持或修改 油卡列表
     * @param oilCardSaveIn
     * @param accessToken
     * @return
     */
    OilCardManagement  saveIn(OilCardSaveIn oilCardSaveIn, String accessToken);

    /**
     * 根据 油卡 卡号 和租户id 查询油卡列表
     * @param oilCarNum
     * @param tenantId
     * @return
     */
    List getOilCardManagementByCard(String oilCarNum, long tenantId);

    /**
     *  修改 油卡 与车辆关系表
     * @param vehicleNumber 车牌号
     * @param id  主键
     * @param oilCarNum 油卡卡号
     * @param tenantId 租户id
     */
    void deleteOilCardVehicleRelByCardId(String vehicleNumber,Long id,String oilCarNum,Long tenantId);

    /**
     *  修改 油卡状态
     * @param serviceUserId 服务商id
     * @param user 当前用户信息
     */
    void loseOilCard(Long serviceUserId, LoginInfo user);

    /**
     * 新增 或修改油卡信息
     * @param oilCardManagement
     * @param isUpdate
     */
    void saveOrUpdates(OilCardManagement oilCardManagement, boolean isUpdate);


    /**
     * 查询服务商信息
     * @param cardType 油卡类型
     * @param accessToken
     * @return
     */
    List<com.youming.youche.market.domain.youka.ServiceInfo> getServiceInfo(Integer cardType, String accessToken);

    /**
     * 40035
     * 司机小程序
     * niejiewei
     * 根据车牌查找有效的油卡集合
     * @param plateNumber
     * @return
     */
    List<OilCardManagementOutDto> getOilCardsByPlateNumber(String plateNumber, String accessToken);

    /**
     * 40036
     * niejeiwei
     * 获取油卡信息
     * 司机小程序
     * @param oilCardNum
     * @param accessToken
     * @return
     */
    List<OilCardManagement> getCardInfoByCardNum(String oilCardNum, String accessToken);
}
