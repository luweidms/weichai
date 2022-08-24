package com.youming.youche.conts;

/**
 * Created by kiya on 2018/5/12.
 */
public class ServiceConsts {
    /**
     * 邀请合作类型
     */
    public static class COOPERATION_TYPE{
        /***初次合作*/
        public static int NEW_COOPERATION = 1;
        /*** 修改合作*/
        public static int UPDATE_COOPERATION = 2;
    }

    /**
     * 该数据是否开用
     */
    public static class COOPERATION_USE {
        /**可用*/
        public static int YES = 1;
        /**不可用*/
        public static int NO = 2;
    }

    /**
     * 是否共享
     */
    public static class IS_SHARE{
        /**共享*/
        public static int YES = 1;
        /**不共享*/
        public static int NO = 2;
    }

    /**
     * 是否已读
     */
    public static class IS_READ{
        /**是*/
        public static int YES = 1;
        /**否*/
        public static int NO = 2;
    }

    /**
     * 服务商审核
     */
    public static class SERVICE_AUTH_STATE{
        /***待审*/
        public static int WAIT = 1;
        /***通过*/
        public static int PASS = 2;
        /***驳回*/
        public static int FAIL = 3;
    }

    /**
     * 合作状态
     */
    public static class COOPERATION_STATE{
        /**
         * 合作中
         */
        public static int IN_COOPERATION = 1;
        /***
         * 已解约
         */
        public static int RELEASED = 2;
    }

    /**
     * 开票能力
     */
    public static class IS_BILL_ABILITY{
        /**
         * 支持开票
         */
        public static int YSE = 1;
        /***
         * 不支持开票
         */
        public static int NO = 2;
    }

    /**
     * 站点新增修改的操作类型
     */
    public static class isFleet{
        /**车队操作*/
        public static  int TENANT = 1;
        /**服务商操作*/
        public static  int SERVICE = 2;
        /**运营平台共享操作*/
        public static  int OBMS_SHARE = 3;
        /**运营平台新增修改*/
        public static  int OBMS = 4;
    }
    /**
     * 站点类型
     */
    public static class PRODUCT_TYPE {
    	  /**无*/
    	  public static  int EMPTY = 0;
    	  /**找油网*/
    	  public static  int ZHAOYOU = 1;
    	  public static  int G7 = 2;
    	  public static  int SHENQI = 3;
	}

    public static long SERVICE_TENANT_ID = -1L;


    public static int TIME_OUT = 2*24*60*60;
    /**
     * 服务商订单状态
     * @author EditTheLife
     */
    public static class SERVICE_ORDER_STATE{
    	/**支付失败**/
    	public static  int FAIL_PAY = 0;
    	/**支付成功**/
    	public static  int SUCCEED_PAY = 1;
    	/**未付**/
    	public static  int NO_PAY = 2;
    	/**取消支付**/
    	public static  int CANCEL_PAY = 3;
    	/**支付中**/
    	public static  int MIDWAY_PAY = 4;
    }
    /**
     * 服务商订单类型
     * @author EditTheLife
     */
    public static class SERVICE_ORDER_TYPE{
    	/**扫码支付**/
    	public static  int OR_CODE = 1;
    	/**找油网**/
    	public static  int ZHAOYOU = 2;
        /**G7**/
        public static  int G7 = 3;
    }
    /**
     * 进程处理状态
     * @author EditTheLife
     */
    public static class TASK_STATE{
    	/**待处理**/
    	public static  int TASK_STATE = 0;
    	/**处理成功**/
    	public static  int TASK_STATE1 = 1;
    	/**处理失败**/
    	public static  int TASK_STATE2 = 2;
    }
    /**
     * 评价类型
     * @author EditTheLife
     */
    public static class EVALUATE_BUSI_TYPE{
    	/**加油记录标识**/
    	public static  int OIL_RECORD_CODE = 1;
    }

    /**
     * 油卡申请状态
     1待审核 2待发卡 3审核不通过 4待交付 5已交付 6已完成 7已取消
     */
    public static class OIL_APPLY_STATE{
        public static  int OIL_APPLY_STATE1 = 1;
        public static  int OIL_APPLY_STATE2 = 2;
        public static  int OIL_APPLY_STATE3 = 3;
        public static  int OIL_APPLY_STATE4 = 4;
        public static  int OIL_APPLY_STATE5 = 5;
        public static  int OIL_APPLY_STATE6 = 6;
        public static  int OIL_APPLY_STATE7 = 7;
    }

    /**
     * 油卡状态
     1未使用 2已发卡 3已交付 4已回收  5已挂失
     */
    public static class OIL_INFO_STATE{
        public static  int OIL_INFO_STATE1 = 1;
        public static  int OIL_INFO_STATE2= 2;
        public static  int OIL_INFO_STATE3 = 3;
        public static  int OIL_INFO_STATE4 = 4;
        public static  int OIL_INFO_STATE5 = 5;
    }

    /**
     * ETC卡状态
     1未使用  3已交付 5已确认收卡 6已注销 7已删除
     */
    public static class ETC_INFO_STATE{
        public static  int ETC_INFO_STATE1 = 1;
        public static  int ETC_INFO_STATE3= 3;
        public static  int ETC_INFO_STATE5 = 5;
        public static  int ETC_INFO_STATE6 = 6;
        public static  int ETC_INFO_STATE7 = 7;
    }

