package com.youming.youche.market.commons;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
/**
 * 订单枚举
 * @author liyiye
 *
 */
public class OrderConsts {
    public static class OrderOpType{
        /**新增*/
        public static final int ADD=1;
        /**修改*/
        public static final int MODIFY=2;
        /**重新指派*/
        public static final int APPOINT=3;
        /**在线接单*/
        public static final int ACCEPT=4;
        /**支付预付款*/
        public static final int PRE_FEE = 5;
        /**轨迹变化*/
        public static final int TRAIL_CHANGE = 6;
        /**取消订单*/
        public static final int CANCEL_ORDER = 7;
        /**回单审核*/
        public static final int RECEIPT = 8;
        /**异常*/
        public static final int PROBLEM = 9;
        /**扫码加油*/
        public static final int SCAN_QR_CODE_OIL = 10;
        /**油费上报*/
        public static final int OIL_FEE_REPORT = 11;
        /**公里数上报*/
        public static final int MILEAGE_REPORT = 12;
        /**满油上报*/
        public static final int FILLED_WITH_OIL_REPORT = 13;
    }


    /**
     * 承包价格的勾选框
     * @author liyiye
     *
     */
    public static class IsContract{
        /**0 没有勾选**/
        public static final int CONTRACT_NO=0;
        /**1 已经勾选*/
        public static final int CONTRACT_YES = 1;
    }


    /**
     * 提现记录核销状态
     */
    public static class PayOutVerificationState{
        /**待核销*/
        public static final int INIT = 1;
        /**已核销*/
        public static final int VERIFICATION_STATE = 2;
        /**已撤销*/
        public static final int REVOCATION_STATE = 3;
        /**部分充值*/
        public static final int PORTION_VERIFICATION_STATE = 4;

        /**已核销*/
        public static final int VERIFICATION_STATE_5 = 5;
    }

    /**
     * 费用支付状态
     * @author zhouchao
     *
     */
    public static class AMOUNT_FLAG{
        /**待支付*/
        public static final int WILL_PAY = 0;
        /**已支付*/
        public static final int ALREADY_PAY = 1;
    }


    /**
     * 回单状态/合同状态
     * @author zhouchao
     *
     */
    public static class ReciveState{
        /**未上传*/
        public static final int NOT_UPLOAD = 1;
        /**未审核*/
        public static final int NOT_VERIFY = 2;
        /**通过*/
        public static final int PASS = 3;
        /**未通过*/
        public static final int NOT_PASS = 4;
    }


    /**
     * 订单是否外发：0没有外发，1已外发	 *
     */
    public static class IsTransit{
        /**0没有外发**/
        public static final int TRANSIT_NO=0;
        /**1已外发*/
        public static final int TRANSIT_YES = 1;
    }


    /**
     * 转单状态(0待接单 1 已接单 2 已拒接)
     * @author liyiye
     *
     */
    public static class TransferOrderState{
        /**待审核**/
//		public static final int TO_BE_AUDIT=-1;
        /**待接单**/
        public static final int TO_BE_RECIVE=0;
        /**已接单*/
        public static final int BILL_YES = 1;
        /**已拒单**/
        public static final int BILL_NOT=2;
        /**订单超时，撤单**/
        public static final int BILL_TIME_OUT=3;
    }

    /**
     * 指派方式
     * @author liyiye
     *
     */
    public static class AppointWay{
        /**共享订单*/
        public static final int APPOINT_SHARE = 0;
        /**竞价*/
        public static final int BIDD = 1;
        /**抢单*/
        public static final int GRAB = 2;
        /**指派车辆*/
        public static final int APPOINT_CAR = 3;
        /**指派员工*/
        public static final int APPOINT_LOCAL = 4;
        /**指派车队*/
        public static final int APPOINT_TENANT = 5;
    }

    public static class OrderType{
        /**固定线路*/
        public static final int FIXED_LINE = 1;
        /**临时线路*/
        public static final int TEMPORARY_LINE = 2;
        /**在线接单*/
        public static final int ONLINE_RECIVE=3;
    }


