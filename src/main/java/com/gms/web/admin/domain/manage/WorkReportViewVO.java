package com.gms.web.admin.domain.manage;

import java.io.Serializable;
import java.util.List;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkReportViewVO extends AbstractVO implements Serializable {

	
	/**Work_Report_Seq      */
	private Integer workReportSeq;
	
	/**User_ID              */
	private String userId;
	
	/** Customer_ID        	*/
	private Integer customerId;
	
	/** Customer_Nm        	*/
	private String customerNm;
	
	/** Order_Amount     	*/
	private int orderAmount;
	
	/** Income_Way		*/  
	/** CASH
	 *  CARD
	 */
	private String incomeWay;
	
	/**Received_Amount     */
	private int receivedAmount;
	
	/** searchDt	*/
	private String  searchDt;
	
	private String salesProducts;
	
	private String backProducts;
	

	
	private List<WorkBottleVO> salesBottles;
	
	private List<WorkBottleVO> backBottles;
}
