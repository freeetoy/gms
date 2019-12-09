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
	
	/** Order_Process_CD   	
	 * 
	 * 0201 주문접수
	 * 0202 주문완료
	 * 0203 생산완료
	 * 0204 납품
	 * 0205 판매완료
	 * 0206 영업완료 -- 삭제 
	 * 0207 거래명세서완료
	 * 0208 세금계산서완료
	 * 0209 입금완료
	 * 0210 거래완료
	 * 0299 삭제			*/
	private String orderProcessCd;
	
	/** Order_Process_Nm   	*/
	private String orderProcessCdNm;

	private List<String> bottList;
}