    public static class DriverType{
        /**主驾驶*/
        public static final int CAR_DRIVER = 1;
        /**副驾驶*/
        public static final int COPILOT = 2;
    }

    public static class TableType{
        /**原表*/
        public static final int  ORI= 1;
        /**历史表*/
        public static final int HIS = 2;
    }
    /**
     * 订单详情查询类型
     * @author EditTheLife
     */
    public static class orderDetailsType{
        /**查看*/
        public static final int  SELECT= 1;
        /**复制*/
        public static final int COPY = 2;
        /**重新指派*/
        public static final int UPDATE = 3;
        /**审核*/
        public static final int AUDIT = 4;
    }

    /**
     * 订单状态
     */
    public static class ORDER_STATE {
        /**待审核**/
        public static final int TO_BE_AUDIT=1;
        /**	审核不通过**/
        public static final int AUDIT_NOT=2;
        /**待接单**/
        public static final int TO_BE_RECIVE=3;
        /**已拒单**/
        public static final int BILL_NOT=4;
        /**待调度**/
        public static final int TO_BE_DISPATCH=5;
        /**调度中**/
        public static final int DISPATCH_ING= 6;
        /**待装货**/
        public static final int  TO_BE_LOAD= 7;
        /**装货中**/
        public static final int  LOADING= 8;
        /**运输中**/
        public static final int  TRANSPORT_ING=9;
        /**已到达**/
        public static final int  ARRIVED=10;
        /**已卸货**/
        public static final int  UNLOADED=11;
        /**回单待审核**/
        public static final int  RECIVE_TO_BE_AUDIT=12;
        /**回单审核不通过**/
        public static final int  RECIVE_AUDIT_NOT=13;
        /**已完成**/
        public static final int  FINISH=14;
        /***撤销*/
        public static final int  CANCELLED=15;
    }
    /**
     * 是否开票枚举
     * @author EditTheLife
     */
    public static class IS_NEED_BILL{
        /**不需发票**/
        public static final int NOT_NEED_BILL=0;
        /**承运商开票**/
        public static final int COMMON_CARRIER_BILL=1;
        /**平台票**/
        public static final int TERRACE_BILL=2;
    }
    /**
     * 订单异常类型枚举
     * @author EditTheLife
     */
    public static class PROBLEM_TYPE{
        /**发货方违约**/
        public static final String SHIPPER_BREAK_CONTRACT="1";
        /**承运方违约**/
        public static final String CARRIER_BREAK_CONTRACT="2";
        /**发货方毁单(销单)**/
        public static final String SHIPPER_DAMAGE_ORDER="3";
        /**承运方毁单(销单)**/
        public static final String CARRIER_DAMAGE_ORDER="4";
        /**货损货差**/
        public static final String FREIGHT_LOSS_DAMAGE="5";
        /**放空补偿**/
        public static final String IN_BALLAST_COMPENSATION="6";
        /**超重超载**/
        public static final String OVERWIGHT="7";
        /**增加经停点**/
        public static final String ADD_STOP_POINT="8";
        /**迟到罚款**/
        public static final String TARDINESS_PENALTY="9";
        /**放空补偿**/
        public static final String PLEDGE_VEHICLE_COMPENSATION="10";
    }
    /**
     * 异常增减枚举
     * @author EditTheLife
     */
    public  static class  PROBLEM_PAY_TYPE{
        /**异常收入(扣承运方钱)**/
        public static final int INCOME=1;
        /**异常支出(给承运方钱)**/
        public static final int EXPEND=2;
    }
    /**
     * 修改订单状态
     * @author EditTheLife
     */
    public static class UPDATE_STATE{
        /**全量修改**/
        public static final int UPDATE_ALL =1;
        /**部分修改**/
        public static final int UPDATE_PORTION =2;
        /**禁止修改**/
        public static final int UPDATE_FORBID =3;
        /**重新指派**/
        public static final int  REASSIGN = 4;
        /**特殊修改(开单司机绑定其他车)**/
        public static final int  UPDATE_SPECIAL = 5;
    }

