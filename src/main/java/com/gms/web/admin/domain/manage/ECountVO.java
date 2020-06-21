package com.gms.web.admin.domain.manage;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ECountVO {
	private static final long serialVersionUID = -1304822960909123260L;
	
	//일자	순번	거래처코드	거래처명	담당자	출하창고	거래유형	통화	환율	수금내역	비고	품목코드	품목명	규격	수량	단가	외화금액	공급가액	부가세	적요	단가(VAT포함)	생산전표생성	Ecount
	
	private String customerId;
	
	private String customerNm;
	
	private String salesNm;
	
	private String wareHouse ="대한특수가스";
	
	private String dealType = "부가세율 적용";		// 거래유형
	
	private String currency;		//통화
	
	private String changetRate;		// 환율
	
	private String collectionContents;		//수금 내역
	
	private String Etc;		// 비고
	
	private Integer productId;
	
	private String eCountCd;	//ECount품목코드
	
	/** ECount_CDS     */
	private String eCountCdS; 	//용기및사스 판매 ECount품목코드
	
	private String productNm;
	
	private String productCapa; // 규격
	
	private int orderCount;	//수량
	
	private int productPrice;	//단가
	
	private int currencyPrice;	//외화 금액
	
	private int supplyPrice;		//공급가액
		
	private int vat;			//부가세
	
	private String summary;			//적요
	
	private int prdouctPriceVAT;		//단가(VAT포함)
	
	private String receipt;		//생산전표생성
	
	private String EcountString ;
		
	/** Bottle_Sale_YN 	*/
	private String bottleSaleYn;	//용기 판매 여부
}