    /**
     * 油卡类型
     1中石油 2中石化
     */
    public static class OIL_CARD_TYPE{
        public static  int OIL_CARD_TYPE1 = 1;
        public static  int OIL_CARD_TYPE2= 2;
    }

    /**
     * 是否已被回收完成
     */
    public static class IS_RECOVERYED{
        /**是*/
        public static int IS_RECOVERYED1 = 1;
        /**否*/
        public static int IS_RECOVERYED0 = 0;
    }

    /**
     * 是否开票
     */
    public static class IS_NEED_BILL{
        /**是*/
        public static int IS_NEED_BILL1 = 1;
        /**否*/
        public static int IS_NEED_BILL0 = 0;
    }

    /**
     * 是否现场价
     */
    public static class LOCALE_BALANCE_STATE{
        /**是*/
        public static int YES = 1;
        /**否*/
        public static int NO = 0;
    }


    /**
     * 付费类型 1预付费 2后付费
     */
    public static class ETC_SERVICE_PAYMENT_TYPE{
        /**1预付费*/
        public static int PAYMENT_TYPE1 = 1;
        /**2后付费*/
        public static int PAYMENT_TYPE2 = 2;
    }


    /**
     * 申请类型 1油卡 2ETC卡
     */
    public static class APPLY_TYPE{
        /**1油卡*/
        public static int APPLY_TYPE1 = 1;
        /**2ETC卡*/
        public static int APPLY_TYPE2 = 2;
    }


    /**
     * 油卡状态
     *
     */
    public static class ETCCARD_STATUS {
        /**有效*/
        public static final int ETCCARD_STATUS1 = 1;
        /**失效*/
        public static final int ETCCARD_STATUS0 = 0;
        /**注销*/
        public static final int ETCCARD_STATUS2 = 2;
    }


    /**
     * 工单状态 YJD已接单 DSH待审核 DSG待施工 SGZ施工中 SGWC施工完成 YFQ已废弃 YWC已完成 YQX已取消
     */
    public static class REPAIR_WORK_STATUS{
        public static final String YJD = "YJD";
        public static final String DSH = "DSH";
        public static final String DSG = "DSG";
        public static final String SGZ = "SGZ";
        public static final String SGWC = "SGWC";
        public static final String YWC = "YWC";
        public static final String YFQ = "YFQ";
        public static final String YQX = "YQX";
    }

    /***
     * 订单状态 1申请待审核 2申请不通过 3待接单 4工单待确认 5工单待审核 6工单不通过 7维保中  8完成待确认   11待支付 12已完成 13废弃
     *  14申请审核中 15工单审核中 16工单支付中
     */
    public static class REPAIR_ORDER_STATUS{
        public static final int REPAIR_ORDER_STATUS1 = 1;
        public static final int REPAIR_ORDER_STATUS2 = 2;
        public static final int REPAIR_ORDER_STATUS3 = 3;
        public static final int REPAIR_ORDER_STATUS4 = 4;
        public static final int REPAIR_ORDER_STATUS5 = 5;
        public static final int REPAIR_ORDER_STATUS6 = 6;
        public static final int REPAIR_ORDER_STATUS7 = 7;
        public static final int REPAIR_ORDER_STATUS8 = 8;
        public static final int REPAIR_ORDER_STATUS9 = 9;
        public static final int REPAIR_ORDER_STATUS10 = 10;
        public static final int REPAIR_ORDER_STATUS11 = 11;
        public static final int REPAIR_ORDER_STATUS12 = 12;
        public static final int REPAIR_ORDER_STATUS13 = 13;
        public static final int REPAIR_ORDER_STATUS14 = 14;
        public static final int REPAIR_ORDER_STATUS15 = 15;
        public static final int REPAIR_ORDER_STATUS16 = 16;
    }

    /***
     * 工单类型，’GHCWX’-维修，’GHCBY’-保养
     */
    public static class REPAIR_WORK_TYPE{
        public static final String GHCWX = "GHCWX";
        public static final String GHCBY = "GHCBY";
    }


    /***
     * 审批动作：
     报价单确认（QUOTE），
     报价单拒绝（REFUSE）,
     施工完成确认（MAINTAIN）

     */
    public static class APPROVAL_ACTION{
        public static final String QUOTE = "QUOTE";
        public static final String REFUSE = "REFUSE";
        public static final String MAINTAIN = "MAINTAIN";
    }
    /**
     * 代收服务商类型
     * @author zhoujia
     */
    public static class AGENT_SERVICE_TYPE{
    	/**加油代收服务商*/
        public static final int OIL = 1;
        /**维修保养代收服务商*/
        public static final int MAINTIAN = 2;
    }


    /**
     * 轮胎账单支付装填
     * @author luoxe
     */
    public static class TYRE_PAY_STATE{
        /**待确认支付*/
        public static final int TYRE_PAY_STATE1 = 1;
        /**待付款*/
        public static final int TYRE_PAY_STATE2 = 2;
        /**确认中*/
        public static final int TYRE_PAY_STATE3 = 3;
        /**已付款*/
        public static final int TYRE_PAY_STATE4 = 4;
        /**已结算*/
        public static final int TYRE_PAY_STATE5 = 5;
        /**打款中*/
        public static final int TYRE_PAY_STATE6 = 6;
    }

}
