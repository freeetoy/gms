package com.gms.web.admin.controller.manage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.gms.web.admin.common.config.PropertyFactory;
import com.gms.web.admin.common.web.utils.RequestUtils;
import com.gms.web.admin.domain.common.CodeVO;
import com.gms.web.admin.domain.manage.BottleVO;
import com.gms.web.admin.domain.manage.CustomerPriceExtVO;
import com.gms.web.admin.domain.manage.CustomerPriceVO;
import com.gms.web.admin.domain.manage.GasVO;
import com.gms.web.admin.domain.manage.OrderProductVO;
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
		logger.info("BottleContoller searchCustomerNm "+ params.getSearchCustomerNm());
		
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
		
		
		Map<String, Object> map = orderService.getOrderList(params);
		
		mav.addObject("orderList", map.get("list"));	
		
		//검색어 셋팅
		mav.addObject("searchOrderDt", params.getSearchOrderDt());	
		mav.addObject("searchCustomerNm", params.getSearchCustomerNm());	
		
		mav.addObject("currentPage", map.get("currentPage"));
		mav.addObject("lastPage", map.get("lastPage"));
		mav.addObject("startPageNum", map.get("startPageNum"));
		mav.addObject("lastPageNum", map.get("lastPageNum"));
		mav.addObject("totalCount", map.get("totalCount"));
		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.order"));	 
		
		
		mav.setViewName("/gms/order/list");
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

	
	@RequestMapping(value = "/gms/order/register.do", method = RequestMethod.POST)
	public ModelAndView registerOrder(
			HttpServletRequest request
			, HttpServletResponse response
			, OrderVO params) {

		ModelAndView mav = new ModelAndView();		

		RequestUtils.initUserPrgmInfo(request, params);		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.order"));
		
		int result = 0;
		
		result = orderService.registerOrder(request,params);
		if(result > 0){
			String alertMessage = "등록되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage, "/gms/order/list.do");
		}
		return null;
	}
	
	
	@RequestMapping(value = "/gms/order/update.do")
	public ModelAndView openOrderUpdate(HttpServletRequest request
			, HttpServletResponse response,
			OrderVO params) {
		
		logger.info("OrderContoller openOrderUpate "+ params.getOrderId());
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.order"));
	
		
		
			
		if (params.getOrderId() == null) {
			return null;
		} else {
			
			OrderVO order = orderService.getOrderDetail(params.getOrderId());		
			
			if (order == null) {
				return null;
			}
			
			// 주문 타입 불러오기
			List<CodeVO> codeList = codeService.getCodeList(PropertyFactory.getProperty("common.code.order.type"));
			mav.addObject("codeList", codeList);	
			
			// 주문 타입 불러오기
			Map<String, Object> map = customerService.searchCustomerList("");
			mav.addObject("customerList", map.get("list"));
			
			mav.addObject("order", order);
			//mav.addObject("orderProductList", orderProductList);
			mav.addObject("currentPage", params.getCurrentPage());
			mav.addObject("searchCustomerNm", params.getSearchCustomerNm());
			mav.addObject("searchOrderDt", params.getSearchOrderDt());
			
		}
		mav.setViewName("/gms/order/update");
		
		return mav;
					
	}
	
	@RequestMapping(value = "/gms/order/modify.do", method = RequestMethod.POST)
	public ModelAndView modifyOrder(
			HttpServletRequest request
			, HttpServletResponse response
			, OrderVO params) {

		ModelAndView mav = new ModelAndView();		

		RequestUtils.initUserPrgmInfo(request, params);		
		mav.addObject("menuId", PropertyFactory.getProperty("common.menu.order"));
		
		int result = 0;
		
		result = orderService.modifyOrder(request,params);
		if(result > 0){
			String alertMessage = "수정되었습니다.";
			RequestUtils.responseWriteException(response, alertMessage,
					"/gms/order/list.do?currentPage="+params.getCurrentPage()+"&searchCustomerNm="+params.getSearchCustomerNm()+"&searchOrderDt="+params.getSearchOrderDt());
		}
		return null;
	}
	
	
	@RequestMapping(value = "/gms/order/orderProductList.do")
	@ResponseBody
	public List<OrderProductVO> getOrderProductList(@RequestParam(value = "orderId", required = false) Integer orderId, Model model)	{	
				
		logger.info("OrderContoller getOrderProductList "+ orderId);
		
		List<OrderProductVO> orderProductList = orderService.getOrderProductList(orderId);
		//model.addAttribute("orderProductList", orderProductList);
		
		return orderProductList;
	}
		
}
