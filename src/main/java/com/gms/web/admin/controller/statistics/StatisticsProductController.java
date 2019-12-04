package com.gms.web.admin.controller.statistics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.DateUtils;
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
	public ModelAndView getStatisticsProductDaily(StatisticsProductVO params) {

		logger.info("StatisticsProductContoller getStatisticsProductDaily");
		logger.info("StatisticsProductContoller searchStatisticsProductDt "+ params.getSearchStatDt());

		ModelAndView mav = new ModelAndView();
		
		//Integer searchProductId = params.getSearchProductId();
				
		String searchStatDt = params.getSearchStatDt();	
		
		String searchStatDtFrom = null;
		String searchStatDtEnd = null;
				
		if(searchStatDt != null && searchStatDt.length() > 20) {						
			searchStatDtFrom = searchStatDt.substring(0, 10) ;			
			searchStatDtEnd = searchStatDt.substring(13, searchStatDt.length()) ;
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);			
		}else {						
			
			searchStatDtFrom = DateUtils.getNextDate(-30,"yyyy/MM/dd");
			logger.debug("****** getStatisticsProductDaily else *****getSearchStatDtFrom===*"+searchStatDtFrom);
			
			searchStatDtEnd = DateUtils.getNextDate(-1,"yyyy/MM/dd");
			logger.debug("****** getStatisticsProductDaily else *****getSearchStatDtEnd===*"+searchStatDtEnd);
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);
			
			searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
		}
		
		
		List<StatisticsProductVO> statProductList = statService.getDailylStatisticsProductList(params);
		
		mav.addObject("statProductList", statProductList);	
		
		//검색어 셋팅
		mav.addObject("searchStatDt", searchStatDt);	
		mav.addObject("searchProductId", params.getSearchProductId());	
		
		List<ProductVO> productList = productService.getProductList();
		mav.addObject("productList", productList);
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.stat_product"));	 
		
		
		mav.setViewName("/gms/statistics/product/daily");
		
		return mav;
	}

	
	@RequestMapping(value = "/gms/statistics/product/monthly.do")
	public ModelAndView getStatisticsProductMonthly(StatisticsProductVO params) {

		logger.info("StatisticsProductContoller getStatisticsProductMonthly");
		logger.info("StatisticsProductContoller searchStatisticsProductDt "+ params.getSearchStatDt());

		ModelAndView mav = new ModelAndView();
		
		//Integer searchProductId = params.getSearchProductId();
				
		String searchStatDt = params.getSearchStatDt();	
		
		String searchStatDtFrom = null;
		String searchStatDtEnd = null;
				
		if(searchStatDt != null && searchStatDt.length() > 20) {						
			searchStatDtFrom = searchStatDt.substring(0, 8) ;			
			searchStatDtEnd = searchStatDt.substring(13, searchStatDt.length()-2) ;
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);			
		}else {						
			
			searchStatDtFrom = DateUtils.getNextDate(-365,"yyyy/MM");
			logger.debug("****** getMonthlylStatisticsProductList else *****getSearchStatDtFrom===*"+searchStatDtFrom);
			
			searchStatDtEnd = DateUtils.getNextDate(-1,"yyyy/MM");
			logger.debug("****** getMonthlylStatisticsProductList else *****getSearchStatDtEnd===*"+searchStatDtEnd);
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);
			
			searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
		}
		
		
		List<StatisticsProductVO> statProductList = statService.getMontlylStatisticsProductList(params);
		
		mav.addObject("statProductList", statProductList);	
		
		//검색어 셋팅
		mav.addObject("searchStatDt", searchStatDt);	
		mav.addObject("searchProductId", params.getSearchProductId());	
		
		List<ProductVO> productList = productService.getProductList();
		mav.addObject("productList", productList);
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.stat_product"));	 
		
		
		mav.setViewName("/gms/statistics/product/monthly");
		
		return mav;
	}
}
