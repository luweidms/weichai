package com.youming.youche.finance.constant;

public class OaLoanData {
    //流程类型
    public static final Long NODE1 = (long) 27;//新建申请，默认提交部门审批人,金额大于5000且是成本类借支
    public static final Long NODE2 = (long) 38;//新建申请，默认提交部门审批人,金额大于5000且是费用类借支
    public static final Long NODE3 = (long) 29;//新建申请，默认提交部门审批人,金额小于等于5000且是成本类借支
    public static final Long NODE4 = (long) 28;//新建申请，默认提交部门审批人,金额小于等于5000且是费用类借支
    public static final Long NODE5 = (long) 2;//预算申请开始
    public static final Long NODE10 = (long) 200;//车管借支开始  借支金额小于等于500  －－A类借支
    public static final Long NODE11 = (long) 204;//车管借支开始  借支金额大于500  －－B类借支
    public static final Long NODE12 = (long) 300;//车管借支开始  －－C类借支
    //报销流程
    public static final Long NODE6 = (long) 100;//预算报销起点
    public static final Long NODE7 = (long) 11;//异常费用<500
    public static final Long NODE8 = (long) 12;//异常费用<=500<10000
    public static final Long NODE9 = (long) 13;//异常费用>=10000

    //借支类型
    public static final int LOANTYPE0 =0;//费用类借支
    public static final int LOANTYPE1 =1;//费用备用金
    public static final int LOANTYPE2 =2;//成本类借支
    public static final int LOANTYPE3 =3;//成本备用金

    //科目类型
    public static final int LOANSUBJECT0 =7;//保养费		2
    public static final int LOANSUBJECT1 =1;//维修费		2
    public static final int LOANSUBJECT2 =2;//轮胎费		2
    public static final int LOANSUBJECT3 =3;//停车费		1
    public static final int LOANSUBJECT4 =4;//过磅费		1
    public static final int LOANSUBJECT5 =5;//违章罚款		2
    public static final int LOANSUBJECT6 =6;//路桥费		1
    public static final int LOANSUBJECT8 =8;//油费		1
    public static final int LOANSUBJECT9 =9;//车祸事故		2
    public static final int LOANSUBJECT10 =10;//出险预报	    2
    public static final int LOANSUBJECT11 =11;//差旅费		1

    //审核状态
    public static final int STS0 =0;//待审核
    public static final int STS1 =1;//审核中
    public static final int STS2 =2;//审核未通过
    public static final int STS3 =3;//未核销
    public static final int STS4 =4;//核销中
    public static final int STS5 =5;//已核销
    public static final int STS6 =6;//已审核
    public static final int STS7 =7;//逾期
    public static final int STS8 =8;//撤销

    //还款类型
    public static final int AMORTIZE0 =0;//一次性还清
    public static final int AMORTIZE1 =1;//分期还清

    //借支金额
    public static final int AMOUNT =5000;//单位元
    public static final int AMOUNT_CAR =500;//单位元

    //下一节点类型 下一节点类型:1组织 ,2部门负责人,  3指定人员  -1流程结束
    public static final int NEXTTYPE = -1;
    public static final int NEXTTYPE1 = 1;
    public static final int NEXTTYPE2 = 2;
    public static final int NEXTTYPE3 = 3;

    //关联数据类型:1借支 2借支核销 3报销' 5付款申请
    public static final int RELTYPE1 = 1;
    public static final int RELTYPE2 = 2;
    public static final int RELTYPE3 = 3;
    public static final int RELTYPE4 = 4;
    public static final int RELTYPE5 = 5;
    public static final int RELTYPE6 = 6;//维修保养

    //审核 type=1通过，type=2驳回
    public static final String TYPE1 = "1";
    public static final String TYPE2 = "2";

    //借支分类 classify=1 内部借支，classify=2车管借支
    public static final int CLASSIFY1 = 1;
    public static final int CLASSIFY2 = 2;

    //发起1车管中心，2司机
    public static final int LAUNCH1 = 1;
    public static final int LAUNCH2 = 2;

    //核销状态0未生效 ，1生效
    public static final int VERIFICATION_STS0 = 0;
    public static final int VERIFICATION_STS1 = 1;

    //审核对象  1 运营（归属司机，需要写入明细表）  2 车管组
    public static final long VERIFY_TYPE_1 = 1;
    public static final long VERIFY_TYPE_2 = 2;

    //审核代码特殊处理
    public static final long NEXT_PORT_ID_1 = -1;// 特定审核人员－－运营负责人
    public static final long NEXT_PORT_ID_2 = -2;//特定审核组织 －－车管组
}
