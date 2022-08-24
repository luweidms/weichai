package com.youming.youche.order.api.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.ClaimExpenseInfo;
import com.youming.youche.order.dto.ClaimExpenseCountDto;
import com.youming.youche.order.dto.ClaimExpenseInfoDto;
import com.youming.youche.order.vo.ClaimExpenseInfoInVo;

import java.util.List;

/**
* <p>
    * 车管报销表 服务类
    * </p>
* @author liangyan
* @since 2022-03-22
*/
    public interface IClaimExpenseInfoService extends IBaseService<ClaimExpenseInfo> {

    /**
     * 通过订单ID查询有效的报销列表
     * @param orderId
     * @return
     * @throws Exception
     */
    List<ClaimExpenseInfo> getValidExpenseInfoByOrder(long orderId);



    /**
     *
     * @Function: com.business.pt.ac.intf.IClaimExpenseInfoTF.java::countWaitAduitExpense
     * @Description: 该函数的功能描述:根据订单查询待审核和审核中报销数量
     * @return
     * @throws
     * @version: v1.1.0
     * @author:liujl
     *-------------------------------------------------------------
     *
     */
    Long countWaitAduitExpense(Long orderId);


    /**
     * 根据订单查询报销的数量和金额
     * @param orderId
     * @param userId
     */
    ClaimExpenseCountDto queryClaimExpenseCount(Long orderId, Long userId);
    /**
     * 借支报销-报销列表查询
     * 接口编码：21009
     */
    Page<ClaimExpenseInfoDto> doQuery(ClaimExpenseInfoInVo infoIn, Integer pageSize, Integer pageNum, String accessToken);
}
