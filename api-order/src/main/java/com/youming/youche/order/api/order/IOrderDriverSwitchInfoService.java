package com.youming.youche.order.api.order;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.order.domain.order.OrderDriverSwitchInfo;
import com.youming.youche.order.dto.OrderDriverSwitchInfoOutDto;
import com.youming.youche.order.dto.UserDataInfoDto;
import com.youming.youche.order.dto.order.OrderDriverSwitchInfoDto;
import com.youming.youche.order.dto.order.QueryOrderDriverSwitchDto;
import com.youming.youche.order.vo.ScanOrderDriverSwitchVo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author CaoYaJie
 * @since 2022-03-24
 */
public interface IOrderDriverSwitchInfoService extends IBaseService<OrderDriverSwitchInfo> {

    /**
     * @param orderId
     * @param state
     * @return
     * @throws Exception
     * @Function: com.business.pt.order.service.IOrderDriverSwitchSV.java::getSwitchInfosByOrder
     * @Description: 该函数的功能描述:根据订单号获取列表（去重）
     * @version: v1.1.0
     * @author:huangqb
     * @date:2018年10月11日 上午9:49:46
     * Modification History:
     * Date         Author          Version            Description
     * -------------------------------------------------------------
     * 2018年10月11日        huangqb           v1.1.0               修改原因
     */
    List<OrderDriverSwitchInfo> getSwitchInfosByOrder(Long orderId, Integer state);

    /***
     * @Description: 查询切换司机列表
     * @Author: luwei
     * @Date: 2022/3/29 1:13 下午
     * @Param orderId:
     * @Param formerUserName:
     * @Param receiveUserName:
     * @Param originUserName:
     * @Param state:
     * @Param pageNum:
     * @Param pageSize:
     * @return: com.baomidou.mybatisplus.core.metadata.IPage<com.youming.youche.order.dto.order.OrderDriverSwitchInfoDto>
     * @Version: 1.0
     **/
    IPage<OrderDriverSwitchInfoDto> getOrderDriverSwitchInfos(Long orderId, String formerUserName, String receiveUserName, String originUserName, int state, int pageNum, int pageSize);

    /***
     * @Description: 查询切换司机详情
     * @Author: luwei
     * @Date: 2022/3/29 1:14 下午
     * @Param switchId:
     * @return: com.youming.youche.order.dto.order.OrderDriverSwitchInfoDto
     * @Version: 1.0
     **/
    OrderDriverSwitchInfoDto queryDetails(Long switchId);

    /**
     * 根据手机号获取自有车司机信息
     * @param billId
     * @return
     * @throws Exception
     */
    UserDataInfoDto getDriverByPhone(String billId,String accessToken);

    /**
     *
     * @Function: com.business.pt.order.intf.IOrderDriverSwitchTF.java::doDriverSwitch
     * @Description: 该函数的功能描述:web端车管操作订单切换司机
     * @param orderId
     * @param userId
     * @param recevieMileage
     * @param mileageFileId
     * @param mileageFileUrl
     * @param receiveRemark
     * @throws Exception
     * @version: v1.1.0
     * @author:huangqb
     * @date:2018年10月11日 下午7:22:27
     * Modification History:
     *   Date         Author          Version            Description
     *-------------------------------------------------------------
     * 2018年10月11日        huangqb           v1.1.0               修改原因
     */
    public void doDriverSwitch(long orderId,long userId,String recevieMileage,String mileageFileId,String mileageFileUrl,String receiveRemark,String accessToken);

    /**
     *
     * @Function: com.business.pt.order.service.IOrderDriverSwitchSV.java::getLatestEffective
     * @Description: 该函数的功能描述:根据订单查询最新的一条有效切换记录（待确认、切换成功）
     * @param orderId
     * @return
     * @throws Exception
     * @version: v1.1.0
     * @author:huangqb
     * @date:2018年10月9日 下午2:12:06
     * Modification History:
     *   Date         Author          Version            Description
     *-------------------------------------------------------------
     * 2018年10月9日        huangqb           v1.1.0               修改原因
     */
    public OrderDriverSwitchInfo getLatestEffective(long orderId);

    /**
     * 根据订单获取切换的记录
     *
     * @param orderId
     * @param state
     * @return
     */
    List<OrderDriverSwitchInfo> getSwitchInfosByOrderId(long orderId,int state);

    /**
     * 查询司机切换列表  接口编号：30101
     * @param orderId
     * @param accessToken
     */
    QueryOrderDriverSwitchDto queryOrderDriverSwitch(Long orderId, String accessToken);

    /**
     * App接口-填写切换司机信息 30104
     * @param orderId
     * @param formerUserId
     * @param formerMileage
     * @param formerMileageFileId
     * @param formerMileageFileUrl
     * @param formerRemark
     * @param receiveUserId
     */
    void formerSwithcInfo(Long orderId, Long formerUserId, Long formerMileage, String formerMileageFileId, String formerMileageFileUrl, String formerRemark, Long receiveUserId, String accessToken);

    /**
     * App接口-查询司机切换详情(30103)
     * @param switchId
     * @return
     */
    OrderDriverSwitchInfoOutDto queryDataById(Long switchId);

    /**
     * 确认司机切换(30105)
     * @param receiveMileage
     * @param receiveMileageId
     * @param receiveMileageUrl
     * @param receiveRemark
     */
    boolean confirmOrderDriverSwitch(long switchId,long receiveMileage,String receiveMileageId,String receiveMileageUrl,String receiveRemark,String accessToken);

    /**
     * 拒绝司机切换(30106)
     * @param switchId
     * @param state
     * @return
     */
    boolean refuseOrderDriverSwitch(Long switchId,String state);

    /**
     * 扫码切换司机(30108)
     * @param scanOrderDriverSwitchVo
     * @return
     */
    boolean scanOrderDriverSwitch(ScanOrderDriverSwitchVo scanOrderDriverSwitchVo,String accessToken);


    /**
     * 切换司机-app扫码
     * @param info
     */
    void doDriverSwitch(OrderDriverSwitchInfo info,String accessToken);



}
