package com.gms.web.admin.domain.manage;

import java.io.Serializable;

import com.gms.web.admin.common.domain.AbstractVO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderProductVO extends AbstractVO implements Serializable {
	
	private static final long serialVersionUID = 8422009842300528104L;
	

	/** Order_ID			*/
	private Integer orderId;
	
	/** Order_Product_Seq	*/
	private Integer orderProductSeq;
	
	/** Product_ID 			*/
	private Integer productId;	
		
	/** Product_Price_Seq 	*/
	private Integer productPriceSeq;
	
	/** Order_Count      	*/
	private int orderCount;	
	
	/** Order_Product_ETC     	*/
	private String orderProductEtc;
	
	/** Bottle_Change_YN 	*/
	private String bottleChangeYn;	
	
	/** Order_Amount     	*/
	private int orderAmount;
	
	/** Bottle_ID			*/
	private String bottleId;     
	
	/** Product_Nm 			*/
	private String productNm;
	
	/** Product_Capa 			*/
	private String productCapa;

}
