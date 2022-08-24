package com.youming.youche.finance.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.finance.domain.AdvanceExpireInfo;
import com.youming.youche.finance.dto.AdvanceExpireOutDto;
import com.youming.youche.order.vo.AdvanceExpireOutVo;


/**
* <p>
    * 手动到期沉淀表 服务类
    * </p>
* @author luona
* @since 2022-04-13
*/
public interface IAdvanceExpireInfoService extends IBaseService<AdvanceExpireInfo> {

    /**
     * 即将到期列表，包含查询手动到期
     * 订单尾款到期、油到期、维修到期
     * @param advanceExpireOutVo
     * @param pageNum
     * @param pageSize
     * @param accessToken
     * @return
     */
    Page<AdvanceExpireOutDto> queryUndueExpires(AdvanceExpireOutVo advanceExpireOutVo, Integer pageNum, Integer pageSize, String accessToken);


    /**
     * 提前到期-尾款到期业务类型
     * @param advanceExpireOutVo
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<AdvanceExpireOutDto> queryUndueExpiresOne(AdvanceExpireOutVo advanceExpireOutVo, Integer pageNum, Integer pageSize, String accessToken);

    /**
     * 提前到期-油到期业务类型
     * @param advanceExpireOutVo
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<AdvanceExpireOutDto> queryUndueExpiresTwo(AdvanceExpireOutVo advanceExpireOutVo, Integer pageNum, Integer pageSize, String accessToken);

    /**
     * 提前到期-维修到期业务类型
     * @param advanceExpireOutVo
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<AdvanceExpireOutDto> queryUndueExpiresThree(AdvanceExpireOutVo advanceExpireOutVo, Integer pageNum, Integer pageSize, String accessToken);

    /**
     * 根据业务流水号、到期类型查询手动到期数据
     * @param flowId 业务流水 订单的未到期显示订单号、消费类显示流水号、维修类显示消费号、加油类显示系统流水号
     * @param signType 到期类型 1司机尾款到期 2油到期 3维修到期
     * @return
     * @throws Exception
     */
    public AdvanceExpireInfo getAdvanceExpireInfoByFlowId(long flowId,String signType);

    /**
     * 手动到期操作
     * @param flowId
     * @param userId
     * @param userType
     * @param signType
     * @param accessToken
     * @return
     */
    boolean doExpire(Long flowId,Long userId,Integer userType,String signType,String accessToken);

}
