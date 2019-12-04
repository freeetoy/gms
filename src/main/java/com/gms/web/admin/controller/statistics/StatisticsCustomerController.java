package com.gms.web.admin.controller.statistics;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.domain.manage.CustomerVO;
import com.gms.web.admin.domain.statistics.StatisticsCustomerVO;
import com.gms.web.admin.service.manage.CustomerService;
import com.gms.web.admin.service.statistics.StatisticsCustomerService;

@Controller
public class StatisticsCustomerController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * UserService 빈(Bean) 선언
	 */
	@Autowired
	private StatisticsCustomerService statService;
	
	@Autowired
	private CustomerService customerService;
	
	@RequestMapping(value = "/gms/statistics/customer/daily.do")
	public ModelAndView getStatisticsCustomerDaily(StatisticsCustomerVO params) {

		logger.info("StatisticsCustomerContoller getStatisticsCustomerDaily");
		logger.info("StatisticsCustomerContoller searchStatisticsCustomerDt "+ params.getSearchStatDt());

		ModelAndView mav = new ModelAndView();
		
		//Integer searchCustomerId = params.getSearchCustomerId();
				
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
			logger.debug("****** getStatisticsCustomerDaily else *****getSearchStatDtFrom===*"+searchStatDtFrom);
			
			searchStatDtEnd = DateUtils.getNextDate(-1,"yyyy/MM/dd");
			logger.debug("****** getStatisticsCustomerDaily else *****getSearchStatDtEnd===*"+searchStatDtEnd);
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);
			
			searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
		}
		params.setSearchStatDt(searchStatDtFrom);
		
		List<StatisticsCustomerVO> statCustomerList = statService.getDailylStatisticsCustomerList(params);
		
		mav.addObject("statCustomerList", statCustomerList);	
		
		//검색어 셋팅
		mav.addObject("searchStatDt", searchStatDt);	
		mav.addObject("searchCustomerId", params.getSearchCustomerId());	
		
		
		Map<String, Object> map = customerService.searchCustomerList("");
		mav.addObject("customerList", map.get("list"));
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.stat_customer"));	 
		
		
		mav.setViewName("/gms/statistics/customer/daily");
		
		return mav;
	}

	
	@RequestMapping(value = "/gms/statistics/customer/monthly.do")
	public ModelAndView getStatisticsCustomerMonthly(StatisticsCustomerVO params) {

		logger.info("StatisticsCustomerContoller getStatisticsCustomerMonthly");
		logger.info("StatisticsCustomerContoller searchStatisticsCustomerDt "+ params.getSearchStatDt());

		ModelAndView mav = new ModelAndView();
		
		//Integer searchCustomerId = params.getSearchCustomerId();
				
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
			logger.debug("****** getMonthlylStatisticsCustomerList else *****getSearchStatDtFrom===*"+searchStatDtFrom);
			
			searchStatDtEnd = DateUtils.getNextDate(-1,"yyyy/MM");
			logger.debug("****** getMonthlylStatisticsCustomerList else *****getSearchStatDtEnd===*"+searchStatDtEnd);
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);
			
			searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
		}
		params.setSearchStatDt(searchStatDtFrom);
		
		List<StatisticsCustomerVO> statCustomerList = statService.getMontlylStatisticsCustomerList(params);
		
		mav.addObject("statCustomerList", statCustomerList);	
		
		//검색어 셋팅
		mav.addObject("searchStatDt", searchStatDt);	
		mav.addObject("searchCustomerId", params.getSearchCustomerId());	
		
		Map<String, Object> map = customerService.searchCustomerList("");
		mav.addObject("customerList", map.get("list"));
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.stat_customer"));	 
		
		
		mav.setViewName("/gms/statistics/customer/monthly");
		
		return mav;
	}
}