    /**
     * 审核来源
     * @author EditTheLife
     */
    public static class AUDIT_SOURCE{
        /**修改订单**/
        public static final int UPDATE_ORDER =1;
        /**价格异常**/
        public static final int ABNORMAL_PRICE =2;
    }

    /**
     * 订单查询类型
     * @author EditTheLife
     */
    public static class SELECT_ORDER_TYPE{
        /**订单跟踪**/
        public static final int ORDER_TAIL_AFTER =1;
        /**回单审核**/
        public static final int RECEIPT_AUDIT =2;
        /**异常列表**/
        public static final int PROBLEMINFO_LIST =3;
        /**订单审核列表**/
        public static final int ORDER_AUDIT =4;
        /**时效审核列表**/
        public static final int AGING_AUDIT = 5;
    }

    /**
     * 是否为手动转为系统自动打款
     */
    public static class IS_TURN_AUTOMATIC{
        /**不是**/
        public static final int IS_TURN_AUTOMATIC_0 =0;
        /**是**/
        public static final int IS_TURN_AUTOMATIC_1 =1;
        /**停止自动中**/
        public static final int IS_TURN_AUTOMATIC_2 =2;
        /**未绑卡打款（绑卡打款成功后自动提现）**/
        public static final int IS_TURN_AUTOMATIC_3 =3;
    }
    /**
     * 系统参数名配置
     * @author EditTheLife
     */
    public static class SYS_CFG_NAME{
        /**订单趋势表格标题**/
        public static final String ORDER_TABLE_TITLE = "ORDER_TABLE_TITLE";
        /**订单趋势表格查询日数**/
        public static final String ORDER_TABLE_SELECT_DAY = "ORDER_TABLE_SELECT_DAY";
        /**订单趋势表格查询月数**/
        public static final String ORDER_TABLE_SELECT_MONTH = "ORDER_TABLE_SELECT_MONTH";
        /**订单趋势表格查询周数**/
        public static final String ORDER_TABLE_SELECT_WEEK = "ORDER_TABLE_SELECT_WEEK";
    }

    /**
     * 进程是否处理
     */
    public static class TASK_TRACK_STS{
        /**未处理**/
        public static final int NO_TRACK =0;
        /**已处理**/
        public static final int TRACK =1;
    }
    /**
     *
     * 订单费用扩展表的审核标示
     *
     */
    public static class AUDIT_STS{
        /**需要审核标示**/
        public static final int YES=1;
        /**不需要审核标示**/
        public static final int NO=0;
    }

    public static class PAY_CENTER{
        /**需要审核标示**/
        public static final String CODE_SUCESS="0000";
    }

