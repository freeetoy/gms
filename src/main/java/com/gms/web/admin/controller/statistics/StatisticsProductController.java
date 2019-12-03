package com.gms.web.admin.controller.statistics;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.domain.manage.ProductVO;
import com.gms.web.admin.domain.statistics.StatisticsProductVO;
import com.gms.web.admin.service.manage.ProductService;
import com.gms.web.admin.service.statistics.StatisticsProductService;

@Controller
public class StatisticsProductController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * UserService 빈(Bean) 선언
	 */
	@Autowired
	private StatisticsProductService statService;
	
	@Autowired
	private ProductService productService;
	
	@RequestMapping(value = "/gms/statistics/product/daily.do")
	public ModelAndView getStatisticsProductList(StatisticsProductVO params) {

		logger.info("StatisticsProductContoller getStatisticsProductList");
		logger.info("StatisticsProductContoller searchStatisticsProductDt "+ params.getSearchStatDt());

		ModelAndView mav = new ModelAndView();
		
		Integer searchProductId = params.getSearchProductId();
				
		String searchStatDt = params.getSearchStatDt();	
		
		String searchStatDtFrom = null;
		String searchStatDtEnd = null;
				
		if(searchStatDt != null && searchStatDt.length() > 20) {						
			searchStatDtFrom = searchStatDt.substring(0, 10) ;			
			searchStatDtEnd = searchStatDt.substring(13, searchStatDt.length()) ;
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);			
		}
		
		
		List<StatisticsProductVO> statProductList = statService.getDailylStatisticsProductList(params);
		
		mav.addObject("statProductList", statProductList);	
		
		//검색어 셋팅
		mav.addObject("searchStatDt", params.getSearchStatDt());	
		mav.addObject("searchProductId", params.getSearchProductId());	
		
		List<ProductVO> productList = productService.getProductList();
		mav.addObject("productList", productList);
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.stat_product"));	 
		
		
		mav.setViewName("/gms/statistics/product/daily");
		
		return mav;
	}

}
