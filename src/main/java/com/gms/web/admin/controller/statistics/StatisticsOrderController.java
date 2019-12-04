package com.gms.web.admin.controller.statistics;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.utils.DateUtils;
import com.gms.web.admin.domain.statistics.StatisticsOrderVO;
import com.gms.web.admin.service.statistics.StatisticsOrderService;
import com.gms.web.admin.service.statistics.StatisticsProductService;

@Controller
public class StatisticsOrderController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * UserService 빈(Bean) 선언
	 */
	
	@Autowired
	private StatisticsOrderService statService;
	
	
	@RequestMapping(value = "/gms/statistics/order/daily.do")
	public ModelAndView getStatisticsOrderDaily(StatisticsOrderVO params) {

		logger.info("StatisticsOrderContoller getStatisticsOrderDaily");
		logger.info("StatisticsOrderContoller searchStatisticsOrderDt "+ params.getSearchStatDt());

		ModelAndView mav = new ModelAndView();
		
		//Integer searchOrderId = params.getSearchOrderId();
				
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
			logger.debug("****** getStatisticsOrderDaily else *****getSearchStatDtFrom===*"+searchStatDtFrom);
			
			searchStatDtEnd = DateUtils.getNextDate(-1,"yyyy/MM/dd");
			logger.debug("****** getStatisticsOrderDaily else *****getSearchStatDtEnd===*"+searchStatDtEnd);
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);
			
			searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
		}		
		
		List<StatisticsOrderVO> statOrderList = statService.getDailylStatisticsOrderList(params);
		
		mav.addObject("statList", statOrderList);	
		
		//검색어 셋팅
		mav.addObject("searchStatDt", searchStatDt);			
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.stat_order"));	 		
		
		mav.setViewName("/gms/statistics/order/daily");
		
		return mav;
	}
	
	
	@RequestMapping(value = "/gms/statistics/order/monthly.do")
	public ModelAndView getStatisticsOrderMonthly(StatisticsOrderVO params) {

		logger.info("StatisticsOrderContoller getStatisticsOrderMonthly");
		logger.info("StatisticsOrderContoller searchStatisticsOrderDt "+ params.getSearchStatDt());

		ModelAndView mav = new ModelAndView();
		
		//Integer searchOrderId = params.getSearchOrderId();
				
		String searchStatDt = params.getSearchStatDt();	
		
		String searchStatDtFrom = null;
		String searchStatDtEnd = null;
				
		if(searchStatDt != null && searchStatDt.length() > 20) {						
			searchStatDtFrom = searchStatDt.substring(0, 10) ;			
			searchStatDtEnd = searchStatDt.substring(13, searchStatDt.length()) ;
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);			
		}else {						
			
			searchStatDtFrom = DateUtils.getNextDate(-365,"yyyy/MM/dd");
			logger.debug("****** getStatisticsOrderMonthly *****getSearchStatDtFrom===*"+searchStatDtFrom);
			
			searchStatDtEnd = DateUtils.getNextDate(-1,"yyyy/MM/dd");
			logger.debug("****** getStatisticsOrderMonthly *****getSearchStatDtEnd===*"+searchStatDtEnd);
			
			params.setSearchStatDtFrom(searchStatDtFrom);
			params.setSearchStatDtEnd(searchStatDtEnd);
			
			searchStatDt = searchStatDtFrom +" - "+ searchStatDtEnd;
		}		
		
		List<StatisticsOrderVO> statOrderList = statService.getMontlylStatisticsOrderList(params);
		
		mav.addObject("statList", statOrderList);	
		
		//검색어 셋팅
		mav.addObject("searchStatDt", searchStatDt);			
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.stat_order"));	 		
		
		mav.setViewName("/gms/statistics/order/monthly");
		
		return mav;
	}
}