    /***
     * 等值卡类型
     *
     */
    public static class EQUIVALENCE_CARD_TYPE{
        /***油卡类型***/
        public static final int OIL_TYPE=1;
    }
    /**
     * 修改类型
     * @author EditTheLife
     */
    public static class UPDATE_TYPE{
        /**自有订单**/
        public static final int OWN_ORDER = 1;
        /**无接单有转单**/
        public static final int IS_TRANSFER_ORDER = 2;
        /**接单无转单**/
        public static final int IS_ACCEPT_ORDER = 3;
        /**中间单**/
        public static final int MIDDLE_ORDER = 4;
    }
    /**
     * 修改订单字段校验模板
     * @author EditTheLife
     *
     */
    public static class UPDATE_COMPARE_PROERTY{
        /**线路信息**/
        public static String[] LINE_PROERTY = {"arriveTime","source","des",
                "addrDtl","desDtl","nand","eand","nandDes","eandDes","navigatDesLocation","navigatSourceLocation"
                ,"sourceProvince","sourceRegion","sourceCounty","desProvince","desRegion","desCounty","sourceCode","sourceName"
                ,"contactPhone","contactName","customName","companyName"};
        /**货物信息**/
        public static String[] GOODS_PROERTY = {"goodsName","square","weight","customNumber","goodsType","linkName","reciveType","linkPhone","address"};
        /**收货信息**/
        public static String[] RECIVE_PROERTY = {"reciveName","recivePhone","reciveCityId"
                ,"reciveProvinceId","reciveAddr"};
        /**收入信息**/
        public static String[] INCOME_PROERTY = {"costPrice","prePayCash","afterPayCash","prePayEquivalenceCardAmount"
                ,"afterPayEquivalenceCardAmount","prePayEquivalenceCardType","afterPayEquivalenceCardType"
                ,"afterPayAcctType","prePayEquivalenceCardNumber","afterPayEquivalenceCardNumber"
                ,"priceUnit","priceEnum"};
        /**收入信息(与运费信息冲突单独列出)**/
        public static String[]  INCOME_PROERTY_SCHEDULER = {"isUrgent"};
        /**调度信息**/
        public static String[] SCHEDULER_PROERTY = {"paymentWay","orgId","localUser","localPhone","localUserName"};
        /**成本信息**/
        public static String[] COST_PROERTY = {"salary","copilotSalary","capacityOil","runOil"
                ,"pontage","reciveAddr","totalFee","preTotalFee","preCashFee","preOilVirtualFee","arrivePaymentFee"
                ,"preOilFee","preEtcFee","finalFee","estFee","oilUseType","oilConsumer","pontagePer","oilAccountType","oilBillType"};
        /**回货信息**/
        public static String[] BAACKHAUL_PROERTY = {"isBackhaul","backhaulPrice","backhaulLinkMan"
                ,"backhaulLinkMan","backhaulLinkManId","backhaulLinkManBill"};
        /**司机信息**/
        public static String[] DRIVER_PROERTY = {"carDriverId","carDriverMan","carDriverPhone"
                ,"copilotMan","copilotPhone","copilotUserId"};
        /**账期信息**/
        public static String[] PAYMENT_DAYS_PROERTY = {"balanceType","paymentDaysType","reciveTime"
                ,"invoiceTime","collectionTime","reconciliationTime","reciveMonth","invoiceMonth","collectionMonth"
                ,"reconciliationMonth","reciveDay","invoiceDay","collectionDay","reconciliationDay"};
        /**经停线路信息**/
        public static String[] TRANSIT_LINE_PROERTY = {"nand","eand","address","navigatLocation","region"
                ,"province","county","arriveTime","distance"};
        /**车辆信息**/
        public static String[]  PLATE_NUMBER_PROERTY = {"plateNumber","vehicleCode","carLengh","carStatus"
                ,"carUserType","trailerId","trailerPlate","licenceType","creditLevel"};

        /**代收信息**/
        public static String[] COLLECTION_PROERTY= {"isCollection","collectionUserId","collectionUserName","collectionUserPhone"};
    }
    /**
     * 油卡
     * @author EditTheLife
     *
     */
    public  static class OIL_CARD_UPDATE_STATE{
        /***待修改***/
        public static final int AWAIT_UPDATE=0;
        /***已修改***/
        public static final int YET_UPDATE=1;
        /***已还原***/
        public static final int RESTORE=2;
    }

    /**
     * 是否修改
     * @author EditTheLife
     *
     */
    public  static class IS_UPDATE{
        /***未修改***/
        public static final int NOT_UPDATE=0;
        /***修改***/
        public static final int UPDATE=1;
    }
    /**
     * 是否临时车队单 0 不是 1 是
     * @author EditTheLife
     */
    public  static class IS_TEMP_TENANT{
        /***不是***/
        public static final int NO=0;
        /***是***/
        public static final int YES=1;
    }
    /**
     * 是否代收 0 不代收 1 代收
     * @author EditTheLife
     */
    public  static class IS_COLLECTION{
        /***不代收***/
        public static final int NO=0;
        /***代收***/
        public static final int YES=1;
    }

