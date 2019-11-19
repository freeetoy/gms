package com.gms.web.admin.domain.manage;

import java.io.Serializable;
import java.util.Date;

import com.gms.web.admin.common.domain.AbstractSearchVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BottleVO extends AbstractSearchVO implements Serializable {

	private static final long serialVersionUID = 3224009822300528104L;
	
	/** Bottle_ID		*/
	private String bottleId;

	/** Bottle_BarCd	*/
	private String bottleBarCd;

	/** Gas ID */
	private Integer gasId ;
	
	/** Bottle_Capa	*/
	private String bottleCapa;

	/** Bottle_Create_Dt	*/
	private Date bottleCreateDt;

	/** Bottle_Charge_Dt	*/
	private Date bottleChargeDt;
	
	/** Bottle_Volumn	*/
	private int bottleVolumn;

	/** Bottle_Charge_Prss	*/
	private int bottleChargePrss;

	/** Bottle_Sales_YN	*/
	private String bottleSalesYn;

	/** Bottle_Work_CD	*/
	/*
	0301	입고
	0302	진공배기
	0303	누출확인
	0304	충전기한확인
	0305	출고
	0306	상차
	0307	판매
	0308	회수
	0309	폐기
	*/
	private String bottleWorkCd;

	/** Bottle_Work_ID	*/
	private String bottleWorkId;

	/** Customer_ID		*/
	private Integer customerId;

	/** Member_Comp_Seq	*/
	private Integer memberCompSeq;

	/** Order_ID		*/
	private Integer orderId;

	/** Order_Product_Seq	*/
	private Integer orderProductSeq;
	
	/** 검색용			*/
	/** SearchBottleiId	*/
	private String  searchBottleId;
	
	/** SearchGasId		*/
	private String  searchGasId;
	
	/** searchChargeDt	*/
	private String  searchChargeDt;
	
	/** searchChargeDtFrom	*/
	private String  searchChargeDtFrom;
	
	/** searchChargeDtEnd	*/
	private String  searchChargeDtEnd;
}
