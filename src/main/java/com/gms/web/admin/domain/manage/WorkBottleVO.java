package com.gms.web.admin.domain.manage;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkBottleVO extends AbstractVO implements Serializable {

	/**Work_Report_Seq      */
	private Integer workReportSeq;
	
	/** Bottle_ID		*/
	private String bottleId;
	
	/** Bottle_BarCd	*/
	private String bottleBarCd;

	/** Customer_ID		*/
	private Integer customerId;

	/** Customer_Nm		*/
	private String customerNm;
	
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
	
	/** Gas ID */
	private Integer gasId ;
	
	/** Product_ID */
	private Integer productId;
		
	/** Product_Price_Seq */
	private Integer productPriceSeq;
	
	/** 삭제여부  Bottle_Type */
	/** E 공병, F 실병 */
	private String bottleType;
	
	
}