    /**
     * 自有车油使用类型
     * <p>Title: OIL_USE_TYPE</p >
     * <p>Description: </p >
     * @author hp
     * @date 2018-08-22
     */
    public static class OIL_USE_TYPE{
        /**
         * 客户油
         */
        public static final int CUSTOMOIL=1;
        /**
         * 车队油
         */
        public static final int TENANTOIL=2;
    }
    /**
     * 账期类型
     * @author EditTheLife
     */
    public  static class PAYMENT_DAYS_TYPE{
        /***成本***/
        public static final int COST = 1;
        /***收入***/
        public static final int INCOME = 2;
    }
    /**
     * 计算账期类型
     * @author EditTheLife
     */
    public static class CALCULATE_TYPE {
        /***回单***/
        public static final int RECIVE = 1;
        /***对账***/
        public static final int RECONCILIATION = 2;
        /***收款***/
        public static final int COLLECTION = 3;
        /***开票***/
        public static final int INVOICE = 4;
    }
    /**
     * 自有车结算方式
     * @author EditTheLife
     */
    public  static class PAYMENT_WAY{
        /***成本***/
        public static final int COST = 1;
        /***实报实销***/
        public static final int EXPENSE = 2;
        /***承包***/
        public static final int CONTRACT = 3;
    }

    /**
     *
     * 上传经纬度的类型
     * @author liyiye
     *
     */
    public static class GPS_TYPE{
        /***G7上传的经纬度***/
        public static final int G7 = 1;
        /***app上传的经纬度***/
        public static final int APP = 2;
        /***易流上传的经纬度***/
        public static final int YL = 3;
        /***中交北斗***/
        public static final int BD = 4;
    }

    /**
     * 费用上报审核状态
     */
    public static class ORDER_COST_REPORT{
        /***未提交***/
        public static final int UN_SUBMITTED = 0;
        /***已提交***/
        public static final int SUBMITTED = 1;
        /***待审核***/
        public static final int TO_BE_AUDITED = 2;
        /***审核中***/
        public static final int AUDIT = 3;
        /***审核不通过***/
        public static final int AUDIT_NOT_PASS = 4;
        /***审核通过***/
        public static final int AUDIT_PASS = 5;

        /***是否曾经审核通过--否***/
        public static final int IS_AUDIT_NOT_PASS = 0;
        /***是否曾经审核通过--是***/
        public static final int IS_AUDIT_PASS = 1;
    }

    /**
     * 费用上报付款方式
     */
    public static class REPORT_PAYMENT_WAY{
        /***油卡***/
        public static final int REPORT_PAYMENT_WAY1 = 1;
        /***现金***/
        public static final int REPORT_PAYMENT_WAY2 = 2;
        /***ETC卡***/
        public static final int REPORT_PAYMENT_WAY3 = 3;
        /**临时油站**/
        public static final int REPORT_PAYMENT_WAY4 = 4;
    }

    /**
     * 费用上报费用类型
     */
    public static class TABLE_TYPE{
        /***1油费***/
        public static final int TABLE_TYPE1 = 1;
        /***2路桥费***/
        public static final int TABLE_TYPE2 = 2;
    }
    /**
     * 油卡渠道
     * @author EditTheLife
     */
    public static class CARD_CHANNEL{
        /***0 添加***/
        public static final int ADD = 0;
        /***1 车辆绑定***/
        public static final int VEHICLE_BIND = 1;
    }

