package com.gms.web.admin.domain.manage;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkReportVO extends AbstractVO implements Serializable {

	/**Work_Report_Seq      */
	private Integer workReportSeq;
	
	/**User_ID              */
	private String userId;
	
	/** Customer_ID        	*/
	private Integer customerId;
	
	/** Customer_Nm        	*/
	private String customerNm;
	
	/** Order_ID		*/
	private Integer orderId;
	
	/**Work_Dt              */
	private Date workDt;	
	
	/** Order_Amount     	*/
	private int orderAmount;
	
	/**Received_Amount     */
	private int receivedAmount;
	
	/** 삭제여부  Bottle_Type */
	/** E 공병, F 실병 */
	private String bottleType;
	
	/** Order_Product_Nm       	*/
	private String orderProductNm;
	
	/** Order_Product_Capa     	*/
	private String orderProductCapa;
	
	/** searchDt	*/
	private String  searchDt;
	
	/** bottleIds	*/
	private String  bottlesIds;
	
	/** Bottle_Work_CD	*/
	/*
	0301	입고
	0302	진공배기
	0303	누출확인
	0304	충전기한확인
	0305	충전
	0306	출고
	0307	상차
	0308	판매
	0309	회수
	0310	기타
	0399	폐기
	*/
	private String bottleWorkCd;
	

	private List<String> bottList;
}
