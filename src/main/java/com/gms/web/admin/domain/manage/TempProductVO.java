package com.gms.web.admin.domain.manage;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TempProductVO {
	
	/** Product_ID */
	private Integer productId;
		
	/** Product_Price_Seq */
	private Integer productPriceSeq;
	
	/** Product_Price 	*/    
	private int productPrice;	
	
	/** Order_ID			*/
	private Integer orderId;
	
	/** Order_Product_Seq	*/
	private Integer orderProductSeq;
	
	/* Order_Bottle_Seq*/
	private Integer orderBottleSeq;

}