    /**
     * 费用上报 是否加满油
     */
    public static class IS_FULL_OIL{
        /***0未加满油***/
        public static final int IS_FULL_OIL0 = 0;
        /***1已加满油***/
        public static final int IS_FULL_OIL1 = 1;
    }
    public static class DRIVER_SWIICH_STATE{
        /**
         * 待确认
         */
        public static final int STATE0=0;
        /**
         * 切换成功
         */
        public static final int STATE1=1;
        /**
         * 已取消
         */
        public static final int STATE2=2;
        /**
         * 被驳回
         */
        public static final int STATE3=3;
    }
    /**
     * 轨迹类型
     * @author EditTheLife
     */
    public static class TRACK_TYPE{
        /**靠台 */
        public static final int TYPE1=1;
        /**离台 */
        public static final int TYPE2=2;
        /**到达 */
        public static final int TYPE3=3;
        /**离场 */
        public static final int TYPE4=4;
    }


    /**
     * 票据类型
     */
    public static class FUNDS_IS_NEED_BILL{
        /**不需要运输专票 */
        public static final int FUNDS_IS_NEED_BILL0=0;
        /**承运商开票 */
        public static final int FUNDS_IS_NEED_BILL1=1;
        /**平台开具运输专票 */
        public static final int FUNDS_IS_NEED_BILL2=2;
        /**服务费票据 */
        public static final int FUNDS_IS_NEED_BILL3=3;
        /**不提供发票 */
        public static final int FUNDS_IS_NEED_BILL4=4;
        /**提供发票 */
        public static final int FUNDS_IS_NEED_BILL5=5;
        /**油票*/
        public static final int FUNDS_IS_NEED_BILL6=6;
    }
    /**
     * GPS来源名称
     * @author EditTheLife
     */
    public static class GPS_TYPE_NAME{

        public static final String APP = "手机GPS";

        public static final String G7 = "G7";

        public static final String YL = "易流";

        public static final String BD = "北斗";
    }
    /**
     * 时效异常事件类型
     * @author EditTheLife
     */
    public static class ABNORMAL_TYPE{
        /**无异常事件*/
        public static final int ABNORMAL_TYPE1 = 0;
        /**行驶缓慢*/
        public static final int ABNORMAL_TYPE2 = 1;
        /**异常停留*/
        public static final int ABNORMAL_TYPE3 = 2;
    }
    /**
     * 时效监听靠台类型
     * @author EditTheLife
     */
    public static class MONITOR_DEPEND_TYPE{
        /**预估晚*/
        public static final int MONITOR_DEPEND_TYPE1 = 1;
        /**晚靠台*/
        public static final int MONITOR_DEPEND_TYPE2 = 2;
        /**实际晚*/
        public static final int MONITOR_DEPEND_TYPE3 = 3;
    }
    /**
     * 时效监听异常类型
     * @author EditTheLife
     */
    public static class MONITOR_ABNORMAL_TYPE{
        /**堵车缓行*/
        public static final int MONITOR_ABNORMAL_TYPE1 = 1;
        /**异常停留*/
        public static final int MONITOR_ABNORMAL_TYPE2 = 2;
    }
    /**
     * 时效监听到达类型
     * @author EditTheLife
     */
    public static class MONITOR_ARRIVE_TYPE{
        /**预估迟到*/
        public static final int ABNORMAL_TYPE1 = 1;
        /**迟到*/
        public static final int ABNORMAL_TYPE2 = 2;
        /**实际迟到*/
        public static final int ABNORMAL_TYPE3 = 3;
    }
    /**
     * 时效监听类型
     * @author EditTheLife
     */
    public static class MONITOR_AGING_TYPE{
        /**全部*/
        public static final int MONITOR_AGING_TYPE0 = 0;
        /**晚靠台*/
        public static final int MONITOR_AGING_TYPE1 = 1;
        /**异常停留*/
        public static final int MONITOR_AGING_TYPE2 = 2;
        /**堵车缓行*/
        public static final int MONITOR_AGING_TYPE3 = 3;
        /**预估迟到*/
        public static final int MONITOR_AGING_TYPE4 = 4;
        /**迟到*/
        public static final int MONITOR_AGING_TYPE5 = 5;
    }


