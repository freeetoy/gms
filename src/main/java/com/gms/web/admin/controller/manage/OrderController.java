package com.gms.web.admin.controller.manage;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.domain.common.CodeVO;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.GasVO;
import com.gms.web.admin.domain.manage.OrderVO;
import com.gms.web.admin.service.common.CodeService;
import com.gms.web.admin.service.manage.CustomerService;
import com.gms.web.admin.service.manage.OrderService;


@Controller
public class OrderController {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	/*
	 * UserService 빈(Bean) 선언
	 */
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CodeService codeService;
	
	@Autowired
	private CustomerService customerService;
	
	
	@RequestMapping(value = "/gms/order/list.do")
	public ModelAndView getOrderList(OrderVO params) {

		logger.info("BottleContoller getBottleList");
		logger.info("BottleContoller currentPage "+ params.getCurrentPage());
		logger.info("BottleContoller searchOrderDt "+ params.getSearchOrderDt());
		
		ModelAndView mav = new ModelAndView();
		
		String searchOrderDt = params.getSearchOrderDt();	
		
		String searchOrderDtFrom = null;
		String searchOrderDtEnd = null;
				
		if(searchOrderDt != null && searchOrderDt.length() > 20) {
						
			searchOrderDtFrom = searchOrderDt.substring(0, 10) ;
			
			searchOrderDtEnd = searchOrderDt.substring(13, searchOrderDt.length()) ;
			
			params.setSearchOrderDtFrom(searchOrderDtFrom);
			params.setSearchOrderDtEnd(searchOrderDtEnd);
			
		}
		
		//params.setBottleWorkCd(PropertyFactory.getProperty("common.bottle.status.0301"));
		
		Map<String, Object> map = orderService.getOrderList(params);
		
		mav.addObject("bottleList", map.get("list"));
		
		
		//검색어 셋팅

		mav.addObject("searchOrderDt", params.getSearchOrderDt());	
		
		mav.addObject("currentPage", map.get("currentPage"));
		mav.addObject("lastPage", map.get("lastPage"));
		mav.addObject("startPageNum", map.get("startPageNum"));
		mav.addObject("lastPageNum", map.get("lastPageNum"));
		mav.addObject("totalCount", map.get("totalCount"));
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.order"));	 
		
		
		mav.setViewName("gms/order/list");
		return mav;
	}
	
	
	@RequestMapping(value = "/gms/order/write.do")
	public ModelAndView openOrderWrite(Model model) {
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.order"));
	
		// 주문 타입 불러오기
		List<CodeVO> codeList = codeService.getCodeList(PropertyFactory.getProperty("common.code.order.type"));
		mav.addObject("codeList", codeList);	
		
		// 주문 타입 불러오기
		Map<String, Object> map = customerService.searchCustomerList("");
		mav.addObject("customerList", map.get("list"));
		
		mav.setViewName("/gms/order/write");
		
		return mav;
					
	}


		
}
