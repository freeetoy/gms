package com.gms.web.admin.controller.common;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.domain.manage.CustomerSimpleVO;
import com.gms.web.admin.domain.manage.WorkBottleVO;
import com.gms.web.admin.domain.manage.WorkReportVO;
import com.gms.web.admin.service.common.ApiService;
import com.gms.web.admin.service.manage.WorkReportService;

@Controller
public class ApiController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final String TAG="ApiController";
	/*
	 * Service 빈(Bean) 선언
	 */
	@Autowired
	private ApiService apiService;
	
	@RequestMapping(value = "/api/controlAction.do")
	@ResponseBody
	public String controllAction(String userId, String bottles, String customerNm, String bottleType, String bottleWorkCd)	{	
				
		logger.debug("userId="+userId+" : bottles ="+bottles +": bottleType ="+ bottleType + ": bottleWorCd ="+bottleWorkCd+" : customerNm ="+customerNm);
		
		boolean phoneCall = true;
		int result = 0;
		
		WorkReportVO workReport = new WorkReportVO();
		
		workReport.setBottleType(bottleType);
		workReport.setBottlesIds(bottles);		//BottleBarCd 모음
		workReport.setCustomerNm(customerNm);		
		workReport.setPhoneFlag(phoneCall);
		workReport.setCreateId(userId);				
		
		
		if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.come"))) {
			logger.debug("입고 start");
			workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0301"));
			result = apiService.registerWorkReportForChangeCd(workReport);
			
		}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.out"))) {		//출고
			workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0306"));
			result = apiService.registerWorkReportForChangeCd(workReport);
			
		}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.incar"))) {		// 상차
			
			workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0307"));
			result = apiService.registerWorkReportForChangeCd(workReport);
		}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.charge"))) {		//충전
			
			workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0305"));
			result = apiService.registerWorkReportForChangeCd(workReport);
		}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.sales"))) {			//판매
			
			workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0308"));
			
			result = apiService.registerWorkReportForSale(workReport);
			
		}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.rental"))) {			//대여
			workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0309"));
			
			result = apiService.registerWorkReportForSale(workReport);
			
		}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.back"))) {			//회수
			workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0310"));
			result = apiService.registerWorkReportForChangeCd(workReport);
		}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.chargeDt"))) {			//충전기한확인
			workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0304"));
			result = apiService.registerWorkReportForChangeCd(workReport);
			
		}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.vacuum"))) {			//진공배기
			workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0302"));
			result = apiService.registerWorkReportForChangeCd(workReport);
		}else if(bottleWorkCd.equals(PropertyFactory.getProperty("common.bottle.status.hole"))) {			//누공확인
			workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0303"));
			result = apiService.registerWorkReportForChangeCd(workReport);
		}
		return "success";
		//return null;
	}

	
	@RequestMapping(value = "/api/controlActionNoGas.do")
	@ResponseBody
	public String controllActionNoGas(String userId, String customerNm, Integer productId, Integer productPriceSeq, int productCount )	{	
				
		logger.debug("userId="+userId+" : productId ="+productId +": productPriceSeq ="+ productPriceSeq + " : customerNm ="+customerNm + " : productCount ="+productCount);
		
		boolean phoneCall = true;
		int result = 0;
		
		WorkBottleVO workBottle = new WorkBottleVO();
		
		workBottle.setProductId(productId);
		workBottle.setProductPriceSeq(productPriceSeq);
		workBottle.setProductCount(productCount);
		workBottle.setCustomerNm(customerNm);				
		workBottle.setCreateId(userId);	
		workBottle.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0308"));
		
		logger.info("단품판매 start");
		//workReport.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0301"));
		result = apiService.registerWorkReportNoGas(workBottle);			
		
		if(result > 0) 		return "success";
		else return "fail";
		//return null;
	}

}
