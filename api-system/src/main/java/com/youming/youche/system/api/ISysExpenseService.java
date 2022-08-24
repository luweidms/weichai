package com.youming.youche.system.api;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.system.domain.SysExpense;

import java.util.List;

/**
 * <p>
 * 费用类型 服务类
 * </p>
 *
 * @author Terry
 * @since 2022-01-20
 */
public interface ISysExpenseService extends IBaseService<SysExpense> {

    /**
     * 方法实现说明 根据登录者信息查询车队的费用类型
     * @author      terry
     * @param accessToken 令牌
     * @return      java.util.List<com.youming.youche.system.domain.SysExpense>
     * @exception
     * @date        2022/5/31 11:39
     */
    List<SysExpense> selectAll(String accessToken);

    /**
     * 方法实现说明 修改上传附件是否必填
     * @author      terry
     * @param id
     * @param accessToken
     * @return      boolean
     * @exception
     * @date        2022/2/17 20:38
     */
    boolean update(Long id, String accessToken);

    /**
     * 方法实现说明 新建费用上报类型
     * @author      terry
     * @param domain {@link SysExpense}
     * @param accessToken 令牌
     * @return      boolean
     * @exception
     * @date        2022/5/31 11:40
     */
    boolean create(SysExpense domain, String accessToken);


    /**
     * 方法实现说明 批量新增 费用上报类型
     * @author      terry
     * @param domains {@link List<SysExpense>}
     * @param accessToken 令牌
     * @return      boolean
     * @exception
     * @date        2022/5/31 11:40
     */
    boolean createList(List<SysExpense> domains, String accessToken);
}