    public static class  PAY_TYPE{
        /**查询超管的银行卡信息*/
        public static final int PAY_TYPE_0 = 0;
        /**查询传进去userId的银行卡信息*/
        public static final int PAY_TYPE_1 = 1;
    }

    public static class  SYNC_56K_TYPE{
        /**新增订单*/
        public static final int SAVE = 1;
        /**修改订单*/
        public static final int UPDATE = 2;
        /**位置同步*/
        public static final int LOCATION = 3;
        /**取消订单*/
        public static final int CANCEL = 4;
        /**轨迹订单*/
        public static final int TRACK_DATE = 5;
    }
    /**
     * 回单操作类型
     * @author EditTheLife
     */
    public static class  RECIVE_TYPE{
        /**单笔回单*/
        public static final int SINGLE = 1;
        /**批量回单*/
        public static final int BATCH = 2;
    }



    /**
     * 订单来源
     *
     */
    public static class  ORDER_SOURCE_TYPE{
        /**同心智行*/
        public static final int GHC = 1;
        /**百世*/
        public static final int BEST = 2;
    }


    /**处理类型
     * 1新增 2修改 3删除
     *
     */
    public static class  DEAL_TYPE{
        /**1新增*/
        public static final int DEAL_TYPE1 = 1;
        /**2修改*/
        public static final int DEAL_TYPE2 = 2;
        //3删除
        public static final int DEAL_TYPE3 = 3;
    }

    /**
     * 处理状态
     *
     */
    public static class  DEAL_STATE{
        /**0未处理*/
        public static final int DEAL_STATE0 = 0;
        /**1已处理*/
        public static final int DEAL_STATE1 = 1;
        /**1处理失败*/
        public static final int DEAL_STATE2 = 2;
    }


    public static String getDate(int day)throws Exception{
        Date date=new   Date(); //取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE,day); //把日期往后增加一天,整数  往后推,负数往前移动
        date=calendar.getTime(); //这个时间就是日期往后推一天的结果
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }
    /**
     * 经停点类型
     * @author zhouj
     */
    public  static class POINT_TYPE{
        /**1装货点*/
        public static final int LOADING = 1;
        /**2卸货点*/
        public static final int UNLOAD = 2;
    }

    /**
     * 虚拟油消费对象
     * <p>Title: OIL_CONSUMER</p >
     * <p>Description: </p >
     * @author hp
     * @date 2019-06-10
     */
    public  static class OIL_CONSUMER{
        /**1自有油站*/
        public static final int SELF = 1;
        /**2共享油站*/
        public static final int SHARE = 2;
    }
    /**
     * 同步类型
     */
    public  static class SYNC_TYPE{
        /**新增*/
        public static final int ADD = 1;
        /**修改*/
        public static final int UPDATE = 2;
        /**协议*/
        public static final int AGREEMENT = 3;
        /**删除*/
        public static final int DELETE = 4;
        /**轨迹*/
        public static final int TRACK = 5;
        /**回单*/
        public static final int RECIVE = 6;
        /**重新上传回单*/
        public static final int ANEW_RECIVE = 7;
        /**运单终结*/
        public static final int FINALITY = 8;
    }
    /**
     * 协议签署状态
     */
    public static class SIGN_AGREE_STATE{
        /**未同步*/
        public static final int NO_SYNC = 0;
        /**待签订*/
        public static final int AWAIT_SIGN = 1;
        /**已签订*/
        public static final int SUCCEED = 2;
        /**已驳回*/
        public static final int TURN_DOWN = 3;
    }

    /**
     * 快速开票订单状态
     * 1待生成订单 2订单生成中 3已生成订单 4订单生成失败
     */
    public static class QUICK_INVOICE_ORDER_STATE{
        //1待生成订单 2订单生成中 3已生成订单 4订单生成失败
        public static final int DEAL_STATE1 = 1;
        public static final int DEAL_STATE2 = 2;
        public static final int DEAL_STATE3 = 3;
        public static final int DEAL_STATE4 = 4;
    }
}
