package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderExtVO extends OrderVO implements Serializable {
	
	private static final long serialVersionUID = 3233229822300528104L;
	
	/** Customer_Nm        	*/
	private String customerNm;	
	
	/**Product_Nm */
	private String productNm;	
	
	/** Product_Capa     */
	private String productCapa;
	
	/** Sales_Nm     */
	private String salesNm;
	
	/** Order_Product_Cnt     */
	private int orderProductCnt;	

}
